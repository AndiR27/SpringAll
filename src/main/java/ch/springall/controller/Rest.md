# Controllers & REST API (Spring Boot)

Dans une application Spring Boot, la couche **Controller** est l’entrée principale de l’application côté serveur.  
Elle définit comment les services sont exposés au monde extérieur via des **API REST**.

Le rôle du controller est clair :
- recevoir une **requête HTTP** (depuis un client, un navigateur, un front Angular/React, un test Postman, etc.),
- extraire les paramètres nécessaires,
- déléguer le travail au **service** (qui gère la logique métier),
- et renvoyer une **réponse HTTP** claire et cohérente.

L’objectif est de construire une API lisible, maintenable et robuste.

## Introduction : Spring Web et la couche Controller

La création de controllers REST dans Spring Boot est rendue possible grâce au module **Spring Web** (souvent intégré via `spring-boot-starter-web`).  
Ce module fournit toute l’infrastructure nécessaire pour transformer une application Java en une application web exposant des API :

- Il s’appuie sur le **pattern MVC (Model-View-Controller)**, mais dans le cas des API REST, la partie *View* est remplacée par des réponses JSON ou XML.
- Il intègre un **moteur de gestion des requêtes HTTP** basé sur **Servlet** (Tomcat par défaut, mais Jetty ou Undertow peuvent aussi être utilisés).
- Chaque requête HTTP entrante est **capturée par le DispatcherServlet**, qui joue le rôle de point central :
    - il analyse l’URL et la méthode HTTP,
    - il recherche le bon controller/mapping,
    - il appelle la méthode correspondante,
    - et il convertit automatiquement les objets Java en réponses HTTP (JSON, XML, texte, etc.) grâce à **Jackson** ou à d’autres convertisseurs (HttpMessageConverters).
- L’intégration est transparente : une simple annotation `@RestController` suffit pour qu’une classe devienne un point d’entrée web.

En résumé, **Spring Web est la brique qui fait le lien entre HTTP et le code métier**.  
Sans écrire de code bas niveau lié aux sockets ou aux entêtes HTTP, il est possible de se concentrer uniquement sur la définition des routes et sur la logique métier.

---

## 1) Conception REST et bonnes pratiques

Avant d’écrire une ligne de code, il est nécessaire de réfléchir à **comment représenter les ressources** et à **quelle sémantique donner aux routes**.

- **Ressources** : une ressource doit correspondre à une entité ou un concept clair du domaine (`/directors`, `/movies`, `/courses`).
- **Méthodes HTTP** : chaque action doit utiliser la méthode appropriée.
    - `GET` pour récupérer,
    - `POST` pour créer,
    - `PUT` pour remplacer complètement,
    - `PATCH` pour modifier partiellement,
    - `DELETE` pour supprimer.
- **Codes HTTP** : il faut renvoyer le bon code, pas uniquement `200 OK`.
    - `201 Created` après une création (avec header `Location`),
    - `204 No Content` après une suppression réussie,
    - `404 Not Found` lorsqu’une ressource n’existe pas,
    - `400 Bad Request` si les données reçues sont invalides.
- **URLs claires et stables** : les chemins doivent représenter des ressources (`/directors/{id}`) et non des verbes (`/getDirector`).
- **Pagination et tri** : pour les listes, il faut prévoir `?page=0&size=20&sort=name,asc`.
- **Filtrage** : les query params permettent des recherches efficaces (`/movies?genre=action&year=2024`).

Une API REST doit être **prévisible** et compréhensible par un développeur externe sans documentation exhaustive.

---

## 2) Organisation d’un Controller

En Spring Boot, un controller est une classe annotée :

- `@RestController` → indique que les méthodes exposent directement des réponses JSON.
- `@RequestMapping("/api/v1/directors")` → définit le chemin de base.
- Les méthodes exposées utilisent :
    - `@GetMapping` pour la lecture,
    - `@PostMapping` pour la création,
    - `@PutMapping` / `@PatchMapping` pour les mises à jour,
    - `@DeleteMapping` pour la suppression.

Chaque paramètre de requête doit être extrait clairement :
- `@PathVariable("id") Long id` pour récupérer un identifiant dans l’URL (`/directors/{id}`),
- `@RequestParam String name` pour un paramètre simple (`?name=...`),
- `@RequestBody` pour recevoir un objet JSON complet (souvent un DTO),
- `@RequestHeader` pour lire un header HTTP (par ex. un token d’authentification).

Un controller doit rester le plus simple possible : **il ne doit pas contenir de logique métier** mais uniquement orchestrer les appels aux services et structurer les réponses HTTP.

---

## 3) Réponses et gestion avec ResponseEntity

Dans un controller Spring Boot, l’envoi de réponses HTTP ne doit pas se limiter au simple retour d’un objet ou d’une chaîne.  
Il est essentiel d’utiliser **ResponseEntity<T>** afin de maîtriser à la fois :

- le **contenu** renvoyé (un DTO, un record, un message),
- le **statut HTTP** (`200`, `201`, `204`, etc.),
- et les **headers** (comme `Location`, `Cache-Control`, etc.).

Cela permet d’exprimer clairement au client ce qui s’est passé, et d’assurer une meilleure compatibilité avec les pratiques REST.

---

### Pourquoi ResponseEntity est important
- Si une méthode retourne uniquement un objet (`DirectorRecord` par exemple), Spring renverra automatiquement `200 OK`.  
  Mais dans le cas d’une création, il est important de renvoyer `201 Created` avec un header `Location`.
- Avec ResponseEntity, il est possible d’être **explicite** sur le statut exact, ce qui permet aux clients de mieux interpréter la réponse.
- Il est aussi possible de contrôler les **headers HTTP**, comme l’ajout d’un ETag pour la mise en cache.

---

### Méthodes principales de ResponseEntity

- **`ok(body)`** → `ResponseEntity.ok(dto)` → renvoie `200 OK` avec un corps JSON.
- **`ok().build()`** → `ResponseEntity.ok().build()` → renvoie `200 OK` sans corps.
- **`created(location).body(body)`** → `ResponseEntity.created(uri).body(dto)` → renvoie `201 Created` avec un header `Location` et un corps.
- **`noContent().build()`** → `ResponseEntity.noContent().build()` → renvoie `204 No Content` sans corps.
- **`notFound().build()`** → `ResponseEntity.notFound().build()` → renvoie `404 Not Found`.
- **`badRequest().body(error)`** → `ResponseEntity.badRequest().body(error)` → renvoie `400 Bad Request` avec un message ou un objet d’erreur.
- **`status(HttpStatus).body(body)`** → `ResponseEntity.status(HttpStatus.CONFLICT).body(error)` → renvoie une réponse avec un statut personnalisé et un corps.
- **`status(HttpStatus).build()`** → `ResponseEntity.status(HttpStatus.FORBIDDEN).build()` → renvoie un statut personnalisé sans corps.


### Cas d’utilisation fréquents

- **Création réussie**  
  Lorsqu’une ressource est créée (ex. un `Director`), il faut renvoyer :
  - `201 Created` comme statut,
  - un header `Location` pointant vers la nouvelle ressource,
  - et éventuellement le corps avec l’objet créé.  
    Exemple logique : `ResponseEntity.created(location).body(dto)`

- **Suppression réussie**  
  Lorsqu’une ressource est supprimée, il n’y a pas de contenu à renvoyer.  
  La bonne pratique est d’envoyer `204 No Content` → `ResponseEntity.noContent().build()`

- **Lecture standard**  
  Pour retourner une ressource, il faut utiliser :
  - `200 OK` avec le body JSON contenant la donnée.  
    Exemple : `ResponseEntity.ok(dto)`

- **Ressource absente**  
  Si la ressource n’existe pas, il faut retourner `404 Not Found`.  
  Exemple logique : `ResponseEntity.notFound().build()`

- **Erreur ou validation invalide**  
  Si la requête est incorrecte, il faut utiliser `400 Bad Request` avec un message explicatif dans le body.  
  Exemple logique : `ResponseEntity.badRequest().body(errorDto)`

---

### Gestion cohérente des erreurs avec ResponseEntity

L’avantage de ResponseEntity est qu’il peut aussi transporter des objets d’erreurs normalisés.  
Avec Spring Boot 3+, l’objet **ProblemDetail** peut être utilisé directement dans le body de `ResponseEntity` pour donner au client une erreur claire et structurée.

Exemple logique attendu :
- `ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail)`
- `ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail)`

Ainsi, le code et le message d’erreur sont envoyés dans un format lisible et exploitable.

---

### Bonnes pratiques avec ResponseEntity
- Être toujours explicite sur le **code HTTP**, même si `200` est implicite.
- Utiliser `ResponseEntity` pour **tous les endpoints** : cela rend les controllers cohérents.
- Renvoyer les **headers utiles** (`Location`, `Cache-Control`, `ETag`) pour améliorer l’expérience client.
- Ne jamais renvoyer d’objet brut en cas d’erreur → toujours envelopper avec un statut adapté et un body explicatif.
- Utiliser `ResponseEntity` comme point central de communication entre la couche REST et les clients (front, API externe, tests).

---

En résumé, **ResponseEntity est l’outil principal** pour rendre une API Spring Boot claire et conforme aux bonnes pratiques REST.  
Il assure un contrôle total sur les réponses et facilite la communication précise entre le serveur et le client.


## 4) Gestion de la sécurité (vue d’ensemble)

Les controllers REST étant exposés, la sécurité doit être prise en compte.  
Sans entrer dans les détails de Spring Security, les points essentiels sont :

- Il ne faut jamais exposer une API sans contrôle d’accès.
- En développement, `@CrossOrigin` peut être utilisé pour permettre l’accès depuis un front externe.
- En production, la configuration CORS doit être centralisée dans la configuration de sécurité.
- L’authentification moderne repose souvent sur des **tokens JWT** ou OAuth2.
- Les restrictions d’accès aux endpoints doivent être gérées au niveau Spring Security, pas directement dans les controllers.

---

## 5) Optimisation et performance

Une API REST doit rester performante et efficace :

- **Éviter le N+1** : il faut utiliser des DTO optimisés, `@EntityGraph` ou des `fetch join` pour limiter les requêtes inutiles.
- **Pagination obligatoire** : un `findAll()` brut n’est jamais acceptable pour une liste.
- **Cache et ETags** : mettre en place un `ETag` permet de renvoyer `304 Not Modified` lorsque la ressource n’a pas changé.
- **Compression HTTP** : GZIP ou Brotli doivent être activés (via proxy ou Spring Boot).
- **Streaming** : pour de gros volumes, il faut envisager les flux (SSE, ND-JSON).

---

## 6) Tests des Controllers

Les controllers doivent être testés pour garantir la stabilité de l’API.

- **Tests unitaires slice web** :
    - Il faut utiliser `@WebMvcTest` pour charger uniquement la couche web.
    - Les services doivent être mockés avec `@MockBean`.
    - Cela permet de tester les statuts, les headers et le JSON renvoyé sans démarrer la base de données.

- **Tests d’intégration** :
    - Il faut utiliser `@SpringBootTest(webEnvironment = RANDOM_PORT)` avec `TestRestTemplate` ou `RestAssured`.
    - Cela permet de vérifier la chaîne complète (controller + service + repository + DB).

- **Contrat** :
    - Les schémas doivent être validés contre la documentation OpenAPI.
    - Les tests de contrat assurent la compatibilité entre versions.

---

## 7) Bonnes pratiques générales

- Les controllers doivent être **fins et simples**. Toute logique métier doit être confiée aux services.
- Il faut toujours utiliser des **DTO/records** et ne jamais exposer directement les entités JPA.
- La sémantique HTTP doit être respectée : `GET` ne modifie rien, `PUT` doit être idempotent.
- Les erreurs doivent être cohérentes et centralisées avec `ProblemDetail`.
- Les endpoints doivent être stables, documentés et cohérents (`/directors/{id}/movies` plutôt que `/addMovieToDirector`).
- La documentation OpenAPI/Swagger doit être activée pour faciliter l’utilisation de l’API.

---

## Exemple d’URLs bien conçues

- `GET /api/v1/directors` → liste des réalisateurs (paginée).
- `POST /api/v1/directors` → création d’un réalisateur (201 + Location).
- `GET /api/v1/directors/{id}` → détail d’un réalisateur.
- `PUT /api/v1/directors/{id}` → mise à jour complète.
- `PATCH /api/v1/directors/{id}` → mise à jour partielle.
- `DELETE /api/v1/directors/{id}` → suppression.
- `POST /api/v1/directors/{id}/movies` → ajout d’un film au réalisateur.
- `GET /api/v1/directors/{id}/movies` → liste des films associés.

---

## À retenir

Un controller REST en Spring Boot doit :
1. Exposer clairement les ressources via des routes stables.
2. Utiliser la bonne sémantique HTTP (méthodes + codes).
3. Déléguer la logique métier aux services.
4. Gérer les erreurs de manière cohérente et centralisée.
5. Être sécurisé même dans un environnement de test.
6. Optimiser les réponses (pagination, cache, DTO).
7. Être validé par des tests unitaires slice et des tests d’intégration.
