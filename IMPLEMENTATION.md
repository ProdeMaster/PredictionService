# Prediction Service - Guía de Implementación

## Propósito de la Aplicación

**Prediction Service** es un microservicio de ProdeMaster (plataforma de predicciones deportivas similar al Prode argentino) responsable de:

- **Registrar predicciones** de usuarios para partidos de fútbol futuros
- **Validar reglas de negocio** antes de aceptar una predicción (timing, estado del partido, unicidad)
- **Exponer predicciones** a otros servicios (principalmente Score Service)
- **Emitir eventos** cuando se crea, modifica o cancela una predicción
- **No calcula puntajes** (esa responsabilidad pertenece a Score Service)

El servicio sigue **Arquitectura Hexagonal (Ports & Adapters)** para mantener el dominio puro y libre de dependencias de infraestructura.

---

## Arquitectura General del Sistema

```
ProdeMaster/
├── User Service         (Gestión de usuarios)
├── Match Service        (Gestión de partidos - puerto 8083)
├── Prediction Service   (Gestión de predicciones - puerto 8082)
└── Score Service        (Cálculo de puntajes)
```

### Tecnologías

- **Java 17** - Lenguaje de programación
- **Spring Boot 3.4.4** - Framework principal
- **Spring Data JPA** - Persistencia con PostgreSQL
- **ValkeyDB** - Cacheo (API compatible con Redis)
- **Spring Kafka** - Publicación de eventos
- **Eureka Client** - Service discovery
- **Resilience4j** - Circuit breaker
- **Maven** - Gestión de dependencias

---

## Estructura de Paquetes

```
src/main/java/com/ProdeMaster/PredictionService/
├── domain/                    # Lógica de negocio pura
│   ├── model/                 # Entidades y value objects
│   │   ├── Prediction.java           (Entidad con JPA + optimistic locking)
│   │   ├── PredictionId.java          (Value Object)
│   │   ├── UserId.java                (Value Object)
│   │   ├── MatchId.java               (Value Object)
│   │   ├── MatchScore.java            (Value Object: scoreline exacto)
│   │   ├── MatchOutcome.java          (Enum derivado: HOME_WIN, DRAW, AWAY_WIN)
│   │   ├── PredictionResult.java      (DEPRECATED - usar MatchOutcome)
│   │   └── PredictionStatus.java      (Enum: PENDING, LOCKED, VOIDED)
│   └── exception/             # Excepciones de dominio
│       ├── PredictionNotFoundException.java
│       ├── InvalidPredictionException.java
│       └── PredictionAlreadyExistsException.java
│
├── application/              # Casos de uso y puertos
│   ├── port/
│   │   ├── inbound/          # Puertos de entrada (ISP - uno por caso de uso)
│   │   │   ├── CreatePredictionInboundPort.java
│   │   │   ├── UpdatePredictionInboundPort.java
│   │   │   ├── CancelPredictionInboundPort.java
│   │   │   ├── GetPredictionByIdInboundPort.java
│   │   │   ├── GetPredictionsByMatchInboundPort.java
│   │   │   ├── GetPredictionsByUserInboundPort.java
│   │   │   ├── LockPredictionsForMatchInboundPort.java
│   │   │   ├── VoidPredictionsForMatchInboundPort.java
│   │   │   ├── PredictionCommandPort.java     (DEPRECATED)
│   │   │   └── PredictionQueryPort.java        (DEPRECATED)
│   │   └── outbound/         # Puertos de salida (interfaces para infraestructura)
│   │       ├── PredictionRepository.java
│   │       ├── PredictionQueryPort.java
│   │       ├── MatchServiceClient.java
│   │       └── EventPublisher.java
│   └── usecase/              # Implementaciones de casos de uso
│       ├── CreatePredictionUseCase.java
│       ├── UpdatePredictionUseCase.java
│       ├── CancelPredictionUseCase.java
│       ├── GetPredictionByIdUseCase.java
│       ├── GetPredictionsByMatchUseCase.java
│       └── GetPredictionsByUserUseCase.java
│
├── infrastructure/           # Adaptadores externos
│   ├── config/
│   │   ├── KafkaConfig.java          (Configuración de topics)
│   │   ├── RedisCacheConfig.java     (Cache Valkey)
│   │   ├── ResilienceConfig.java     (Circuit breaker)
│   │   └── UseCaseConfig.java        (Wiring de use cases)
│   ├── client/
│   │   └── MatchServiceClientImpl.java   (REST client para Match Service)
│   ├── messaging/
│   │   ├── EventPublisherImpl.java       (Productor Kafka)
│   │   └── MatchStatusChangedListener.java  (Consumidor Kafka)
│   └── persistence/
│       ├── PredictionJpaRepository.java
│       ├── PredictionRepositoryImpl.java
│       └── PredictionQueryAdapter.java
│
└── api/                     # Capa de presentación
    ├── controller/
    │   └── PredictionController.java
    ├── dto/
    │   ├── CreatePredictionRequest.java
    │   ├── UpdatePredictionRequest.java
    │   └── PredictionResponse.java
    └── exception/
        └── GlobalExceptionHandler.java
```

---

## Dependencias del Proyecto (pom.xml)

Las siguientes dependencias fueron agregadas al `pom.xml` existente:

```xml
<!-- Kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<!-- Redis / Valkey -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Resilience4j Circuit Breaker -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.2.0</version>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- WebClient for REST calls -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

---

## Configuración (application.properties)

```properties
# Nombre del servicio para Eureka
spring.application.name=prediction-service
server.port=8082

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5433/prodeMaster_predication_db
spring.datasource.username=postgres
spring.datasource.password=admin

# Kafka Producer
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# Kafka Consumer
spring.kafka.consumer.group-id=prediction-service
spring.kafka.consumer.auto-offset-reset=earliest

# Topics
app.kafka.topic.prediction-events=prediction.events
app.kafka.topic.match-events=match.events

# Valkey (Redis)
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.cache.ttl=120

# Match Service URL
app.match-service.url=http://localhost:8083/api/v1/matches

# Resilience4j
resilience4j.circuitbreaker.configs.default.sliding-window-size=10

# Buffer antes del partido
app.prediction.buffer-minutes-before-match=5
```

---

## Endpoints API REST

| Método | Path | Descripción |
|--------|------|-------------|
| POST | `/api/v1/predictions` | Crear predicción |
| PUT | `/api/v1/predictions/{id}` | Modificar predicción |
| DELETE | `/api/v1/predictions/{id}` | Cancelar predicción (con reason) |
| GET | `/api/v1/predictions/{id}` | Obtener predicción por ID |
| GET | `/api/v1/predictions/match/{matchId}` | Listar predicciones por partido |
| GET | `/api/v1/predictions/user/{userId}?page=0&size=20` | Listar predicciones por usuario (paginado) |

### Request/Response Examples

**POST /api/v1/predictions**
```json
// Request (scoreline exacto)
{
    "userId": "user-123",
    "matchId": "match-456",
    "homeTeamGoals": 2,
    "awayTeamGoals": 1
}

// Response (201 Created)
{
    "id": "uuid-generated",
    "userId": "user-123",
    "matchId": "match-456",
    "homeTeamGoals": 2,
    "awayTeamGoals": 1,
    "outcome": "HOME_WIN",
    "status": "PENDING",
    "createdAt": "2026-04-11T10:00:00Z",
    "updatedAt": "2026-04-11T10:00:00Z"
}
```

**PUT /api/v1/predictions/{id}**
```json
// Request (actualizar scoreline)
{
    "homeTeamGoals": 3,
    "awayTeamGoals": 1
}

// Response
{
    "id": "uuid-generated",
    "userId": "user-123",
    "matchId": "match-456",
    "homeTeamGoals": 3,
    "awayTeamGoals": 1,
    "outcome": "HOME_WIN",
    "status": "PENDING",
    "createdAt": "2026-04-11T10:00:00Z",
    "updatedAt": "2026-04-11T10:05:00Z"
}
```

---

## Eventos Kafka

### Publish (Prediction Service → Score Service)

| Topic | Evento | Datos Clave |
|-------|--------|-------------|
| `prediction.events` | `PredictionCreatedEvent` | predictionId, userId, matchId, homeTeamGoals, awayTeamGoals, status |
| `prediction.events` | `PredictionUpdatedEvent` | predictionId, oldHomeTeamGoals, oldAwayTeamGoals, newHomeTeamGoals, newAwayTeamGoals |
| `prediction.events` | `PredictionVoidedEvent` | predictionId, userId, matchId, reason |

**Nota:** Los eventos usan scoreline exacto (`homeTeamGoals`, `awayTeamGoals`) en lugar del enum deprecated (`PredictionResult`).

### Consume (Match Service → Prediction Service)

| Topic | Evento | Acción |
|-------|--------|--------|
| `match.events` | `MatchStatusChangedEvent` | Recibe cambios de estado del partido y ejecuta: |

**Lógica del consumidor:**
- Si el partido inicia (`IN_PLAY_1ST_HALF`, `IN_PLAY_2ND_HALF`, `HT`, `FT`): Lockea todas las predicciones PENDING
- Si el partido se cancela/aplaza (`CANCELLED`, `POSTPONED`, `SUSPENDED`, `ABANDONED`): Voided todas las predicciones

---

## Integración con Match Service

El Prediction Service consume el endpoint `GET /api/v1/matches/{id}` del Match Service para:

1. **Validar que el partido existe** antes de crear una predicción
2. **Obtener el estado del partido** (PENDING, NS, SCHEDULED, TBA son válidos)
3. **Obtener la hora programada** para validar el cutoff time (5 minutos antes del inicio)

**Respuesta esperada del Match Service:**
```json
{
    "id": "match-123",
    "homeTeam": { "name": "Team A" },
    "awayTeam": { "name": "Team B" },
    "scheduledAt": "2026-04-15T20:00:00Z",
    "status": "SCHEDULED"
}
```

---

## Reglas de Negocio Implementadas

| # | Regla | Descripción |
|---|-------|-------------|
| 1 | **Scoreline exacto** | El usuario predice el resultado exacto (goles de cada equipo), no solo el outcome |
| 2 | **Validación de goals** | Los goles no pueden ser negativos ni superar 30 por equipo |
| 3 | **Unicidad** | Un usuario solo puede tener UNA predicción por partido |
| 4 | Cutoff time | No se puede predecir después de 5 minutos antes del inicio |
| 5 | Estados válidos | Solo se puede predecir en partidos PENDING, NS, SCHEDULED, TBA |
| 6 | Modificación | Solo predicciones en estado PENDING pueden modificarse |
| 7 | Lock automático | Cuando el partido inicia, las predicciones se lockean |
| 8 | Void automático | Si el partido se cancela/aplaza, las predicciones se voided |
| 9 | Optimistic locking | Uso del campo `version` para control de concurrencia |
| 10 | Outcome derivado | El outcome (HOME_WIN, DRAW, AWAY_WIN) se deriva automáticamente del MatchScore |

---

## Modelo de Dominio

### Value Objects

**MatchScore** - Representa el scoreline exacto predicho:
```java
MatchScore score = MatchScore.of(2, 1);  // Home 2 - Away 1
score.getHomeTeamGoals();  // 2
score.getAwayTeamGoals();  // 1
score.deriveOutcome();     // HOME_WIN
```

**Invariantes:**
- `homeTeamGoals >= 0`
- `awayTeamGoals >= 0`
- `homeTeamGoals <= 30`
- `awayTeamGoals <= 30`

**MatchOutcome** - Derivado del MatchScore:
- `HOME_WIN` - homeTeamGoals > awayTeamGoals
- `DRAW` - homeTeamGoals == awayTeamGoals
- `AWAY_WIN` - awayTeamGoals > homeTeamGoals

---

## Estados de Predicción

| Estado | Descripción |
|--------|-------------|
| `PENDING` | Predicción activa, puede modificarse |
| `LOCKED` | Partido iniciado, no se puede modificar |
| `VOIDED` | Predicción anulada (partido cancelado o usuario canceló) |

---

## Comandos de Verificación

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar el servicio
mvn spring-boot:run

# Verificar que compila sin errores (silencioso)
mvn clean compile -q
```

---

## Pasos Implementados (Completados)

1. ✅ Agregar dependencias al pom.xml (Kafka, Redis, Resilience4j, Lombok, Validation, WebFlux)
2. ✅ Crear estructura de paquetes hexagonal
3. ✅ Implementar Domain Layer (entidades, value objects, exceptions)
4. ✅ Implementar Puertos (PredictionCommandPort, PredictionQueryPort, PredictionRepository, MatchServiceClient, EventPublisher)
5. ✅ Implementar Casos de Uso (Create, Update, Cancel, GetById, GetByMatch, GetByUser)
6. ✅ Implementar Infraestructura (JPA Repository, REST Client, Kafka Producer/Consumer)
7. ✅ Configurar Kafka (application.properties, KafkaConfig)
8. ✅ Configurar Cache Valkey (RedisCacheConfig)
9. ✅ Configurar Circuit Breaker (ResilienceConfig)
10. ✅ Implementar API REST (PredictionController con 6 endpoints)
11. ✅ Implementar consumidor Kafka (MatchStatusChangedListener)
12. ✅ Implementar manejo de errores (GlobalExceptionHandler)
13. ✅ Validar compilación exitosa

---

## Pasos Pendientes (Por Completar)

### 1. Tests Unitarios

**Ubicación:** `src/test/java/com/ProdeMaster/PredictionService/`

**Archivos a crear:**

1. **Domain Tests**
   - `domain/model/PredictionTest.java` - Tests para métodos del aggregate
   - `domain/model/MatchScoreTest.java` - Tests para Value Object MatchScore

2. **UseCase Tests**
   - `application/usecase/CreatePredictionUseCaseTest.java`
   - `application/usecase/UpdatePredictionUseCaseTest.java`
   - `application/usecase/CancelPredictionUseCaseTest.java`

3. **Controller Tests**
   - `api/controller/PredictionControllerTest.java`

**Ejemplo de test para Prediction (scoreline exacto):**

```java
package com.ProdeMaster.PredictionService.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PredictionTest {

    @Test
    void shouldCreatePredictionWithPendingStatus() {
        MatchScore score = MatchScore.of(2, 1);
        Prediction p = Prediction.create("user1", "match1", score);
        
        assertNotNull(p.getId());
        assertEquals("user1", p.getUserId());
        assertEquals("match1", p.getMatchId());
        assertEquals(2, p.getPredictedScore().getHomeTeamGoals());
        assertEquals(1, p.getPredictedScore().getAwayTeamGoals());
        assertEquals(MatchOutcome.HOME_WIN, p.getDerivedOutcome());
        assertEquals(PredictionStatus.PENDING, p.getStatus());
    }

    @Test
    void shouldAllowModificationWhenPending() {
        Prediction p = Prediction.create("user1", "match1", MatchScore.of(2, 1));
        
        assertTrue(p.canModify());
        
        p.updatePredictedScore(MatchScore.of(3, 1));
        assertEquals(3, p.getPredictedScore().getHomeTeamGoals());
    }

    @Test
    void shouldNotAllowModificationWhenLocked() {
        Prediction p = Prediction.create("user1", "match1", MatchScore.of(2, 1));
        p.lock();
        
        assertFalse(p.canModify());
        assertThrows(Exception.class, () -> p.updatePredictedScore(MatchScore.of(3, 1)));
    }

    @Test
    void shouldDeriveOutcomeFromScore() {
        Prediction drawPrediction = Prediction.create("user1", "match1", MatchScore.of(1, 1));
        assertEquals(MatchOutcome.DRAW, drawPrediction.getDerivedOutcome());
        
        Prediction awayWinPrediction = Prediction.create("user2", "match2", MatchScore.of(0, 2));
        assertEquals(MatchOutcome.AWAY_WIN, awayWinPrediction.getDerivedOutcome());
    }
}
```

**Ejemplo de test para MatchScore:**

```java
package com.ProdeMaster.PredictionService.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatchScoreTest {

    @Test
    void shouldCreateValidScore() {
        MatchScore score = MatchScore.of(2, 1);
        assertEquals(2, score.getHomeTeamGoals());
        assertEquals(1, score.getAwayTeamGoals());
    }

    @Test
    void shouldDeriveHomeWinOutcome() {
        MatchScore score = MatchScore.of(3, 1);
        assertEquals(MatchOutcome.HOME_WIN, score.deriveOutcome());
    }

    @Test
    void shouldDeriveDrawOutcome() {
        MatchScore score = MatchScore.of(1, 1);
        assertEquals(MatchOutcome.DRAW, score.deriveOutcome());
    }

    @Test
    void shouldRejectNegativeGoals() {
        assertThrows(IllegalArgumentException.class, () -> MatchScore.of(-1, 1));
    }

    @Test
    void shouldRejectExcessiveGoals() {
        assertThrows(IllegalArgumentException.class, () -> MatchScore.of(31, 0));
    }
}
```

### 2. Integración JWT en Controller

**Agregar validación de JWT en PredictionController:**

El controller actualmente recibe `userId` en el request body. Para un sistema en producción:

1. Extraer `userId` del JWT en el header `Authorization`
2. Validar el token con User Service
3. Eliminar `userId` del request body en endpoints que no lo necesiten

```java
// Ejemplo de modificación
@PostMapping
public ResponseEntity<PredictionResponse> createPrediction(
        @RequestHeader("Authorization") String token,
        @Valid @RequestBody CreatePredictionRequest request) {
    String userId = extractUserIdFromToken(token); // Implementar
    // ...
}
```

### 3. Transactional Outbox (Opcional - Mejora)

Para garantizar entrega de eventos si Kafka falla:

1. **Crear entidad OutboxEvent** en `infrastructure/persistence/`
2. **Modificar repositorio** para guardar evento en la misma transacción que Prediction
3. **Crear scheduler** `OutboxPublisherScheduler.java` que publique eventos pendientes

---

## Resumen de Pendientes

| # | Tarea | Prioridad |
|---|-------|-----------|
| 1 | Tests unitarios para Prediction y MatchScore | Media |
| 2 | Tests unitarios para UseCases | Media |
| 3 | Tests de integración controller | Baja |
| 4 | Integración JWT en controller | Media |
| 5 | Transactional Outbox (opcional) | Baja |

---

## Notas Importantes del Diseño

### Cambio de Modelo: Resultado Simple → Scoreline Exacto

El diseño inicial usaba un enum simple (`HOME`, `DRAW`, `AWAY`) para las predicciones. **El diseño final evolucionó** para usar scoreline exacto:

| Aspecto | Anterior (Deprecated) | Nuevo |
|---------|----------------------|-------|
| Predicción | Enum: HOME, DRAW, AWAY | MatchScore: homeGoals, awayGoals |
| Ejemplo | "HOME" | "2-1" |
| Puntuación | Solo resultado | Exacto + resultado |
|DTO | `result: "HOME"` | `homeTeamGoals: 2, awayTeamGoals: 1` |

**Rationale:** En ProdeMaster, predecir el scoreline exacto otorga más puntos que solo predecir el winner/draw. El `MatchOutcome` se deriva automáticamente del `MatchScore`.

### Interface Segregation Principle (ISP)

Los puertos de entrada siguen ISP con interfaces pequeñas y enfocadas:
- `CreatePredictionInboundPort` - 1 método
- `UpdatePredictionInboundPort` - 1 método
- `CancelPredictionInboundPort` - 1 método

Esto permite implementar solo lo necesario y facilita el testing.

---

## Servicios Externos Requeridos

| Servicio | Puerto | URL |
|----------|--------|-----|
| Prediction Service | 8082 | localhost |
| Match Service | 8083 | localhost |
| PostgreSQL | 5433 | localhost |
| Kafka | 9092 | localhost |
| Valkey (Redis) | 6379 | localhost |
| Eureka | 8761 | localhost |
| Zipkin | 9411 | localhost |

---

*Esta guía fue generada para Prediction Service de ProdeMaster. Cualquier agente de IA puede utilizarla para completar la implementación o realizar mantenimiento del servicio.*