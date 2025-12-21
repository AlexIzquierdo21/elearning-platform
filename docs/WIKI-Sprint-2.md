# WIKI - Sprint 2: User Domain & Authentication

---

## Objetivo

Implementar el **bounded context de usuarios** con registro, login y persistencia JPA, estableciendo las bases del sistema de autenticación basado en JWT.

---

## Roadmap del Sprint

### Día 1-2: Domain Layer
- User entity con validaciones  
- Value Objects (UserId, Email reutilizado)  
- Enum UserRole (ADMIN, INSTRUCTOR, STUDENT)  
- Excepciones de dominio (UserAlreadyExistsException, InvalidCredentialsException)  
- UserRepositoryPort (interfaz de salida)

### Día 3: Application Layer
- RegisterUserUseCase  
- LoginUseCase  
- Commands (RegisterUserCommand, LoginCommand)  
- Integration con JwtUtil del shared kernel

### Día 4: Infrastructure - Persistence
- UserEntity (JPA)  
- JpaUserRepository (Spring Data)  
- UserRepositoryAdapter (implementación del puerto)  
- UserMapper (domain ↔ entity)  
- Schema H2 generado automáticamente

### Día 5: Infrastructure - Web
- AuthController (REST endpoints)  
- DTOs (RegisterRequest, LoginRequest, RegisterResponse, LoginResponse)  
- Validaciones con Bean Validation  
- UserBeanConfig (configuración de beans)

---

## Arquitectura Implementada

### Flujo Hexagonal Completo

```
┌──────────────────────────────────────────────────────┐
│            WEB ADAPTER (Infrastructure)              │
│  AuthController → DTOs → Commands                    │
└────────────────┬─────────────────────────────────────┘
                 ↓
┌──────────────────────────────────────────────────────┐
│         APPLICATION LAYER (Use Cases)                │
│  RegisterUserUseCase  →  LoginUseCase                │
│         ↓                      ↓                      │
│    UserRepositoryPort    JwtUtil (shared)            │
└────────────────┬─────────────────────────────────────┘
                 ↓
┌──────────────────────────────────────────────────────┐
│              DOMAIN LAYER                            │
│  User  →  UserId  →  UserRole  →  Email              │
│  Excepciones de negocio (sin dependencias)           │
└──────────────────────────────────────────────────────┘
                 ↑
┌──────────────────────────────────────────────────────┐
│      PERSISTENCE ADAPTER (Infrastructure)            │
│  UserRepositoryAdapter → JpaUserRepository           │
│  UserEntity  ←  UserMapper  →  User                  │
└──────────────────────────────────────────────────────┘
```

**Flujo de dependencias**: Web → Application → Domain ← Persistence

**Inversión de dependencias**: Persistence implementa puerto definido en Domain

---

## Componentes Creados

### 1. Domain Layer (`user/domain/`)

#### User.java
**Ubicación**: `user/domain/model/User.java`

**Responsabilidades**:
- Entidad de dominio con identidad (UserId)
- Validaciones defensivas en construcción
- Factory method `create()` para encapsular construcción

**Validaciones implementadas**:
- Email válido (delegado a value object Email)
- Password: mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 número
- Nombres no vacíos ni solo espacios

**Características**:
- Constructor privado (control total de creación)
- Getters sin setters (inmutabilidad parcial)
- Timestamps automáticos (createdAt, updatedAt)
- Flag `active` (soft delete)

**Decisión**: Modelo anémico intencional. Las validaciones son defensivas, pero la lógica de negocio compleja (como verificar duplicados) vive en Use Cases.

---

#### UserId.java
**Ubicación**: `user/domain/valueobject/UserId.java`

**Tipo**: UUID (no Long autoincremental)

**Ventajas UUID**:
- No expone cantidad de usuarios
- Mejor para sistemas distribuidos
- Generación sin acceso a BD

**Factory method**: `UserId.generate()` → UUID.randomUUID()

---

#### UserRole.java
**Ubicación**: `user/domain/model/UserRole.java`

**Enum con 3 valores**:
- `ADMIN` - Administrador del sistema
- `INSTRUCTOR` - Creador de contenido
- `STUDENT` - Consumidor de contenido

**Decisión**: Un usuario = un rol (RBAC simple, no multi-rol)

**Mapeo JPA**: `@Enumerated(EnumType.STRING)` para legibilidad en BD

---

#### UserRepositoryPort.java
**Ubicación**: `user/domain/port/out/UserRepositoryPort.java`

**Interfaz (puerto de salida)** que define:
```
User save(User user)
Optional<User> findByEmail(Email email)
boolean existsByEmail(Email email)
```

**Principio clave**: El dominio define QUÉ necesita, no CÓMO se persiste.

**Implementación**: UserRepositoryAdapter (infrastructure layer)

---

#### Excepciones
**Ubicación**: `user/domain/exception/`

**UserAlreadyExistsException**:
- Se lanza cuando email ya existe
- Capturada en GlobalExceptionHandler → 400 Bad Request

**InvalidCredentialsException**:
- Se lanza en login fallido
- Capturada en GlobalExceptionHandler → 401 Unauthorized

**UserNotFoundException**:
- Se lanza cuando usuario no existe
- Extiende NotFoundException del shared kernel → 404

**Todas extienden**: `DomainException` (RuntimeException)

---

### 2. Application Layer (`user/application/`)

#### RegisterUserUseCase.java
**Ubicación**: `user/application/usecase/RegisterUserUseCase.java`

**Flujo de ejecución**:
1. Recibe `RegisterUserCommand`
2. Verifica que email no exista (`userRepository.existsByEmail`)
3. Hashea password con BCrypt (PasswordEncoder del shared)
4. Crea entidad User con `User.create()`
5. Persiste vía `userRepository.save()`
6. Retorna `UserId`

**Dependencias inyectadas**:
- `UserRepositoryPort` (puerto de salida)
- `PasswordEncoder` (configurado en SecurityConfig)

**Decisión**: La validación de unicidad de email está en el Use Case (no en el dominio), porque requiere acceso al repositorio.

---

#### LoginUseCase.java
**Ubicación**: `user/application/usecase/LoginUseCase.java`

**Flujo de ejecución**:
1. Recibe `LoginCommand`
2. Busca usuario por email (`userRepository.findByEmail`)
3. Verifica que existe (lanza `InvalidCredentialsException` si no)
4. Compara password hasheada con BCrypt
5. Si OK → Genera token JWT (`jwtUtil.generateToken`)
6. Retorna `LoginResponse` (token + expiresIn)

**Dependencias inyectadas**:
- `UserRepositoryPort`
- `PasswordEncoder`
- `JwtUtil` (del shared infrastructure)

**Seguridad**: No revela si email existe o password es incorrecta (mismo error en ambos casos).

---

#### Commands (Port IN)
**Ubicación**: `user/application/port/in/`

**RegisterUserCommand**:
```
- Email email
- String password
- String firstName
- String lastName
- UserRole role
```

**LoginCommand**:
```
- Email email
- String password
```

**Patrón**: Java records inmutables que representan intenciones del usuario.

**Separación**: Commands ≠ DTOs. El Controller traduce DTOs a Commands.

---

### 3. Infrastructure - Persistence (`user/infrastructure/adapter/out/persistence/`)

#### UserEntity.java
**Mapeo JPA**:
```
@Entity
@Table(name = "users")
- id: UUID (PK)
- email: String (unique, not null)
- password: String (hashed)
- firstName: String
- lastName: String
- role: UserRole (EnumType.STRING)
- active: boolean (default true)
- createdAt: Timestamp
- updatedAt: Timestamp
```

**Separación crítica**: `UserEntity ≠ User`
- UserEntity: Modelo de persistencia (conoce JPA)
- User: Modelo de dominio (puro Java, sin anotaciones)

**Comunicación**: UserMapper traduce entre ambos

---

#### JpaUserRepository.java
**Tipo**: Interface Spring Data JPA

**Métodos heredados** de `JpaRepository<UserEntity, UUID>`:
- `save()`, `findById()`, etc.

**Métodos custom**:
```
Optional<UserEntity> findByEmail(String email)
boolean existsByEmail(String email)
```

**Nota**: Trabaja con `UserEntity`, no con `User` (dominio)

---

#### UserRepositoryAdapter.java
**Rol**: Implementación de `UserRepositoryPort`

**Responsabilidad**: Adaptar entre dominio y persistencia

**Flujo**:
```
Domain (User) → UserMapper.toEntity() → UserEntity
                       ↓
              JpaUserRepository.save()
                       ↓
UserEntity → UserMapper.toDomain() → User (retorno)
```

**Anotación**: `@Component` para inyección de dependencias

**Dependencias inyectadas**: `JpaUserRepository`

---

#### UserMapper.java
**Tipo**: Clase estática con métodos de conversión

**Métodos**:
```
User toDomain(UserEntity entity)
UserEntity toEntity(User user)
```

**Decisión**: Mapper estático (no framework como MapStruct) para:
- Simplicidad en proyecto de aprendizaje
- Control total sobre conversiones
- Sin dependencias adicionales

**Patrón**: El dominio nunca conoce JPA, solo la infrastructure conoce ambos.

---

### 4. Infrastructure - Web (`user/infrastructure/adapter/in/web/`)

#### AuthController.java
**Endpoints REST**:

**POST /auth/register**:
- Recibe: `RegisterRequest` (DTO)
- Valida: `@Valid` (Bean Validation)
- Traduce: DTO → Command
- Ejecuta: `registerUserUseCase.execute()`
- Retorna: `RegisterResponse` (201 Created)

**POST /auth/login**:
- Recibe: `LoginRequest` (DTO)
- Valida: `@Valid`
- Traduce: DTO → Command
- Ejecuta: `loginUseCase.execute()`
- Retorna: `LoginResponse` (200 OK)

**Anotaciones**:
- `@RestController`
- `@RequestMapping("/auth")`
- `@Slf4j` (logging)

**Dependencias inyectadas**:
- `RegisterUserUseCase`
- `LoginUseCase`

---

#### DTOs (`dto/`)

**RegisterRequest**:
```
@Email(message = "Invalid email format")
String email

@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
String password

@NotBlank
String firstName

@NotBlank
String lastName

@NotNull
UserRole role
```

**LoginRequest**:
```
@Email
String email

@NotBlank
String password
```

**RegisterResponse**:
```
String userId
String message
```

**LoginResponse**:
```
String token
String type (siempre "Bearer")
Long expiresIn (milisegundos)
```

**Decisión**: Validaciones en DTOs (no en dominio) porque son específicas de la capa web.

---

#### UserBeanConfig.java
**Ubicación**: `user/infrastructure/config/UserBeanConfig.java`

**Responsabilidad**: Configurar beans de Application Layer

**Beans creados**:
```java
@Bean
RegisterUserUseCase registerUserUseCase(...)

@Bean
LoginUseCase loginUseCase(...)
```

**Razón**: Los Use Cases no son `@Service` para mantener el Application Layer libre de anotaciones de Spring.

---

## Data Seeder

**Archivo**: `shared/infrastructure/config/DataSeeder.java`

**Propósito**: Crear usuario admin por defecto

**Ejecución**: `@PostConstruct` (después de iniciar Spring)

**Usuario creado**:
```
Email: admin@elearning.com
Password: Admin123!
Role: ADMIN
```

**Verificación**: Comprueba si existe antes de crear

**Decisión**: En shared porque es transversal (no específico de user domain)

---

## Decisiones Arquitectónicas Clave

### 1. ¿Por qué separar User y UserEntity?

| Aspecto | User (Domain) | UserEntity (Infrastructure) |
|---------|---------------|----------------------------|
| Dependencias | ✅ Cero (Java puro) | ❌ JPA annotations |
| Testabilidad | ✅ Tests unitarios puros | ❌ Requiere Spring context |
| Portabilidad | ✅ Cambiar BD sin tocar dominio | ❌ Acoplado a JPA |
| Validaciones | ✅ Reglas de negocio | ❌ Solo constraints BD |

**Trade-off**: Más código (mapper), pero dominio protegido.

---

### 2. ¿Por qué Commands separados de DTOs?

**DTOs** (Web layer):
- Validaciones HTTP (formato email, longitud)
- Pueden cambiar por requisitos de API
- Conocen anotaciones Jakarta

**Commands** (Application layer):
- Representan intenciones de negocio
- Inmutables (Java records)
- Sin dependencias de frameworks

**Beneficio**: Cambiar API REST no afecta casos de uso.

---

### 3. ¿Por qué hashear password en Use Case (no en User)?

**Opción descartada**: `User.create(..., passwordEncoder)`

**Problema**: User dependería de PasswordEncoder (infrastructure)

**Solución**: Use Case hashea antes de llamar a `User.create()`

**Resultado**: Dominio puro, Use Case orquesta infraestructura.

---

### 4. ¿Por qué UserRole es enum (no String)?

**Ventajas**:
-  Type safety (compilador previene errores)
-  Autocompletado en IDE
-  Imposible valores inválidos
-  Fácil agregar métodos (ej: `hasPermission()`)

**Desventaja**:
-  Agregar roles requiere cambio de código (vs configuración)

**Decisión**: Para MVP con 3 roles fijos, enum es óptimo.

---

### 5. ¿Por qué UUID en lugar de Long?

| Aspecto | Long autoincremental | UUID |
|---------|---------------------|------|
| Predictibilidad | ❌ Expone cantidad de usuarios | ✅ Impredecible |
| Generación | ❌ Requiere BD | ✅ Local (sin BD) |
| Colisiones | ✅ Imposibles | ✅ Estadísticamente imposibles |
| Performance | ✅ Índices más pequeños | ❌ Índices más grandes |
| Sistemas distribuidos | ❌ Sincronización compleja | ✅ Generación independiente |

**Decisión**: UUID para escalabilidad futura.

---

## Testing Realizado

### Tests Manuales (Postman)

**POST /auth/register**:
```
 Usuario creado correctamente (201)
 Email duplicado rechazado (400)
 Password débil rechazado (400)
 Email inválido rechazado (400)
```

**POST /auth/login**:
```
 Login exitoso retorna JWT (200)
 Credenciales incorrectas rechazadas (401)
 Usuario inexistente rechazado (401)
```

**Verificación JWT**:
```
 Token contiene email en subject
 Token contiene role en claims
 Token expira en 24h
 Token validado por JwtUtil
```

### Pendiente (Sprint 3)
- Tests de integración (WebMvcTest)
- Tests de repositorio (DataJpaTest)
- Tests unitarios de Use Cases con mocks

---

## Estructura de Archivos Final

```
user/
├── domain/
│   ├── model/
│   │   ├── User.java
│   │   └── UserRole.java
│   ├── valueobject/
│   │   └── UserId.java
│   ├── exception/
│   │   ├── UserAlreadyExistsException.java
│   │   ├── InvalidCredentialsException.java
│   │   └── UserNotFoundException.java
│   └── port/
│       └── out/
│           └── UserRepositoryPort.java
│
├── application/
│   ├── usecase/
│   │   ├── RegisterUserUseCase.java
│   │   └── LoginUseCase.java
│   └── port/
│       └── in/
│           ├── RegisterUserCommand.java
│           └── LoginCommand.java
│
└── infrastructure/
    ├── adapter/
    │   ├── in/
    │   │   └── web/
    │   │       ├── AuthController.java
    │   │       └── dto/
    │   │           ├── RegisterRequest.java
    │   │           ├── RegisterResponse.java
    │   │           ├── LoginRequest.java
    │   │           └── LoginResponse.java
    │   └── out/
    │       └── persistence/
    │           ├── UserEntity.java
    │           ├── JpaUserRepository.java
    │           ├── UserRepositoryAdapter.java
    │           └── UserMapper.java
    └── config/
        └── UserBeanConfig.java
```

---

## Métricas del Sprint

- **Archivos Java**: 18
- **Líneas de código**: ~1200 (sin tests/JavaDoc)
- **Endpoints**: 2
- **Entidades JPA**: 1
- **Use Cases**: 2
- **Value Objects**: 1 (+ Email reutilizado)
- **Commits**: 12
- **Branches mergeadas**: 5

---

## Integración con Shared Kernel

### Componentes reutilizados:
 `Email` (value object)  
 `DomainException` (jerarquía de excepciones)  
 `GlobalExceptionHandler` (captura excepciones de user)  
 `JwtUtil` (generación de tokens en LoginUseCase)  
 `PasswordEncoder` (BCrypt de SecurityConfig)

### Componentes extendidos:
 `NotFoundException` → `UserNotFoundException`  
 `ErrorResponse` → Usado en respuestas de AuthController

---

## Preparación para Sprint 3

### Infraestructura lista:
 Usuarios persistidos en H2  
 JWT generado en login  
 Password hasheado con BCrypt  
 DTOs con validaciones

### Próximo Sprint: JWT Authentication Filter & Authorization

**Día 1**: JwtAuthenticationFilter (OncePerRequestFilter)  
**Día 2**: SecurityConfig con role-based authorization  
**Día 3**: Testing de seguridad (MockMvc)  
**Día 4**: Manejo correcto de 401 vs 403  
**Día 5**: Integration tests completos

---

## Comandos Útiles

### Testing con cURL
```bash
# Registro
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test1234","firstName":"John","lastName":"Doe","role":"STUDENT"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test1234"}'
```

### Verificar BD H2
```sql
SELECT * FROM users;
-- Ver usuarios creados con passwords hasheados
```

---

## Verificación Sprint Completado

- [x] Registro de usuarios funcional
- [x] Login retorna JWT válido
- [x] Passwords hasheadas en BD
- [x] Email duplicado rechazado
- [x] Validaciones de input funcionan
- [x] GlobalExceptionHandler captura errores
- [x] Usuario admin creado automáticamente
- [x] Arquitectura hexagonal respetada
- [x] Sin dependencias de infrastructure en domain

---

**Sprint 2 completado exitosamente - User domain funcional con autenticación JWT**