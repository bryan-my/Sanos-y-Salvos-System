# Sanos y Salvos System

Sistema basado en microservicios (Spring Boot + Spring Cloud) para gestionar usuarios y mascotas, con descubrimiento de servicios (Eureka) y un API Gateway como punto de entrada.

## Qué hay en este repo

- `eurekaserver/`: servidor de descubrimiento (Eureka).
- `api-gateway/`: gateway de entrada (Spring Cloud Gateway) + validación JWT + CORS.
- `auth-service/`: autenticación (login) + emisión de JWT.
- `ms-usuarios/`: CRUD básico de usuarios (PostgreSQL) + lookup por email para login.
- `ms_mascotas/`: CRUD básico de mascotas (PostgreSQL).
- `infrastructure/docker-compose.yml`: Postgres para bases de datos locales (por servicio).
- `docs/`: entregables/documentación del proyecto (docx/pptx).

## Arquitectura (alto nivel)

- **Eureka Server** (puerto `8761`) mantiene el registro de instancias.
- **Microservicios** (`ms-usuarios`, `ms-mascotas`, `auth-service`) se registran en Eureka como clientes.
- **API Gateway** (puerto `8080`) enruta por path hacia cada servicio usando `lb://` (load balancer vía Eureka) y valida JWT para endpoints protegidos.

## Servicios y puertos

| Componente | Carpeta | Puerto | Nombre en Eureka / `spring.application.name` |
|---|---:|---:|---|
| Eureka Server | `eurekaserver/` | `8761` | `eureka-server` |
| API Gateway | `api-gateway/` | `8080` | `api-gateway` |
| Auth Service | `auth-service/` | `9000` | `auth-service` |
| MS Usuarios | `ms-usuarios/` | `8081` | `ms-usuarios` |
| MS Mascotas | `ms_mascotas/` | `8082` | `ms-mascotas` |

## Infra (PostgreSQL local)

El compose vive en [docker-compose.yml](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/infrastructure/docker-compose.yml) y levanta 4 bases:

- `db-usuarios` en `localhost:5433` (DB: `db_usuarios`)
- `db-mascotas` en `localhost:5434` (DB: `db_mascotas`)
- `db-geolocalizacion` en `localhost:5435` (DB: `db_geo`)
- `db-coincidencias` en `localhost:5436` (DB: `db_coincidencias`)

En el estado actual del repo, **solo** `ms-usuarios` y `ms-mascotas` están configurados para usar las bases `db_usuarios` y `db_mascotas`.

## Configuración (local)

Cada servicio tiene su propio `application.properties`/`application.yml`:

- Gateway: [application.yml](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/api-gateway/src/main/resources/application.yml)
- Auth: [application.properties](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/auth-service/src/main/resources/application.properties)
- Usuarios: [application.properties](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/ms-usuarios/src/main/resources/application.properties)
- Mascotas: [application.properties](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/ms_mascotas/src/main/resources/application.properties)
- Eureka: [application.properties](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/eurekaserver/src/main/resources/application.properties)

Variables importantes (valores por defecto en los archivos de config):

- **Eureka**: `eureka.client.service-url.defaultZone=http://localhost:8761/eureka/`
- **DB Usuarios**: `spring.datasource.url=jdbc:postgresql://localhost:5433/db_usuarios`
- **DB Mascotas**: `spring.datasource.url=jdbc:postgresql://localhost:5434/db_mascotas`
- **Inter-servicio (Usuarios → Mascotas)**: `mascotas.service.url=http://localhost:8082`
- **JWT secret**: `jwt.secret=...` (misma clave en gateway/auth/mascotas)

Nota: el secret JWT está versionado en este repo. Para un uso real, conviene reemplazarlo/inyectarlo por variables de entorno (por ejemplo `JWT_SECRET`) y no commitearlo.

## Seguridad y JWT

- El token se emite en `auth-service` vía [JwtProvider](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/auth-service/src/main/java/com/sanosysalvos/auth_service/security/JwtProvider.java) con algoritmo `HS256` y expiración de **1 hora**.
- El gateway valida el token como **Resource Server JWT** en [SecurityConfig](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/api-gateway/src/main/java/com/sanosysalvos/api_gateway/config/SecurityConfig.java).

Endpoints públicos en el gateway (sin token):

- `POST /api/usuarios/registro`
- `POST /api/auth/**`
- `GET /api/mascotas/lista`
- `GET /api/mascotas/*` (por id)

El resto requiere:

- Header `Authorization: Bearer <token>`

### CORS

El gateway habilita CORS para `http://localhost:3000` (típico frontend local). Ver [SecurityConfig](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/api-gateway/src/main/java/com/sanosysalvos/api_gateway/config/SecurityConfig.java).

## Ruteo por el Gateway

Configurado en [application.yml](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/api-gateway/src/main/resources/application.yml):

- `/api/usuarios/**` → `lb://MS-USUARIOS`
- `/api/mascotas/**` → `lb://MS-MASCOTAS`
- `/api/auth/**` → `lb://AUTH-SERVICE`

## Flujo de trabajo (runtime)

### 1) Registro de usuario

El cliente llama al gateway:

- `POST /api/usuarios/registro`

El gateway lo deja pasar sin JWT y lo enruta a `ms-usuarios`, que guarda el usuario en Postgres.

Endpoints del MS Usuarios:

- `POST /api/usuarios/registro`
- `GET /api/usuarios/lista`
- `GET /api/usuarios/search?email=...`
- `DELETE /api/usuarios/{id}` (además intenta borrar mascotas del usuario llamando a `ms-mascotas`)

Código: [UsuarioController](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/ms-usuarios/src/main/java/com/sanosysalvos/ms_usuarios/controller/UsuarioController.java)

### 2) Login y emisión del token

El cliente llama al gateway:

- `POST /api/auth/login`

`auth-service` busca al usuario por email llamando a `ms-usuarios` vía Feign y, si la contraseña matchea, devuelve un token JWT.

Código:

- [AuthController](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/auth-service/src/main/java/com/sanosysalvos/auth_service/controller/AuthController.java)
- [UserFeignClient](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/auth-service/src/main/java/com/sanosysalvos/auth_service/feign/UserFeignClient.java)
- [AuthService](file:///c:/Users/Mystic/Desktop/Nueva%20carpeta/Sanos-y-Salvos-System/auth-service/src/main/java/com/sanosysalvos/auth_service/service/AuthService.java)

### 3) Consumo de recursos protegidos

Para crear/editar/borrar recursos (por ejemplo mascotas), se usa el token en `Authorization: Bearer ...` contra el gateway. El gateway valida el token y enruta al microservicio.

## Cómo levantar todo en local

Prerequisitos:

- Java 17 (los `pom.xml` declaran `java.version=17`)
- Docker Desktop (para Postgres)

### 1) Levantar bases de datos

Desde la raíz del repo:

```bash
docker compose -f infrastructure/docker-compose.yml up -d
```

En Windows (PowerShell) también podés usar:

```powershell
docker compose -f .\infrastructure\docker-compose.yml up -d
```

### 2) Levantar Eureka

En `eurekaserver/`:

```bash
./mvnw spring-boot:run
```

En Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

Dashboard:

- http://localhost:8761

### 3) Levantar microservicios

En cada carpeta (en este orden recomendado):

```bash
# ms-usuarios (8081)
cd ms-usuarios
./mvnw spring-boot:run
```

```bash
# ms_mascotas (8082)
cd ms_mascotas
./mvnw spring-boot:run
```

```bash
# auth-service (9000)
cd auth-service
./mvnw spring-boot:run
```

En Windows (PowerShell), el equivalente es `.\mvnw.cmd spring-boot:run`.

### 4) Levantar el Gateway

En `api-gateway/` (8080):

```bash
./mvnw spring-boot:run
```
