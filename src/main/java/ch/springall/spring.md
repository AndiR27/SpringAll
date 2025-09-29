# Spring / Spring-boot

## 1) Introduction
Spring est un framework java complet pour le développement d'applications web et d'API RESTful. Il fournit une infrastructure robuste pour construire des applications Java modernes, en mettant l'accent sur la simplicité, la modularité et la facilité de test. (Inversion de Contrôle, injection de dépendances, web, data, sécurité, tâches planifiées, etc.)

Spring Boot est une **extension de Spring** qui simplifie la configuration et le déploiement des applications Spring. Il permet de créer des applications autonomes, prêtes pour la production, avec une configuration minimale. Spring Boot propose des conventions par défaut et des configurations automatiques pour accélérer le développement. ((auto-configuration, starters, exécutable unique, observabilité)

- **Inversion de Contrôle (IoC)** : le conteneur Spring gère la création et l’injection des objets.
- **Injection de Dépendances (DI)** : simplifie le code et favorise le découplage.
- **Convention over configuration** : des valeurs par défaut intelligentes pour limiter la configuration manuelle.
- **Auto-configuration** : activation automatique de la configuration en fonction des dépendances présentes.
- **Starters** : ensembles de dépendances pré-configurées pour un usage courant (web, data, sécurité…).
- **Actuator** : endpoints prêts à l’emploi pour la santé, les métriques et les informations de l’application.  

⚠️ *Attention : Spring Boot n’efface pas la complexité de Spring, il l’automatise. Comprendre Spring reste essentiel pour bien l’utiliser.*  

## 2) Structure typique d’un projet
Un projet Spring Boot suit généralement une structure claire et organisée qui sépare les préoccupations.  
Cette organisation améliore la lisibilité, la maintenabilité et la testabilité.

- **config** : configuration applicative (sécurité, CORS, sérialisation…).
- **domain** : cœur métier (entités, objets de valeur).
- **repository** : interfaces d’accès aux données (CRUD, requêtes personnalisées).
- **service** : logique métier, transactions, orchestration.
- **web** : contrôleurs REST, gestion des erreurs et validation.
- **dto / mapper** : modèles d’échange et conversions (API ↔ entités).
- **resources** : configuration (`application.yml`), migrations (Flyway/Liquibase), templates.
  
### Bonnes pratiques :
- Séparer **Web ≠ Métier ≠ Persistance**.
- Ne pas exposer directement les entités en API (utiliser des DTO).
- Externaliser la configuration et gérer les secrets hors du code source.  


## 3) Configuration & Injection de dépendances

La configuration dans Spring Boot repose sur l’idée que l’application doit être **facilement adaptable selon l’environnement** et que les dépendances doivent être **clairement injectées et testables**.

- **Beans** : objets gérés par Spring (création, injection, cycle de vie).
- **Injection** : par constructeur (préférée), champ ou setter.
- **Scopes** : singleton (par défaut), prototype, request, session.
- **Profils** : `dev`, `test`, `prod` pour varier la configuration selon l’environnement.
- **Propriétés** : définies dans `application.yml` et surchargeables par variables d’environnement ou ligne de commande.
- **Propriétés typées** : regroupées dans des classes dédiées pour plus de clarté et de validation.
- **Validation** : appliquer la validation autant sur les entrées API que sur les paramètres de configuration.
- **Auto-configuration** : Spring Boot active automatiquement des comportements selon les dépendances présentes.

## 4) Constructions d’un projet (Maven, pom.xml et dépendances)

### Maven et Spring Boot
Spring Boot s’appuie sur **Maven** (ou Gradle) comme outil de build.  
Maven organise le projet, gère le cycle de vie (compile, test, package, déploiement) et centralise les dépendances dans un fichier unique : `pom.xml`.

### Le fichier pom.xml
Le `pom.xml` (Project Object Model) décrit :
- **Le projet** : nom, version, packaging (`jar` ou `war`).
- **Le parent** : Spring Boot fournit un parent `spring-boot-starter-parent` qui définit les versions par défaut des dépendances.
- **Les dépendances** : bibliothèques nécessaires (Spring Web, Data JPA, Validation, etc.).
- **Les plugins** : extensions du cycle de build (Spring Boot Maven Plugin, compiler, surefire pour les tests).
- **Le BOM (Bill of Materials)** : garantit des versions compatibles des dépendances en important un `dependencyManagement`.

### Les dépendances
Spring Boot introduit la notion de **starters** : des regroupements de dépendances préconfigurées pour couvrir un besoin donné.
- **Exemples courants** :
    - `spring-boot-starter-web` : pour exposer des APIs REST.
    - `spring-boot-starter-data-jpa` : pour interagir avec une base relationnelle via JPA/Hibernate.
    - `spring-boot-starter-validation` : pour activer la validation (Jakarta Validation).
    - `spring-boot-starter-test` : pour les tests unitaires et d’intégration.
- Cela évite d’avoir à gérer les versions une par une : tout est testé et validé par l’équipe Spring.

### Cycle de vie Maven
Maven définit des **phases standards** qui sont exécutées en séquence :
- **compile** : compilation des sources.
- **test** : exécution des tests unitaires.
- **package** : génération du JAR ou WAR.
- **verify** : exécution des tests d’intégration.
- **install** : installation du package dans le dépôt local (`~/.m2/repository`).
- **deploy** : publication du package dans un dépôt distant (Nexus, Artifactory, Maven Central).

### Plugins essentiels
- **spring-boot-maven-plugin** : permet de lancer l’appli avec `mvn spring-boot:run` et de générer un JAR exécutable.
- **maven-compiler-plugin** : définit la version de Java utilisée (ex. Java 21).
- **maven-surefire-plugin** : exécution des tests unitaires.
- **maven-failsafe-plugin** : exécution des tests d’intégration.

### Bonnes pratiques
- Toujours définir la **version de Java** dans le `pom.xml` (Java 21+ recommandé).
- Utiliser les **starters Spring Boot** au lieu de gérer les dépendances individuellement.
- S’appuyer sur le **parent Spring Boot** ou le **BOM officiel** pour éviter les conflits de version.
- Vérifier régulièrement les mises à jour avec `mvn versions:display-dependency-updates`.
- Garder le `pom.xml` lisible en regroupant les dépendances par type (web, data, tests).

---
---

## 📚 Concepts fondamentaux à intégrer

Au-delà de l’introduction, la structure et la configuration, il est important de comprendre ces **piliers** :

### Cycle de vie d’une application Spring Boot
- Démarrage via `SpringApplication.run` → scanning des classes → instanciation des beans.
- ApplicationContext : conteneur central qui orchestre les beans et la configuration.
- Hooks possibles : `ApplicationRunner`, `CommandLineRunner` (exécuter du code au démarrage).

### Gestion des dépendances
- **Spring Boot Starters** → simplifient la gestion des bibliothèques.
- **Bill of Materials (BOM)** → garantit la compatibilité des versions.
- Toujours vérifier la **compatibilité Spring Boot ↔ Spring Framework ↔ Java**.

### Logging moderne
- **SLF4J + Logback** par défaut.
- Config via `application.yml` ou fichiers dédiés.
- Utiliser un **MDC (Mapped Diagnostic Context)** pour corrélation de logs (traçabilité microservices).

---

## Bonnes pratiques dès le départ

### Architecture claire
- Respecter la **séparation des couches** : Web ↔ Service ↔ Repository.
- Utiliser des **DTO pour les échanges API** et ne pas exposer directement les entités.
- Organiser par **feature/domain** (plutôt que technique) si le projet devient grand.

### Gestion de la configuration
- Externaliser toute configuration sensible (secrets, mots de passe DB, clés API).
- Utiliser les **profils Spring** (`dev`, `test`, `prod`) pour isoler les comportements.
- Prévoir dès le début un outil de **migrations de schéma** (Flyway ou Liquibase).

### Qualité & tests
- Intégrer les tests unitaires et d’intégration dès les premières étapes.
- Utiliser **JUnit 5** et **Spring Boot Test**.
- Envisager **Testcontainers** pour tester avec une vraie base de données.

### Documentation
- Intégrer **springdoc-openapi** pour exposer une documentation interactive (Swagger UI).
- Mettre à jour le README régulièrement avec les choix d’architecture et les dépendances clés.

---
