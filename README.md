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

- **Nombre completo:** Carlos Andres Sanchez Jimenez
- **Asignatura:** Desarrollo Orientado a Servicios Web (DOSW)
- **Grupo:** Grupo 1
- **Institución:** Escuela Colombiana de Ingeniería Julio Garavito
- **Profesor agregado como colaborador al repositorio:** ✔ Confirmado

---

## 2. Descripción del proyecto

Servicio web backend modular basado en Spring Boot 3 y Java 21, diseñado para soportar la arquitectura de microservicios y APIs REST bajo el patrón Modelo-Vista-Controlador (MVC). Cuenta con integración a base de datos NoSQL MongoDB, seguridad stateless basada en tokens JWT con Spring Security, documentación viva con OpenAPI 3 / Swagger UI y empaquetamiento optimizado mediante contenedores Docker con compilación multietapa.

---

## 3. Despliegue

El servicio cuenta con un flujo de despliegue continuo automatizado hacia Azure App Service (Linux Containers):

- **URL base del servicio (Azure):** `https://dosw-parcial-t2-carlossanchez.azurewebsites.net` *(reemplazar por el nombre del App Service asignado)*
- **Endpoint de verificación de salud (Health Check):**  
  [`https://dosw-parcial-t2-carlossanchez.azurewebsites.net/api/v1/health`](https://dosw-parcial-t2-carlossanchez.azurewebsites.net/api/v1/health)
- **Documentación interactiva Swagger UI:**  
  [`https://dosw-parcial-t2-carlossanchez.azurewebsites.net/swagger-ui/index.html`](https://dosw-parcial-t2-carlossanchez.azurewebsites.net/swagger-ui/index.html)

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
