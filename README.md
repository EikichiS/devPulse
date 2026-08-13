# DevPulse

Analizador de perfil técnico de GitHub basado en evidencia. En vez de inferir tecnologías solo por el lenguaje dominante de un repositorio, DevPulse inspecciona archivos reales (`pom.xml`, `build.gradle`, `Dockerfile`, `.github/workflows`) para detectar qué usás realmente — y muestra la evidencia detrás de cada detección.

## Por qué existe

Proyecto de portafolio construido en búsqueda activa de trabajo como backend developer (Java/Spring Boot). El objetivo no es solo "otro CRUD": cubre autenticación OAuth2, integración con una API externa real, procesamiento asíncrono, persistencia relacional + semiestructurada (JSONB), y caché.

## Stack

- Java 21, Spring Boot 4.1
- Spring Security 6 + OAuth2 Client (login con GitHub)
- PostgreSQL (datos relacionales) + Redis (caché / control de rate limit)
- Docker Compose para desarrollo local

## Cómo correr en local

```bash
docker compose up -d          # levanta Postgres y Redis
export GITHUB_CLIENT_ID=...
export GITHUB_CLIENT_SECRET=...
./mvnw spring-boot:run
```

## Roadmap

**v1 (en construcción)**
- [ ] Login con GitHub (OAuth2)
- [ ] Cliente para GitHub REST/GraphQL API (repos, lenguajes, actividad)
- [ ] Inspección estática de archivos clave → detección de tecnologías con evidencia
- [ ] Persistencia (usuarios, repos, jobs de análisis, perfiles)
- [ ] Patrón asíncrono `POST /analyses` → `202 Accepted` + `GET /analyses/{id}`
- [ ] CI (GitHub Actions) + Docker

**Diferido (evaluar según necesidad real, no por adelantado)**
- GitHub App en vez de OAuth App (permisos granulares, installation tokens)
- Webhooks + validación HMAC
- Procesamiento de eventos vía Kafka
