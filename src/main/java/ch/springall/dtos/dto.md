# Data Transfer Objects (DTOs) dans Spring

## 1) Définition et rôle
Un **DTO (Data Transfer Object)** est un objet conçu pour **transporter des données** entre couches de l’application (API ↔ Service ↔ Client), sans exposer directement les entités JPA ou le domaine.

- Sépare le **contrat externe** (ce que le client voit) du **modèle interne** (entités, agrégats, policies).
- Sert à protéger le domaine contre les fuites (sécurité, stabilité).
- Permet de **contrôler le format des données** exposées et reçues.
- Peut contenir des informations dérivées, agrégées ou calculées (ex. `fullName`, `statusLabel`).

---

## 2) Types de DTO
- **Request DTO** : représente les données entrantes (ex. payload JSON d’un POST/PUT).
- **Response DTO** : représente les données sortantes (ex. retour JSON d’une API).
- **Projection DTO** : récupère seulement certains champs d’une entité (optimisation).
- **Composite DTO** : assemble plusieurs entités/valeurs pour un cas d’usage précis.

---

## 3) Différences avec les entités
- **Entité JPA** = modèle persistant (liée à une table SQL, avec annotations Jakarta).
- **DTO** = modèle d’échange (sans logique de persistance, sans dépendance à JPA).
- **Pourquoi séparer ?**
    - Ne pas exposer des champs sensibles (`passwordHash`, `internalNotes`).
    - Éviter les problèmes de sérialisation JSON avec les relations (lazy loading, boucles infinies).
    - Permettre l’évolution de l’API sans casser le domaine.

---

## 4) Conception moderne (Java 21+, Spring Boot 3.x)
- 
- 
- **Records Java** (immutables, concis) : idéaux pour les DTO.
- **Validation Jakarta** (`jakarta.validation.*`) directement sur les champs DTO d’entrée (ex. `@NotBlank`, `@Email`).
- **Mapping** : utiliser un mapper (MapStruct, conversion manuelle, ou méthodes dédiées).
- **Versionnement** : si l’API évolue, versionner les DTO (ex. `UserDTOv2`).
- **Noms clairs** : `UserCreateRequest`, `UserResponse`, `OrderSummaryDTO`.

---

### Les Records en Java

Un Record est une classe spéciale en Java qui est conçue pour être un simple conteneur de données. Ils sont immuables et fournissent automatiquement des méthodes comme `equals()`, `hashCode()`, et `toString()`.
Il s'agit en fait d'une forme particulière d'un DTO : c'est un moyen concis de définir des objets qui transportent des données sans avoir à écrire beaucoup de code boilerplate.

Le record se déclare avec le mot-clé `record` suivi du nom et des composants (champs).
-> Le constructeur et les accesseurs sont générés automatiquement.
- Si le DTO est juste un sac de données immuable → record.
- Si le DTO doit avoir des comportements avancés ou être mutable → dto classique.



## 5) Validation et sécurité
- **Validation API** : s’effectue sur le DTO d’entrée, pas sur l’entité.
- **Annotations fréquentes** :
    - `@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@Positive`.
- **Sécurité** : ne jamais exposer d’identifiants internes (séquences DB), d’informations sensibles (hashes, secrets, tokens).
- **Normalisation** : nettoyer/valider les formats (ex. `email.toLowerCase()`) dans la couche service.

---

## 6) Mapping (Entity ↔ DTO)

En java, il est possible de faire le mapping manuellement au niveau du service, ou d'utiliser des bibliothèques comme MapStruct pour automatiser ce processus.

- **Ne jamais exposer directement une entité en JSON** → passer par un DTO.
- **Mapper dédié** (classe utilitaire ou framework comme MapStruct) pour isoler la logique de conversion.
- **Séparer les responsabilités** :
    - Le contrôleur reçoit/renvoie des DTO.
    - Le service manipule des entités + DTO en entrée/sortie.
    - Le repository ne manipule que des entités.

**Approches courantes** :
- MapStruct : générateur de mappers à la compilation.
- Conversion manuelle : `UserDTO.fromEntity(user)` et `user.toEntity()`.

@Mapper(componentModel = "spring") → MapStruct génère un bean Spring que tu peux injecter.

@Mapping → permet d’ignorer ou transformer des champs.

expression = "java(...)" → permet de construire un champ à partir de plusieurs.

MapStruct génère automatiquement le code de conversion à la compilation (rapide et type-safe).
---

## 7) Bonnes pratiques
- Utiliser **records immuables** pour clarté et sécurité.
- Séparer les DTO **par usage** (Request vs Response).
- Ajouter uniquement les champs pertinents au contrat API.
- Grouper les DTO dans un package dédié (`dto/`).
- Documenter les DTO exposés dans l’API (via OpenAPI/Swagger).
- Valider systématiquement les DTO entrants.
- Éviter les dépendances au domaine (pas d’annotations JPA dans les DTO).

---

## 8) Anti-patterns à éviter
- **Exposer les entités directement** → fuite de données, instabilité de l’API.
- **Réutiliser le même DTO partout** → manque de clarté, risque de champs inutiles ou sensibles.
- **Mettre de la logique métier dans les DTO** → ils doivent rester de simples conteneurs de données.
- **Coupler DTO et entités** → les DTO doivent évoluer indépendamment du modèle persistant.

---

## 9) Checklist rapide
- [ ] DTO = record immuable.
- [ ] DTO d’entrée validé avec `jakarta.validation`.
- [ ] DTO ≠ entité (jamais d’annotation JPA).
- [ ] Mapping centralisé (MapStruct ou classe dédiée).
- [ ] Pas de données sensibles exposées.
- [ ] DTO versionnés si contrat API évolue.
- [ ] Documentés via OpenAPI (Swagger).

---
