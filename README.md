# hardwareguru-simulador

Microservicio core de **HardwareGuru** (Repositorio 1). Responsable de ejecutar la lógica matemática para calcular cuellos de botella entre CPU y GPU, estimar el consumo energético total en Watts y almacenar el historial de configuraciones (builds) de los usuarios. No interactúa con el frontend directamente; recibe peticiones exclusivas del API Gateway (BFF) mediante gRPC.

> Proyecto universitario · Curso de Microservicios · Repo 1 de 7

## Tecnologías

- Java 17
- Spring Boot 3.3 (o superior)
- gRPC (net.devh grpc-server-spring-boot-starter)
- PostgreSQL (Spring Data JPA + Hibernate)
- JUnit 5 + Mockito (Testing)
- Maven
- Docker

## Arquitectura y Comunicación

Este servicio opera en el "backend del backend" y **es completamente stateless para los cálculos**. 
- **Entrada:** Todas las peticiones ingresan vía **gRPC por el puerto 9090**. No expone endpoints HTTP/REST.
- **Autenticación:** Delega la seguridad en el API Gateway (BFF). Asume que las peticiones gRPC recibidas ya están autenticadas.
- **Base de Datos:** Posee una conexión exclusiva a una base de datos PostgreSQL lógica (`hardwareguru_sim`). Ningún otro microservicio tiene acceso a estas tablas.

### Contrato gRPC (`simulator.proto`)

El contrato define dos RPC principales que se implementan en `SimulatorServiceImpl`:

| RPC | Descripción |
|---|---|
| `SimularRendimiento` | Recibe los TDP y scores de CPU/GPU. Calcula el consumo total (con 20% de margen) y el cuello de botella según resolución (1080p/1440p). Devuelve un estado (`Balanceado`, `En riesgo`, `Incompatible`) sin guardar en BD. |
| `GuardarBuild` | Mapea la petición a la entidad `BuildGuardada` y persiste la configuración (IDs de componentes, userId y consumo) en PostgreSQL. Devuelve un `success` booleano y el UUID generado. |

## Lógica Matemática

El algoritmo de cálculo de rendimiento está encapsulado en el servicio:
- **Cuello de Botella:** Se basa en el ratio `cpuScore / gpuScore` ajustado por un factor de resolución (en 1440p la CPU penaliza menos).
- **Consumo Total:** `(CPU TDP + GPU TDP + 50W base) * 1.20`. El margen del 20% asegura que la fuente no opere al límite.

## Variables de Entorno y Configuración

La configuración reside en `src/main/resources/application.properties`. Puedes sobreescribir estos valores usando variables de entorno en producción:

```properties
server.port=8081                     # Puerto web interno de Spring
grpc.server.port=9090                # Puerto principal de escucha gRPC

spring.datasource.url=jdbc:postgresql://localhost:5432/hardwareguru_sim
spring.datasource.username=postgres
spring.datasource.password=adminpassword

spring.jpa.hibernate.ddl-auto=update # Genera/actualiza la tabla 'builds' automáticamente



Cómo ejecutarlo localmente
Requisitos: Java 17 (JDK), Maven y Docker Desktop.

1. Levantar la Base de Datos
El proyecto requiere un contenedor PostgreSQL con una base de datos llamada hardwareguru_sim. Utiliza el archivo docker-compose.yml provisto en la raíz:

Bash
docker compose up -d

2. Generar clases gRPC y Compilar
Antes de iniciar la aplicación, Maven debe compilar el archivo .proto y generar las clases base.

Bash
./mvnw clean compile
En Windows utiliza .\mvnw.cmd clean compile. Si da error de "No compiler provided", asegúrate de tener instalada una JDK(17) y configurada la variable JAVA_HOME.

3. Iniciar el Servidor
Levanta la aplicación Spring Boot:

Bash
./mvnw spring-boot:run

Verás en los logs que Hibernate crea la tabla builds y gRPC comienza a escuchar en el puerto 9090.



Pruebas (Testing)
El objetivo es alcanzar ≥ 60% de cobertura, excluyendo el código autogenerado por gRPC. Las pruebas se centran en la lógica matemática pura del servicio.

Bash
./mvnw test


Estructura

hardwareguru-simulador/
├── src/
│   ├── main/
│   │   ├── java/com/hardwareguru/simulator/
│   │   │   ├── SimulatorApplication.java       # Punto de entrada
│   │   │   ├── SimulatorServiceImpl.java       # Lógica gRPC y algoritmos
│   │   │   ├── BuildGuardada.java              # Entidad JPA
│   │   │   └── BuildRepository.java            # Interfaz JPA
│   │   ├── proto/
│   │   │   └── simulator.proto                 # Contrato gRPC
│   │   └── resources/
│   │       └── application.properties          # Configuración
├── docker-compose.yml                          # DB local
├── pom.xml                                     # Dependencias y plugins gRPC
└── README.md


Checklist de avance
[x] Inicializar Spring Boot 3 con JPA y PostgreSQL.

[x] Configurar plugins de compilación gRPC en pom.xml.

[x] Definir contrato simulator.proto.

[x] Implementar algoritmos de simulación (SimulatorServiceImpl).

[x] Configurar entidad BuildGuardada y repositorio.

[x] Levantar base de datos aislada vía Docker Compose.

[ ] Implementar Tests Unitarios (JUnit/Mockito) para superar 60% coverage.

[ ] Dockerfile.

[ ] CI/CD (GitHub Actions).