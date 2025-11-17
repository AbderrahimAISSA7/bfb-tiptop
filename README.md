# TheTipTop – API Jeu-Concours

## Architecture
- **Backend** : Spring Boot 3 / Java 21
- **Persistence** : PostgreSQL (Flyway migrations `V1__schema` → `V3__add_user_role`)
- **Domain Layer** : entités JPA (`User`, `Prize`, `Code`, `Concours`, `Participation`, `Newsletter`)
- **Repositories** : Spring Data JPA + projections pour les statistiques
- **Services** : logique métier (validation de codes, participations, stats, newsletters, utilisateurs)
- **API** : contrôleurs REST (`/api/auth`, `/api/users`, `/api/newsletters`, `/api/participations`, `/api/admin`)
- **Sécurité** : Spring Security + JWT (bearer token, rôles `USER` / `ADMIN`), filtre `JwtAuthenticationFilter`
- **Documentation** : Springdoc OpenAPI (`/swagger-ui/index.html`)

## Fonctionnel clé
- **Auth** : inscription + login (`POST /api/auth/register`, `POST /api/auth/login`) → renvoie un JWT
- **Profil** : `GET /api/users/me` (token requis)
- **Newsletter** : inscription / suppression d'email publics
- **Participations** : déclaration d’un code + historique utilisateur
- **Admin** : statistiques globales + participants paginés (rôle ADMIN requis)
- **Seed data** : Flyway `V2__test_data.sql` charge 3 utilisateurs (`alicepass`, `brunopass`, `chloepass`), des lots, des codes

## Démarrage local
```bash
# Lancer PostgreSQL + pgAdmin
docker-compose up -d postgres pgadmin

# Démarrer l'API
./mvnw spring-boot:run
```
Profils : `application.yml` active `dev` par défaut (connexion `jdbc:postgresql://localhost:5432/thetiptop`).

## Docker (API + Postgres)
Build multi-stage + image JRE légère :
```bash
# construire et lancer toute la stack (Postgres + API + pgAdmin)
docker-compose up --build

# logs temps réel
docker-compose logs -f api
```
L'API écoute sur `localhost:8080` et consomme la base `postgres` déclarée dans `docker-compose.yml`.

## Scénarios de validation
1. **Inscription user** – `POST /api/auth/register` avec email unique, vérifier que le token est renvoyé.
2. **Connexion user** – `POST /api/auth/login` avec un compte seed (`alice@example.com` / `alicepass`).
3. **Newsletter** – `POST /api/newsletters` puis `DELETE /api/newsletters/{id}`.
4. **Participation** – récupérer un `code` existant (`SELECT code FROM thetiptop.codes`) et appeler `POST /api/participations` (Authorization: Bearer `<token>`).
5. **Stats admin** – appeler `GET /api/admin/stats` / `GET /api/admin/participants` avec un token ADMIN (Alice).
Tous les endpoints sont aussi documentés dans Swagger pour rejouer rapidement via l'UI.

## Tests
- **Unit tests** : `ParticipationServiceTest` (validation de code, règles métier)
- **Repository tests** : `CodeRepositoryTest` vérifie `findByCode`, `findByStatus`, `countByStatus`
- **Spring Boot smoke test** : `TiptopApplicationTests`
- Profil `test` utilise H2 en mode PostgreSQL (`src/test/resources/application-test.yml`)

Commande :
```bash
./mvnw test
```

## Documentation API & Postman
- OpenAPI/Swagger : `http://localhost:8080/swagger-ui/index.html`
- Chaque endpoint possède `@Operation` / `@Tag` (+ schéma de sécurité `bearerAuth`).
- Pour tester via Postman/Insomnia :
  1. `POST /api/auth/login` (ex: `alice@example.com` / `alicepass`) → récupérer le token JWT
  2. Ajouter `Authorization: Bearer <token>` sur les appels protégés (`/api/users/**`, `/api/participations/**`, `/api/admin/**`)

## Sécurité & rôles
- `USER` : accès aux participations, profil
- `ADMIN` : endpoints `/api/admin/**`
- Clé JWT configurable via `tiptop.security.jwt.secret` (profil `dev` → valeur de développement, à surcharger en prod)

## Notes complémentaires
- Les migrations Flyway créent la base et insèrent des données de test.
- Les passwords seed sont hashés en BCrypt.
- Newsletter endpoints sont laissés publics (double opt-in à implémenter plus tard).
