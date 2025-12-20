# WIKI - Sprint 1: Fundación Hexagonal

---

## Objetivo

Establecer la **base arquitectónica hexagonal** con componentes compartidos que servirán como fundación para todos los bounded contexts del proyecto.


---

## Stack Tecnológico

- **Java 21** (LTS)
- **Spring Boot 3.2.x**
- **H2 Database** (desarrollo)
- **JWT** (io.jsonwebtoken 0.12.3)
- **JUnit 5** + Mockito
- **Maven**

---

## Roadmap del Sprint

### Día 1: Setup y Estructura Base
 Proyecto Spring Boot inicializado  
 Estructura hexagonal completa (shared/, user/, course/, enrollment/)  
 Configuración application.properties (H2, JWT, logging)  
 Git workflow establecido (develop + feature branches)

### Día 2: Shared Domain
 Jerarquía de excepciones (DomainException, NotFoundException, ValidationException)  
 Value Objects (Email con regex, Money con BigDecimal)  
 Validaciones inmutables en construcción

### Día 3: Shared Infrastructure
 GlobalExceptionHandler con ErrorResponse DTO  
 JwtUtil (generación y validación de tokens)  
 Dependencias JWT añadidas

### Día 4: Security Básico
 SecurityConfig temporal (permitAll)  
 PasswordEncoder (BCrypt strength 12)  
 Tests unitarios de JwtUtil (4 tests)  
 README.md profesional

---

## Arquitectura Implementada

### Principios Hexagonales

```
┌─────────────────────────────────────────────┐
│           INFRASTRUCTURE LAYER              │
│  (Adaptadores: REST, JPA, JWT, Security)    │
└────────────────┬────────────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────────────┐
│          APPLICATION LAYER                  │
│       (Use Cases - Sprint 2+)               │
└────────────────┬────────────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────────────┐
│            DOMAIN LAYER                     │
│  (Entidades, Value Objects, Excepciones)    │
│          Sin dependencias externas          │
└─────────────────────────────────────────────┘
```

**Flujo de dependencias**: Infrastructure → Application → Domain

**Nunca al revés**: El dominio no conoce Spring, JPA ni HTTP

---

## Componentes Creados

### 1. Shared Domain (`shared/domain/`)

#### Excepciones
```
DomainException (base)
    ├── NotFoundException
    └── ValidationException
```

**Decisión clave**: RuntimeException para no forzar try-catch, captura centralizada en GlobalExceptionHandler.

#### Value Objects

**Email**:
- Validación regex automática
- Normalización a lowercase
- Factory method `Email.of()`
- Inmutable (final, sin setters)

**Money**:
- BigDecimal (precisión decimal crítica)
- Validación de currency en operaciones
- Operaciones: `add()`, `multiply()`
- Inmutable

**¿Por qué classes y no primitivos?**
- Encapsulación de validaciones
- Type safety (compilador previene errores)
- Semántica clara

---

### 2. Shared Infrastructure (`shared/infrastructure/`)

#### GlobalExceptionHandler

**Propósito**: Traducir excepciones de dominio a respuestas HTTP

**Mapeo**:
- `DomainException` → 400 Bad Request
- `NotFoundException` → 404 Not Found
- `ValidationException` → 400 + lista de errores
- `Exception` (catch-all) → 500 Internal Server Error

**Diagrama de flujo**:

```
Use Case ejecuta lógica de negocio
         │
         ├─ Todo OK → Retorna resultado
         │
         └─ Error → Lanza DomainException
                    │
                    ↓
         GlobalExceptionHandler captura
                    │
                    ↓
         Crea ErrorResponse (DTO)
                    │
                    ↓
         ResponseEntity<ErrorResponse>
                    │
                    ↓
         Cliente recibe JSON con error
```

#### JwtUtil

**Responsabilidades**:
- Generar tokens JWT
- Validar tokens (firma + expiración + email)
- Extraer claims (email, role)

**Configuración**:
- Secret: String UTF-8 directo (no Base64 por problema con guiones)
- Expiración: 24h (86400000 ms)
- Algoritmo: HMAC-SHA256

**Decisión**: `validateToken(token, email)` valida 2 cosas → previene uso de token robado

#### SecurityConfig

**Estado actual**: Temporal (seguridad desactivada)

**Configuración**:
- `csrf.disable()` → API REST stateless
- `permitAll()` → Facilitar desarrollo Sprint 1-2

**Activación futura** (Sprint 3):
- JWT authentication filter
- Role-based authorization
- Endpoints protegidos por rol

#### PasswordEncoder

**BCrypt strength 12** = 4096 rounds

**Razón**: Balance entre seguridad y performance

---

## Estructura de Archivos

```
src/main/java/com/elearning/elearning_platform/
│
├── shared/
│   ├── domain/
│   │   ├── exception/
│   │   │   ├── DomainException.java
│   │   │   ├── NotFoundException.java
│   │   │   └── ValidationException.java
│   │   └── valueobject/
│   │       ├── Email.java
│   │       └── Money.java
│   └── infrastructure/
│       ├── config/
│       │   ├── ErrorResponse.java
│       │   └── GlobalExceptionHandler.java
│       └── security/
│           ├── JwtUtil.java
│           └── SecurityConfig.java
│
├── user/ (vacío - Sprint 2)
├── course/ (vacío - Sprint 4)
└── enrollment/ (vacío - Sprint 6)

src/test/java/
└── shared/infrastructure/security/
    └── JwtUtilTest.java
```

---

## Decisiones Arquitectónicas Clave

### 1. ¿Por qué Hexagonal?

**Ventajas**:
-  Dominio independiente de frameworks
-  Testabilidad (mocks en puertos)
-  Flexibilidad (cambiar BD sin tocar dominio)

**Trade-offs**:
- X Más archivos inicialmente
- X Curva de aprendizaje

---

### 2. Value Objects vs Primitivos

| Aspecto | Primitivo (String) | Value Object (Email) |
|---------|-------------------|---------------------|
| Validación | Repetida en múltiples lugares | Una vez en constructor |
| Type Safety | ❌ Puedes pasar cualquier String | ✅ Compilador previene errores |
| Semántica | ❌ `String email` poco expresivo | ✅ `Email email` claro |
| Inmutabilidad | ❌ String es inmutable pero no valida | ✅ Inmutable + validado |

---

### 3. RuntimeException vs Checked

**Decisión**: Todas las excepciones de dominio extienden `RuntimeException`

**Razones**:
- No forzar try-catch
- Captura centralizada en GlobalExceptionHandler
- Código más limpio
- Compatible con Spring

---

### 4. Records vs Classes

**Records** → DTOs (ErrorResponse)
- Inmutabilidad gratis
- equals/hashCode automáticos

**Classes** → Value Objects (Email, Money)
- Constructor privado necesario
- Factory method `of()` para validación
- Records no permiten constructor privado

---

### 5. Ports: ¿Dónde ubicarlos?

**Decisión tomada**:
- **Ports OUT** → `domain/port/out` (el dominio define qué necesita)
- **Ports IN** → `application/port/in` (casos de uso)

**Justificación**: El dominio es dueño del contrato de persistencia

---

## Testing Implementado

### JwtUtilTest (4 tests)

```
 testGenerateToken() - Token no null/blank
 testExtractRole() - Extrae rol correctamente
 testValidateToken_Valid() - Valida token correcto
 testValidToken_WrongEmail() - Rechaza email incorrecto
```

**Cobertura**: JwtUtil 100%

**Pendientes** (opcionales):
- EmailTest.java
- MoneyTest.java

---

## Git Workflow

### Estrategia

```
main (producción)
  ↓
develop (integración)
  ↓
  ├── feature/sprint1-setup
  ├── feature/shared-domain
  ├── feature/infrastructure-shared
  └── feature/security-config-basic
```

### Conventional Commits

```
feat(scope): descripción corta

- Cambio 1
- Cambio 2
```

**Tipos**: feat, fix, docs, test, refactor, chore

---

## Configuración H2

**JDBC URL**: `jdbc:h2:mem:elearning`  
**Username**: `sa`  
**Password**: (vacío)  
**Console**: http://localhost:8080/h2-console

**DDL**: `create-drop` (recrea schema en cada inicio)

---

## Métricas del Sprint

- **Archivos Java**: 12
- **Líneas de código**: ~800 (sin tests/JavaDoc)
- **Tests**: 5 métodos
- **Commits**: 8
- **Branches mergeadas**: 4

---

## Preparación para Sprint 2

### Infraestructura lista
- Excepciones base reutilizables  
- Value Objects compartidos  
- JWT listo para usar  
- GlobalExceptionHandler extensible

### Próximo Sprint: User Domain & Auth

**Día 1**: Domain Layer (User, Role, excepciones)  
**Día 2**: Application Layer (Use Cases, Commands)  
**Día 3**: Infrastructure Persistence (JPA entities, mappers)  
**Día 4**: Infrastructure Web (Controllers, DTOs)  
**Día 5**: Testing & Integration

---

## Comandos Útiles

### Maven
```bash
./mvnw clean compile          # Compilar
./mvnw test                   # Ejecutar tests
./mvnw spring-boot:run        # Arrancar aplicación
```

### Git
```bash
git checkout -b feature/nombre  # Nueva feature
git add .                       # Stage cambios
git commit -m "feat: ..."       # Commit
git push -u origin feature/...  # Push
git checkout develop            # Cambiar a develop
git merge feature/nombre        # Merge
```

---

## Verificación Sprint Completado

- [x] Proyecto compila sin errores
- [x] Tests pasan (5/5)
- [x] H2 Console accesible
- [x] JWT genera y valida tokens
- [x] GlobalExceptionHandler captura excepciones
- [x] README.md documentado
- [x] Git workflow establecido

---

**Sprint 1 completado exitosamente - Fundación sólida para desarrollo ágil de bounded contexts**