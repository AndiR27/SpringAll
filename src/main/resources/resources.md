# ressources.md – Rôle et usage des ressources dans Spring Boot

## 1) À quoi servent les ressources ?

Dans un projet Spring Boot, le dossier `src/main/resources` contient les **fichiers non compilés** et **configurables** au runtime (fichiers de configuration, migrations, fichiers statiques, templates, fichiers de messages, schémas SQL, etc.).  
L’idée est de **séparer le code** (dans `src/main/java`) de **tout ce qui est paramétrable ou fourni au runtime**.

Les ressources permettent de :

- Externaliser la configuration (URL, identifiants, seuils, profils).
- Gérer les fichiers de migration de base de données (Flyway / Liquibase).
- Stocker les templates (Thymeleaf, FreeMarker) ou pages statiques.
- Fournir les fichiers de localisation (i18n).
- Inclure des fichiers statiques (images, CSS, JS) si tu as une partie front embarquée.

---

## 2) Le fichier de configuration `application.properties` / `application.yml`

### Usage général

Spring Boot recherche automatiquement, à l’**initialisation de l’application**, un fichier de configuration dans `classpath:/`, avec les noms suivants par ordre de priorité :

- `application.properties`
- `application.yml`
- `application-dev.yml`, `application-prod.yml`, etc. selon le **profil actif**

Ces fichiers définissent les **propriétés de configuration** (data source, ports, logs, sécurité, etc.). Spring Boot les lie automatiquement aux **classes de configuration typées** ou aux **beans** via `@ConfigurationProperties`, `@Value`, etc.

### Adapter selon le profil

Tu peux définir des fichiers spécifiques par profil, par exemple :

- `application.yml` → propriétés communes
- `application-dev.yml` → overrides pour le profil `dev`
- `application-prod.yml` → overrides pour `prod`

Tu actives un profil via :

- Variable d’environnement : `SPRING_PROFILES_ACTIVE=prod`
- Argument JVM : `--spring.profiles.active=prod`
- Dans le `application.yml` de base (déconseillé en prod)

### Format recommandé : YAML

Bien que `.properties` fonctionne toujours, **YAML** est souvent préféré car :

- Il permet des structures hiérarchiques claires.
- Il est plus lisible pour les configurations imbriquées.
- Spring Boot le supporte nativement.

---

## 3) Principales catégories de propriétés & normes modernes

Voici les **domaines de configuration** courants et les **normes à respecter** :

### 3.1. Informations d’application

- `spring.application.name` → nom logique de l’application
- `info.app.version`, `info.build.time` → exposés via Actuator (info endpoint)

### 3.2. Serveur Web

- `server.port` → port HTTP
- `server.servlet.context-path` → chemin de base de l’application
- `server.error.*` → templates d’erreur, pages d’erreur personnalisées

### 3.3. Source de données / JPA

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.datasource.driver-class-name` (souvent implicite)
- `spring.jpa.hibernate.ddl-auto` (validate / update / none / create-drop)
- `spring.jpa.show-sql` (log SQL)
- `spring.jpa.properties.*` → passer des propriétés spécifiques à Hibernate
- `spring.jpa.open-in-view=false` (recommandé pour désactiver Open Session in View)

### 3.4. Transaction & pool de connexions

- `spring.datasource.hikari.maximum-pool-size`
- `spring.datasource.hikari.minimum-idle`
- `spring.datasource.hikari.idle-timeout`, `max-lifetime`
- `spring.transaction.default-timeout`

### 3.5. Logs & niveaux

- `logging.level.<package>=DEBUG|INFO|WARN|ERROR`
- `logging.pattern.console`, `logging.pattern.file`
- `logging.file.name` ou `logging.file.path`
- Activer logs JSON en production si besoin (via `logback-spring.xml` ou configuration YAML)

### 3.6. Sécurité & accès

- `spring.security.user.name` / `.password` (pour apps simples)
- `spring.security.oauth2.*` / `spring.security.jwt.*` pour JWT / OAuth2
- `management.endpoints.web.exposure.include` / `.exclude` → quelles endpoints Actuator exposer
- `management.security.*` / `management.server.port`

### 3.7. Observabilité & Actuator / metrics

- `management.endpoints.web.exposure.include=health,info,metrics,prometheus`
- `management.endpoint.health.show-details=when_authorized`
- `management.metrics.export.prometheus.enabled=true`
- `management.tracing.sampling.probability` (avec Spring Cloud Sleuth / Micrometer)

### 3.8. Cache, async, scheduling, messaging

- `spring.cache.type`, `spring.cache.redis.*`
- `spring.task.execution.pool.core-size` / `max-size`
- `spring.task.scheduling.pool.size`
- `spring.kafka.bootstrap-servers`, `spring.rabbitmq.*`, etc.

---

## 4) Bonnes pratiques & normes

- **Ne jamais hardcoder les secrets** (mots de passe, clés API) dans `application.yml` versionné → utiliser variables d’environnement, vault, secrets manager.
- **Validation de la configuration** dès le démarrage (via `@ConfigurationProperties` avec `@Validated`).
- **Fail-fast** : lever une exception si une configuration critique est manquante ou invalide.
- **Centraliser les keys** dans des classes typées (avec classes `@ConfigurationProperties`).
- **Commentaires / documentation** dans le fichier YAML pour expliciter les valeurs non triviales.
- **Profils clairs et séparés** (éviter les conditions compliquées dans un même fichier).
- **Versionner / historiser** les fichiers de configuration critiques (par exemple via Git).
- **Fallback raisonnables** : prévoir des valeurs par défaut non critiques afin que l’application démarre même en mode “minimal config”.
- **Watcher / rechargement dynamique** (Spring Boot DevTools) uniquement en développement, jamais en production.

---

## 5) Pièges fréquents & recommandations

- Oublier d’activer le bon profil → les overrides ne sont pas appliqués.
- `ddl-auto=update` en production → peut faire des modifications non désirées du schéma.
- `open-in-view=true` → injections de problèmes N+1 via vues web.
- Exposer trop d’endpoints Actuator sans sécurité.
- Trop de configurations imbriquées sans validation → erreurs silencieuses.
- Logs trop verbeux en production → surcharge / fuite d’informations sensibles.

---

## 6) Exemple d’une configuration minimale (YAML) de base (à adapter selon projet)

```yaml
spring:
  application:
    name: my-app
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: ${DB_USER}
    password: ${DB_PASS}
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false

server:
  port: 8080

logging:
  level:
    root: INFO
    com.myapp: DEBUG

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when_authorized
