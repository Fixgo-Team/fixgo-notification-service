# fixgo-notification-service

Microservicio de Notificaciones de FixGo: registro de dispositivos, envío de notificaciones push por FCM e historial.
El correo electrónico queda fuera del MVP (ver ADR-005 en `fixgo-docs`).

## Documentación de diseño

En el repositorio `fixgo-docs`:
- `05-architecture/decisions/records/ADR-005-notification-service.md`
- `09-microservices/services/05-notification-service/`
- `07-api/contracts/openapi/notification-service.yaml`

## Requisitos

- Java 17+ y Maven 3.9+
- Docker (solo para MySQL local)

## Arranque local

```bash
cp .env.example .env            # opcional; el perfil "local" ya trae valores de desarrollo
docker compose up -d mysql
mvn spring-boot:run -Dspring-boot.run.profiles=local
curl http://localhost:3004/health
curl http://localhost:3004/health/ready
```

Con el perfil `local` el envío usa un sender falso (`logging`); no se necesita Firebase.

## Pruebas

```bash
mvn test
```

Incluye una prueba de arquitectura (ArchUnit) que falla si `domain` importa Spring, JPA, Firebase o las capas externas.

## Estructura

```
src/main/java/com/fixgo/notification/
  domain/           # hexágono: entidades, reglas y puertos (sin frameworks)
  application/      # casos de uso
  infrastructure/   # adaptadores: REST, JPA, FCM, configuración
```

## Convenciones

Ramas `feat|fix|chore/...` → PR a `main`; Conventional Commits; PR de máximo ~400 líneas. Nunca subir `.env` ni credenciales de Firebase.
