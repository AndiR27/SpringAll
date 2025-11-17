## Mappers (Entity ↔ DTO)

Un **Mapper** est un composant qui sert à convertir les objets du **domaine persistant** (les entités JPA ou R2DBC) vers
les **DTOs** utilisés par la couche service et exposés à l’extérieur, et inversement.  
Son rôle est d’assurer une **séparation claire** entre la représentation interne (entités) et la représentation
externe (DTO), tout en centralisant la logique de transformation pour éviter la duplication et les incohérences.

---

### Rôle et objectifs

Les mappers existent pour :

- Protéger le domaine en évitant que les entités soient exposées directement dans les API.
- Maintenir une couche de conversion centralisée et testable.
- Garantir que les données sensibles, techniques ou inutiles ne sortent pas du domaine.
- Uniformiser les transformations (ex. formatage, concaténation de champs, normalisation).

Le mapper doit être un **outil de conversion pur**, sans contenir de logique métier. Son unique responsabilité est le
passage d’un modèle à un autre.

---

### Méthodes de mapping

Il existe deux manières principales de créer un mapper :

1. **Mapping manuel** : on écrit soi-même la conversion champ par champ. C’est simple mais répétitif et source d’erreurs
   à grande échelle.

```java    
    DirectorDTO dto = new DirectorDTO();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
```


2. **Mapping automatisé par framework** : des bibliothèques comme **MapStruct** génèrent du code de conversion à la
   compilation, garantissant performance et sécurité de type. D’autres solutions comme **ModelMapper** existent mais
   elles reposent sur de la réflexion et sont moins performantes.

Dans les projets modernes (Spring Boot 3.x, Java 21), **MapStruct est la norme recommandée**.

---

### Normes modernes avec MapStruct

Avec MapStruct, les mappers :

- Sont déclarés comme composants Spring pour bénéficier de l’injection de dépendance.
- Supportent nativement les **records** en Java 21, ce qui permet d’écrire des DTOs immuables et concis.
- Offrent la possibilité de définir des **mappings explicites** (par ex. champ source différent du champ cible).
- Permettent de **mapper automatiquement des collections** (listes, sets, maps).
- Peuvent ignorer certains champs sensibles ou techniques à l’aide d’annotations.
- Acceptent l’utilisation d’expressions Java dans les cas complexes (par exemple pour transformer plusieurs champs en un
  seul).

---

### Implémentation moderne avec MapStruct
### ➤ 1. Définition d’un mapper

    @Mapper(componentModel = "spring")
    public interface MapperDirector {
        DirectorDTO toDto(Director entity);
        Director fromDto(DirectorDTO dto);
    }

Points importants :

- `componentModel = "spring"` transforme le mapper en bean Spring injectable.
- MapStruct génère le code directement dans le dossier `/target/generated-sources`.
- Il gère records, listes, sets, maps et objets imbriqués automatiquement.


### ➤ 2. Mapping explicite

Si les champs n’ont pas le même nom :

    @Mapper(componentModel = "spring")
    public interface MapperMovie {
        @Mapping(source = "releaseDate", target = "published")
        MovieDTO toDto(Movie movie);
    }


### ➤ 3. Mapping des collections

MapStruct génère automatiquement la boucle :

    List<DirectorDTO> toDtoList(List<Director> list);

Si un DTO contient lui-même une liste, MapStruct gère tout sans configuration supplémentaire.


### ➤ 4. Mapping inverse

    @InheritInverseConfiguration
    Director fromDto(DirectorDTO dto);

MapStruct réutilise les mêmes règles que le mapping aller.

### ➤ 5. Mapping avancé

Pour combiner plusieurs champs :

    @Mapping(target = "fullName", expression = "java(d.getFirstName() + \" \" + d.getLastName())")
    DirectorDTO toDto(Director d);

Pour post-traiter un mapping (exemple : rattacher un parent) :

    @AfterMapping
    default void linkMovies(@MappingTarget Director entity) {
        if (entity.getMoviesDirected() != null) {
            entity.getMoviesDirected().forEach(m -> m.setDirector(entity));
        }
    }

---

### Gestion des relations

Pour les relations entre entités (OneToMany, ManyToOne, etc.), les mappers doivent appliquer les règles suivantes :

- Ne jamais exposer de relations **bidirectionnelles** pour éviter les boucles de sérialisation.
- Dans le sens parent → enfants, exposer les enfants sous forme de liste de DTOs.
- Dans le sens enfant → parent, exposer uniquement l’**identifiant du parent**, plutôt que le parent complet, afin de
  garder un modèle simple et éviter les cycles.
- La logique de rattachement (ex. transformer un `parentId` en une vraie entité Parent) doit être effectuée dans la
  couche service, et non dans le mapper.

### ✔ Parent → enfants
On expose une liste de DTOs :

    record StudioRecord(Long id, String name, List<DirectorRecord> directors) {}

### ✔ Enfant → parent
On expose UNIQUEMENT l’identifiant du parent :

    record DirectorRecord(Long id, String name, Long studioId) {}

Cela évite les cycles parent → enfant → parent → enfant…

---

### Bonnes pratiques

Un mapper doit :

- Être placé dans un package dédié (`mapper`) pour être facilement identifiable.
- Mapper uniquement entre entités et DTOs, jamais directement vers le format JSON.
- Rester un **composant stateless** et sans logique métier.
- Être testé unitairement pour garantir que les conversions respectent les contrats attendus.
- Être cohérent : un même champ doit toujours être mappé de la même manière à travers l’application.

---

### Anti-patterns à éviter

- Ne pas mettre de règles métier dans un mapper, celles-ci doivent rester dans le service.
- Ne pas mélanger validation et mapping : la validation appartient aux DTOs d’entrée, pas au mapper.
- Ne pas exposer directement des entités en JSON sans passer par un mapper.
- Ne pas mapper une relation circulaire complète (parent ↔ enfant ↔ parent).

---

### Checklist rapide

- Les DTOs sont définis comme **records immuables**.
- Les mappers utilisent **MapStruct** avec le modèle `spring` pour l’intégration DI.
- Les champs sensibles ou internes sont **ignorés explicitement**.
- Les relations sont **simplifiées** : liste côté parent, identifiant côté enfant.
- Les conversions complexes sont documentées et testées.
- Les mappers sont **centralisés** dans un package unique et réutilisés dans toute l’application.  
