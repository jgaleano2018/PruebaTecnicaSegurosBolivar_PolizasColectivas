# PruebaTecnicaSegurosBolivar_PolizasColectivas — Microservicio Pólizas Colectiva

Orientado a inmobiliarias y administraciones de copropiedad: el tomador es la inmobiliaria/administración, los asegurados son los arrendatarios de cada inmueble y los beneficiarios los arrendadores. 1..N riesgos.

Java 21 · Spring Boot 3.3 · WebFlux · Kafka · Keycloak (JWT) + `x-api-key` · Resilience4j · Swagger · Docker.
Arquitectura **hexagonal**: `domain` / `application` (port in-out, service) / `infrastructure` (in: web, messaging |
out: rest hacia el CORE, realtime SSE). La persistencia está centralizada en el CORE (monolito modular);
este microservicio aplica las reglas de su segmento, valida el tipo de póliza y consume el CORE con timeout,
circuit breaker, reintentos con backoff exponencial + jitter e `Idempotency-Key` (siempre generada).

## Ejecución

Todo el ecosistema se levanta desde `PruebaTecnicaSegurosBolivar_Core` con `docker compose up -d --build`.
Localmente (con CORE, Kafka y Keycloak arriba): `mvn spring-boot:run` (lee `.env`).
Solo este servicio en Docker (red `seguros-net` ya creada): `docker compose up -d --build`.

Swagger: http://localhost:8082/swagger-ui.html — Headers: `x-api-key: 123456` y `Authorization: Bearer <token>`.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/polizas-colectivas` | Crear póliza |
| GET | `/api/v1/polizas-colectivas?estado=&pagina=&tamano=` | Listar |
| GET | `/api/v1/polizas-colectivas/{id}` | Consultar |
| PATCH | `/api/v1/polizas-colectivas/{id}` | Modificar |
| POST | `/api/v1/polizas-colectivas/{id}/renovaciones` | Renovar (+IPC) |
| POST | `/api/v1/polizas-colectivas/{id}/cancelaciones` | Cancelar |
| GET | `/api/v1/polizas-colectivas/{id}/riesgos` | Listar riesgos |
| POST | `/api/v1/polizas-colectivas/{id}/riesgos` | Agregar riesgo |
| POST | `/api/v1/polizas-colectivas/{id}/riesgos/{riesgoId}/cancelaciones` | Eliminar (cancelar) riesgo |
| GET | `/api/v1/polizas-colectivas/eventos` | Eventos en tiempo real (SSE desde Kafka) |

Ejemplo de creación:

```json
{
  "fechaInicio": "2026-11-01",
  "mesesVigencia": 12,
  "renovacionAutomatica": true,
  "tomador": { "tipoDocumento": "NIT", "numeroDocumento": "900777888", "nombre": "Inmobiliaria Centro", "tipoPersona": "JURIDICA" },
  "riesgos": [{
    "direccion": "Calle 10 # 43-20", "ciudad": "Medellín", "canonMensual": 2000000,
    "arrendatario": { "tipoDocumento": "CC", "numeroDocumento": "1037111222", "nombre": "Ana Ruiz", "tipoPersona": "NATURAL" },
    "arrendador":   { "tipoDocumento": "CC", "numeroDocumento": "70111333",  "nombre": "Juan Paz", "tipoPersona": "NATURAL" }
  }]
}
```

## Pruebas

`mvn test` — `PolizaServiceTest` valida que solo se operen pólizas colectivas y la generación de Idempotency-Key.
