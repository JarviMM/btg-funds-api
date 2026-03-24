# BTG Pactual - API de Gestión de Fondos de Inversión

API REST desarrollada con Spring Boot que permite a los clientes gestionar sus fondos de inversión (suscripciones, cancelaciones y consulta de historial).

## Tecnologías

- Java 17
- Spring Boot 3.4.4
- Spring Security + JWT
- SQLite (base de datos embebida)
- JPA / Hibernate
- Gradle 8.7
- JUnit 5 + Mockito

> Se eligió SQLite para simplificar la ejecución local sin necesidad de instalar una base de datos externa. Para producción se recomienda migrar a DynamoDB o MongoDB.

## Arquitectura

Arquitectura en capas (Controller → Service → Repository):

```
com.btg.funds/
├── config/          → Inicialización de datos
├── controller/      → Endpoints REST
├── dto/             → Request/Response objects
├── entity/          → Entidades JPA
├── exception/       → Excepciones y handler global
├── notification/    → Notificaciones Email/SMS (Strategy + Factory)
├── repository/      → Repositorios Spring Data JPA
├── security/        → JWT, filtros y configuración de seguridad
└── service/         → Lógica de negocio
```

## Modelo de Datos

| Entidad      | Descripción                                      |
|--------------|--------------------------------------------------|
| Fund         | Fondos de inversión con monto mínimo y categoría |
| Client       | Clientes con saldo y preferencia de notificación |
| Subscription | Relación cliente-fondo (suscripciones activas)   |
| Transaction  | Historial de aperturas y cancelaciones           |
| User         | Usuarios del sistema para autenticación          |

## Seguridad

La API está protegida con JWT y perfilamiento por roles:

- Contraseñas encriptadas con BCrypt
- Tokens con expiración de 24 horas
- Endpoints públicos: `/api/v1/auth/**`
- El resto requiere header `Authorization: Bearer <token>`
- **Roles:** `ADMIN` y `USER`
  - `ADMIN`: acceso completo (incluye consulta de clientes)
  - `USER`: puede operar fondos, suscripciones y transacciones

**Usuarios de prueba:**
- `admin` / `admin123` (rol ADMIN)
- `user` / `user123` (rol USER)

## Endpoints

### Autenticación (públicos)

| Método | Endpoint                | Descripción       |
|--------|-------------------------|--------------------|
| POST   | `/api/v1/auth/login`    | Iniciar sesión     |
| POST   | `/api/v1/auth/register` | Registrar usuario  |

### Fondos y suscripciones (requieren token)

| Método | Endpoint                           | Descripción                    |
|--------|------------------------------------|--------------------------------|
| GET    | `/api/v1/funds`                    | Listar fondos disponibles      |
| POST   | `/api/v1/subscriptions`            | Suscribirse a un fondo         |
| DELETE | `/api/v1/subscriptions`            | Cancelar suscripción           |
| GET    | `/api/v1/subscriptions/{clientId}` | Suscripciones activas          |
| GET    | `/api/v1/transactions/{clientId}`  | Historial de transacciones     |
| GET    | `/api/v1/clients/{clientId}`       | Información del cliente (ADMIN)|

## Reglas de Negocio

- Saldo inicial del cliente: COP $500.000
- Cada fondo tiene un monto mínimo de vinculación
- Al cancelar una suscripción, el monto se devuelve al saldo del cliente
- No se permiten suscripciones duplicadas al mismo fondo
- Se envía notificación (Email o SMS) según la preferencia del cliente

## Fondos Disponibles

| ID | Nombre                        | Monto Mínimo  | Categoría |
|----|-------------------------------|----------------|-----------|
| 1  | FPV_BTG_PACTUAL_RECAUDADORA  | COP $75.000    | FPV       |
| 2  | FPV_BTG_PACTUAL_ECOPETROL    | COP $125.000   | FPV       |
| 3  | DEUDAPRIVADA                  | COP $50.000    | FIC       |
| 4  | FDO-ACCIONES                  | COP $250.000   | FIC       |
| 5  | FPV_BTG_PACTUAL_DINAMICA     | COP $100.000   | FPV       |

## Cómo Ejecutar

**Requisitos:** Java 17+

```bash
# Compilar y ejecutar
gradlew.bat bootRun        # Windows
./gradlew bootRun          # Linux/Mac

# Ejecutar pruebas
gradlew.bat test
```

La API se levanta en `http://localhost:8080`. La base de datos SQLite (`btg_funds.db`) se crea automáticamente con los datos de prueba.

## Ejemplos con cURL

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

**Suscribirse a un fondo:**
```bash
curl -X POST http://localhost:8080/api/v1/subscriptions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"clientId": 1, "fundId": 1}'
```

**Cancelar suscripción:**
```bash
curl -X DELETE http://localhost:8080/api/v1/subscriptions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"clientId": 1, "fundId": 1}'
```

**Consultar transacciones:**
```bash
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/v1/transactions/1
```

> También se incluye una colección de Postman en `postman/BTG_Funds_API.postman_collection.json` lista para importar.

## Despliegue en AWS

La API está desplegada en: **http://44.202.34.198:8080**

Se utilizó un template de CloudFormation (`infra/cloudformation.yml`) para aprovisionar la infraestructura:

```bash
aws cloudformation create-stack \
  --stack-name btg-funds-api \
  --template-body file://infra/cloudformation.yml \
  --parameters \
    ParameterKey=KeyPairName,ParameterValue=btg-funds-key
```

Recursos creados:
- EC2 (t3.micro) con Amazon Linux 2023 y Java 17
- Security Group (puertos 22 y 8080)

Para consultar el estado del stack:
```bash
aws cloudformation describe-stacks --stack-name btg-funds-api --query "Stacks[0].Outputs"
```

## Parte 2 - SQL

La consulta SQL solicitada se encuentra en `sql/parte2_consulta.sql`.
