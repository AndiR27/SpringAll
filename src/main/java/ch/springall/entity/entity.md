# entity.md – Entités et persistance dans Spring Boot

## 1) Introduction

Les entités représentent les **objets métier persistés en base de données**.  
Dans un projet Spring Boot moderne (3.x avec Java 21+), elles se définissent en utilisant :

- **Jakarta Persistence (JPA)** avec `@Entity` → approche traditionnelle, synchrone, adaptée aux bases relationnelles via Hibernate.
- **Spring Data R2DBC** avec `@Table` et `@Column` → approche réactive, non bloquante, optimisée pour des applications massivement concurrentes.

Les deux approches ne doivent pas être mélangées dans le même contexte :
- **JPA** → simplicité, maturité, transactions classiques.
- **R2DBC** → scalabilité, intégration avec WebFlux, absence de blocage.

---

## 2) Entités avec Jakarta Persistence (JPA)

### Normes récentes
- Depuis Spring Boot 3.x, toutes les annotations viennent de **`jakarta.persistence.*`** (et plus de `javax`).
- Compatible avec Hibernate 6 et Jakarta EE 10.
- Utilisation courante avec `spring-boot-starter-data-jpa`.

### Principes clés
- Chaque entité correspond à une **table** en base de données.
- Chaque champ correspond à une **colonne**.
- Un champ est désigné comme **clé primaire** via `@Id`.
- Les relations entre entités sont exprimées avec :
    - `@OneToOne`
    - `@OneToMany`
    - `@ManyToOne`
    - `@ManyToMany`

## Principes clés d’une Entité Jakarta (JPA) en détails

### 1) Définir une entité
- **Annotation :** `@Entity`
- Marque une classe comme persistable en base.
- Optionnellement, on peut spécifier le nom de la table avec `@Table(name = "nom_table")`.

**Paramètres fréquents de `@Table` :**
- `name` → nom de la table.
- `schema` → schéma de la base.
- `uniqueConstraints` → contraintes d’unicité au niveau de la table.
- `indexes` → création d’index pour certaines colonnes.

---

### 2) Définir la clé primaire
- **Annotation :** `@Id`
- Chaque entité doit avoir une clé primaire.

**Génération automatique :**
- `@GeneratedValue(strategy = …)`
    - `GenerationType.IDENTITY` → auto-incrément (DB gère).
    - `GenerationType.SEQUENCE` → séquence DB (Oracle, PostgreSQL).
    - `GenerationType.TABLE` → table spéciale (rare).
    - `GenerationType.AUTO` → laisse le provider décider.

**Paramètres utiles de `@GeneratedValue` :**
- `strategy` → mode de génération.
- `generator` → nom du générateur personnalisé.

---

### 3) Colonnes de la table
- **Annotation :** `@Column`
- Configure le mapping entre champ et colonne SQL.

**Paramètres fréquents :**
- `name` → nom de la colonne en base.
- `nullable` → si la colonne accepte `NULL`.
- `unique` → contrainte d’unicité.
- `length` → longueur maximale (utile pour `VARCHAR`).
- `precision` et `scale` → nombre de chiffres totaux et décimales (pour `BigDecimal`).
- `insertable` et `updatable` → si la colonne est incluse dans les opérations SQL.

---

### 4) Contraintes de validation (Bean Validation)
- Utilisation de **Jakarta Validation** (`jakarta.validation.*`).
- Ajout de règles directement sur les champs d’entité.

**Exemples fréquents :**
- `@NotNull` → champ obligatoire.
- `@NotBlank` → chaîne non vide.
- `@Size(min, max)` → taille minimale/maximale.
- `@Email` → format d’adresse email.
- `@Past` / `@Future` → dates contraintes.

---

### 5) Relations entre entités
Communication entre entités via des associations.
On définit le propriétaire de la relation pour gérer les opérations en cascade.
Pour définir le propriétaire, on utilise `mappedBy` sur le côté inverse.
Le cascade permet de propager les opérations (persist, merge, remove) de l’entité propriétaire vers les entités liées.
Le fetch définit la stratégie de chargement (immédiat ou différé) des entités associées.

les relations peuvent être unidirectionnelles ou bidirectionnelles.
#### a) OneToOne
- **Annotation :** `@OneToOne`
- Relation 1-1 entre deux entités.
- Souvent combinée avec `@JoinColumn`.

**Paramètres fréquents :**
- `mappedBy` → côté inverse de la relation.
- `cascade` → opérations propagées (`PERSIST`, `MERGE`, `REMOVE`…).
- `fetch` → stratégie de chargement (`LAZY`, `EAGER`).

---

#### b) ManyToOne
- **Annotation :** `@ManyToOne`
- Relation N-1 (plusieurs entités liées à une seule).

**Paramètres fréquents :**
- `fetch` → `LAZY` recommandé par défaut.
- `optional` → si la relation peut être nulle.

---

#### c) OneToMany
- **Annotation :** `@OneToMany`
- Relation 1-N (une entité liée à plusieurs).

**Paramètres fréquents :**
- `mappedBy` → champ du côté propriétaire.
- `cascade` → définir quelles opérations sont propagées.
- `orphanRemoval` → suppression automatique des enfants orphelins.

---

#### d) ManyToMany
- **Annotation :** `@ManyToMany`
- Relation N-N entre deux entités.
- Utilisation de `@JoinTable` pour préciser la table d’association.

**Paramètres fréquents :**
- `mappedBy` → côté inverse.
- `cascade` → propagation des opérations.
- `fetch` → `LAZY` recommandé.

---

### 6) Colonnes spécifiques
#### a) Colonnes temporelles
- **Annotation :** `@Temporal` (pour `Date`)
- Mais **préférer `LocalDate`, `LocalDateTime`, `Instant`** avec Java 21.

#### b) Colonnes immuables
- `@Column(updatable = false)` → valeur définie uniquement lors de l’insertion.

#### c) Colonnes générées
- `@Generated(…)` → colonnes calculées par la DB (rarement utilisé).

---

### 7) Gestion de version et concurrence
- **Annotation :** `@Version`
- Implémente le **verrouillage optimiste** (optimistic locking).
- Ajoute un champ version (int, long, timestamp).

---

### 8) Héritage
- **Annotation :** `@Inheritance(strategy = …)`
- Gère l’héritage objet → tables en DB.

**Stratégies possibles :**
- `SINGLE_TABLE` → toutes les classes dans une seule table (avec discriminant).
- `JOINED` → une table par sous-classe reliée à la table parent.
- `TABLE_PER_CLASS` → une table par classe concrète.

**Paramètres :**
- `discriminatorColumn` et `discriminatorValue` pour identifier le type.

---


### Bonnes pratiques
- Utiliser des **types modernes Java** (`LocalDate`, `Instant`, `UUID`) plutôt que les anciens (`Date`, `Calendar`).
- Préférer les **records** pour les DTO et conserver les entités en **classes** (car elles nécessitent un constructeur vide, setters, proxy Hibernate).
- Définir les contraintes de colonne directement (`nullable`, `unique`, `length`).
- Désactiver **Open Session in View** (`spring.jpa.open-in-view=false`) pour éviter les fuites de contexte en couche web.
- Versionner le schéma avec **Flyway** ou **Liquibase**.

### Points critiques
- Attention aux **relations bidirectionnelles** → risque de boucles infinies dans la sérialisation JSON (à gérer avec DTO).
- Éviter les **fetch eager** par défaut, privilégier `LAZY` pour limiter les surcharges.
- Contrôler les transactions via `@Transactional`.

---

## 3) Entités avec Spring Data R2DBC

### Normes récentes
- API **Reactive Relational Database Connectivity (R2DBC)** → compatible avec Spring Boot 3.x.
- Dépendances : `spring-boot-starter-data-r2dbc` + driver R2DBC spécifique (Postgres, MySQL, etc.).
- Annotations de mapping issues de **`org.springframework.data.relational.core.mapping`**.

### Principes clés
- Chaque entité est annotée avec `@Table`.
- Chaque champ correspond à une colonne avec `@Column`.
- La clé primaire est définie avec `@Id`.
- Pas de support natif pour les relations complexes (`@OneToMany`, `@ManyToOne`).
    - Il faut gérer les relations manuellement ou via des **agrégats**.

### Bonnes pratiques
- Utiliser des **records** Java pour les entités R2DBC → immuabilité, plus simple que JPA.
- Conserver les modèles plats (éviter les graphes d’objets complexes).
- Encapsuler la logique de jointures dans les **repositories** ou services.
- Tirer parti du **flux réactif (Flux/Mono)** pour chaîner les opérations non bloquantes.

### Points critiques
- Pas de **lazy loading** comme JPA → les relations doivent être chargées explicitement.
- Pas de gestion automatique des transactions multi-requêtes → il faut utiliser `TransactionalOperator`.
- Ne pas mélanger **blocant (JPA)** et **réactif (R2DBC)** dans une même chaîne de traitement.

---

## 4) Comparaison JPA vs R2DBC

| Aspect                   | JPA (Jakarta Persistence)           | R2DBC (Réactif)                       |
|--------------------------|-------------------------------------|---------------------------------------|
| API                      | `jakarta.persistence.*`             | `org.springframework.data.relational.*` |
| Exécution                | Synchrone, bloquant                 | Réactif, non bloquant                  |
| Transactions             | Oui (`@Transactional`)              | Oui mais manuelles (`TransactionalOperator`) |
| Relations entre entités  | Support complet (OneToOne, etc.)    | Basique, relations manuelles           |
| Lazy loading             | Oui                                 | Non                                    |
| Typologie d’applications | Standard (monolithe, microservice)  | Scalabilité, WebFlux, haute concurrence |
| Modélisation             | Entités riches                      | Agrégats simples                       |

---

## 5) Bonnes pratiques transversales

- Toujours séparer **entités** (persistence) et **DTO** (API).
- Centraliser les mappings via **MapStruct** ou des classes dédiées.
- Versionner la base dès le début (Flyway/Liquibase).
- Gérer les IDs avec des **UUID** plutôt qu’autoincrement dans les projets distribués.
- Ne jamais exposer directement les entités dans les réponses REST.
- Favoriser une **approche orientée domaine** (DDD léger) : entités = modèle métier + règles de validité de base.

---

## 6) Checklist rapide

- Utiliser **Jakarta Persistence (`@Entity`)** si application synchrone classique.
- Utiliser **R2DBC (`@Table`)** si besoin d’un flux réactif non bloquant.
- Respecter les **types modernes** (`UUID`, `LocalDate`, `Instant`).
- Séparer **entités ≠ DTO ≠ services**.
- Protéger la sérialisation JSON via DTO pour éviter les cycles et fuites.
- Configurer les **migrations DB** avant d’écrire les entités.
- Ne pas mélanger JPA et R2DBC dans la même logique métier.

---


