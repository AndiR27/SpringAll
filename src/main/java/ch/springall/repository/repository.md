## 5) Repositories Spring

### Introduction
Les **repositories** représentent la couche d’accès aux données.  
Avec Spring Boot, on s’appuie sur **Spring Data** qui fournit une abstraction puissante pour interagir avec les bases de données sans écrire de code répétitif.  
Deux grandes familles existent :
- **Spring Data JPA** (Jakarta Persistence, basé sur Hibernate, bloquant).
- **Spring Data R2DBC** (Reactive Relational Database Connectivity, non bloquant).

---

### 1) Principes de base
- Les repositories sont définis comme **interfaces**.
- Spring génère automatiquement les implémentations au démarrage.
- Ils héritent d’interfaces de Spring Data (`JpaRepository`, `CrudRepository`, `ReactiveCrudRepository`…), ce qui donne accès à un ensemble de méthodes CRUD par défaut.

**Avantages clés :**
- Réduction du code boilerplate.
- Génération automatique de requêtes par convention (`findByEmail`, `findByNameAndAge`).
- Extension possible avec requêtes personnalisées (JPQL, SQL natif, Criteria, projections).

---

### 2) Spring Data JPA Repositories

#### Interfaces principales
- `CrudRepository<T, ID>` : opérations CRUD de base.
- `JpaRepository<T, ID>` : inclut pagination, tri, batch.
- `PagingAndSortingRepository<T, ID>` : pagination et tri uniquement.

#### Génération de requêtes par convention
- Spring interprète le nom de la méthode et génère la requête correspondante.
    - Exemples :
        - `findByEmail(String email)`
        - `findByNameAndAge(String name, int age)`
        - `existsByUsername(String username)`

**Mots-clés fréquemment utilisés :**
- `findBy`, `existsBy`, `deleteBy`, `countBy`
- `And`, `Or`, `Between`, `LessThan`, `GreaterThan`, `Like`, `OrderBy`

#### Pagination et tri
- Utiliser `Pageable` et `Page<T>` pour paginer les résultats.
- Utiliser `Sort` pour trier.

#### Requêtes personnalisées
- **@Query** : définir des requêtes JPQL ou SQL natif.
- **Named queries** : définies dans les entités (`@NamedQuery`).
- **Specifications** : API de critères dynamiques (`JpaSpecificationExecutor`).

---

### 3) Spring Data R2DBC Repositories

Avec R2DBC, on entre dans le monde réactif : le repository doit gérer des flux de données non bloquants.
Il s'agira donc de travailler avec **Mono** (0..1 résultat) et **Flux** (0..n résultats).
Comme avec JPA, on définit des **interfaces**, mais on utilise des interfaces spécifiques à R2DBC.

#### Interface principale
- `ReactiveCrudRepository<T, ID>` : équivalent réactif de `CrudRepository`.
- Renvoie des **Mono<T>** (0..1 résultat) ou **Flux<T>** (0..n résultats).

#### Points importants
- Pas de lazy loading ni de relations complexes → penser en **agrégats simples**.
- Pas de transactions implicites comme JPA → utiliser `TransactionalOperator`.
- Pas de `EntityManager` → on passe par des `DatabaseClient` pour les cas spécifiques.

#### Avantages
- Non bloquant, adapté aux applications à haute concurrence (WebFlux).
- Intégration native avec Reactor (Flux/Mono).

---

### 4) Personnalisation des repositories

#### Méthodes par défaut
- `save()`, `findById()`, `findAll()`, `deleteById()`, `count()`.

#### Ajout de méthodes custom
- Définir une méthode dans l’interface → Spring Data la génère si possible.
- Si trop complexe, on peut :
    - Utiliser `@Query`.
    - Créer un repository custom avec une implémentation manuelle (`CustomRepository + Impl`).

#### Fragments de repository
- Découper les repositories en **interfaces fragmentées** et fournir des implémentations spécifiques pour réutiliser des morceaux de logique.

---

### 5) Gestion des transactions

- **Spring Data JPA** :
    - `@Transactional` sur les méthodes de service (couche métier).
    - Le repository ne gère pas les transactions lui-même, mais participe à celles ouvertes en amont.

- **Spring Data R2DBC** :
    - Transactions gérées via `TransactionalOperator`.
    - Attention : pas de propagation automatique comme JPA.

---

### 6) Projections et DTO

- Objectif : **ne pas exposer directement les entités**.
- **Projections** permettent de sélectionner uniquement certains champs.
    - Interfaces : Spring mappe les résultats sur une interface définie.
    - Classes (DTO) : Spring instancie un DTO avec les champs nécessaires.

**Avantages :**
- Réduire la quantité de données échangées.
- Protéger le domaine en évitant l’exposition d’entités.
- Améliorer la performance en limitant le `SELECT`.

---

### 7) Bonnes pratiques

- Toujours séparer **entités** (JPA/R2DBC) et **DTO** (API).
- Utiliser `Pageable` et `Slice` pour la pagination, pas de listes non bornées.
- Préférer `Optional<T>` pour les retours uniques (JPA) → `Mono<T>` pour R2DBC.
- Ne pas mettre de logique métier dans les repositories : **ils ne doivent gérer que la persistance**.
- Pour JPA, privilégier `fetch = LAZY` et utiliser des **EntityGraph** pour optimiser.
- Pour R2DBC, modéliser les entités comme **agrégats simples** sans navigation complexe.

---

### 8) Pièges fréquents

- Mauvaise gestion du `fetch` → N+1 problem avec JPA.
- Relations bidirectionnelles mal configurées → boucles infinies lors de la sérialisation JSON.
- Trop de logique métier dans le repository → doit rester une **abstraction d’accès aux données**.
- Mélanger **JPA (bloquant)** et **R2DBC (réactif)** dans la même application → incohérences et blocages.
- Confusion entre `Page` (retourne le total) et `Slice` (pas de total → plus léger).

---

### 9) Checklist rapide

- Choisir **JpaRepository** (bloquant) ou **ReactiveCrudRepository** (non bloquant) selon l’archi.
- Ne pas exposer directement les entités → utiliser DTO/projections.
- Centraliser les transactions au niveau **service**, pas au niveau repository.
- Limiter les relations complexes dans R2DBC.
- Utiliser `@Query` ou un repository custom pour les cas spécifiques.
- Vérifier les performances → attention au N+1 avec JPA.  
