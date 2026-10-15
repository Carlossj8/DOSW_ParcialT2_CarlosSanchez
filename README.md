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
9. [Docker y Docker Compose (Bono)](#9-docker-y-docker-compose-bono)
10. [CI/CD](#10-cicd)
11. [Herramientas de modelado y diseño (evidencias)](#11-herramientas-de-modelado-y-diseño-evidencias)
12. [Implementación y evidencias](#12-implementación-y-evidencias)

---

## 1. Datos del estudiante

- **Nombre completo:** Carlos Andres Sanchez Jimenez y Juan Diego Gaitan
- **Asignatura:** Desarrollo Orientado a Servicios Web (DOSW)
- **Grupo:** Grupo 1
- **Institución:** Escuela Colombiana de Ingeniería Julio Garavito
- **Repositorio de mi compañero:** https://github.com/mamagege/DOSW_Parcial_T2_JuanGaitan
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

> **Nota de seguridad:** Ninguna credencial productiva, clave de acceso o secreto se versiona en el repositorio. En entornos locales se usan valores de desarrollo por defecto y en despliegue se configuran mediante Azure Application Settings y GitHub Secrets.

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

## 9. Docker y Docker Compose (Bono)

El servicio implementa una construcción multietapa (*multi-stage build*) con Docker y orquestación local completa con Docker Compose:

### 9.1. Dockerfile Multi-Stage
1. **Fase 1 (Builder):** Utiliza `maven:3.9-eclipse-temurin-21` para descargar dependencias y compilar el archivo `.jar`.
2. **Fase 2 (Runtime):** Utiliza `eclipse-temurin:21-jre-alpine` para obtener una imagen liviana, segura y optimizada para producción.

### 9.2. Orquestación local con Docker Compose (Bono)
El archivo [`docker-compose.yml`](docker-compose.yml) define dos servicios integrados sobre la red puente `bobs-barber-net`:
- **`mongo`:** Servidor MongoDB 7.0 oficial con volumen persistente `mongo-data` y *healthcheck* activo mediante `mongosh`.
- **`api`:** Servicio Spring Boot 3 compilado a partir del `Dockerfile`, configurado para iniciar únicamente tras verificar que MongoDB está en estado *healthy*.

#### Comandos de uso:
```bash
# Levantar MongoDB y la API en segundo plano
docker compose up -d

# Consultar estado de los contenedores
docker compose ps

# Ver logs en tiempo real de la API
docker compose logs -f api

# Detener los servicios y liberar recursos
docker compose down -v
```

### Evidencias de Docker y Docker Compose:
- **Contenedores corriendo y respuesta 200 en health check:**
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


---

# Solucion Parcial

## 1. Asignación del equipo de Producto - Definición del contrato de la API:
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

## 2. Asignación del equipo de Arquitectura - Distribución de las responsabilidades:
### Diagrama de componentes general
![Diagrama de componentes general ](docs/uml/componentesGeneral.png)
### Diagrama de componentes especifico
![Diagrama de componentes específico siguiendo el esquema de MVC. ](docs/uml/DiagramaEspecifico.png)
### Diagrama de clases
![Diagrama de clases: modelos de dominio ](docs/uml/DiagramadeClasesPatrones.png)

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


## 3. Asignación del Equipo de Base de datos - Diseño de la persistencia:


### Estrategia de Modelado
Para solucionar las necesidades de Bobs Barber se utiliza un modelo híbrido:

* Datos referenciados (Colecciones separadas):
  * Barbero se almacena en su propio documento/colección independiente y se enlaza al turno mediante una referencia.
  * Razón: El barbero tiene ciclo de vida propio; existe antes y después de los turnos, y mantiene sus horarios y especialidades fijas.
* Datos embebidos (Subdocumentos):
  * Servicio no cuenta con una colección separada en el agregado principal, sino que se embebe directamente dentro del documento Turno.
  * Razón: Guarda una "foto" del servicio solicitado (nombre y precio cobrado) al momento de apartar el turno, garantizando integridad ante futuros posibles cambios de tarifas.


### Análisis de Consultas, Actualizaciones y Crecimiento

* Consultas y lecturas rápidas: Consultar las reservas o turnos es directo; basta con buscar en la colección de reservas filtrando por el ID del cliente o la fecha, trayendo toda la información sin necesidad de cruzar tablas.
* Actualización segura: Cambiar el estado de un turno (de DISPONIBLE a OCUPADO) ocurre en un único documento, lo que garantiza que no se pisen reservas simultáneas.
* Duplicación controlada: Al embeber los datos del servicio se genera una duplicación controlada que evita que modificaciones futuras en las tarifas alteren registros pasados.
* Control de crecimiento: El tamaño del documento está delimitado. Cada reserva contiene únicamente los servicios de esa cita específica, evitando documentos pesados o lentos.


### Ventaja y Desventaja de la Elección

* Ventaja: Velocidad y simplicidad en las consultas. Al momento de consultar las reservas o turnos, la API obtiene todos los datos necesarios en una sola lectura, sin hacer combinaciones costosas de datos.
* Desventaja: Redundancia de datos. Si un dato maestro cambia (por ejemplo, el nombre de un barbero), los registros de turnos antiguos conservarán el nombre anterior a menos que se actualicen manualmente.

### Metamodelo conceptual
![Metamodelo conceptual](docs/uml/metamodelo.png)


## 4. Asignación del Equipo de Seguridad - Proteger el acceso y la información

### Preguntas:

#### Diferencia entre autenticación, autorización e integridad

* Autenticación: Es comprobar quién es la persona que entra al sistema. En la API se aplica en el inicio de sesión, donde el usuario envía su correo y contraseña, y el sistema comprueba sus credenciales para confirmar su identidad y darle un token.
* Autorización: Es determinar qué acciones tiene permiso de realizar un usuario ya identificado. En la API se aplica revisando el rol en cada llamada; por ejemplo, si un cliente intenta crear un turno, el sistema le niega la acción con un error 403 porque solo el administrador tiene ese permiso.
* Integridad: Es garantizar que la información no sea alterada ni manipulada en el camino o en la base de datos. En la API se aplica asegurando que los datos viajen protegidos y que el cálculo del precio y el estado del turno se guarden de forma exacta, sin que nadie pueda cambiar los valores de la reserva por fuera de las reglas.

#### Riesgos de OWASP Top 10 aplicables a la API

2. Riesgos de OWASP Top 10 aplicables a la barbería

Riesgo 1: Broken Access Control

* Escenario en la barbería: Un cliente autenticado intenta llamar al endpoint para ver todos los turnos realizados por otros clientes, o intenta enviar un identificador ajeno para acceder a reservas que no le pertenecen.
* Mitigación: Validar el rol del usuario en cada endpoint antes de procesar la petición y obtener la identidad del cliente directamente desde el token de sesión verificado, nunca desde parámetros enviados en la petición.
* Verificación: Ejecutar una prueba enviando una solicitud a la ruta de ver todos los turnos usando el token de un cliente y comprobar que la API responda con código HTTP 403 impidiendo el acceso.

Riesgo 2: Identification and Authentication Failures

* Escenario en la barbería: El inicio de sesión permite intentos infinitos sin bloqueo, o el sistema genera tokens que no expiran nunca, lo que facilita que un atacante adivine contraseñas predefinidas o use indefinidamente una sesión interceptada.
* Mitigación: Generar tokens de acceso firmados con un tiempo de vida limitado y exigir credenciales seguras que impidan el uso continuo de sesiones viejas o robadas.
* Verificación: Intentar consumir cualquier endpoint protegido usando un token con firma alterada o cuya fecha de expiración haya pasado, confirmando que la API retorne un código HTTP 401.


#### Tipo de autenticación más adecuado para el MVP

El esquema más apropiado es la autenticación basada en tokens mediante JWT.

Permite una arquitectura sin estado acorde con REST, evitando guardar sesiones en el servidor o consultar MongoDB en cada solicitud. El token firmado almacena el usuario y rol para validar permisos rápidamente en el filtro de seguridad.


## 5. Asignación del Equipo de Calidad - Definir cómo comprobar el comportamiento:

### Escenarios de prueba con TDD (Red, Green, Refactor)

Se aplica TDD escribiendo la prueba antes del código para garantizar que falle inicialmente (Red), implementando la solución mínima (Green) y optimizando el diseño (Refactor).

* Exitoso: Solicitud con turno libre, barbero disponible con la especialidad, datos de cliente válidos y servicios existentes. Comprueba respuesta HTTP 201 Created, turno en OCUPADO, reserva CONFIRMADA y cálculo correcto del total.
* Rechazos:
    * E01 - Franja llena: Cuarta reserva en la misma hora o turno no disponible.
    * E02 / E100 - Barbero no disponible o sin la especialidad requerida.
    * E03 / E07 / E08 - Cliente inválido: Nombre vacío, teléfono distinto a 10 dígitos o correo fuera de @gmail.com.
    * E09 - Servicio inexistente en el catálogo.

### Comprobación del orden estricto de la cadena

* Pruebas con fallas simultáneas: Se envía una petición con múltiples errores a la vez (como franja llena y correo inválido) y se valida que la API retorne únicamente el error de la primera regla en la secuencia.

### Comprobación de no creación de reserva ni modificación de turno

* Comprobación en mocks: Se verifica que no se ejecuten llamadas de persistencia para Reserva ni Turno ante un rechazo.
* Comprobación de estado: Se consulta el turno tras la prueba fallida para asegurar que permanezca en estado DISPONIBLE y que la colección de reservas no incremente su cantidad de registros.

## Preguntas para la revisión con el equipo de Calidad:

###  Aporte de las pruebas y alcance de las pruebas unitarias

* Aporte de evidencia:
  Las pruebas automatizadas actúan como una especificación ejecutable del contrato y de las reglas del negocio. Cada éxito demuestra que el sistema reacciona exactamente como exige el dominio ante entradas válidas y casos de error.

* Qué se verifica con pruebas unitarias:
  Aíslan componentes individuales para evaluar su lógica interna sin depender de bases de datos ni de la red:
    * Cumplimiento estricto del orden secuencial de la cadena de validaciones y parada inmediata.
    * Reglas de validación aisladas: formato de teléfono (10 dígitos), dominio @gmail.com, nombre no vacío y catálogo de servicios activos.
    * Verificación de llamadas atómicas: confirmar que ante una falla jamás se invoquen métodos para guardar reservas ni alterar turnos.

###  ¿Garantizan las pruebas por sí solas la ausencia de errores?

No. Las pruebas demuestran la presencia de defectos conocidos, pero no certifican su ausencia total. Una suite de pruebas unitarias puede tener una cobertura del 100% y aun así no detectar problemas de concurrencia en tiempo real (condiciones de carrera al reservar el último turno), fallas de conectividad con MongoDB, configuraciones incorrectas en Azure o comportamientos imprevistos en producción provocados por escenarios de borde que no fueron diseñados en los casos de prueba.

## Asignación del Equipo de Devops - Definir cómo comprobar el comportamiento:


###  Etapas principales de un pipeline CI/CD

* Compilación / Build: Compila el código fuente y descarga las dependencias para comprobar la integridad del proyecto.
* Test: Ejecuta pruebas unitarias e integración para validar que la lógica de negocio y los contratos no presenten regresiones.
* Análisis de calidad y seguridad: Evalúa métricas con herramientas como SonarQube para revisar cobertura de código, vulnerabilidades y deuda técnica.
* Despliegue / Deploy: Publica automáticamente el contenedor en el entorno de destino (QA o Producción en Azure App Service).

###  Comportamiento ante fallo de pruebas

* Acción del pipeline: Se detiene inmediatamente y marca la ejecución como fallida.
* ¿Debe permitirse el despliegue?: No.
* Justificación: Desplegar con pruebas rotas rompe el principio de entrega continua, introduciendo bugs o inconsistencias conocidas en los ambientes de prueba o producción.

###  Propósito de los logs e investigación de fallos

* Propósito: Registrar eventos del sistema en tiempo real para facilitar auditoría, monitoreo y diagnóstico de incidentes.
* Reserva fallida: Se registra el identificador, la fecha y hora del intento, el motivo exacto del rechazo según la cadena de validación y el estado previo del turno.
* Error de autenticación: Se registra la fecha y hora, la dirección IP de origen, el correo intentado y el motivo del fallo (credencial incorrecta o token expirado).

###  Información para rastrear y datos que no deberian registrarse

* Información útil: IDs de transacción, método y ruta HTTP consumida, identificador de usuario autenticado, códigos de respuesta y tiempos de ejecución.
* Datos que no deben registrarse: Contraseñas en texto plano, tokens JWT completos, números de tarjetas de crédito o información personal sensible de clientes.

###  Diferencia entre log interno y mensaje al cliente

* Log interno: Contiene detalles técnicos profundos, trazas completas de error (stacktraces), variables de contexto y estados de la base de datos para análisis del equipo de desarrollo.
* Mensaje al cliente: Es una respuesta simple, amigable y estructurada (HTTP 4xx/5xx con motivo puntual), diseñada para no exponer detalles de arquitectura interna o vulnerabilidades de seguridad.

![Diagrama de despliegue](docs/uml/diagramaDespliegue.png)

---

# 12. Implementación y evidencias

En esta sección se consolidan las instrucciones de ejecución, evidencias de pruebas funcionales con sus niveles de logging, reportes de calidad JaCoCo y SpotBugs, y el estado de integración continua y despliegue en Azure.

---

### 12.1. Bono: Docker Compose (API + MongoDB local)

Se implementó el archivo [`docker-compose.yml`](docker-compose.yml) en la raíz del proyecto para permitir el levantamiento completo del ecosistema local con un único comando:

* **Servicio `mongo`:** Imagen oficial `mongo:7.0`, puerto expuesto `27017:27017`, base de datos inicializada `dosw_parcial`, volumen de persistencia `mongo-data` y verificación de salud (*healthcheck*) periódica cada 5 segundos mediante comando `mongosh`.
* **Servicio `api`:** Construcción automatizada multi-stage con JDK 21 Temurin y Alpine JRE, puerto expuesto `8080:8080`, conectado a la red privada `bobs-barber-net` y dependiente (`depends_on`) del estado saludable del contenedor de MongoDB.

#### Instrucciones de uso:
```bash
# 1. Construir imágenes y levantar servicios en segundo plano
docker compose up -d

# 2. Verificar que MongoDB esté 'healthy' y la API 'Up'
docker compose ps

# 3. Consultar endpoint de verificación de salud
curl -i http://localhost:8080/api/v1/health

# 4. Inspeccionar trazas de ejecución en vivo
docker compose logs -f api

# 5. Detener el entorno limpiando volúmenes
docker compose down -v
```

#### Evidencia de verificación funcional de Docker Compose:
![Docker Compose corriendo](docs/images/docker_running.png)

---

### 12.2. Instrucciones para ejecutar la aplicación y las pruebas

#### Variables de entorno requeridas (sin valores sensibles):
| Variable de Entorno | Tipo | Propósito | Valor por Defecto Local |
| :--- | :---: | :--- | :--- |
| `MONGODB_URI` | Obligatoria | Cadena de conexión hacia la base de datos MongoDB | `mongodb://localhost:27017/dosw_parcial` |
| `JWT_SECRET` | Obligatoria | Clave simétrica Base64 (mín. 256 bits) para firma HMAC-SHA256 de tokens JWT | Dummy key para desarrollo/pruebas |
| `PORT` | Opcional | Puerto TCP en el que el servidor Spring Boot escucha peticiones HTTP | `8080` |
| `WEBSITES_PORT` | Opcional | Puerto utilizado por Azure App Service para enrutar tráfico al contenedor | `8080` |

#### Ejecución local con Maven:
```bash
# 1. Asegurar MongoDB corriendo localmente o vía docker compose
docker compose up -d mongo

# 2. Compilar y ejecutar la aplicación Spring Boot
mvn clean spring-boot:run
```

#### Ejecución de pruebas y validaciones de calidad:
```bash
# Ejecutar todas las 58 pruebas unitarias (JUnit 5 + Mockito en estructura AAA)
mvn test

# Generar reporte HTML de cobertura de código JaCoCo
mvn jacoco:report

# Ejecutar análisis estático de código con SpotBugs
mvn spotbugs:check

# Ejecución completa de Quality Gates (Tests + JaCoCo Check >= 80% + SpotBugs Check = 0 bugs)
mvn clean verify
```

---

### 12.3. Evidencias de pruebas funcionales y niveles de logging

La aplicación cuenta con niveles de logging configurados en `src/main/resources/application.yaml` (`edu.dosw.parcial: DEBUG`, `root: INFO`, `org.springframework.web: WARN`):

* **`DEBUG`:** Trazas detalladas de entrada a métodos de servicio y validadores intermedios.
* **`INFO`:** Peticiones HTTP recibidas, pasos superados en la cadena de responsabilidad, creación exitosa de turnos, confirmación atómica de reservas y expedición de tokens JWT.
* **`WARN`:** Rechazos controlados por reglas de negocio (errores 400, 401, 403, 404, 409 y 422) interceptados por `GlobalExceptionHandler`.
* **`ERROR`:** Errores internos no controlados (500) con traza de pila para auditoría técnica.

#### Casos de uso cubiertos y trazas de logging asociadas:

| Caso de Uso | Método y Endpoint | Rol | Escenario y Logs Asociados |
| :--- | :---: | :---: | :--- |
| **CU 5: Iniciar Sesión** | `POST /api/v1/auth/login` | Público | `INFO`: Autenticación exitosa de cliente `carlos.ruiz@gmail.com` con rol `CLIENTE`.<br>`WARN`: Intento de inicio de sesión fallido: contraseña incorrecta (401). |
| **CU 1: Crear Turno** | `POST /api/v1/turnos` | ADMIN | `INFO`: Turno creado exitosamente con ID `T01` para el barbero Miguel.<br>`WARN`: Rechazo 422: La hora 08:00 está fuera del horario de atención del barbero.<br>`WARN`: Rechazo 409: Ya existe un turno publicado para el barbero en esa fecha y hora. |
| **CU 2: Pedir Reserva** | `POST /api/v1/reservas` | CLIENTE | `INFO`: Paso 1 superado (1 turno disponible).<br>`INFO`: Paso 2 superado (Barbero cubre especialidades).<br>`INFO`: Paso 3 superado (Cliente validado).<br>`INFO`: Paso 4 superado (Servicios activos en catálogo).<br>`INFO`: Paso 5 superado (Asignación automática y cálculo de total).<br>`INFO`: Reserva `R06` guardada en estado `CONFIRMADA` y turno marcado como `OCUPADO`.<br>`WARN`: Rechazo en Paso 1: Franja horaria con 3 reservas activas (409 Conflict). |
| **CU 3: Consultar mis reservas** | `GET /api/v1/reservas/mis-reservas` | CLIENTE | `INFO`: Consultando reservas del cliente `carlos.ruiz@gmail.com` (identidad extraída de JWT).<br>`DEBUG`: Retornando lista ordenada de reservas en orden descendente (`fecha DESC, hora DESC`). |
| **CU 4: Consultar todas las reservas** | `GET /api/v1/reservas` | ADMIN | `INFO`: Consultando todas las reservas del sistema (ADMIN).<br>`DEBUG`: Retornando consolidado de reservas ordenadas descendentemente. |

---

### 12.4. Reportes de cobertura JaCoCo y análisis estático

#### Cobertura JaCoCo (> 80% requerida):
El análisis arrojó un **94% de cobertura de instrucciones** (2,126 de 2,250 instrucciones cubiertas, 0 clases perdidas):
* `edu.dosw.parcial.controller.handlers`: **100%**
* `edu.dosw.parcial.core.utils`: **100%**
* `edu.dosw.parcial.core.services`: **99%**
* `edu.dosw.parcial.controller`: **98%**
* `edu.dosw.parcial.core.validators`: **94%**
* `edu.dosw.parcial.core.validators.chain`: **86%**

![Reporte de Cobertura JaCoCo](docs/images/jacoco_report.png)

#### Análisis estático de código (SpotBugs & Compilador Java 21):
* **0 bugs, 0 errores, 0 hallazgos de severidad.**
* Todas las 58 pruebas unitarias ejecutadas satisfactoriamente bajo la convención Arrange, Act, Assert (AAA).

![Resultado de pruebas y análisis estático](docs/images/test_results.png)

---

### 12.5. Ejecución del pipeline y despliegue en Azure

El pipeline de GitHub Actions se ejecuta automáticamente ante cada integración en la rama `develop`:

* **Enlace a la ejecución exitosa del pipeline:**  
  [https://github.com/Carlossj8/DOSW_ParcialT2_CarlosSanchez/actions/runs/38359247193](https://github.com/Carlossj8/DOSW_ParcialT2_CarlosSanchez/actions/runs/38359247193)

* **Etapas ejecutadas en orden:**
  1. `Build y Pruebas` (Maven verify + Tests unitarios + JaCoCo Check >= 80% + SpotBugs Check) — *Passed (20s)*
  2. `Construccion y Push Docker` (Build de imagen multi-etapa y publicacion en Docker Hub) — *Passed (1m 12s)*
  3. `Despliegue en Azure App Service` (Login con Service Principal y actualizacion de contenedor en Azure) — *Passed (17s)*

![Pipeline de GitHub Actions en verde](docs/images/github_actions_green.png)

---

### 12.6. Enlaces de acceso a la aplicación y Swagger UI

* **URL base del servicio en Azure:**  
  [`https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net`](https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net)

* **Endpoint de Verificación de Salud (Health Check - 200 OK):**  
  [`https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net/api/v1/health`](https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net/api/v1/health)

* **Documentación interactiva Swagger UI (OpenAPI 3 con botón Authorize JWT):**  
  [`https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net/swagger-ui/index.html`](https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net/swagger-ui/index.html)

