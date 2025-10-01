# tests.md – Les tests dans Spring Boot

## 1) Rôle et importance des tests
Les tests dans Spring Boot permettent de **garantir la stabilité** et la **qualité du code** en automatisant la validation du comportement de l’application.  
Ils assurent que :
- La logique métier fonctionne correctement (tests unitaires).
- L’intégration entre couches (service ↔ repository ↔ DB) est correcte.
- L’application répond correctement via HTTP (tests d’API).
- Les régressions sont détectées tôt dans le cycle de développement.

---

## 2) Types de tests dans Spring Boot

### 2.1 Tests unitaires
- Ne démarrent **pas le contexte Spring**.
- Testent une **classe isolée** avec ses dépendances mockées (ex. `@Service` avec repository mock).
- Frameworks : **JUnit 5** (standard), **Mockito** (mocks/stubs).

### 2.2 Tests d’intégration
- Démarrent tout ou partie du contexte Spring (`@SpringBootTest`).
- Vérifient l’intégration entre plusieurs couches (repository ↔ DB, service ↔ repo, etc.).
- Souvent couplés à une **base de test** (H2 en mémoire, Testcontainers pour une DB réelle).

### 2.3 Tests web (API REST)
- Utilisent **MockMvc** ou **WebTestClient** pour tester les endpoints.
- Vérifient les statuts HTTP, les réponses JSON, la sécurité, etc.

### 2.4 Tests de repository
- Vérifient uniquement la couche repository (SQL/JPQL, mapping entités).
- Annotation : `@DataJpaTest` → démarre une base embarquée (par défaut H2).
- Permettent de tester la persistance sans charger tout le contexte.

### 2.5 Tests end-to-end (E2E)
- Simulent un scénario complet (ex. création d’un utilisateur → login → récupération d’un profil).
- Peuvent utiliser **Testcontainers** pour démarrer les vraies dépendances (PostgreSQL, Kafka, Redis, etc.).

---

## 3) Outils et bibliothèques utilisés

- **JUnit 5 / Jupiter** : moteur de tests moderne par défaut dans Spring Boot 3.x.
- **Spring Test** : annotations comme `@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest`.
- **Mockito** : mocks et stubs pour isoler les dépendances.
- **MockMvc** : simulateur d’appels HTTP.
- **WebTestClient** : alternative réactive à MockMvc (notamment pour WebFlux).
- **AssertJ** : assertions lisibles et expressives (`assertThat`).
- **Testcontainers** : démarrage de dépendances réelles (Postgres, Kafka…) dans des conteneurs Docker pour plus de réalisme.

---

## 4) Normes modernes (Spring Boot 3.x, Java 21)

- Utiliser **JUnit 5** (ne plus utiliser JUnit 4).
- Toujours nommer clairement les tests (`shouldDoX_whenY`).
- Préférer **@DataJpaTest** pour isoler les tests de repository.
- Utiliser `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)` pour des tests complets REST.
- Utiliser **profils dédiés aux tests** (`application-test.yml` avec H2 ou Testcontainers).
- Séparer les **tests unitaires** et les **tests d’intégration** (par packages ou conventions de nommage).

---

## 5) Bonnes pratiques

- Les tests doivent être **rapides** → isoler les vrais tests lourds (E2E).
- **Éviter les dépendances externes** en tests unitaires → mocker.
- **Toujours nettoyer** la DB entre tests (H2 recréée, ou Testcontainers avec réinitialisation).
- **Écrire d’abord les tests** pour les cas critiques (TDD si possible).
- Les **tests de sécurité** (authentification, autorisations) doivent faire partie de la suite.
- **Couverture de code** raisonnable : viser les cas critiques métier plutôt que 100 % aveugles.

---

## 6) Annotations clés

- `@Test` : test JUnit standard.
- `@BeforeEach`, `@AfterEach` : initialisation/cleanup par test.
- `@SpringBootTest` : démarre le contexte Spring complet.
- `@WebMvcTest(Controller.class)` : charge uniquement la couche web (controller + MVC infra).
- `@DataJpaTest` : tests repository avec DB embarquée.
- `@MockBean` : injection de dépendance mockée dans le contexte Spring.
- `@ActiveProfiles("test")` : active un profil spécifique (ex. DB H2).
- `@Testcontainers` et `@Container` : intégration avec Testcontainers.

---

## 7) Gestion des bases de données pour les tests

### Option 1 : H2 en mémoire
- Simple, rapide.
- Suffisant pour la plupart des tests unitaires.
- Attention : comportement différent de PostgreSQL/MySQL (dialectes SQL).

### Option 2 : Testcontainers
- Lance une vraie instance PostgreSQL/MySQL dans Docker.
- Idéal pour les tests d’intégration réalistes.
- Plus lent mais plus fidèle à la production.
- S’utilise avec l’annotation `@Testcontainers` + configuration JDBC dynamique.

---

## 8) Observabilité et reporting

- Générer des rapports JUnit XML (intégration CI/CD).
- Ajouter **JaCoCo** pour mesurer la couverture de code.
- Intégrer les tests dans GitHub Actions / GitLab CI / Jenkins.
- Catégoriser les tests (unitaires, intégration, e2e).

---

## 9) Checklist rapide

- [ ] JUnit 5 activé et configuré.
- [ ] Tests unitaires avec Mockito pour isoler les dépendances.
- [ ] Tests d’intégration avec `@SpringBootTest`.
- [ ] Repositories testés avec `@DataJpaTest`.
- [ ] Profils `test` avec H2 ou Testcontainers.
- [ ] Tests API avec MockMvc ou WebTestClient.
- [ ] Rapports de couverture via JaCoCo.
- [ ] Tests exécutés automatiquement en CI/CD.  

## 10) Les Mocks dans Spring Boot

### 10.1 Définition
Un **mock** est un objet simulé qui remplace une vraie dépendance dans un test.  
Il permet de tester une classe **en isolation** en contrôlant les comportements de ses dépendances.  
En Spring Boot, les mocks sont généralement utilisés dans les **tests unitaires** et dans les **tests d’intégration partielle**.

---

### 10.2 Pourquoi utiliser des mocks ?
- **Isoler** la classe testée : on évite que le comportement dépende d’un repository, d’une API externe, ou d’une autre couche.
- **Contrôler le retour** d’une dépendance : on force un résultat pour tester des cas précis.
- **Accélérer** les tests : on ne démarre pas inutilement une base de données ou un service distant.
- **Détecter les régressions** au niveau métier sans dépendre de l’infrastructure.

---

### 10.3 Outils pour les mocks
- **Mockito** (par défaut avec Spring Boot Test) → création et gestion des mocks.
- **@MockBean** (Spring Boot) → insère un mock dans le **contexte Spring**, remplaçant un vrai bean.
- **@Mock** (Mockito pur) → crée un mock indépendant du contexte Spring (pur test unitaire).
- **@InjectMocks** (Mockito) → injecte automatiquement les mocks dans la classe testée.

---

### 10.4 Différences entre les annotations
- `@Mock` : crée un mock isolé (utile hors contexte Spring).
- `@InjectMocks` : permet à Mockito d’injecter automatiquement les mocks dans la classe testée.
- `@MockBean` : créé par Spring Boot, remplace le vrai bean dans le contexte Spring par un mock.
    - Utilisé surtout dans les tests annotés avec `@SpringBootTest` ou `@WebMvcTest`.

---

### 10.5 Bonnes pratiques d’utilisation
- **N’utiliser des mocks que pour les dépendances externes** (repositories, API externes, services tiers).
- **Ne pas mocker la classe que l’on teste** (mauvaise pratique → test inutile).
- **Limiter le nombre de mocks** : trop de mocks indique souvent que la classe a trop de responsabilités.
- **Nommer explicitement les scénarios de mocks** (ex. `when(repository.findById(1L)).thenReturn(Optional.of(entity))`).
- **Vérifier les interactions** uniquement quand c’est pertinent (`verify(repository).save(any())`).

---

### 10.6 Avantages et limites
✅ **Avantages**
- Rapidité d’exécution des tests.
- Contrôle total sur les dépendances.
- Permet de simuler des cas d’erreur difficiles à reproduire (ex. exception réseau).

❌ **Limites**
- Un mock ne garantit pas que l’implémentation réelle fonctionne (nécessité de compléter par des tests d’intégration).
- Risque de tests trop “coupés de la réalité” si on abuse des mocks.
- Maintenance : si l’on change trop souvent les interactions internes, les tests deviennent fragiles.

---

### 10.7 Stratégie recommandée
- **Tests unitaires (isolés)** → utiliser **Mockito @Mock / @InjectMocks**.
- **Tests Spring Boot partiels** (ex. controller avec un service mocké) → utiliser **@MockBean**.
- **Tests d’intégration complets** → éviter les mocks, tester avec la vraie infrastructure (H2, Testcontainers).

---

### 10.8 Checklist d’utilisation des mocks
- [ ] Seule la classe testée est réelle, toutes ses dépendances sont mockées.
- [ ] Les retours de mocks couvrent les cas normaux et les cas d’erreurs.
- [ ] Les interactions essentielles sont vérifiées (`verify`).
- [ ] Pas de mocks inutiles (chaque mock doit avoir un rôle dans le scénario testé).
- [ ] Compléter les mocks par des **tests d’intégration réels**.  

# Marche à suivre simple – Tester un Service avec des Mocks

### Étape 1 : Identifier ce qu’on veut tester
- On choisit **UNE classe de service** (ex : `UserService`).
- On décide quelle **méthode** on veut vérifier (ex : `createUser`).

👉 Exemple : `UserService` utilise un `UserRepository`.

---

### Étape 2 : Remplacer les dépendances par des Mocks
- Un **mock** = une **fausse version** d’une dépendance.
- Ici, au lieu d’utiliser le vrai `UserRepository` (qui irait en DB), on le remplace par un **mock**.
- Cela permet de **tester le service seul** sans avoir besoin d’une vraie base.


---

### Appliquer la démarche AAA

Le schéma **AAA (Arrange – Act – Assert)** aide à écrire un test clair et structuré :

- **Arrange (Préparer)**  
  Préparer les données d’entrée et dire aux mocks quoi répondre.  
  👉 Exemple : “quand on cherche `alice@example.com`, le repo renvoie vide.”

- **Act (Agir)**  
  Appeler la méthode du service à tester.  
  👉 Exemple : appeler `createUser("Alice")`.

- **Assert (Vérifier)**  
  Vérifier que le résultat est correct **et** que les mocks ont bien été utilisés comme prévu.  
  👉 Exemple : vérifier que l’email est normalisé en minuscules et que `save()` a été appelé une fois.


### Étape 3 : Définir le comportement attendu des Mocks
- On dit au mock **quoi répondre** quand on l’appelle.
- Exemple : *“quand le repo cherche un user avec l’ID 1, il doit renvoyer un faux utilisateur”*.
- On peut aussi dire au mock de **lancer une exception** (ex : pour tester une erreur).

---

### Étape 4 : Appeler la méthode du Service
- On appelle la méthode du service (ex : `createUser(dto)`).
- Comme le repo est mocké, il ne fera pas d’accès réel à la DB → il utilisera la réponse qu’on a définie à l’étape 3.

---

### Étape 5 : Vérifier le résultat
- On vérifie que la méthode du service a donné **le bon résultat** (ex : user créé avec le bon nom).
- On peut aussi vérifier que le service a bien **appelé le mock** (ex : `save()` a été utilisé une fois).

---

### Étape 6 : Répéter pour les cas différents
- Un test pour le **chemin normal** (succès).
- Un test pour le **cas d’erreur** (ex : repo renvoie vide → le service doit lancer une exception).
- Un test pour les **valeurs spéciales** (ex : email déjà existant).

---

## Résumé ultra-simple
1. **Choisir la méthode** du service à tester.
2. **Remplacer ses dépendances** (repo, API externe) par des mocks.
3. **Dire aux mocks quoi répondre** (réponse normale ou exception).
4. **Appeler la méthode du service**.
5. **Vérifier le résultat** et que le service a bien utilisé les mocks.
6. **Répéter** pour succès + erreur.

---

✅ Avec cette méthode, tu testes ton service **sans DB, sans réseau** → rapide et isolé.  
Ensuite, tu feras des **tests d’intégration** pour vérifier que tout marche avec la vraie DB.
