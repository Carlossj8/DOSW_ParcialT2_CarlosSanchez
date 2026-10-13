# DOSW_ParcialT2_CarlosSanchez

Repositorio con el proyecto base, arquitectura MVC completa, configuración de seguridad, persistencia MongoDB, contenedorización y pipeline CI/CD con GitHub Actions y Azure App Service para el Parcial Práctico del Segundo Tercio de Desarrollo Orientado a Servicios Web (DOSW).

---

## Índice
1. [Datos del estudiante](#1-datos-del-estudiante)
2. [Descripción del proyecto](#2-descripción-del-proyecto)
3. [Despliegue](#3-despliegue)
4. [Estructura del proyecto](#4-estructura-del-proyecto)
5. [Tecnologías y dependencias](#5-tecnologías-y-dependencias)
6. [Configuración y variables de entorno](#6-configuración-y-variables-de-entorno)
7. [Cómo ejecutar](#7-cómo-ejecutar)
8. [Pruebas](#8-pruebas)
9. [Docker](#9-docker)
10. [CI/CD](#10-cicd)
11. [Herramientas de modelado y diseño (evidencias)](#11-herramientas-de-modelado-y-diseño-evidencias)

---

## 1. Datos del estudiante

- **Nombre completo:** Carlos Andres Sanchez Jimenez y Juan Diego Gaitan
- **Asignatura:** Desarrollo Orientado a Servicios Web (DOSW)
- **Grupo:** Grupo 1
- **Institución:** Escuela Colombiana de Ingeniería Julio Garavito
- **Repositorio de mi compañero:** 
- **Profesor agregado como colaborador al repositorio:**

---

## 2. Descripción del proyecto

Servicio web backend modular basado en Spring Boot 3 y Java 21, diseñado para soportar la arquitectura de microservicios y APIs REST bajo el patrón Modelo-Vista-Controlador (MVC). Cuenta con integración a base de datos NoSQL MongoDB, seguridad stateless basada en tokens JWT con Spring Security, documentación viva con OpenAPI 3 / Swagger UI y empaquetamiento optimizado mediante contenedores Docker con compilación multietapa.

---

## 3. Despliegue

El servicio cuenta con un flujo de despliegue continuo automatizado hacia Azure App Service (Linux Containers):

- **URL base del servicio (Azure):** `https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net`
- **Endpoint de verificación de salud (Health Check):**  
  [`https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net/api/v1/health`](https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net/api/v1/health)
- **Documentación interactiva Swagger UI:**  
  [`https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net/swagger-ui/index.html`](https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net/swagger-ui/index.html)

---

## 4. Estructura del proyecto

El proyecto sigue una arquitectura multicapa MVC desacoplada, asegurando la separación de responsabilidades y la no exposición de modelos de persistencia a los controladores:

```
DOSW_ParcialT2_CarlosSanchez/
├── docs/
│   ├── uml/                      # Diagramas fuente de arquitectura y modelos (.drawio, etc.)
│   └── images/                   # Evidencias gráficas, capturas de pruebas y diseño
│
├── src/
│   ├── main/
│   │   ├── java/edu/dosw/parcial/
│   │   │   ├── controller/       # Controladores REST expuestos
│   │   │   │   ├── dtos/         # Objetos de transferencia de datos de entrada/salida
│   │   │   │   │   ├── request/  # DTOs de petición recibidos por la API
│   │   │   │   │   └── response/ # DTOs de respuesta retornados al cliente
│   │   │   │   ├── mappers/      # Mappers IN: Conversión RequestDTO ↔ Dominio y Dominio ↔ ResponseDTO
│   │   │   │   └── handlers/     # Manejador global de excepciones (GlobalExceptionHandler)
│   │   │   │
│   │   │   ├── core/             # Núcleo de lógica de negocio y dominio
│   │   │   │   ├── services/     # Interfaces e implementaciones de servicios de negocio
│   │   │   │   ├── models/       # Entidades de dominio puras independientes de persistencia
│   │   │   │   ├── validators/   # Validadores y reglas de negocio personalizadas
│   │   │   │   └── utils/        # Utilidades transversales de lógica de negocio
│   │   │   │
│   │   │   ├── persistence/      # Capa de acceso a datos y almacenamiento
│   │   │   │   ├── entities/     # Documentos de MongoDB mapeados con anotación @Document
│   │   │   │   ├── repositories/ # Interfaces que extienden MongoRepository
│   │   │   │   └── mappers/      # Mappers OUT: Conversión entre Modelo de Dominio ↔ Mongo Document
│   │   │   │
│   │   │   ├── config/           # Configuraciones del framework (SecurityConfig, SwaggerConfig)
│   │   │   └── main/             # Clase principal de arranque (@SpringBootApplication)
│   │   │
│   │   └── resources/
│   │       └── application.yaml  # Configuración unificada de Spring Boot, MongoDB y JWT
│   │
│   └── test/
│       ├── java/edu/dosw/parcial/
│       │   ├── controller/       # Pruebas unitarias de controladores web (MockMvc)
│       │   ├── core/             # Pruebas unitarias de servicios y lógica de negocio
│       │   └── persistence/      # Pruebas de integración/repositorios de persistencia
│       └── resources/
│           └── application.yaml  # Configuración aislada para ejecución de pruebas
│
├── Dockerfile                    # Definición de contenedor multi-stage (Build Maven + JRE 21 Alpine)
├── .dockerignore                 # Exclusión de archivos y carpetas locales en el build de Docker
├── .github/workflows/
│   └── ci-cd.yml                 # Pipeline automatizado de GitHub Actions (Test -> Docker -> Azure)
├── pom.xml                       # Descriptor de dependencias Maven, plugins y perfiles
├── .gitignore                    # Reglas de exclusión de Git (credenciales, artefactos compilados)
└── README.md                     # Documentación general y guía de ejecución
```

### Responsabilidad por paquete:
- `controller`: Expone los endpoints HTTP/REST y recibe las solicitudes externas.
- `controller.dtos`: Modela los contratos de datos que viajan hacia y desde los clientes REST.
- `controller.mappers`: Transforma DTOs a modelos de dominio y viceversa.
- `controller.handlers`: Captura excepciones y unifica las respuestas de error HTTP.
- `core.services`: Orquesta y ejecuta las reglas de negocio de la aplicación.
- `core.models`: Representa los conceptos centrales de la lógica de negocio.
- `core.validators`: Implementa validaciones semánticas de dominio.
- `persistence.entities`: Define la estructura física de las colecciones en MongoDB (`@Document`).
- `persistence.repositories`: Provee operaciones CRUD y consultas hacia MongoDB (`MongoRepository`).
- `persistence.mappers`: Traduce objetos de dominio a documentos MongoDB y viceversa.
- `config`: Establece las políticas de seguridad (rutas públicas/privadas) y documentación Swagger.
- `main`: Contiene la clase de inicialización con escaneo explícito de componentes.

---

## 5. Tecnologías y dependencias

| Tecnología / Dependencia | Versión | Propósito |
|---|---|---|
| **Java JDK** | 21 (LTS) | Lenguaje base de desarrollo de la plataforma |
| **Spring Boot** | 3.3.4 | Framework base para desarrollo rápido de microservicios |
| **Spring Boot Starter Web** | 3.3.4 | Construcción de APIs REST y servidor embebido Tomcat |
| **Spring Data MongoDB** | 3.3.4 | Persistencia NoSQL e interacción con MongoDB / Cosmos DB |
| **Spring Boot Starter Validation** | 3.3.4 | Validación declarativa de DTOs con anotaciones Bean Validation |
| **Spring Boot Starter Security** | 3.3.4 | Control de acceso, políticas de autenticación y autorización |
| **JJWT (jjwt-api, impl, jackson)** | 0.12.3 | Generación, firmado y validación de tokens JSON Web Tokens |
| **Springdoc OpenAPI UI** | 2.5.0 | Generación automática de especificación OpenAPI y Swagger UI |
| **Lombok** | 1.18.x (BOM) | Reducción de código repetitivo (getters, setters, builders, @Slf4j) |
| **MapStruct** | 1.5.5.Final | Generación de mappers de alto rendimiento en tiempo de compilación |
| **JUnit 5 & Mockito** | 3.3.4 (Test Starter) | Pruebas unitarias de componentes y simulación de dependencias |
| **Jacoco Maven Plugin** | 0.8.12 | Medición y reporte de cobertura de código en pruebas |
| **SonarQube Maven Plugin** | 3.10.0.2594 | Análisis estático de calidad de código y deuda técnica |

---

## 6. Configuración y variables de entorno

La aplicación lee su configuración de variables de entorno del sistema o del archivo `application.yaml`:

| Variable de Entorno | Descripción | Ejemplo de valor (referencial) |
|---|---|---|
| `MONGODB_URI` | Cadena de conexión a MongoDB local o Azure Cosmos DB | `mongodb://user:pass@host:10255/db?ssl=true` |
| `PORT` | Puerto HTTP en el que escucha la aplicación Spring Boot | `8080` |
| `JWT_SECRET` | Llave secreta en Base64 para firma HMAC-SHA de tokens (mínimo 44 caracteres) | `dGhpc2lzYWR1bW15c2VjcmV0Zm9ydGVzdGluZ3B1cnBvc2Vzb25seTEyMzQ1Njc4OTA=` |
| `WEBSITES_PORT` | Variable requerida por Azure App Service para redirigir tráfico al contenedor | `8080` |

> ⚠️ **Nota de seguridad:** Ninguna credencial productiva, clave de acceso o secreto se versiona en el repositorio. En entornos locales se usan valores de desarrollo por defecto y en despliegue se configuran mediante Azure Application Settings y GitHub Secrets.

---

## 7. Cómo ejecutar

### A. Ejecución local con Maven

1. Asegurar que Java 21 esté instalado y configurado en el sistema:
   ```bash
   java -version
   ```
2. Compilar y levantar la aplicación:
   ```bash
   mvn clean spring-boot:run
   ```
3. Verificar en el navegador o cliente REST:
   - Health: [http://localhost:8080/api/v1/health](http://localhost:8080/api/v1/health)
   - Swagger: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### B. Ejecución de MongoDB local con Docker

Para disponer de una base de datos MongoDB localmente sin instalación directa:
```bash
docker run -d --name mongo-parcial -p 27017:27017 mongo:7
```

### C. Ejecución local con Docker

1. Construir la imagen local:
   ```bash
   docker build -t dosw-parcial:local .
   ```
2. Correr el contenedor pasando las variables de entorno requeridas:
   ```bash
   docker run -d -p 8080:8080 -e JWT_SECRET=dGhpc2lzYWR1bW15c2VjcmV0Zm9ydGVzdGluZ3B1cnBvc2Vzb25seTEyMzQ1Njc4OTA= --name parcial-service dosw-parcial:local
   ```
3. Probar el endpoint:
   ```bash
   curl http://localhost:8080/api/v1/health
   ```

---

## 8. Pruebas

Para ejecutar las pruebas unitarias y generar el informe de cobertura de código con Jacoco:

```bash
mvn clean test
```

Para verificar todo el ciclo de validación (incluyendo empaquetado):
```bash
mvn clean verify
```

El informe interactivo de cobertura se genera en:
`target/site/jacoco/index.html`

### Evidencias de pruebas:
- **Resultado de pruebas en verde:** *(Colocar captura en `docs/images/test_results.png`)*  
  ![Pruebas en verde](docs/images/test_results.png)
- **Reporte de cobertura Jacoco:** *(Colocar captura en `docs/images/jacoco_report.png`)*  
  ![Reporte Jacoco](docs/images/jacoco_report.png)

---

## 9. Docker

El servicio implementa una construcción multietapa (*multi-stage build*) con Docker:
1. **Fase 1 (Builder):** Utiliza `maven:3.9-eclipse-temurin-21` para descargar dependencias y compilar el archivo `.jar`.
2. **Fase 2 (Runtime):** Utiliza `eclipse-temurin:21-jre-alpine` para obtener una imagen liviana, segura y optimizada para producción.

### Comandos de uso:
```bash
# Construir imagen
docker build -t dosw-parcial:local .

# Ejecutar contenedor
docker run -p 8080:8080 -e JWT_SECRET=dGhpc2lzYWR1bW15c2VjcmV0Zm9ydGVzdGluZ3B1cnBvc2Vzb25seTEyMzQ1Njc4OTA= dosw-parcial:local
```

### Evidencias de Docker:
- **Contenedor corriendo y respuesta 200 en health:** *(Colocar captura en `docs/images/docker_running.png`)*  
  ![Contenedor corriendo](docs/images/docker_running.png)

---

## 10. CI/CD

El pipeline de integración y despliegue continuo está definido en [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml) y se activa automáticamente con cada `push` a las ramas `main` y `develop`.

### Etapas del pipeline:
1. **test:** Realiza el checkout del código, prepara Java 21, descarga dependencias con caché de Maven y ejecuta `mvn verify`.
2. **docker:** Tras el éxito de los tests, inicia sesión en Docker Hub, compila la imagen y la publica con la etiqueta `:latest`.
3. **deploy:** Autentica con Azure mediante Service Principal y despliega el contenedor actualizado en Azure App Service.

### Secrets requeridos en GitHub Repository Settings:
- `DOCKERHUB_USERNAME`: Usuario de cuenta en Docker Hub.
- `DOCKERHUB_TOKEN`: Personal Access Token generado en Docker Hub (Read/Write).
- `AZURE_CREDENTIALS`: JSON del Service Principal generado con Azure CLI.
- `AZURE_WEBAPP_NAME`: Nombre del recurso App Service en Azure.
- `JWT_SECRET`: Llave secreta en Base64 utilizada para pruebas y ejecución.

### Evidencias del pipeline:
- **Workflow de GitHub Actions completado en verde:** *(Colocar captura en `docs/images/github_actions_green.png`)*  
  ![Pipeline en verde](docs/images/github_actions_green.png)

---

## 11. Herramientas de modelado y diseño (evidencias)

Se cuenta con acceso y perfiles configurados en las herramientas de diseño y modelado de software requeridas:

1. **Herramienta de modelado (Draw.io / Lucidchart / Miro):**  
   ![Evidencia Herramienta de Modelado](docs/images/evidence_modeling.png)

2. **Herramienta de diseño de interfaces (Figma):**  
   ![Evidencia Figma](docs/images/evidence_figma.png)



# Solucion Parcial

## Asignación del equipo de Producto - Definición del contrato de la API:
* Tabla de excel en docs/office


### ¿Cuál es la diferencia entre una validación de entrada y una validación de negocio? Seleccionen tres reglas del caso, incluyendo al menos una de cada tipo, y justifiquen su clasificación. 

La diferencia principal radica en qué comprueban:
#### Validación de entrada: Revisa la forma y estructura del dato (sintaxis). Por ejemplo, este dato es un número, un correo válido o texto sin potenciales caracteres maliciosos? Consume muy pocos recursos.

Reglas identificadas en el caso:
* Nombre nulo o vacío
* teléfono que no tenga 10 dígitos
* correo inválido (manejo de @gmail.com)
* Justificacion: Todos se pueden validar solamente con la entrada, sin necesidad de consultar reglas de negocio o el estado del sistema

#### Validación de negocio: Revisa el significado y las reglas de la operación. Requiere consultar el estado del sistema o la base de datos
* El barbero debe existir
* La fecha debe ser futura y caer en un día de disponibilidad del barbero
* Ningún barbero con la especialidad requerida está libre en esa franja
* Justificacion: Para validar todos se necesita consultar la base de datos, especificamente la tabla barberos

### En Pedir un turno, ¿cómo debe responder la API si fallan varias condiciones? Expliquen qué error se informa y qué cambios deben evitarse.
### Manejo de errores múltiples en "Pedir un turno"

**Comportamiento de la API:**
La cadena de validaciones se ejecuta en orden estricto (1 a 5) y el procesamiento se interrumpe de inmediato al detectar la primera inconsistencia.

**Error informado:**
Se retorna únicamente el motivo específico y el código de estado HTTP correspondiente a la primera falla encontrada en la secuencia (disponibilidad de franja, barbero disponible, datos de cliente o servicio permitido), sin evaluar ni concatenar los errores subsiguientes.

**Cambios que deben evitarse:**
* **No persistir la reserva:** No debe crearse ni guardarse ningún documento o registro de reserva en la base de datos
* **No alterar el estado del turno:** El turno debe mantenerse en estado DISPONIBLE y bajo ningún motivo transicionar a OCUPADO
* **Evitar cambios parciales:** Debe garantizarse la atomicidad de la transacción evitando cualquier cambio colateral en la disponibilidad de los barberos o el catálogo.

## Asignación del equipo de Arquitectura - Distribución de las responsabilidades:
Los diagramas estan en docs/uml

### Preguntas para la revisión con Arquitectura:


#### ¿Qué problemas aparecen cuando no se separan correctamente las capas? Presenten un ejemplo del caso y expliquen cómo distribuyen sus responsabilidades.


Cuando no se delimitan las capas, la lógica de transporte HTTP, las reglas de negocio y el acceso a la base de datos terminan mezclados en un solo punto. Esto genera alto acoplamiento, impide realizar pruebas unitarias aisladas y propicia modificaciones accidentales en el estado de los datos.

Por ejemplo, si el controlador realiza consultas directas a MongoDB y modifica el estado del turno a ocupado antes de comprobar que el correo termine en @gmail.com o que el teléfono tenga 10 dígitos, un error en los datos del cliente dejaría el turno bloqueado indebidamente en la base de datos.

Para resolverlo, las responsabilidades se distribuyen así:
* Controlador: gestiona peticiones HTTP, extrae la identidad del cliente desde el token y entrega la solicitud al servicio sin aplicar lógica de negocio.
* Servicio: orquesta el caso de uso, ejecuta la cadena de validaciones en orden estricto y coordina las entidades.
* Repositorio: maneja únicamente las consultas y la persistencia de datos.


#### ¿Qué diferencia existe entre un validador, una utilidad y un servicio? Ubiquen un ejemplo de cada uno en su diseño e indiquen su responsabilidad.

Un validador se enfoca de forma exclusiva en comprobar una condición específica y corta la ejecución si detecta un fallo, sin alterar datos ni coordinar transacciones. En este diseño, BarberoDisponibleValidator comprueba que el barbero esté libre en la franja y cuente con la especialidad requerida para los servicios solicitados.

Una utilidad es una clase sin estado que provee funciones técnicas reutilizables e independientes de las reglas de negocio. Por ejemplo, JwtUtil se encarga de verificar la firma del token y extraer los datos del usuario autenticado.

Un servicio es el componente central que orquesta la lógica del negocio. ReservaService se encarga de coordinar la secuencia de validaciones, asignar el barbero disponible si el cliente no indicó preferencia, cambiar el estado del turno a ocupado y guardar la reserva en la base de datos.


#### ¿Qué diferencia existe entre el Mapper IN y el Mapper OUT? Indiquen dónde se ubican y qué representaciones transforman cada uno.


Mapper IN se sitúa en la entrada, entre el controlador y la capa de servicio. Su propósito es convertir los datos del DTO recibido en la petición HTTP hacia los modelos o entidades de dominio, evitando que los cambios de formato del cliente afecten la estructura interna.

Mapper OUT se sitúa en la salida, entre el servicio y el repositorio. Su función es transformar la entidad guardada en la base de datos hacia un DTO de respuesta estructurado para el cliente, garantizando que no se expongan datos sensibles ni detalles internos de persistencia.


#### Retomen las tres reglas clasificadas en el paso anterior: ¿en qué componente debe ejecutarse cada una y por qué?

Regla de datos del cliente:
El nombre no puede ser vacío, el teléfono debe tener exactamente 10 dígitos numéricos y el correo debe tener dominio gmail.com. Debe procesarse en un validador de entrada en el controlador o DTO. Se ubica allí porque es una comprobación sintáctica que no requiere consultar el estado de la base de datos y debe rechazar solicitudes mal formadas antes de procesar el flujo.

Regla de disponibilidad de franja:
No pueden superarse 3 reservas en la misma fecha y hora, y debe existir un turno en estado disponible. Debe procesarse en un validador de negocio dentro de la capa de servicio. Se ubica allí porque necesita consultar las colecciones de turnos y reservas en MongoDB para validar en tiempo real.

Regla de especialidad del barbero:
El barbero debe contar con la especialidad necesaria para atender cada servicio solicitado. Debe procesarse en un validador de negocio en la capa de servicio. Se ubica allí porque requiere cruzar la lista de servicios del catálogo activo con las especialidades registradas del barbero en la base de datos.


## Asignación del Equipo de Base de datos - Diseño de la persistencia:


