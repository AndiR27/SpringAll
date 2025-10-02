# Logging dans Spring Boot

Le **logging** est un mécanisme essentiel pour observer, diagnostiquer et superviser une application Spring Boot.  
Il permet d’analyser le comportement de l’application, de tracer les événements importants et de faciliter la maintenance en production.

Spring Boot utilise **SLF4J** comme façade de logging et **Logback** comme implémentation par défaut.  
Cette combinaison garantit des performances élevées, une flexibilité de configuration et une compatibilité avec la plupart des bibliothèques Java modernes.

---

## 1. Principe de fonctionnement

### Abstraction et implémentation
- **SLF4J** : façade de logging, utilisée comme API standard par Spring et d’autres frameworks.
- **Logback** : implémentation par défaut, performante et flexible.
- Autres implémentations possibles : Log4j2, Java Util Logging, mais Logback reste le standard recommandé.

### Hiérarchie des niveaux de logs
Chaque log est associé à un **logger** identifié par son nom (souvent le nom de classe).  
Les niveaux de sévérité sont hiérarchisés ainsi :
- TRACE : détails extrêmement fins, rarement utilisés en production.
- DEBUG : informations techniques pour le développement.
- INFO : messages standards sur le fonctionnement normal de l’application.
- WARN : avertissements concernant des comportements inattendus mais non critiques.
- ERROR : erreurs bloquantes ou exceptions à analyser en priorité.

---

## 2. Configuration dans Spring Boot

Spring Boot permet de configurer facilement le logging via les fichiers de configuration (`application.properties` ou `application.yml`).  
On peut définir :
- le **niveau global** de logs,
- des niveaux spécifiques pour certains packages,
- la **destination** des logs (console, fichiers, systèmes externes).

La configuration avancée se fait via un fichier dédié (`logback-spring.xml`), permettant de personnaliser les sorties, les patterns et la rotation des fichiers.

---

## 3. Logging dans le code

### Principes essentiels
- Utiliser **SLF4J** via Lombok (`@Slf4j`) ou `LoggerFactory`.
- Ne jamais utiliser `System.out.println` pour tracer des événements.
- Toujours utiliser les placeholders `{}` pour les variables afin d’éviter la concaténation inutile.

### Où logguer et à quel niveau
- **Au démarrage de l’application** : logs `INFO` indiquant la configuration principale (ports, profil actif, BDD utilisée).
- **Dans les contrôleurs REST** : log `INFO` pour chaque requête entrante (méthode, endpoint, identifiant de la requête si possible).
- **Dans les services** : log `DEBUG` pour les étapes techniques, log `INFO` pour les événements métier importants (exemple : "Nouvel utilisateur créé : ID=123").
- **Dans les repositories** : pas besoin de logs manuels, Hibernate/JPA peut être configuré pour tracer les requêtes SQL.
- **En cas d’erreur** : log `ERROR` avec le message d’exception et les éléments de contexte utiles.
- **Pour les warnings fonctionnels** : log `WARN` (exemple : "Quota presque atteint", "Connexion lente au service externe").

### Bonnes pratiques
- Loguer uniquement ce qui aide à comprendre ou diagnostiquer.
- Éviter les logs trop verbeux qui polluent la lecture (ne pas transformer le log en “console de debug permanente”).
- S’assurer que chaque log critique contient suffisamment de **contexte** (identifiant utilisateur, requête, opération métier).
- Utiliser un **correlationId / traceId** pour suivre un flux complet dans une application distribuée.

---

## 4. Bonnes pratiques modernes

### Logging structuré
Il est recommandé d’adopter un logging structuré (clé-valeur, format JSON) afin de faciliter l’analyse par des systèmes externes tels que ELK, Grafana Loki ou d’autres plateformes de monitoring.  
Cela permet d’enrichir les logs avec des métadonnées (id de requête, utilisateur, etc.).

### Sécurité et confidentialité
Les logs ne doivent jamais contenir de données sensibles telles que des mots de passe, tokens ou informations personnelles.  
Une attention particulière doit être portée à la conformité (par ex. RGPD).

### Performance
- Utiliser les placeholders `{}` pour éviter la concaténation inutile de chaînes.
- N’activer TRACE ou DEBUG que localement ou temporairement.
- Garder un niveau global INFO en environnement de production.

### Observabilité et centralisation
En production, les logs doivent être collectés et centralisés pour être exploitables.  
Cela se fait généralement via des systèmes de type ELK (Elasticsearch, Logstash, Kibana), Grafana Loki ou des solutions Cloud.  
Il est également recommandé d’ajouter des identifiants de corrélation (traceId, correlationId) pour suivre une requête à travers plusieurs microservices.

---

## 5. Tests et logging

Pendant les tests, Spring Boot utilise la même configuration de logging.  
Il est possible d’ajuster les niveaux pour réduire le bruit ou de capturer les sorties de log afin de vérifier que certains messages sont bien générés.  
Cela permet de tester non seulement la logique métier mais aussi la traçabilité de l’application.

---

## 6. Résumé des bonnes pratiques

1. Utiliser SLF4J comme API standard, Logback comme implémentation par défaut.
2. Configurer les niveaux de logs via les fichiers de configuration.
3. Logguer dans le code **aux bons endroits** (démarrage, contrôleurs, services, erreurs).
4. En production : garder INFO comme niveau global, DEBUG uniquement en local.
5. Ne jamais inclure d’informations sensibles dans les logs.
6. Favoriser le logging structuré pour l’analyse et la supervision.
7. Centraliser les logs et ajouter des identifiants de corrélation pour améliorer l’observabilité.
8. Utiliser les tests pour vérifier la présence de logs critiques.

---
