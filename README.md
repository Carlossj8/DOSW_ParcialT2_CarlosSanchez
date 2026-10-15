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
12. [Implementación y evidencias](#implementación-y-evidencias)

---

## 1. Datos del estudiante

- **Nombre completo:** Carlos Andres Sanchez Jimenez y Juan Diego Gaitan
- **Asignatura:** DOSW
- **Grupo:** Grupo 1
- **Institución:** Escuela Colombiana de Ingeniería Julio Garavito
- **Repositorio de mi compañero:** https://github.com/mamagege/DOSW_Parcial_T2_JuanGaitan

---

## 2. Descripción del proyecto

Servicio web backend modular basado en Spring Boot 3 y Java 21, diseñado para soportar la arquitectura de microservicios y APIs REST bajo el patrón Modelo-Vista-Controlador (MVC). Cuenta con integración a base de datos NoSQL MongoDB, seguridad stateless basada en tokens JWT con Spring Security, documentación viva con Swagger UI y empaquetamiento optimizado mediante contenedores Docker.

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
| **JJWT (jjwt-api, impl, jackson)** | 0.12.3 | Generación, firmado y validación de tokens JSON Web Tokens |
| **Springdoc OpenAPI UI** | 2.5.0 | Generación automática de especificación OpenAPI y Swagger UI |
| **Lombok** | 1.18.x (BOM) | Reducción de código repetitivo (getters, setters, builders, @Slf4j) |
| **JUnit 5 & Mockito** | 3.3.4 (Test Starter) | Pruebas unitarias de componentes y simulación de dependencias |
| **Jacoco Maven Plugin** | 0.8.12 | Medición y reporte de cobertura de código en pruebas |

---

## 6. Configuración y variables de entorno

La aplicación lee su configuración de variables de entorno del sistema o del archivo `application.yaml`:

| Variable de Entorno | Descripción | Ejemplo de valor (referencial) |
|---|---|---|
| `MONGODB_URI` | Cadena de conexión a MongoDB local o Azure Cosmos DB | `mongodb://user:pass@host:10255/db?ssl=true` |
| `PORT` | Puerto HTTP en el que escucha la aplicación Spring Boot | `8080` |
| `JWT_SECRET` | Llave secreta en Base64 para firma HMAC-SHA de tokens (mínimo 44 caracteres) | `dGhpc2lzYWR1bW15c2VjcmV0Zm9ydGVzdGluZ3B1cnBvc2Vzb25seTEyMzQ1Njc4OTA=` |
| `WEBSITES_PORT` | Variable requerida por Azure App Service para redirigir tráfico al contenedor | `8080` |


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

# Implementación y evidencias

## 1. Instrucciones para ejecutar la aplicación y las pruebas

### Variables de entorno requeridas (sin valores sensibles):
| Variable de Entorno | Tipo | Descripción |
| :--- | :---: | :--- |
| `MONGODB_URI` | Obligatoria | Cadena de conexión para MongoDB (local o Azure Cosmos DB/Atlas) |
| `JWT_SECRET` | Obligatoria | Llave secreta en Base64 para la firma HMAC-SHA de los tokens JWT |
| `PORT` | Opcional | Puerto en el que escucha el servidor Spring Boot (por defecto `8080`) |
| `WEBSITES_PORT` | Opcional | Puerto requerido por Azure App Service para redirigir tráfico al contenedor |

### Ejecución local con Maven:
1. Asegurar tener una instancia de MongoDB activa en el puerto 27017 o configurar `MONGODB_URI`.
2. Compilar e iniciar la aplicación:
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```
3. Comprobar el servicio local:
   - Health Check: [http://localhost:8080/api/v1/health](http://localhost:8080/api/v1/health)
   - Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Bono: Ejecución orquestada con Docker Compose (API + MongoDB local):
Se incluye el archivo [`docker-compose.yml`](docker-compose.yml) para levantar todo el stack local con un solo comando:
```bash
# Iniciar MongoDB y la API en segundo plano
docker compose up -d

# Verificar estado de los contenedores
docker compose ps

# Ver logs en tiempo real
docker compose logs -f api

# Detener los servicios y liberar volúmenes
docker compose down -v
```

*(Colocar captura de Docker Compose corriendo en `docs/images/mvp_docker_compose.png`)*  
![Docker Compose corriendo](docs/images/mvp_docker_compose.png)

### Ejecución de pruebas automatizadas y cobertura:
```bash
# Ejecutar la suite completa de 58 pruebas unitarias y de integración
mvn test

# Ejecutar verificación completa con empaquetado y chequeo de cobertura (>80%)
mvn clean verify
```

---

## 2. Evidencias de las pruebas funcionales y sus logs asociados

La API cuenta con niveles de logging estructurados con `@Slf4j`:
- **`INFO`**: Registro de entrada a cada caso de uso, transiciones exitosas (creación de turnos, confirmación de reservas atómicas, emisión de tokens JWT).
- **`WARN`**: Registro detallado de rechazos de validación sintáctica (400) y reglas de negocio controladas (401 credenciales erróneas, 403 permisos insuficientes, 404 barbero o servicio inexistente, 409 conflicto por franja llena o turno duplicado, y 422 barberos sin especialidades requeridas o fechas inválidas).
- **`ERROR`**: Excepciones internas no controladas (500) capturadas en `GlobalExceptionHandler`.

### Resumen de flujos funcionales y logs:

| Caso de Uso | Método y Endpoint | Rol Requerido | Comportamiento y Logs Asociados |
| :--- | :---: | :---: | :--- |
| **CU 5: Iniciar Sesión** | `POST /api/v1/auth/login` | Público | `INFO`: Autenticación exitosa y emisión de token JWT.<br>`WARN`: Fallo 401 por usuario no existente o contraseña errónea. |
| **CU 1: Crear Turno** | `POST /api/v1/turnos` | ADMIN | `INFO`: Turno creado exitosamente con identificador `T01`.<br>`WARN`: Rechazo 422 por día/hora fuera de atención o fecha pasada; 409 por turno duplicado. |
| **CU 2: Pedir Reserva** | `POST /api/v1/reservas` | CLIENTE | `INFO`: Ejecución y aprobación de los 5 pasos de la cadena; turno a `OCUPADO` y reserva `CONFIRMADA`.<br>`WARN`: Parada inmediata y corte atómico ante rechazo en pasos 1 a 4. |
| **CU 3: Consultar mis reservas** | `GET /api/v1/turnos/{id}` | CLIENTE | `INFO`: Consulta de reservas para el cliente autenticado según su token JWT (orden descendente). |
| **CU 4: Consultar todas las reservas** | `GET /api/v1/turnos` | ADMIN | `INFO`: Consulta de la totalidad de reservas del sistema con cliente embebido, servicios y totales. |

*(Colocar captura de pruebas funcionales y logs en `docs/images/mvp_pruebas_funcionales_logs.png`)*  
![Evidencias de pruebas funcionales y logs](docs/images/mvp_pruebas_funcionales_logs.png)

---

## 3. Capturas y enlaces a los reportes de JaCoCo y análisis estático

- **Suite de pruebas:** 58 pruebas automáticas pasando con 0 errores y 0 fallos (`BUILD SUCCESS`).
- **Cobertura de código con JaCoCo:** **94%** de cobertura general de instrucciones, superando el límite mínimo del 80% configurado en la regla de Maven `check`.
- **Ubicación del reporte local interactivo:** `target/site/jacoco/index.html`.

*(Colocar captura del resultado de las 58 pruebas en `docs/images/mvp_test_results.png`)*  
![Resultado de pruebas en verde](docs/images/mvp_test_results.png)

*(Colocar captura del reporte de cobertura JaCoCo al 94% en `docs/images/mvp_jacoco_report.png`)*  
![Reporte de cobertura JaCoCo](docs/images/mvp_jacoco_report.png)

---

## 4. Enlace a ejecución exitosa del pipeline y evidencia desplegada en Azure

- **Enlace a la ejecución exitosa del pipeline en GitHub Actions:**  
  [https://github.com/Carlossj8/DOSW_ParcialT2_CarlosSanchez/actions/runs/38675280562](https://github.com/Carlossj8/DOSW_ParcialT2_CarlosSanchez/actions/runs/38675280562)

- **Fases del flujo continuo:**
  1. **Build y Pruebas:** Ejecuta `mvn verify`, validando las 58 pruebas unitarias y la regla de cobertura de JaCoCo.
  2. **Construcción y Push Docker:** Compila la imagen multi-stage y la publica en Docker Hub (`latest`).
  3. **Despliegue en Azure:** Conecta mediante Azure Credentials y actualiza la App Service automáticamente.

*(Colocar captura del workflow de GitHub Actions en verde en `docs/images/mvp_github_actions.png`)*  
![Pipeline de CI/CD en verde](docs/images/mvp_github_actions.png)

*(Colocar captura del servicio desplegado en Azure en `docs/images/mvp_azure_deployed.png`)*  
![Despliegue y funcionamiento en Azure](docs/images/mvp_azure_deployed.png)

---

## 5. Enlaces de acceso a la aplicación y documentación Swagger

- **URL base de la aplicación (Azure):**  
  [`https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net`](https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net)

- **Endpoint de verificación de salud (Health Check):**  
  [`https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net/api/v1/health`](https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net/api/v1/health)

- **Documentación interactiva Swagger UI con OpenAPI 3:**  
  [`https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net/swagger-ui/index.html`](https://dosw-parcial-t2-carlossanchez-c5fahsaphggpe6dv.chilecentral-01.azurewebsites.net/swagger-ui/index.html)

*(Colocar captura de la documentación Swagger UI interactiva con el botón Authorize en `docs/images/mvp_swagger_ui.png`)*  
![Documentación Swagger UI](docs/images/mvp_swagger_ui.png)
