# Gestion des exceptions dans Spring Boot

La gestion des exceptions dans Spring Boot constitue un pilier fondamental de la qualité d’un projet. Elle permet de rendre les erreurs **prévisibles**, **cohérentes** et **compréhensibles** aussi bien pour le client que pour le développeur. Une bonne gestion des exceptions ne consiste pas à masquer les erreurs, mais à les transformer en réponses maîtrisées, claires et traçables.

---

## Principe général

Spring Boot repose sur le principe de **propagation et de traduction contrôlée des exceptions**. Lorsqu’une erreur se produit dans la couche métier ou dans la couche d’accès aux données, elle remonte jusqu’à la couche web, où elle est capturée et transformée en une réponse HTTP structurée.  
L’objectif est double :
- préserver la **clarté fonctionnelle** du message côté client (sans détails techniques inutiles),
- conserver la **traçabilité complète** côté serveur pour le diagnostic.

Une exception ne doit jamais être laissée brute dans une API : elle doit être contextualisée, traduite et enregistrée dans les logs avec les métadonnées nécessaires (date, identifiant de requête, cause).

---

## Types d’exceptions à distinguer

Dans un projet bien structuré, les exceptions se classent en trois grandes catégories :

1. **Exceptions métier** : elles représentent une violation de règle fonctionnelle. Par exemple, une ressource inexistante ou un conflit d’état. Ces exceptions doivent être explicites et porteuses d’un message clair, sans notion technique.

2. **Exceptions techniques** : elles proviennent des couches d’infrastructure (base de données, réseau, sérialisation, etc.). Elles ne doivent jamais être exposées au client. Spring recommande de les intercepter, de les loguer, puis de les transformer en erreur HTTP générique.

3. **Exceptions de validation** : issues des contraintes de Bean Validation (`@NotNull`, `@Email`, etc.), elles doivent être traitées séparément afin de retourner une réponse structurée, listant les champs invalides et les messages associés.

---

## Centralisation du traitement

Spring Boot encourage l’utilisation d’un point central pour gérer les erreurs à travers l’annotation `@RestControllerAdvice`. Cette approche regroupe les règles de traduction des exceptions dans une seule classe, évitant leur répétition dans chaque contrôleur.

Chaque méthode de cette classe est annotée avec `@ExceptionHandler`, qui précise le type d’exception à intercepter. Cela permet de définir le comportement attendu pour chaque type d’erreur : code HTTP, message, corps de la réponse, etc.  
Cette centralisation renforce la cohérence et garantit que toutes les erreurs produisent un **format uniforme** de réponse.

---

## Standardisation des réponses

Depuis Spring Boot 3, le format recommandé pour les erreurs est **Problem Details** (standard RFC 7807). Il s’agit d’une structure normalisée contenant des champs tels que :

- `type` : un identifiant stable de l’erreur, souvent une URI descriptive
- `title` : une courte description du problème
- `status` : le code HTTP correspondant
- `detail` : un message explicatif adapté à la situation
- `instance` : l’URI de la ressource concernée

Ce modèle garantit que toutes les réponses d’erreurs respectent une même forme, facilitant la compréhension et l’intégration côté client.

---

## Bonnes pratiques d’implémentation

Une implémentation correcte des exceptions repose sur quelques principes simples mais essentiels :

- **Nommer clairement les exceptions** selon leur nature : par exemple `UserNotFoundException`, `InvalidRequestException` ou `DuplicateEntryException`.
- **Ne jamais exposer de détails internes** (stacktrace, SQL, configuration). Seul le message fonctionnel doit être retourné au client.
- **Utiliser des statuts HTTP appropriés** : `404` pour une ressource manquante, `400` pour une requête invalide, `409` pour un conflit, `500` pour une erreur interne.
- **Isoler les erreurs de validation** : les erreurs liées à la validation d’un DTO doivent produire une réponse structurée avec la liste des champs invalides.
- **Tracer systématiquement l’exception** côté serveur**, avec un identifiant de corrélation (traceId)**, pour permettre un suivi efficace des incidents.
- **Ne pas utiliser de `try/catch` silencieux** : toute exception attrapée doit être soit traitée, soit relancée sous une forme contrôlée.

---

## Intégration avec la validation et la sécurité

Les exceptions issues de la validation (`MethodArgumentNotValidException` ou `ConstraintViolationException`) doivent être capturées et traduites pour renvoyer des messages compréhensibles.  
Elles ne doivent pas interrompre le flux de manière brute, mais informer clairement le client sur les champs erronés.

Du côté de la sécurité, les erreurs comme `AccessDeniedException` ou `AuthenticationException` sont interceptées par les filtres Spring Security. Il est toutefois recommandé d’unifier leur format de sortie avec celui du reste des erreurs applicatives, afin de conserver une cohérence d’ensemble.

---

## Rôle du logging et de la traçabilité

Chaque exception interceptée doit être **journalisée** avec le bon niveau (`WARN` pour les erreurs fonctionnelles, `ERROR` pour les défaillances techniques).  
Les logs doivent contenir le **message complet**, la **classe d’origine**, la **cause racine** et le **traceId** si le traçage distribué est activé (Micrometer Tracing ou OpenTelemetry).  
Le but n’est pas de multiplier les logs, mais de disposer d’une **source unique et exploitable** pour les analyses post-incidents.

---

## Résumé

Une gestion moderne des exceptions dans Spring Boot repose sur quelques fondations solides :
- définir des exceptions métier explicites et isolées,
- centraliser le traitement via `@RestControllerAdvice` et `@ExceptionHandler`,
- normaliser les réponses au format **Problem Details (RFC 7807)**,
- garantir la traçabilité complète grâce à la journalisation et aux identifiants de corrélation,
- éviter toute exposition d’informations internes ou sensibles.

En appliquant ces principes, l’application gagne en robustesse, en clarté et en maintenabilité.  
Les exceptions cessent d’être un simple signal d’échec pour devenir un **véritable outil de communication fiable** entre le serveur et le client.
