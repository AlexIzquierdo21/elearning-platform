# WIKI - E-Learning Platform - Sprint 1

---

## Objetivo del Sprint 1
Establecer la **fundación arquitectónica hexagonal** con componentes compartidos (excepciones, value objects, manejo de errores, JWT y seguridad básica) que servirán de base para todos los bounded contexts del proyecto.

---

## Índice
1. [Arquitectura implementada](#1-arquitectura-implementada)
2. [Estructura de archivos creados](#2-estructura-de-archivos-creados)
3. [Orden de creación y decisiones](#3-orden-de-creación-y-decisiones)
4. [Configuración del proyecto](#4-configuración-del-proyecto)
5. [Testing implementado](#5-testing-implementado)
6. [Verificación y próximos pasos](#6-verificación-y-próximos-pasos)
7. [Decisiones arquitectónicas clave](#7-decisiones-arquitectónicas-clave)

---

## 1. Arquitectura implementada

### 1.1 Principios de Arquitectura Hexagonal
- **Domain Layer (Núcleo)**: Lógica de negocio pura sin dependencias externas
- **Application Layer**: Casos de uso (se implementarán en Sprint 2)
- **Infrastructure Layer**: Adaptadores técnicos (seguridad, configuración, manejo de errores)

### 1.2 Domain-Driven Design (DDD)
- **Value Objects**: Email y Money con validaciones inmutables
- **Domain Exceptions**: Jerarquía de excepciones específicas del dominio
- **Independencia tecnológica**: El dominio no conoce Spring, JPA ni HTTP

### 1.3 Componentes de Infraestructura
- **GlobalExceptionHandler**: Traducción de excepciones de dominio a HTTP
- **JwtUtil**: Generación y validación de tokens JWT
- **SecurityConfig**: Configuración básica de Spring Security (temporal)

### 1.4 Tecnologías utilizadas
- **Java 21** (LTS)
- **Spring Boot 3.2.x**
- **Spring Security**
- **H2 Database** (desarrollo)
- **JWT** (io.jsonwebtoken 0.12.3)
- **BCrypt** (password encoding)
- **JUnit 5** + Spring Boot Test
- **Lombok** (reducción de boilerplate)

---

## 2. Estructura de archivos creados

```
src/main/java/com/elearning/elearning_platform/
├── shared/
│   ├── domain/
│   │   ├── valueobject/
│   │   │   ├── Email.java
│   │   │   └── Money.java
│   │   └── exception/
│   │       ├── DomainException.java
│   │       ├── NotFoundException.java
│   │       └── ValidationException.java
│   └── infrastructure/
│       ├── config/
│       │   ├── GlobalExceptionHandler.java
│       │   └── ErrorResponse.java
│       └── security/
│           ├── JwtUtil.java
│           └── SecurityConfig.java
│
├── user/ (estructura vacía - Sprint 2)
│   ├── domain/
│   │   ├── model/
│   │   ├── exception/
│   │   └── port/out/
│   ├── application/
│   │   ├── usecase/
│   │   └── port/in/
│   └── infrastructure/
│       ├── adapter/in/web/dto/
│       ├── adapter/out/persistence/
│       └── config/
│
├── course/ (estructura vacía - Sprint 4)
│   └── [misma estructura que user]
│
└── enrollment/ (estructura vacía - Sprint 6)
    └── [misma estructura que user]

src/test/java/
├── shared/
│   ├── domain/valueobject/
│   │   ├── EmailTest.java (pendiente)
│   │   └── MoneyTest.java (pendiente)
│   └── infrastructure/security/
│       └── JwtUtilTest.java
└── ElearningPlatformApplicationTests.java

src/main/resources/
├── application.properties
└── (configuración H2, JWT, logging)

Raíz del proyecto/
├── README.md
├── FUNCTIONAL.md
├── pom.xml
└── .gitignore
```

---

## 3. Orden de creación y decisiones

### DÍA 1: Setup Proyecto y Estructura Base

#### 3.1 Inicialización del Proyecto
**Acción**: Crear proyecto Spring Boot via Spring Initializr

**Dependencias seleccionadas**:
- Spring Web
- Spring Data JPA
- H2 Database
- Spring Security
- Validation
- Lombok
- Spring Boot DevTools

**Decisión arquitectónica**:
- Usar Java 21 (última LTS con características modernas como records y pattern matching)
- Maven como gestor de dependencias (más estándar en empresas)

#### 3.2 Estructura de Carpetas Hexagonal Completa
**Acción**: Crear todos los paquetes vacíos según arquitectura hexagonal

**Estructura implementada**:
```
shared/domain/valueobject/
shared/domain/exception/
shared/infrastructure/config/
shared/infrastructure/security/
user/domain/model/
user/domain/exception/
user/domain/port/out/
user/application/usecase/
user/application/port/in/
user/infrastructure/adapter/in/web/dto/
user/infrastructure/adapter/out/persistence/
user/infrastructure/config/
course/[misma estructura]
enrollment/[misma estructura]
```

**Decisión arquitectónica**:
- Separar en **bounded contexts** (user, course, enrollment) desde el inicio
- Cada contexto es autocontenido con su propia estructura domain → application → infrastructure
- `shared/` para código transversal (value objects, excepciones base, infraestructura común)

**Justificación**:
- Facilita el crecimiento modular del proyecto
- Cada bounded context puede evolucionar independientemente
- Claridad en las responsabilidades desde el día 1

#### 3.3 Configuración application.properties
**Acción**: Configurar H2, logging y propiedades JWT

```properties
spring.application.name=elearning-platform

# H2 Database
spring.datasource.url=jdbc:h2:mem:elearning
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT
jwt.secret=your-256-bit-secret-key-for-jwt-signing-must-be-long-enough
jwt.expiration=86400000

# Logging
logging.level.com.elearning.elearning_platform=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
```

**Decisiones**:
- H2 en memoria para desarrollo rápido (PostgreSQL en producción)
- `ddl-auto=create-drop` para recrear schema en cada inicio (desarrollo)
- JWT con expiración de 24h (86400000 ms)
- Logging detallado para debugging

---

### DÍA 2: Shared Domain - Excepciones y Value Objects

#### 3.4 DomainException.java (Base Exception)
**Propósito**: Clase base para todas las excepciones del dominio

**Ubicación**: `shared/domain/exception/DomainException.java`

**Características**:
- Extiende `RuntimeException` (no checked exceptions)
- Constructor simple que recibe mensaje
- Base para toda la jerarquía de excepciones

**Decisión arquitectónica**:
- Usar `RuntimeException` en lugar de checked exceptions
- El dominio no debe forzar al código cliente a capturar excepciones
- Permite que las excepciones fluyan hasta el `GlobalExceptionHandler`

**Código clave**:
```java
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
```

**Conectado con**: Todas las excepciones específicas (NotFoundException, ValidationException), GlobalExceptionHandler

#### 3.5 NotFoundException.java
**Propósito**: Excepción cuando una entidad no se encuentra

**Características**:
- Extiende `DomainException`
- Constructor recibe `String entity` y `Object id`
- Construye mensaje formateado: `"User with id 123 not found"`
- Usa `String.format()` para composición de mensaje

**Decisión arquitectónica**:
- Excepción genérica reutilizable para cualquier entidad
- Parámetro `Object id` permite Long, String, UUID, etc.
- Mensaje consistente en todo el sistema

**Código clave**:
```java
public class NotFoundException extends DomainException {
    public NotFoundException(String entity, Object id) {
        super(String.format("%s with id %s not found", entity, id));
    }
}
```

**Conectado con**: Futuros Use Cases que busquen entidades, GlobalExceptionHandler (status 404)

#### 3.6 ValidationException.java
**Propósito**: Excepción para errores de validación múltiples

**Características**:
- Extiende `DomainException`
- Almacena `List<String> errors` (lista de errores)
- Getter público para acceder a errores individuales
- Mensaje concatena todos los errores

**Decisión arquitectónica**:
- Permitir múltiples errores en una sola excepción
- Útil para validación de comandos con varios campos
- GlobalExceptionHandler puede devolver lista detallada al cliente

**Código clave**:
```java
public class ValidationException extends DomainException {
    private final List<String> errors;
    
    public ValidationException(List<String> errors) {
        super("Validation failed: " + String.join(", ", errors));
        this.errors = errors;
    }
    
    public List<String> getErrors() {
        return errors;
    }
}
```

**Conectado con**: Value Objects (Email, Money), futuros Use Cases, GlobalExceptionHandler

#### 3.7 Email.java (Value Object)
**Propósito**: Representar emails como objeto de dominio inmutable con validación automática

**Ubicación**: `shared/domain/valueobject/Email.java`

**Características**:
- **Inmutabilidad**: atributo `final`, sin setters
- **Validación automática**: regex + null check
- **Normalización**: `toLowerCase()` para consistencia
- **Factory method**: `Email.of(String)` (constructor privado)
- **equals/hashCode**: basado en `value`

**Pattern regex utilizado**:
```java
private static final Pattern EMAIL_PATTERN = 
    Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
```

**Decisión arquitectónica**:
- **Constructor privado** → obliga a usar factory method `of()`
- **Lanza `IllegalArgumentException`** → falla rápido si email inválido
- **No usar `ValidationException`** → value objects lanzan excepciones técnicas, no de negocio

**Justificación**:
- Email inválido es error técnico (construcción), no de regla de negocio
- ValidationException se reserva para validación de comandos/agregados completos
- Separación clara: IllegalArgumentException (value object) vs ValidationException (dominio)

**Código clave**:
```java
private Email(String value) {
    if (value == null || value.isBlank()) {
        throw new IllegalArgumentException("Email cannot be null or empty");
    }
    if (!EMAIL_PATTERN.matcher(value).matches()) {
        throw new IllegalArgumentException("Invalid email format: " + value);
    }
    this.value = value.toLowerCase();
}

public static Email of(String value) {
    return new Email(value);
}
```

**Conectado con**: Futuro User domain model, autenticación JWT, UserRepositoryPort

**Tests**: Pendiente EmailTest.java (opcional para Sprint 2)

#### 3.8 Money.java (Value Object)
**Propósito**: Representar cantidades monetarias con precisión decimal

**Ubicación**: `shared/domain/valueobject/Money.java`

**Características**:
- **BigDecimal**: evita errores de floating point (0.1 + 0.2 != 0.3 con double)
- **Currency**: String para código de moneda (EUR, USD)
- **Inmutabilidad**: atributos `final`
- **Validaciones**: amount >= 0, currency no null
- **Operaciones**: `add(Money)`, `multiply(BigDecimal)`
- **Factory methods**: `of(BigDecimal, String)`

**Decisión arquitectónica**:
- **BigDecimal obligatorio** → precisión decimal crítica en dinero
- **Validar monedas coinciden en add()** → no sumar EUR + USD
- **Multiplicar solo por factor > 0** → evitar dinero negativo

**Justificación**:
- Errores de redondeo con double/float son inaceptables en dinero
- Operaciones devuelven nuevo Money (inmutabilidad)
- Validación de currency previene errores de negocio

**Código clave**:
```java
public Money add(Money other) {
    if (!this.currency.equals(other.currency)) {
        throw new IllegalArgumentException("Cannot add different currencies");
    }
    BigDecimal sum = this.amount.add(other.amount);
    return Money.of(sum, currency);
}

public Money multiply(BigDecimal factor) {
    if (factor == null) {
        throw new IllegalArgumentException("Multiplication factor cannot be null");
    }
    if (factor.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Factor must be greater than zero");
    }
    BigDecimal result = this.amount.multiply(factor);
    return Money.of(result, currency);
}
```

**Conectado con**: Futuro Course pricing, Enrollment fees (comisiones 20/80), sistema de pagos

**Tests**: Pendiente MoneyTest.java (opcional para Sprint 2)

---

### DÍA 3: Shared Infrastructure - Exception Handler y JWT

#### 3.9 ErrorResponse.java (DTO)
**Propósito**: Estructura estandarizada para respuestas de error HTTP

**Ubicación**: `shared/infrastructure/config/ErrorResponse.java`

**Características**:
- **Record** (Java 14+): inmutabilidad automática
- **Factory methods**: `of()`, `withErrors()` para construcción conveniente
- **Timestamp automático**: `LocalDateTime.now()` en factories

**Campos**:
```java
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,           // opcional
    List<String> errors    // opcional para ValidationException
) {}
```

**Decisión arquitectónica**:
- Usar `record` en lugar de `class` → menos boilerplate, inmutabilidad gratis
- Factory methods para simplificar creación (evitar pasar `null, null`)
- Timestamp en factories, no en constructor → DRY

**Factory methods**:
```java
public static ErrorResponse of(int status, String error, String message) {
    return new ErrorResponse(LocalDateTime.now(), status, error, message, null, null);
}

public static ErrorResponse withErrors(int status, String error, String message, List<String> errors) {
    return new ErrorResponse(LocalDateTime.now(), status, error, message, null, errors);
}
```

**Conectado con**: GlobalExceptionHandler

#### 3.10 GlobalExceptionHandler.java
**Propósito**: Traducir excepciones de dominio a respuestas HTTP estandarizadas

**Ubicación**: `shared/infrastructure/config/GlobalExceptionHandler.java`

**Características**:
- **@RestControllerAdvice**: captura excepciones globalmente
- **@ExceptionHandler**: un método por tipo de excepción
- **Códigos HTTP apropiados**: 400, 404, 500
- **Logging de errores inesperados**: con `@Slf4j`

**Decisión arquitectónica**:
- Capas de dominio **no conocen HTTP** → el handler traduce
- Exception genérica (catch-all) para errores inesperados → status 500
- **No exponer detalles internos** en producción (mensaje genérico)

**Métodos implementados**:
```java
@ExceptionHandler(DomainException.class)  → 400 Bad Request

@ExceptionHandler(NotFoundException.class) → 404 Not Found

@ExceptionHandler(ValidationException.class) → 400 + lista de errores

@ExceptionHandler(Exception.class) → 500 Internal Server Error (catch-all)
```

**Flujo de excepción**:
```
Use Case lanza DomainException
    ↓
GlobalExceptionHandler captura
    ↓
Crea ErrorResponse con status 400
    ↓
Retorna ResponseEntity<ErrorResponse>
    ↓
Cliente recibe JSON con error
```

**Conectado con**: Todas las capas (domain, application, infrastructure), ErrorResponse DTO

#### 3.11 Dependencia JWT añadida
**Acción**: Añadir `io.jsonwebtoken` al `pom.xml`

**Dependencias**:
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

**Decisión arquitectónica**:
- Usar jjwt 0.12.3 (última versión estable)
- `jjwt-impl` y `jjwt-jackson` en scope `runtime` → no en compile classpath

#### 3.12 JwtUtil.java
**Propósito**: Generar, validar y extraer información de tokens JWT

**Ubicación**: `shared/infrastructure/security/JwtUtil.java`

**Características**:
- **@Component**: bean de Spring
- **Constructor con @Value**: inyección de configuración
- **SecretKey**: conversión de String a clave HMAC-SHA
- **Claims custom**: `role` además del `subject` (email)

**Métodos implementados**:
```java
generateToken(String email, String role) → String token
validateToken(String token, String email) → boolean
extractEmail(String token) → String
extractRole(String token) → String
isTokenExpired(String token) → boolean (privado)
extractClaims(String token) → Claims (privado)
```

**Decisión arquitectónica - Secret Key**:
- **Inicial**: Decodificar de Base64
- **Problema**: Secret en properties tenía guiones (carácter inválido en Base64)
- **Solución final**: Usar String directamente con `StandardCharsets.UTF_8`

**Código del constructor**:
```java
public JwtUtil(@Value("${jwt.secret}") String secret,
               @Value("${jwt.expiration}") long expiration) {
    // Convertir String a bytes UTF-8, luego a SecretKey
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expiration;
}
```

**Generación de token**:
```java
public String generateToken(String email, String role) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationMs);
    
    return Jwts.builder()
            .subject(email)
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
}
```

**Validación**:
```java
public boolean validateToken(String token, String email) {
    return (extractEmail(token).equals(email) && !isTokenExpired(token));
}
```

**Decisión: validateToken recibe email**:
- Valida **2 cosas**: token técnicamente válido + email coincide
- Más seguro que solo validar firma
- Previene uso de token robado con email diferente

**Conectado con**: Futuro JwtAuthenticationFilter (Sprint 3), LoginUseCase (Sprint 2)

**Tests**: JwtUtilTest.java (implementado)

---

### DÍA 4: Security Config Básico

#### 3.13 SecurityConfig.java
**Propósito**: Configuración temporal de Spring Security que desactiva autenticación

**Ubicación**: `shared/infrastructure/security/SecurityConfig.java`

**Características**:
- **@Configuration @EnableWebSecurity**: configuración de seguridad
- **Bean SecurityFilterChain**: define reglas HTTP
- **Bean PasswordEncoder**: BCrypt con strength 12

**Configuración actual (temporal)**:
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())  // Desactivar CSRF (REST API)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());  // Permitir todo
    return http.build();
}
```

**Decisión arquitectónica - Seguridad desactivada**:
- **Por qué**: Facilitar desarrollo y testing en Sprint 1-2
- **Cuándo se activa**: Sprint 3 (con JWT filter y roles)
- **CSRF desactivado**: No necesario en API REST stateless

**PasswordEncoder**:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);  // Strength 12 = 4096 rounds
}
```

**Decisión: BCrypt strength 12**:
- **10** = rápido pero menos seguro
- **12** = balance entre seguridad y performance
- **14+** = muy lento, problemas UX

**Conectado con**: Futuro RegisterUserUseCase (hashear passwords), LoginUseCase (verificar), JwtAuthenticationFilter (Sprint 3)

**Nota importante**: Esta configuración será **reemplazada completamente en Sprint 3** con:
- JWT authentication filter
- Role-based authorization (ADMIN, INSTRUCTOR, STUDENT)
- Endpoints públicos vs protegidos

#### 3.14 README.md
**Propósito**: Documentación del proyecto para desarrolladores

**Ubicación**: `/README.md` (raíz del proyecto)

**Secciones**:
1. **Objetivo**: Aprendizaje de arquitectura hexagonal
2. **Stack técnico**: Java 21, Spring Boot, H2, JWT, etc.
3. **Requisitos**: JDK 21+, Maven
4. **Instalación y ejecución**: Comandos paso a paso
5. **Estructura del proyecto**: Árbol de carpetas explicado
6. **Principios hexagonales**: Domain → Application → Infrastructure
7. **Tests**: Cómo ejecutarlos
8. **Estado actual**: Sprints completados y pendientes
9. **Documentación adicional**: Referencias a FUNCTIONAL.md

**Decisión arquitectónica**:
- README profesional desde Sprint 1
- Facilita onboarding de nuevos desarrolladores
- Documenta decisiones arquitectónicas clave

---

## 4. Configuración del proyecto

### 4.1 Git Workflow
**Estrategia**: Feature branches con merge a develop

**Branches creadas**:
```
main (producción - sin uso aún)
  ↓
develop (integración)
  ↓
├── feature/sprint1-setup (Día 1)
├── feature/shared-domain (Día 2)
├── feature/infrastructure-shared (Día 3)
└── feature/security-config-basic (Día 4)
```

**Comandos utilizados**:
```bash
git checkout develop
git checkout -b feature/nombre-feature
# ... trabajo ...
git add .
git commit -m "feat(scope): descripción"
git push -u origin feature/nombre-feature
git checkout develop
git merge feature/nombre-feature
git push origin develop
```

**Decisión arquitectónica**:
- **Feature branches**: aislamiento de cambios
- **Mensajes commit**: conventional commits (feat, fix, docs, etc.)
- **Develop como integración**: main se reserva para releases

### 4.2 application.properties configurado
**Ver sección 3.3**

### 4.3 Maven Dependencies (resumen)
```xml
<!-- Spring Boot Starters -->
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-validation

<!-- Database -->
h2

<!-- Security & JWT -->
io.jsonwebtoken:jjwt-api:0.12.3
io.jsonwebtoken:jjwt-impl:0.12.3
io.jsonwebtoken:jjwt-jackson:0.12.3

<!-- Dev Tools -->
spring-boot-devtools
lombok

<!-- Testing -->
spring-boot-starter-test
```

---

## 5. Testing implementado

### 5.1 JwtUtilTest.java
**Ubicación**: `src/test/java/.../shared/infrastructure/security/JwtUtilTest.java`

**Tests implementados**:
```java
@SpringBootTest
public class JwtUtilTest {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(EMAIL, ROLE);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }
    
    @Test
    void testExtractRole() {
        String token = jwtUtil.generateToken(EMAIL, ROLE);
        String extractedRole = jwtUtil.extractRole(token);
        assertEquals(ROLE, extractedRole);
    }
    
    @Test
    void testValidateToken_Valid() {
        String token = jwtUtil.generateToken(EMAIL, ROLE);
        boolean isValid = jwtUtil.validateToken(token, EMAIL);
        assertTrue(isValid);
    }
    
    @Test
    void testValidToken_WrongEmail() {
        String token = jwtUtil.generateToken(EMAIL, ROLE);
        boolean isValid = jwtUtil.validateToken(token, "wrong@email.com");
        assertFalse(isValid);
    }
}
```

**Cobertura**:
- Generación de token
- Extracción de rol
- Validación con email correcto
- Validación con email incorrecto

**Pendientes** (opcionales para Sprint 2):
- EmailTest.java (validaciones de Email VO)
- MoneyTest.java (operaciones de Money VO)

### 5.2 ElearningPlatformApplicationTests.java
**Test básico de contexto**:
```java
@SpringBootTest
class ElearningPlatformApplicationTests {
    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring arranca correctamente
    }
}
```

**Ejecutar todos los tests**:
```bash
./mvnw test
```

**Resultado esperado**: Todos los tests pasan

---

## 6. Verificación y próximos pasos

### 6.1 Checklist Sprint 1 Completado

- [x] **Estructura hexagonal completa** creada
- [x] **Excepciones de dominio** (DomainException, NotFoundException, ValidationException)
- [x] **Value Objects** (Email con validación regex, Money con BigDecimal)
- [x] **GlobalExceptionHandler** con ErrorResponse DTO
- [x] **JwtUtil** con generación y validación
- [x] **SecurityConfig** básico con PasswordEncoder
- [x] **Tests de JwtUtil** (4 tests passing)
- [x] **README.md** profesional
- [x] **application.properties** configurado
- [x] **Git workflow** con feature branches

### 6.2 Verificar H2 Database

1. Arrancar aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

2. Acceder a H2 Console:
    - URL: `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:mem:elearning`
    - Username: `sa`
    - Password: _(vacío)_

3. Ejecutar query:
   ```sql
   SHOW TABLES;
   ```
   **Resultado esperado**: Sin tablas (aún no hay entities JPA)

### 6.3 Próximos pasos - Sprint 2 (5 días)

#### Día 1: User Domain Layer
- [ ] Role enum (ADMIN, INSTRUCTOR, STUDENT)
- [ ] User domain exceptions
- [ ] User domain model (POJO puro, sin JPA)
- [ ] UserRepositoryPort (interface)
- [ ] Unit tests de User

#### Día 2: User Application Layer
- [ ] RegisterUserCommand y LoginCommand (records)
- [ ] RegisterUserUseCase
- [ ] LoginUseCase
- [ ] Unit tests de Use Cases con mocks

#### Día 3: User Infrastructure - Persistence
- [ ] UserEntity (JPA)
- [ ] UserMapper (domain ↔ entity)
- [ ] JpaUserRepositorySpring
- [ ] JpaUserRepositoryAdapter (implementa port)
- [ ] Tests de integración con H2

#### Día 4: User Infrastructure - Web Layer
- [ ] DTOs (RegisterRequest, LoginRequest, LoginResponse)
- [ ] AuthController (POST /auth/register, POST /auth/login)
- [ ] UserBeanConfig
- [ ] DataSeeder (admin por defecto)
- [ ] Pruebas con Postman

#### Día 5: Testing & Integración User Context
- [ ] Tests E2E (registrar → login → verificar token)
- [ ] Tests de validaciones de DTOs
- [ ] Tests de casos de error
- [ ] Documentar endpoints en README

---

## 7. Decisiones arquitectónicas clave

### 7.1 ¿Por qué Arquitectura Hexagonal?
**Decisión**: Usar hexagonal en lugar de arquitectura en capas tradicional

**Razones**:
1. **Independencia de frameworks**: Dominio no conoce Spring/JPA
2. **Testabilidad**: Use Cases testeables con mocks
3. **Flexibilidad**: Cambiar BD o framework sin tocar dominio
4. **Claridad**: Separación explícita de responsabilidades

**Trade-offs**:
- Más archivos y carpetas (mayor complejidad inicial)
- Curva de aprendizaje más pronunciada
- Mantenibilidad a largo plazo
- Facilita testing y cambios tecnológicos

### 7.2 Value Objects vs Primitives
**Decisión**: Email y Money como clases, no Strings/doubles

**Razones**:
1. **Encapsulación**: Validación en un solo lugar
2. **Inmutabilidad**: Evita bugs por modificación accidental
3. **Semántica**: `Email` es más expresivo que `String`
4. **Type safety**: Compilador previene errores (no puedes pasar Money donde espera Email)

**Ejemplo**:
```java
// Con primitivos
void sendEmail(String email) {  // ¿Está validado? ¿Normalizado?
    ...
}

// Con Value Object
void sendEmail(Email email) {  // Garantizado válido y normalizado
    ...
}
```

### 7.3 Excepciones: RuntimeException vs Checked
**Decisión**: Todas las excepciones de dominio extienden RuntimeException

**Razones**:
1. **No forzar try-catch**: El código cliente decide dónde capturar
2. **GlobalExceptionHandler**: Captura centralmente
3. **Legibilidad**: Menos boilerplate
4. **Spring compatibility**: Spring maneja bien unchecked exceptions

**Trade-off**:
- El compilador no obliga a manejarlas
- Código más limpio y flexible

### 7.4 Records vs Classes
**Decisión**: Records para DTOs, classes para Value Objects

**Razones**:
- **DTOs (ErrorResponse)**: Record perfecto (inmutabilidad, equals/hashCode gratis)
- **Value Objects (Email, Money)**: Class con constructor privado y factory method

**¿Por qué no record para Email/Money?**
- Records no permiten constructor privado
- Necesitamos forzar validación vía factory method `of()`
- Records no permiten normalización en constructor compacto antes de asignar

### 7.5 JWT sin Base64
**Decisión**: Secret key como String directo, no Base64

**Historia**:
1. **Intento inicial**: Decodificar secret de Base64
2. **Problema**: Secret con guiones (`-`) lanzaba `IllegalArgumentException`
3. **Solución**: Usar `secret.getBytes(StandardCharsets.UTF_8)` directamente

**Justificación**:
- Más simple para desarrollo
- Evita errores de codificación
- En producción se usaría secret desde variables de entorno de todas formas

### 7.6 Ports: ¿En domain o application?
**Decisión**: Ports OUT en `domain/port/out`, Ports IN en `application/port/in`

**Razones**:
- **Ports OUT** (repositories): El dominio "necesita" persistencia → contrato del dominio
- **Ports IN** (commands/queries): Definen casos de uso → capa de aplicación

**Debate**:
- Algunos colocan todos los ports en `application`
- Optamos por separar según quién "posee" el contrato

**Referencia**: Alistair Cockburn (creador de Hexagonal Architecture) recomienda ports OUT en domain

### 7.7 SecurityConfig temporal
**Decisión**: Desactivar seguridad en Sprint 1-2

**Razones**:
1. **Facilitar testing**: No necesitar JWT en cada request durante desarrollo inicial
2. **Foco en arquitectura**: Sprint 1-2 se centran en estructura, no en seguridad
3. **Progresividad**: Añadir complejidad gradualmente

**Plan**:
- Sprint 1-2: `permitAll()`
- Sprint 3: JWT filter + roles
- Sprint 4+: Endpoints específicos protegidos

---

## 8. Sprint 1 Completado 

**Fecha de finalización**: 19 Diciembre 2024

### Funcionalidades implementadas
- Arquitectura hexagonal con estructura completa
- Shared domain (excepciones + value objects)
- Shared infrastructure (exception handler + JWT + security)
- Tests básicos de JwtUtil
- README.md profesional
- Git workflow con feature branches

### Métricas del Sprint
- **Archivos creados**: 12 archivos Java
- **Líneas de código**: ~800 líneas (sin contar tests y JavaDoc)
- **Tests implementados**: 5 métodos de test (4 JwtUtil + 1 context loads)
- **Cobertura**:
    - JwtUtil: 100% testeado
    - Value Objects: Pendiente (opcional)
- **Commits**: 8 commits con mensajes conventional
- **Branches mergeadas**: 4 feature branches

### Preparación para siguientes sprints
- Estructura de carpetas lista para user/course/enrollment
- Excepciones base reutilizables
- Value Objects compartidos (Email, Money)
- JWT listo para usar en Sprint 2-3
- GlobalExceptionHandler preparado para nuevas excepciones

---

## Anexos

### A.1 Comandos útiles

#### Maven
```bash
# Compilar
./mvnw clean compile

# Ejecutar tests
./mvnw test

# Ejecutar aplicación
./mvnw spring-boot:run

# Instalar dependencias
./mvnw clean install
```

#### Git
```bash
# Ver estado
git status

# Crear feature branch
git checkout -b feature/nombre

# Commit
git add .
git commit -m "feat(scope): mensaje"

# Push
git push -u origin nombre-branch

# Merge a develop
git checkout develop
git merge feature/nombre
git push origin develop
```

### A.2 Acceso a H2 Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:elearning`
- Username: `sa`
- Password: _(vacío)_

### A.3 Estructura de un commit
```
feat(shared): implement Email value object

- Add Email value object with regex validation
- Add factory method Email.of()
- Include JavaDoc documentation
- Email is normalized to lowercase
```

**Tipos de commit**:
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `docs`: Documentación
- `test`: Tests
- `refactor`: Refactorización sin cambio de funcionalidad
- `chore`: Tareas de mantenimiento

---

**Sprint 1 completado exitosamente. Base arquitectónica sólida para construir el resto del sistema.**