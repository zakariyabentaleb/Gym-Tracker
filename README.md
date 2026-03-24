Gym-Tracker — Documentation technique

Résumé
======
Ce document fournit une documentation complète du backend "Gym-Tracker" (Spring Boot, Java 17). Il couvre : architecture, installation, exécution locale, endpoints principaux, intégration frontend (AngularJS), Postman, variables d'environnement, dépannage des erreurs courantes et bonnes pratiques de développement.

Arborescence importante
=======================
- `pom.xml` — dépendances Maven
- `src/main/java/com/gymtracker` — code source Java (controllers, services, repositories, entities, security)
- `src/main/resources/application.properties` — configuration principale
- `src/main/resources/schema.sql` — script de création de tables (utilisé pour H2/local)
- `db/schema-postgres.sql` — script SQL pour PostgreSQL (contraintes, CHECK, indexes)
- `postman/` — collections Postman (Payments, Members, Subscriptions, Coach, etc.)
- `uploads/` — dossier des fichiers uploadés (photos, documents)
- `jira_*` et `jira_full_backlog.csv` — backlog / tâches (export Jira) ajoutés à la racine

Prérequis
=========
- Java 17
- Maven (ou utiliser les wrappers `mvnw` / `mvnw.cmd` fournis)
- PostgreSQL local ou distant (configurable via `application.properties`)
- (Optionnel) Postman pour tests d'API

Configuration (variables importantes)
====================================
Configurer ces propriétés dans `src/main/resources/application.properties` (ou via variables d'environnement en production) :

- spring.datasource.url=jdbc:postgresql://<HOST>:<PORT>/<DBNAME>
- spring.datasource.username=<DB_USER>
- spring.datasource.password=<DB_PASSWORD>
- spring.jpa.hibernate.ddl-auto=none (ou validate)
- jwt.secret=<secret-pour-jwt>
- paypal.clientId=<PAYPAL_CLIENT_ID>
- paypal.clientSecret=<PAYPAL_SECRET>
- mail.* (SMTP) pour envoi d'emails

Si tu utilises profiles (test/prod), vérifie `application-test.properties` et le fichier `target/classes/application.properties` lors des builds.

Démarrage local (Windows cmd.exe)
=================================
1) Build avec le wrapper (Windows) :

```cmd
mvnw.cmd clean package -DskipTests
```

2) Lancer l'application :

```cmd
mvnw.cmd spring-boot:run
```

ou exécuter le jar :

```cmd
java -jar target\Gym-Tracker-0.0.1-SNAPSHOT.jar
```

Notes : si tu rencontres des erreurs au démarrage, relis la section "Dépannage" ci-dessous.

Architecture & Conventions
==========================
- Architecture en couches : controller -> service -> repository
- DTOs pour échanges via API (dans `dto/`) et mappers (MapStruct si présent)
- Spring Security + JWT pour authentification et RBAC (roles : ROLE_ADMIN, ROLE_RECEPTION, ROLE_COACH, ROLE_MEMBER)
- Base de données : PostgreSQL (en prod), H2 pour tests

Entités principales (vue conceptuelle)
=====================================
- User (auth + roles)
- Member (profil client, documents, état santé)
- SubscriptionPlan (formules)
- Subscription (souscriptions des membres)
- Payment (paiements)
- Course (type de cours)
- CourseSchedule (instance planifiée : start/end, coach, salle, capacité)
- Booking (réservations / waitlist via status WAITLISTED)
- Coach (profil coach)
- Equipment, Access (optionnel selon avancement)

Endpoints REST (raccourci)
==========================
- /api/auth/** — login, register (attention : registration public peut être désactivée)
- /api/members/** — gestion des membres (CREATE restreint à ADMIN/RECEPTION)
- /api/subscription-plans/** — CRUD des formules
- /api/subscriptions/** — souscription d'un membre
- /api/payments/** — création/listing des paiements
- /api/courses/** — CRUD courses
- /api/schedules/** — CRUD et listing des sessions (CourseSchedule)
- /api/bookings/** — réserver / annuler / consulter réservations
- /api/coaches/** — CRUD coachs, /api/coaches/{id}/schedules pour horaires
- /api/stats/** — agrégations / dashboard

Sécurité & RBAC
===============
- JWT est utilisé pour protéger les routes. L'en-tête Authorization doit être :
  Authorization: Bearer <JWT_TOKEN>
- Règles courantes :
  - POST /api/members : ADMIN ou RECEPTION uniquement
  - Endpoints de paiement : authentifiés (ADMIN pour testing ou flux public sécurisé)

Guide rapide Postman (tests)
============================
1) Importer la collection `postman/*.postman_collection.json` dans Postman.
2) Définir les variables globales :
   - baseUrl = http://localhost:8080
   - adminToken = <JWT token obtenu via /api/auth/login>

Exemples d'appels utiles

- Auth (se connecter) :
  POST {{baseUrl}}/api/auth/login
  Body JSON: { "username": "admin", "password": "adminpass" }

- Créer un membre (Admin token requis) :
  POST {{baseUrl}}/api/members
  Headers: Authorization: Bearer {{adminToken}}
  Body JSON:

```json
{
  "userId": 7,
  "firstName": "Jean",
  "lastName": "Dupont",
  "phone": "+33123456789",
  "birthDate": "1990-05-21",
  "active": true
}
```

- Créer un paiement (exemple) :
  POST {{baseUrl}}/api/payments
  Headers: Authorization: Bearer {{adminToken}}
  Body JSON:

```json
{
  "subscriptionId": 2,
  "memberId": 2,
  "amountCents": 3000,
  "method": "CARD",
  "reference": "POS-1616161616-01"
}
```

- Créer une réservation (Booking) :
  POST {{baseUrl}}/api/bookings
  Headers: Authorization: Bearer {{adminToken}} (ou membre pour self-book)
  Body JSON:

```json
{
  "memberId": 2,
  "scheduleId": 5
}
```

Dépannage — erreurs fréquentes et corrections
============================================
1) Erreur Jackson lors du démarrage :
   "Failed to bind properties under 'spring.jackson.serialization' ... No enum constant ... WRITE_DATES_AS_TIMESTAMPS"
   - Cause : mauvaise configuration dans application.properties utilisant une valeur non reconnue pour spring.jackson.serialization.
   - Correction : supprimez/ajustez les clés incorrectes. Exemple minimal :

```
spring.jackson.default-property-inclusion=non_null
spring.jackson.serialization.indent_output=true
```

   - Si vous avez besoin d'un ObjectMapper custom, ajoutez une configuration :

```java
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
```

2) Problème "Could not autowire. No beans of 'ObjectMapper' type found" :
   - Assurez-vous que `com.fasterxml.jackson.databind.ObjectMapper` est sur le classpath (dépendance jackson-databind transitivement fournie par Spring Boot). Si vous avez une configuration custom, exposez le bean comme ci-dessus.

3) PayPal - INVALID_PARAMETER_SYNTAX (montant envoyé au format "2500,00") :
   - Cause : format décimal avec virgule (locale FR) envoyé à PayPal.
   - Correction : convertir amountCents en string avec point décimal (ex: 2500 cents -> "25.00"). Utiliser NumberFormat avec Locale.US ou formater manuellement :

```java
String value = String.format(Locale.US, "%.2f", amountCents / 100.0);
```

4) Violations CHECK en base (ex: bookings_status_check, chk_schedule_time) :
   - Cause : mismatch entre valeurs d'enum en Java et contraintes SQL, ou insertion d'un schedule avec end_time <= start_time.
   - Correction :
     - Vérifier et aligner enums Java et CHECK SQL.
     - Ajouter validations côté backend pour refuser les requêtes invalides avant persistance.
     - Modifier `db/schema-postgres.sql` si la règle métier a changé.

5) Booking error example (DataIntegrityViolationException) :
   - Si vous obtenez une erreur lors de la création d'une réservation, vérifiez :
     - status envoyé est dans la liste acceptée
     - schedule existe et capacity non dépassée
     - transactions/locking pour capacité (éviter race conditions)

Tests
=====
- Tests unitaires : `mvnw.cmd test`
- Tests d’intégration : les classes de test se trouvent dans `src/test/java` et utilisent H2 (profile test).

Déploiement
===========
- Build jar : `mvnw.cmd clean package -DskipTests`
- Docker : préparer un `Dockerfile` (multi-stage) si tu veux containeriser l'app. Exemple basique :

```dockerfile
FROM eclipse-temurin:17-jre
COPY target/Gym-Tracker-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```



