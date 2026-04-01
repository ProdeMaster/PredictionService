# 🎯 Prediction Service

Prediction Service es un microservicio perteneciente al ecosistema **ProdeMaster**, una plataforma de predicciones deportivas entre amigos inspirada en el tradicional juego argentino Prode.

Este servicio es responsable de gestionar las predicciones realizadas por los usuarios sobre partidos de fútbol, actuando como la fuente de verdad para todas las jugadas registradas en el sistema.

El servicio no calcula puntajes.
El cálculo de puntos y rankings corresponde al Score Service.

---

## 🚀 ¿Qué hace este servicio?

Prediction Service expone una API REST que permite a los usuarios:

* Registrar predicciones de resultados para partidos
* Actualizar predicciones existentes antes de que comience el partido
* Consultar predicciones realizadas
* Relacionar predicciones con usuarios y partidos

Además, el servicio participa en una arquitectura event-driven, publicando eventos cuando se crean o modifican predicciones.

---

## 📆 Endpoints destacados

| Método | Ruta                            | Descripción              |
|--------|---------------------------------|--------------------------|
| POST   | /predictions/                   | Registra una predicción  |
| PUT    | /predictions/{userId}/{matchId} | Actualiza una predicción |
| GET    | /predictions/{id}               | devuelve la predicción   |

## 🛠️ Tecnologías

- Java 17
- Spring Boot 3.4.x
- Spring Security (con JWT)
- Spring Data JPA
- PostgreSQL
- Spring Cloud Netflix Eureka Client
- Docker & Docker Compose

---

## 📁 Estructura del proyecto

```plaintext
user-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ProdeMaster/PredictionService/
│   │   │       ├── Controller/
│   │   │       │   └── PredictionController.java
│   │   │       ├── Model/
│   │   │       │   └── PredictionModel.java       
│   │   │       ├── Repository/
│   │   │       │   └── PredictionRepository.java
│   │   │       ├── Service/
│   │   │       │   └── PredictionService.java
│   │   │       └── predictionServiceApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│   └── test/java/com/ProdeMaster/PredictionService/
│       └── UserModelServicesApplicationTests.java
├── Dockerfile
├── pom.xml
└── readme.md
```

---

## ⚙️ Configuración

El archivo `application.properties` contiene la configuración principal del servicio de predicciones, incluyendo puertos, nombre del servicio y detalles de conexión a Eureka.

### application.properties
```plaintext
spring.application.name=prediction-service
server.port=8082

#Configuracion general
spring.config.import=optional:configserver:

#Conectividad con zipkin para manejo de trazabilidad
spring.zipkin.base-url=http://zipkin:9411
management.zipkin.tracing.endpoint=http://zipkin:9411/api/v2/spans
management.tracing.sampling.probability=1.0

#Configuracion de Eureka
eureka.client.serviceUrl.defaultZone=http://eureka:8761/eureka/
eureka.instance.prefer-ip-address=true

#Configuracion de la base de datos PostgreSQL
spring.datasource.url=jdbc:postgresql://postgres:5433/prodeMaster_predication_db
spring.datasource.username=postgres
spring.datasource.password=admin
spring.datasource.driver-class-name=org.postgresql.Driver
```
> ⚠️ **IMPORTANTE**: Las credenciales de Postgres son valores de ejemplo proporcionados para facilitar el despliegue y pruebas en local. Bajo ningún concepto se recomienda conservar estos valores si desea llevar el servicio a producción

---

## 🧪 Cómo probarlo en local
### ✅ Requisitos
- JDK 17
- Maven
- Docker

#### Opción 1: sin Docker
```bash
mvn clean install
mvn spring-boot:run
```

> 💡 **NOTA:** Es necesario disponer de un servicio de Postgres, con las credenciales anteriormente mencionadas, para que el servicio se conecte.
>
> Es necesario modificar la propiedad `spring.datasource.url` configurándolo de la siguiente manera `jdbc:postgresql://localhost:5433/prode_db`


#### Opción 2: con Docker
```bash
mvn clean package
docker image build -t prediction-service .
docker run -p 5433:5432 --name my-postgres -e POSTGRES_PASSWORD=admin -e POSTGRES_USER=postgres -e POSTGRES_DB=prodeMaster_predication_db postgres:17
docker run prediction-service
```

---

## 📦 Docker

### Dockerfile

El servicio cuenta con un Dockerfile optimizado para producción basado en una imagen Java 17.

#### Build manual de la imagen
```bash
mvn clean package
docker image build -t prediction-service .
```
> Es necesario empaquetar la aplicación con `mvn clean package` antes de crear la imagen de docker

---

## 🧩 Integración con otros servicios

📂 Integración con otros servicios

Este servicio se registra en Eureka bajo el nombre prediction-service. Puede ser accedido a través del ApiGateway usando:

```bash
http://localhost:8080/prediction-service/
```

Asegúrate de que `spring.application.name=prediction-service` coincida con la ruta configurada en ApiGateway.

---

## 📚 Documentación adicional
* [Spring Boot](https://docs.spring.io/spring-boot/index.html)
* [Spring cloud](https://docs.spring.io/spring-cloud/docs/current/reference/html/)
* [Ciclo de vida de las aplicaciones con Maven](https://keepcoding.io/blog/que-es-maven-lifecycle-y-sus-fases/)

## 🧑‍💻 Autor
> Nombre: Gastón Herrlein
>
> GitHub: @Gaston-Herrlein

## 📄 Licencia
Sin licencia