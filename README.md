# ColdTrace Maintenance Service

Reuses preventive maintenance and technical service workflows from
`Veltis-Software/coldtrace-backend` at `f66b04a`. Schedules and requests belong to
this service; identity, asset, subscription and incident snapshots are obtained
from their owners. No historical backend process or shared business database is
required.

## Runtime

Java 26, Spring Boot 4.0.6 and Maven Wrapper. Run `./mvnw test` and
`./mvnw spring-boot:run` (`.\mvnw.cmd` on Windows). Local port: 8086.
Flyway creates the two owner tables; Hibernate validates the schema.

| Variable | Purpose |
| --- | --- |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | Connection restricted to `ct_maintenance` |
| `IDENTITY_URL` | Identity, default `http://localhost:8081` |
| `ASSET_URL` | Asset, default `http://localhost:8082` |
| `SUBSCRIPTION_URL` | Subscription, default `http://localhost:8088` |
| `ALERT_URL` | Alert, default `http://localhost:8084` |
| `INTERNAL_SERVICE_KEY` | Required workload credential, at least 32 characters |
| `PORT` | Listener port; Docker uses 8080 |

Public requests validate the actor session, organization and asset permissions.
The subscription owner decides whether maintenance is included in the plan.
A missing resource is distinct from an unavailable owner. Tests cover this
contract; integration verification uses isolated databases and explicit plan
fixtures, without charging or claiming payment processing.

OpenAPI: `/v3/api-docs`. Health: `/actuator/health`.
Internal read snapshots require `X-Service-Key`; browser tokens cannot replace it.
Secrets are supplied by the environment and are never committed.
