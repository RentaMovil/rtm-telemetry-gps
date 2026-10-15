# rtm-telemetry-gps

Captura de ubicación GPS durante rentas activas e historial de ruta — el servicio de mayor
volumen de escritura del sistema. Hexagonal, Spring Boot 4.1. Dueño de `gps`, `location`.

- **Puerto local:** 3005
- **Base de datos:** `rentamovil` (SQL Server), schema `telemetry` — migraciones en
  `rtm-telemetry-gps-db`.
- **Acceso exclusivo Admin/Super Admin** a `/tracking/*` (`Location.INV-002`): un `CLIENT` nunca
  puede consultarlo, ni para su propia reserva. `403` siempre.
- **Dos esquemas de autenticación:**
  - `/tracking/*`: JWT de iam (igual que el resto de los servicios), permiso `tracking:read`.
  - `/locations`: una API key fija compartida (header `X-Device-Api-Key`) para todos los
    dispositivos GPS — no hay columna por dispositivo en el modelo de datos documentado.

## La única dependencia saliente (y solo para consultas, no para la ingesta)

`POST /locations` (la ingesta, el endpoint de mayor volumen) **no llama a nadie** — valida
`Location.INV-001` contra un tracker en memoria, alimentado por los eventos que consume.

`GET /tracking/{rentalId}/live` y `/history` sí llaman a `booking-reservation`
(`GET /internal/rentals/{id}`, sin auth) para resolver `rentalId -> gpsId` y la ventana de fechas
de esa renta. Es necesario: `location` no tiene `rental_id`, y un mismo dispositivo físico se
reutiliza en rentas distintas con el tiempo — "todas las ubicaciones de ese gpsId" no es lo mismo
que "las de esta renta".

## Eventos consumidos

`RentalStarted`/`RentalCompleted` de `booking-reservation`, vía `POST /internal/events` (mismo
sobre que usan todos los servicios). Mantienen el tracker en memoria de qué `gpsId` tiene una
renta `IN_PROGRESS` ahora mismo — se pierde al reiniciar el servicio (aceptable para un MVP de un
solo nodo; sin tabla propia porque `data-model.md` solo documenta `gps` y `location`).

## Cómo correrlo en local

```bash
cp .env.example .env
# completa APP_TELEMETRY_GPS_JWT_PUBLIC_KEY (la misma clave pública de iam) y
# APP_TELEMETRY_GPS_DEVICE_API_KEY (cualquier valor, es lo que mandarán los dispositivos de prueba)

mvn spring-boot:run

curl http://localhost:3005/health
# {"status":"ok","db":"connected"}

# Simular un reporte de un dispositivo GPS (requiere que haya una renta IN_PROGRESS para ese gpsId)
curl -X POST http://localhost:3005/locations \
  -H "X-Device-Api-Key: $DEVICE_KEY" -H 'Content-Type: application/json' \
  -d '{"gpsId":1,"latitude":4.6097,"longitude":-74.0817,"timestamp":"2026-10-15T15:10:00"}'
```

### Con Docker

```bash
cd ../rtm-telemetry-gps-db && docker compose up -d   # migraciones primero
docker compose up -d
```

## Related documents

- `rtm-docs/09-microservices/services/06-telemetry-gps/` — README, data-model, decisions, events
- `rtm-docs/07-api/contracts/openapi/telemetry-gps.yaml`

## Ramas

`feat/`, `fix/`, `chore/` → PR a `develop` (Squash and Merge).
