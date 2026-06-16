# 🐾 Sanos y Salvos — Backend

Sistema distribuido de microservicios para la **búsqueda, registro y coincidencia de mascotas perdidas**. Permite a ciudadanos reportar avistamientos y al sistema calcular automáticamente si coinciden geográficamente con una mascota perdida usando el **algoritmo de Haversine**.

---

## 🏗️ Arquitectura General

```
                        ┌─────────────────────────────┐
  Cliente / Frontend ──▶│     API Gateway :8080        │
                        │  Spring Cloud Gateway + JWT  │
                        └──────────────┬──────────────┘
                                       │ lb:// (Eureka)
              ┌────────────────────────┼────────────────────────┐
              ▼                        ▼                        ▼
     ┌──────────────┐       ┌──────────────────┐     ┌──────────────────┐
     │ auth-service │       │   ms-usuarios    │     │   ms-mascotas    │
     │    :9000     │       │     :8081        │     │     :8082        │
     └──────────────┘       └──────────────────┘     └──────────────────┘
              ▼                        ▼                        ▼
     ┌──────────────────┐   ┌──────────────────┐     ┌──────────────────┐
     │ ms-geolocalizacion│  │   ms-reportes    │     │ ms-coincidencias │
     │     :8083        │  │     :8085        │     │     :8084        │
     └──────────────────┘   └──────────────────┘     └──────────────────┘
              │                        │  RabbitMQ             │
              └────────────────────────┴───────────────────────┘
                                       │
                          ┌────────────▼───────────┐
                          │   Eureka Server :8761   │
                          │  Registro de servicios  │
                          └────────────────────────┘
```

### Componentes clave

| Componente | Rol |
|---|---|
| **API Gateway** | Punto de entrada único. Valida JWT, enruta por path, aplica Circuit Breaker (Resilience4j) |
| **Eureka Server** | Registro y descubrimiento de servicios. Los microservicios se registran y se localizan por nombre |
| **auth-service** | Emite tokens JWT firmados con HS256. Valida credenciales consultando ms-usuarios vía OpenFeign |
| **ms-usuarios** | CRUD de usuarios. Encriptación de contraseñas con BCrypt |
| **ms-mascotas** | CRUD de mascotas. Gestión de estados: `PERDIDA`, `EN_CASA`, `ENCONTRADA` |
| **ms-geolocalizacion** | Almacena coordenadas (lat/lng) separadas de la mascota. Provee la última ubicación conocida |
| **ms-reportes** | Registra avistamientos ciudadanos. Publica eventos en RabbitMQ al crear un reporte |
| **ms-coincidencias** | Motor espacial. Consume eventos de RabbitMQ, calcula Haversine y genera matches |

---

## ⚙️ Tecnologías

- **Java 17**
- **Spring Boot 4.0.6**
- **Spring Cloud 2025.1.1** — Gateway, Eureka, OpenFeign
- **Spring Security** — JWT (HS256) como OAuth2 Resource Server
- **PostgreSQL 15** — base de datos independiente por microservicio
- **RabbitMQ 3** — mensajería asíncrona entre ms-reportes y ms-coincidencias
- **Resilience4j** — Circuit Breaker en el API Gateway con fallback controlado
- **Docker + Docker Compose** — contenedores con multi-stage build (Maven → JRE)
- **SonarQube** — análisis estático de calidad de código

---

## 🌐 Tabla de Servicios y Puertos

| Servicio | Puerto interno | Puerto BD (host) | Base de datos |
|---|:---:|:---:|---|
| Eureka Server | `8761` | — | — |
| API Gateway | `8080` | — | — |
| auth-service | `9000` | — | — |
| ms-usuarios | `8081` | `5433` | `db_usuarios` |
| ms-mascotas | `8082` | `5434` | `db_mascotas` |
| ms-geolocalizacion | `8083` | `5435` | `db_geo` |
| ms-coincidencias | `8084` | `5436` | `db_coincidencias` |
| ms-reportes | `8085` | `5437` | `db_reportes` |
| RabbitMQ | `5672` / `15672` | — | — |
| SonarQube | `9000` (host: `9000`) | — | — |

> ⚠️ **Nota:** `auth-service` corre en el puerto interno `9000` del contenedor. En `docker-compose.yml` se mapea al host como `9090:9000` para no colisionar con SonarQube.

---

## 🧠 Motor de Coincidencias Espaciales

El microservicio `ms-coincidencias` implementa el **algoritmo de Haversine** para calcular la distancia en kilómetros entre dos coordenadas geográficas sobre la superficie terrestre:

```
d = 2r · arcsin(√( sin²(Δlat/2) + cos(lat₁)·cos(lat₂)·sin²(Δlon/2) ))
```

**Flujo completo:**

```
1. Usuario reporta avistamiento (ms-reportes)
         │
         ▼
2. ms-reportes publica mensaje en RabbitMQ [reportes.queue]
         │
         ▼
3. ms-coincidencias consume el evento
         │
         ▼
4. Consulta coordenadas de mascotas PERDIDAS → ms-geolocalizacion (OpenFeign síncrono)
         │
         ▼
5. Aplica Haversine: si distancia ≤ 10 km → genera Match (PENDIENTE)
         │
         ▼
6. Porcentaje de similitud = 100 - (distancia × 5), rango [0, 100]
```

---

## 🔐 Seguridad

- Tokens JWT emitidos por `auth-service`, firmados con `HS256`, expiración: **1 hora**
- El API Gateway actúa como **OAuth2 Resource Server** y valida el token en cada petición
- **Circuit Breaker** en todas las rutas: si un microservicio falla, el gateway responde con `503` y un mensaje JSON controlado (sin errores en cadena)

**Endpoints públicos** (no requieren token):

```
POST /api/auth/login
POST /api/usuarios/registro
GET  /api/mascotas/lista
GET  /api/mascotas/{id}
GET  /api/geolocalizacion/mapa
POST /api/reportes/avistamiento
GET  /api/reportes/recientes
```

---

## 🚀 Instrucciones de Ejecución

### Prerrequisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y corriendo
- No se requiere Java ni Maven instalados localmente — el build ocurre **dentro de Docker**

### ▶️ Levantar todo el sistema (recomendado)

Desde la carpeta `Sanos-y-Salvos-System/`:

```bash
docker compose up --build -d
```

> La primera vez descarga las imágenes base y compila todos los microservicios (~5-10 min).  
> Las siguientes veces, si solo cambió código fuente (no el `pom.xml`), el caché de Maven acelera el proceso significativamente.

### ♻️ Actualizar un microservicio tras cambios

```bash
# Reconstruir y reiniciar solo el servicio modificado
docker compose up --build ms-coincidencias

# Ver logs en tiempo real
docker compose logs -f ms-coincidencias
```

### ⏹️ Detener todo

```bash
docker compose down
```

### 🗑️ Liberar espacio en disco (caché de Docker)

```bash
# Elimina capas huérfanas y caché de build (NO borra las BDs)
docker system prune -f
docker builder prune -f
```

---

## 🗄️ Gestión de Bases de Datos

Conectar a una base de datos específica:

```bash
docker exec -it db-mascotas psql -U user_sanos -d db_mascotas
```

Limpiar datos para demo:

```sql
-- Ejecutar en cada BD en este orden:
-- 1. db_coincidencias
TRUNCATE TABLE matches RESTART IDENTITY;
-- 2. db_geo
TRUNCATE TABLE ubicaciones RESTART IDENTITY;
-- 3. db_mascotas
TRUNCATE TABLE mascotas RESTART IDENTITY;
-- 4. db_reportes
TRUNCATE TABLE avistamientos RESTART IDENTITY;
```

---

## 📁 Estructura del Repositorio

```
Sanos-y-Salvos-System/
├── api-gateway/          # Enrutamiento, JWT, Circuit Breaker
├── auth-service/         # Login + emisión de JWT
├── eurekaserver/         # Registro de servicios
├── ms-usuarios/          # CRUD usuarios + BCrypt
├── ms_mascotas/          # CRUD mascotas + estados
├── ms-geolocalizacion/   # Coordenadas lat/lng por mascota
├── ms-reportes/          # Avistamientos + publicación RabbitMQ
├── ms-coincidencias/     # Motor Haversine + matches
└── docker-compose.yml    # Orquestación completa
```

---

## 📊 Calidad de Código (SonarQube)

El análisis de calidad se ejecuta con SonarQube Community Edition (incluido en `docker-compose.yml`).  
Panel disponible en: **http://localhost:9000**

| Microservicio | Cobertura de pruebas |
|---|:---:|
| ms-usuarios | 81.3% |
| ms-reportes | 73.7% |
| ms-mascotas | 70.0% |
| ms-coincidencias | 68.9% |

El ecosistema completo supera los **Quality Gates** sin vulnerabilidades ni Security Hotspots.
