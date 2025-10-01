## 1) Rôle et responsabilités

Un **Service** porte le **cas d’usage** (use case) : il coordonne repositories, adaptateurs externes (email, paiement, S3…), mappers et règles métier pour produire un **effet observable** (un état métier modifié, un événement publié, une réponse consolidée).

**Concrètement**
- *Créer une commande* : vérifie l’existence du client, la disponibilité des articles, applique les politiques de prix/remise, réserve le stock, persiste la commande, publie un événement “OrderCreated”, retourne un récapitulatif (DTO).
- *Mettre à jour un profil* : contrôle l’ownership (l’utilisateur modifie bien son profil), normalise les champs (emails en lower-case), persiste, invalide le cache, renvoie la vue courante du profil.
- *Rapprocher un paiement* : récupère la transaction PSP, contrôle idempotence (ne pas appliquer 2×), met à jour le statut de la facture, publie “PaymentReconciled”.

**Ce qu’un Service NE fait pas**
- Pas d’I/O Web (pas de parsing HTTP, ni de sérialisation JSON) → c’est le contrôleur.
- Pas de SQL/HQL direct → c’est le repository.
- Pas de détails d’infrastructure (client HTTP concret, SDK cloud) → passer par un **port** (interface), implémenté en **adapter**.

**Objectifs**
- **Clarté du contrat** : chaque méthode correspond à un cas d’usage métier.
- **Testabilité** : dépendances injectées et mockables, effets vérifiables.
- **Stabilité** : API interne du service stable même si l’infra évolue.


## 2) Conception et style (Java 21+, Boot 3.x)

**Principes**
- **Une responsabilité claire** par service (éviter les “god services”). Si un service gère des sujets hétérogènes, scinde-le par domaines/flows.
- **Nommer par cas d’usage** : `OrderService.approveOrder`, `EnrollmentService.registerStudent`. Les verbes expriment l’intention métier.
- **Frontières explicites** : côté Web ↔ **Service** ↔ Persistance/Externes. Les DTO exposés par le service peuvent être différents de ceux de l’API.
- **Données de sortie utiles** : renvoyer des **DTO** contenant l’état utile post-opération (id, statut, horodatage, liens décisionnels), pas l’entité brute.

**Exemples concrets**
- *Service de commande* : `create`, `approve`, `cancel`, `ship` → chaque méthode valide le statut courant avant transition, publie un événement, déclenche d’éventuels side-effects (mail via port).
- *Service d’inscription* : `register`, `confirm`, `withdraw` → applique les quotas, les fenêtres temporelles, la priorité, met à jour les compteurs.

**Ergonomie & maintenance**
- **Méthodes courtes** orchestrant des composants spécialisés (policies, calculators, mappers).
- **Structures immuables** (records) pour les DTO d’entrée/sortie du service.
- **Journalisation** orientée métier (ids, statuts, résultat), jamais de données sensibles.


## 3) Injection de dépendances (DI) & IoC

**But** : découpler le service de ses collaborations pour le rendre **testable** et **remplaçable**.

**Comment structurer**
- **Constructeur unique** qui reçoit des **interfaces** (ports) et des abstractions (repositories, mappers). Les champs sont `final`.
- **Ports & adapters** : le service dépend d’un `PaymentPort` (interface). En prod, on injecte `StripePaymentAdapter`; en test, un fake.
- **Zéro “new”** dans le service pour des dépendances significatives. Les objets lourds/protocolaires viennent du conteneur.
- **Config typée** pour les paramètres (limites, délais, seuils) : les règles paramétrables ne sont pas “magiques” dans le code.

**Exemples concrets**
- *Envoyer un e-mail de confirmation* : le service appelle `NotificationPort.sendOrderConfirmation(orderDTO)` ; l’implé concrète choisit SMTP/Provider X.
- *Appeler un système tiers* : `CatalogPort.reserveSkus(items)` ; en test, un double renvoie des réponses déterministes.

**Bénéfices**
- **Tests isolés** sans réseau/DB.
- **Substitution** simple des implémentations (ex. basculer de Mailgun à SES).
- **Évolution** de l’infra sans impacter le domaine.


## 4) Transactions

**Idée clé** : la *frontière transactionnelle* se situe **au niveau de la méthode du service** (cas d’usage), pour garantir l’atomicité métier.

**Règles pratiques**
- **Écrire sous transaction** : création/transition de statut, écritures multi-repositories → même unité de travail.
- **Lecture pure** : marquer **read-only** (optimisations, intentions claires).
- **Propagation** : par défaut `REQUIRED`. Si un service A appelle B dans la même unité métier, ne casse pas la transaction sauf raison explicite.
- **Événements après commit** : publier les événements métier *après* validation (pattern outbox recommandé pour la fiabilité inter-services).

**Idempotence**
- **Clés d’idempotence** (ex. `requestId`) ou **verrouillage optimiste** (`version`) pour éviter double exécution (retries réseau, replays de messages).
- **Cas concrets** : rapprochement paiement, envoi d’e-mail, écriture côté partenaire → safe à rejouer sans effets indésirables.

**R2DBC**
- Pas d’`EntityManager`. Utiliser un **opérateur transactionnel** réactif ; rester non bloquant de bout en bout (pas de mix JPA ↔ R2DBC dans le même flow).


## 5) Validation, invariants et règles métier

**Frontière de validation**
- **Entrée service** : au-delà de la validation syntaxique de l’API, vérifier les **préconditions métier** (droit d’agir, fenêtre temporelle, stocks, compatibilité de statut).
- **Invariants** : garantir que l’état résultant respecte les règles (ex. une commande `APPROVED` ne peut pas avoir `total=0`).

**Erreurs métier claires**
- Lever des **exceptions métier** explicites (`NotAllowed`, `InvalidStateTransition`, `DomainConflict`) → elles seront mappées par la couche Web.
- Messages utiles pour le diagnostic (ids, statut), **sans données sensibles**.

**Paramétrage**
- Les **règles variables** (seuils, quotas, fenêtres) viennent d’une **configuration typée** ou d’un référentiel de paramètres, pas de constantes “magiques” dispersées.

**Exemples concrets**
- *Approve order* : préconditions = commande `PENDING`, stock réservé, paiement autorisé. Invariant post = statut `APPROVED`, `approvedAt` défini, événement “OrderApproved” publié.
- *Register student* : préconditions = session ouverte, capacité disponible, profil éligible. Invariant post = inscription unique par étudiant, compteur cohérent, émission d’un reçu.

**Séparation des préoccupations**
- **Policies/calculators** : isoler les règles calculatoires (taxes, remises, score) pour les tester indépendamment.
- **Mappers** : conversions entité↔DTO dans un composant dédié ; le service n’est pas un “convertisseur géant”.

