# 🛡️ Sécurité dans Spring Boot

## Introduction

Spring Boot intègre nativement un puissant module de sécurité : **Spring Security**.  
Ce framework fournit tous les mécanismes nécessaires pour **authentifier les utilisateurs**, **protéger les endpoints** et **contrôler les autorisations d’accès**.  
Dans les versions récentes (Spring Boot 3.x / Spring Security 6+), l’approche est **fonctionnelle, explicite et modulaire**, s’appuyant sur la **programmation déclarative** et la compatibilité **Jakarta EE 10**.

Spring Security agit comme une **chaîne de filtres (Filter Chain)** placée entre les requêtes entrantes et la logique métier, interceptant chaque appel pour vérifier les identités, les permissions et les règles de sécurité définies.

---

## 1. Architecture générale

Spring Security repose sur trois piliers :
1. **Authentification** : validation de l’identité (login/password, token JWT, OAuth2, etc.).
2. **Autorisation** : vérification des droits d’accès selon les rôles et permissions.
3. **Protection HTTP** : défense contre les attaques courantes (CSRF, XSS, CORS, etc.).

Lorsqu’une requête HTTP atteint l’application :
- Elle passe d’abord par les **filtres de sécurité** (Spring Security Filters).
- Le système vérifie la présence d’un **jeton d’accès valide** ou d’une **session authentifiée**.
- Si l’accès est autorisé, la requête continue jusqu’au contrôleur REST.

L’ensemble des informations d’un utilisateur authentifié est stocké dans le **SecurityContext**, rendu accessible à tout moment au sein de l’application.

---

## 2. Authentification moderne

Spring Security offre plusieurs modes d’authentification selon le contexte d’application :

- **Form Login** : authentification classique via formulaire web.
- **HTTP Basic** : pour des tests rapides ou des appels internes d’API.
- **JWT (JSON Web Token)** : pour les API REST stateless, via un token signé.
- **OAuth2 / OpenID Connect** : pour l’authentification déléguée (Google, GitHub, Azure, Keycloak...).
- **LDAP ou JDBC** : pour les environnements d’entreprise ou les bases utilisateurs internes.

### Le modèle stateless
Dans les applications REST modernes, chaque requête est **indépendante**.  
L’état n’est pas conservé côté serveur. L’utilisateur s’authentifie via un **JWT** envoyé dans l’en-tête HTTP `Authorization: Bearer <token>`.  
Le serveur décode ce token à chaque requête pour identifier l’utilisateur et vérifier ses permissions.

---

## 3. OAuth2 et OpenID Connect

### 3.1 OAuth2 : principe d’autorisation déléguée
OAuth2 permet à une application cliente d’accéder à des ressources au nom d’un utilisateur sans manipuler son mot de passe.  
Il repose sur trois rôles :
- **Authorization Server** : émet les tokens d’accès (Keycloak, Auth0, Okta, etc.).
- **Resource Server** : héberge les APIs protégées (ex : ton application Spring Boot).
- **Client** : application ou front-end qui demande un accès (Angular, React, etc.).

Le flux typique :
1. L’utilisateur s’authentifie sur le serveur OAuth2.
2. Le serveur renvoie un **access token**.
3. Le client inclut ce token dans ses appels API.
4. Le Resource Server valide le token avant d’autoriser l’accès.

### 3.2 OpenID Connect : extension d’OAuth2
OpenID Connect (OIDC) étend OAuth2 pour gérer **l’authentification complète**.  
Il ajoute une identité vérifiée (`ID Token`) contenant des **claims** (nom, email, rôles, etc.).  
Spring Boot 3+ intègre nativement OIDC et gère automatiquement :
- la validation des tokens JWT,
- la récupération du profil utilisateur (`OAuth2User`),
- et la création du `SecurityContext` pour les requêtes authentifiées.

---

## 4. Autorisation et gestion des accès

### 4.1 Concepts fondamentaux
Spring Security distingue deux types de permissions :
- **Rôles (Roles)** : permissions globales comme `ROLE_ADMIN` ou `ROLE_USER`.
- **Autorités (Authorities)** : permissions fines sur les ressources (`movie:read`, `director:write`, etc.).

Les contrôles d’accès peuvent être :
- **déclaratifs** : via des annotations comme `@PreAuthorize("hasRole('ADMIN')")`.
- **programmatifs** : via un contrôle explicite dans le code de service.

### 4.2 Modèle de hiérarchie
Les rôles peuvent être hiérarchiques (ex. `ADMIN` inclut `USER`).  
Spring Security peut être configuré pour reconnaître ces relations, simplifiant la gestion des autorisations complexes.

### 4.3 SecurityContext et UserDetails
Lorsqu’un utilisateur s’authentifie :
- Spring Security crée un **UserDetails** représentant son identité.
- Ce UserDetails est stocké dans le **SecurityContext**, accessible à tout moment.
- Le contexte est propagé dans le thread courant et nettoyé à la fin de la requête.

---

## 5. Sécurisation des endpoints REST

Les APIs REST sont particulièrement sensibles car elles sont exposées publiquement.  
Spring Security fournit une gestion fine et flexible des routes HTTP :
- Certaines routes peuvent être **publiques** (`/login`, `/register`, `/public/**`).
- D’autres nécessitent une **authentification** (`/api/**`).
- Les routes critiques peuvent exiger un **rôle précis** (`/admin/**`).

Chaque requête est filtrée selon une **chaîne de filtres déclarée explicitement** (`SecurityFilterChain`), où l’ordre détermine la priorité d’exécution.

### Recommandations REST
- Utiliser exclusivement **HTTPS** (TLS obligatoire en production).
- Refuser toute requête sans en-tête `Authorization`.
- Toujours définir un **temps d’expiration court** sur les tokens JWT.
- Renouveler les tokens avec un **Refresh Token sécurisé**.
- Désactiver la gestion de session (`sessionCreationPolicy = STATELESS`).

---

## 6. Bonnes pratiques générales

### 6.1 Gestion des tokens et secrets
- Stocker les clés et secrets dans des **variables d’environnement** ou un **Vault sécurisé**.
- Ne jamais commiter un secret ou client_secret dans le code source.
- Vérifier la **signature et la validité temporelle** des tokens JWT.
- Protéger le Refresh Token par un canal HTTPS uniquement.

### 6.2 CORS et accès front-end
- Configurer CORS de manière restrictive :  
  seules les origines connues (ex. le front Angular officiel) doivent être autorisées.
- Éviter `*` sur les origines, méthodes et headers.

### 6.3 Journalisation et surveillance
- Ne jamais enregistrer de mots de passe, tokens ou données personnelles dans les logs.
- Utiliser des **logs d’audit de sécurité** pour tracer les connexions et accès sensibles.
- Surveiller les erreurs d’authentification répétées (prévention brute-force).

---

## 7. Intégration et extensions possibles

Spring Security s’intègre aisément avec d’autres modules de l’écosystème Spring :
- **Spring Data JPA** pour la gestion des utilisateurs persistés.
- **Spring Authorization Server** pour héberger un serveur OAuth2 complet.
- **Spring Cloud Gateway** pour centraliser la sécurité d’un ensemble de microservices.
- **Spring Boot Actuator** pour surveiller et sécuriser les endpoints techniques (`/actuator/**`).

---

## 8. Synthèse

Spring Security dans les versions modernes de Spring Boot est :
- **Déclaratif** : la sécurité est décrite comme un ensemble de règles explicites.
- **Modulaire** : chaque composant (authentification, autorisation, token) est indépendant.
- **Stateless-first** : la sécurité sans session est désormais la norme.
- **Interopérable** : compatible avec les standards OAuth2, OpenID Connect et JWT.
- **Sécurisé par défaut** : toutes les routes sont protégées tant qu’elles ne sont pas explicitement ouvertes.

En combinant **Spring Security**, **JWT** et **OAuth2**, une application peut atteindre un niveau de sécurité robuste, évolutif et conforme aux standards actuels du web moderne.
