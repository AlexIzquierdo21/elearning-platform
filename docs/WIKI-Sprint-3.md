# WIKI - Sprint 3: JWT Authentication & Role-Based Authorization

---

## Objetivo

Implementar **autenticación JWT funcional** y **control de acceso basado en roles (RBAC)**, completando el sistema de seguridad con distinción correcta entre errores 401 (Unauthorized) y 403 (Forbidden).

---

## Roadmap del Sprint

### Día 1: JWT Authentication Filter
✅ JwtAuthenticationFilter (OncePerRequestFilter)  
✅ Extracción de token del header Authorization  
✅ Validación de token y carga de autenticación  
✅ Integración con SecurityContextHolder

### Día 2: Security Configuration
✅ SecurityConfig con role-based authorization  
✅ RequestAttributeSecurityContextRepository para stateless  
✅ Rutas públicas vs protegidas por rol  
✅ Custom exception handlers (401 vs 403)

### Día 3: Exception Handling
✅ UnauthorizedException (dominio)  
✅ AccessDeniedException (dominio)  
✅ GlobalExceptionHandler extendido  
✅ Logs de debug en handlers

### Día 4: Testing & Debugging
✅ Tests manuales en Postman  
✅ TestSecurityController para validación  
✅ Resolución de problemas de persistencia de contexto  
✅ Verificación de 401 vs 403

### Día 5: CORS & Refinamiento
✅ Configuración CORS para desarrollo  
✅ Documentación JavaDoc completa  
✅ Limpieza de logs de debug  
✅ Validación final de todos los endpoints

---

## Arquitectura Implementada

### Flujo de Autenticación JWT

```
┌──────────────────────────────────────────────────────┐
│                    HTTP REQUEST                      │
│       Authorization: Bearer <JWT_TOKEN>              │
└────────────────┬─────────────────────────────────────┘
                 ↓
┌──────────────────────────────────────────────────────┐
│         JwtAuthenticationFilter                      │
│  1. Extrae token del header                          │
│  2. Valida token con JwtUtil                         │
│  3. Extrae email y role                              │
│  4. Crea SimpleGrantedAuthority("ROLE_" + role)      │
│  5. Crea UsernamePasswordAuthenticationToken         │
│  6. Guarda en SecurityContextHolder                  │
└────────────────┬─────────────────────────────────────┘
                 ↓
┌──────────────────────────────────────────────────────┐
│    RequestAttributeSecurityContextRepository         │
│  Persiste contexto en atributos de request           │
│  (NO usa sesiones HTTP)                              │
└────────────────┬─────────────────────────────────────┘
                 ↓
┌──────────────────────────────────────────────────────┐
│         Spring Security Filter Chain                 │
│  AuthorizationFilter verifica roles                  │
│  - hasRole("ADMIN")                                  │
│  - hasRole("INSTRUCTOR")                             │
│  - hasRole("STUDENT")                                │
└────────────────┬─────────────────────────────────────┘
                 ↓
        ┌────────┴────────┐
        │                 │
    ✅ ACCESO         ❌ DENEGADO
        │                 │
        ↓                 ↓
   Controller      Exception Handler
        │                 │
        ↓                 ↓
    200 OK          401 / 403
```

---

## Componentes Creados

### 1. JWT Authentication Filter

#### JwtAuthenticationFilter.java
**Ubicación**: `shared/infrastructure/security/JwtAuthenticationFilter.java`

**Responsabilidades**:
- Interceptar cada request HTTP
- Extraer JWT del header `Authorization: Bearer <token>`
- Validar token con `JwtUtil`
- Crear autenticación con authorities
- Guardar en SecurityContext

**Flujo de ejecución**:
1. Extrae token con `extractTokenFromRequest()`
2. Extrae email del token con `jwtUtil.extractEmail()`
3. Verifica que no haya autenticación previa
4. Valida token con `jwtUtil.validateToken(token, email)`
5. Extrae rol con `jwtUtil.extractRole()`
6. Crea `SimpleGrantedAuthority` con prefijo `ROLE_`
7. Crea `UsernamePasswordAuthenticationToken`
8. Guarda en `SecurityContextHolder.getContext().setAuthentication()`
9. Continúa filter chain con `filterChain.doFilter()`

**Características clave**:
- Extends `OncePerRequestFilter` (ejecuta solo una vez por request)
- Prefijo `ROLE_` obligatorio para compatibilidad con `hasRole()`
- No modifica response, solo añade autenticación al contexto

**Decisión**: Filtro como clase independiente (no bean) para control de instanciación.

---

### 2. Security Configuration

#### SecurityConfig.java
**Ubicación**: `shared/infrastructure/security/SecurityConfig.java`

**Responsabilidades**:
- Configurar filter chain de Spring Security
- Definir rutas públicas y protegidas
- Registrar JwtAuthenticationFilter
- Configurar SecurityContextRepository
- Manejar errores de autenticación/autorización

**Configuraciones implementadas**:

**CSRF**: Deshabilitado (API stateless)

**Frame Options**: Deshabilitadas (H2 console)

**Authorization Rules**:
```
/auth/**          → permitAll() (público)
/h2-console/**    → permitAll() (desarrollo)
/courses          → permitAll() (catálogo público)
/admin/**         → hasRole("ADMIN")
/instructor/**    → hasRole("INSTRUCTOR")
/student/**       → hasRole("STUDENT")
anyRequest()      → authenticated()
```

**Session Management**:
```
SessionCreationPolicy.STATELESS (sin sesiones HTTP)
```

**SecurityContextRepository**:
```
RequestAttributeSecurityContextRepository
```

**Filter Registration**:
```
addFilterBefore(
    JwtAuthenticationFilter,
    UsernamePasswordAuthenticationFilter.class
)
```

**Exception Handlers**:
```
authenticationEntryPoint → 401 Unauthorized
accessDeniedHandler      → 403 Forbidden
```

---

#### RequestAttributeSecurityContextRepository
**Bean personalizado**: `securityContextRepository()`

**Propósito**: Persistir SecurityContext entre filtros en aplicaciones stateless

**Problema que resuelve**:
- En Spring Security 6.x con `STATELESS`, el contexto creado en filtros custom no persiste automáticamente
- `HttpSessionSecurityContextRepository` no funciona (usa sesiones)
- `NullSecurityContextRepository` descarta el contexto inmediatamente

**Solución**:
- `RequestAttributeSecurityContextRepository` guarda el contexto en atributos del request HTTP
- Disponible durante toda la ejecución de la petición
- Compatible con arquitectura stateless

**Decisión crítica**: Sin este bean, los tokens válidos con rol incorrecto daban 401 en lugar de 403.

---

#### CORS Configuration
**Bean**: `corsConfigurationSource()`

**Configuración**:
```
Allowed Origins: http://localhost:*, http://127.0.0.1:*
Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
Allowed Headers: *
Allow Credentials: true
Exposed Headers: Authorization
```

**Propósito**: Permitir requests desde frontend en desarrollo

**Producción**: Configurar origins específicos

---

### 3. Domain Exceptions

#### UnauthorizedException.java
**Ubicación**: `shared/domain/exception/UnauthorizedException.java`

**Casos de uso**:
- Token ausente
- Token inválido
- Token expirado
- Token malformado

**HTTP Status**: 401 Unauthorized

**Mensaje por defecto**: "Authentication required"

**Extiende**: `DomainException` (RuntimeException)

---

#### AccessDeniedException.java
**Ubicación**: `shared/domain/exception/AccessDeniedException.java`

**Casos de uso**:
- Usuario autenticado pero sin rol necesario
- STUDENT intentando acceder a `/instructor/**`
- INSTRUCTOR intentando acceder a `/admin/**`

**HTTP Status**: 403 Forbidden

**Mensaje por defecto**: "Insufficient privileges to access this resource"

**Extiende**: `DomainException` (RuntimeException)

**Nota**: Nombre idéntico a excepción de Spring Security, pero vive en dominio (sin dependencias).

---

### 4. Global Exception Handler (Extendido)

#### Nuevos Handlers en GlobalExceptionHandler.java

**handleUnauthorizedException**:
```
@ExceptionHandler(UnauthorizedException.class)
→ ResponseEntity<ErrorResponse> (401)
```

**handleAccessDeniedException**:
```
@ExceptionHandler(AccessDeniedException.class)
→ ResponseEntity<ErrorResponse> (403)
```

**handleIllegalStateException**:
```
@ExceptionHandler(IllegalStateException.class)
→ ResponseEntity<ErrorResponse> (400)
```

**Handlers previos** (mantenidos):
- `DomainException` → 400
- `NotFoundException` → 404
- `ValidationException` → 400

**Decisión**: Handler genérico de `Exception.class` permanece comentado para no interferir con excepciones de Spring Security.

---

### 5. Test Security Controller

#### TestSecurityController.java
**Ubicación**: `shared/infrastructure/security/TestSecurityController.java`

**Propósito**: Endpoints stub para validar sistema de autorización

**Endpoints implementados**:

```java
GET  /courses                   // Público
POST /instructor/courses        // INSTRUCTOR
POST /admin/users              // ADMIN
GET  /admin/test               // ADMIN
GET  /instructor/test          // INSTRUCTOR
GET  /student/test             // STUDENT
```

**Respuestas**: Strings simples confirmando acceso

**Uso**: Validación manual con Postman

**Eliminación futura**: Sprint 4+ (reemplazado por controllers reales)

---

## Problemas Encontrados y Soluciones

### Problema 1: 401 en lugar de 403

**Síntoma**:
- Token válido con STUDENT accediendo a `/instructor/**` → 401 Unauthorized
- Debería ser: 403 Forbidden

**Causa raíz**:
- En Spring Security 6.x con `SessionCreationPolicy.STATELESS`, el contexto de seguridad no persistía entre filtros
- `JwtAuthenticationFilter` cargaba la autenticación
- `AuthorizationFilter` no la encontraba
- Spring Security interpretaba: "sin autenticación" → 401

**Soluciones intentadas (fallidas)**:
1. `.requireExplicitSave(true)` → Empeoró el problema
2. `HttpSessionSecurityContextRepository` → No funciona en stateless
3. `NullSecurityContextRepository` → Descarta contexto inmediatamente
4. Cambiar orden de `.exceptionHandling()` → Sin efecto
5. Modificar `GlobalExceptionHandler` → No era el problema
6. `addFilterAfter` vs `addFilterBefore` → Necesario pero insuficiente

**Solución final**:
```java
.securityContext(context -> context
    .securityContextRepository(securityContextRepository())
)

@Bean
public SecurityContextRepository securityContextRepository() {
    return new RequestAttributeSecurityContextRepository();
}
```

**Explicación**:
- `RequestAttributeSecurityContextRepository` guarda el contexto en atributos de la petición HTTP
- El contexto persiste durante toda la ejecución del request
- Compatible con arquitectura stateless
- Permite que `AuthorizationFilter` vea la autenticación creada por `JwtAuthenticationFilter`

---

### Problema 2: Prefijo ROLE_ inconsistente

**Síntoma**: Authorization siempre fallaba incluso con roles correctos

**Causa**:
- JWT contenía `role: "STUDENT"` (sin prefijo)
- `JwtAuthenticationFilter` creaba authority con prefijo: `ROLE_STUDENT`
- Spring Security `hasRole("STUDENT")` busca internamente `ROLE_STUDENT`

**Solución**:
```java
List<SimpleGrantedAuthority> authorities = List.of(
    new SimpleGrantedAuthority("ROLE_" + role)  // ✅ Prefijo explícito
);
```

**Validación**: Logs mostraban authorities con formato correcto: `[ROLE_STUDENT]`

---

### Problema 3: GlobalExceptionHandler interfiriendo

**Síntoma**:
- Excepciones de Spring Security capturadas por handler genérico
- `accessDeniedHandler` nunca se ejecutaba

**Causa**:
```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGenericException(Exception ex)
```

**Solución**: Comentar handler genérico durante desarrollo de seguridad

**Estado actual**: Handler genérico desactivado

**Futuro**: Re-activar con exclusión explícita de excepciones de Spring Security

---

### Problema 4: DevTools reiniciando app constantemente

**Síntoma**: App reiniciaba en cada cambio, perdiendo estado en Postman

**Solución temporal**:
```properties
spring.devtools.restart.enabled=false
```

**Alternativa**: Usar perfil de producción para tests

---

## Decisiones Arquitectónicas Clave

### 1. ¿Por qué OncePerRequestFilter?

**Alternativas**:
- `GenericFilterBean` → Puede ejecutarse múltiples veces
- `Filter` (servlet) → Menos integración con Spring

**Decisión**: `OncePerRequestFilter`

**Ventajas**:
- Garantiza ejecución única por request
- Manejo automático de forwards/includes
- Mejor integración con Spring Security

---

### 2. ¿Por qué addFilterBefore (no addFilterAfter)?

**Orden de filtros crítico**:

```
SecurityContextHolderFilter
    ↓
JwtAuthenticationFilter          ← NUESTRO FILTRO
    ↓
UsernamePasswordAuthenticationFilter
    ↓
AuthorizationFilter
```

**addFilterBefore**: Ejecuta JWT filter ANTES de autenticación estándar

**addFilterAfter**: Ejecutaría DESPUÉS (contexto podría limpiarse)

**Resultado**: `addFilterBefore` garantiza que autenticación JWT esté disponible para todos los filtros posteriores

---

### 3. ¿Por qué RequestAttributeSecurityContextRepository?

| Repository | Uso | Problema en Stateless |
|------------|-----|----------------------|
| `HttpSessionSecurityContextRepository` | Sesiones HTTP | ❌ No compatible con STATELESS |
| `NullSecurityContextRepository` | Descarta contexto | ❌ Pierde autenticación |
| `RequestAttributeSecurityContextRepository` | Atributos request | ✅ Persiste durante request |

**Decisión**: `RequestAttributeSecurityContextRepository` es el único compatible con JWT stateless en Spring Security 6.x

---

### 4. ¿Por qué excepciones de dominio para 401/403?

**Opción descartada**: Usar excepciones de Spring Security directamente

**Problema**:
- Dominio dependería de Spring Security
- Violación de arquitectura hexagonal

**Solución**:
- `UnauthorizedException` y `AccessDeniedException` en `shared/domain/exception`
- Sin dependencias de frameworks
- GlobalExceptionHandler traduce a HTTP

**Beneficio**: Dominio puro, infrastructure adapta

---

### 5. ¿Por qué no usar @PreAuthorize?

**Alternativa**: Anotaciones en métodos
```java
@PreAuthorize("hasRole('ADMIN')")
public String adminEndpoint()
```

**Decisión**: Configuración centralizada en `SecurityConfig`

**Ventajas**:
- Visibilidad global de reglas de acceso
- Más fácil auditar seguridad
- Menos acoplamiento entre seguridad y controllers

**Trade-off**: Configuración por paths (menos granular que por método)

---

## Testing Realizado

### Tests Manuales (Postman)

**Setup**:
1. Registrar usuarios (INSTRUCTOR, STUDENT)
2. Login y obtener tokens
3. Verificar admin predefinido

**Test Suite Completa**:

| # | Request | Token | Expected | Status |
|---|---------|-------|----------|--------|
| 1 | GET `/courses` | None | 200 OK | ✅ |
| 2 | POST `/instructor/courses` | None | 401 Unauthorized | ✅ |
| 3 | POST `/instructor/courses` | STUDENT | 403 Forbidden | ✅ |
| 4 | POST `/instructor/courses` | INSTRUCTOR | 200 OK | ✅ |
| 5 | POST `/admin/users` | INSTRUCTOR | 403 Forbidden | ✅ |
| 6 | POST `/admin/users` | ADMIN | 200 OK | ✅ |
| 7 | GET `/student/test` | STUDENT | 200 OK | ✅ |
| 8 | GET `/student/test` | ADMIN | 403 Forbidden | ✅ |
| 9 | GET `/admin/test` | STUDENT | 403 Forbidden | ✅ |

**Validación de errores**:
```
✅ Token inválido → 401
✅ Token expirado → 401
✅ Sin token en endpoint protegido → 401
✅ Token válido pero rol insuficiente → 403
```

### Tests Pendientes (Sprint 4)

**Integration Tests** con `@WebMvcTest`:
```java
@Test
void shouldReturn403WhenStudentAccessesInstructorEndpoint()

@Test
void shouldReturn401WhenNoTokenProvided()

@Test
void shouldReturn200WhenValidTokenWithCorrectRole()
```

**SecurityIntegrationTest** ya existe pero requiere actualización post-cambios

---

## Estructura de Archivos Final

```
shared/
├── domain/
│   └── exception/
│       ├── DomainException.java
│       ├── NotFoundException.java
│       ├── ValidationException.java
│       ├── UnauthorizedException.java          ← NUEVO
│       └── AccessDeniedException.java          ← NUEVO
│
└── infrastructure/
    ├── config/
    │   ├── ErrorResponse.java
    │   ├── GlobalExceptionHandler.java         ← EXTENDIDO
    │   └── DataSeeder.java
    └── security/
        ├── JwtUtil.java
        ├── SecurityConfig.java                  ← COMPLETADO
        ├── JwtAuthenticationFilter.java         ← NUEVO
        └── TestSecurityController.java          ← NUEVO (temporal)
```

---

## Métricas del Sprint

- **Archivos Java nuevos**: 4
- **Archivos Java modificados**: 2
- **Líneas de código**: ~600 (sin tests/JavaDoc)
- **Endpoints de prueba**: 6
- **Tests manuales**: 9
- **Problemas resueltos**: 4 (críticos)
- **Commits**: 15
- **Branches mergeadas**: 6

---

## Integración con Sprints Anteriores

### Sprint 1 (Shared Kernel):
✅ `JwtUtil` → Usado en JwtAuthenticationFilter  
✅ `DomainException` → Base para nuevas excepciones  
✅ `GlobalExceptionHandler` → Extendido con handlers 401/403  
✅ `PasswordEncoder` → Reutilizado de SecurityConfig

### Sprint 2 (User Domain):
✅ `UserRole` → Validado en filtro JWT  
✅ `LoginUseCase` → Genera tokens compatibles  
✅ JWT claims → Contienen email y role necesarios  
✅ `RegisterUserUseCase` → Crea usuarios con roles válidos

---

## Configuración application.properties

```properties
# Security
spring.devtools.restart.enabled=false

# Logging (desarrollo)
logging.level.org.springframework.security=DEBUG
logging.level.com.elearning=DEBUG

# H2
spring.h2.console.enabled=true
spring.jpa.show-sql=true
```

---

## Lecciones Aprendidas

### 1. SecurityContext no persiste por defecto en STATELESS

**Lección**: En Spring Security 6.x, crear autenticación en filtro custom ≠ disponible en filtros posteriores.

**Solución**: Configurar explícitamente `SecurityContextRepository` adecuado.

**Aplicación futura**: Siempre usar `RequestAttributeSecurityContextRepository` en APIs JWT.

---

### 2. Orden de filtros es crítico

**Lección**: `addFilterAfter(SecurityContextHolderFilter)` era demasiado temprano.

**Solución**: `addFilterBefore(UsernamePasswordAuthenticationFilter)` es el estándar.

**Aplicación futura**: Documentar orden de filtros explícitamente.

---

### 3. GlobalExceptionHandler puede interferir

**Lección**: Handler genérico `Exception.class` captura excepciones de Spring Security.

**Solución**: Excluir o dejar que Spring Security maneje sus propias excepciones.

**Aplicación futura**: Handlers específicos primero, genérico solo como último recurso.

---

### 4. Prefijo ROLE_ no es opcional

**Lección**: Spring Security requiere `ROLE_` internamente aunque `hasRole()` no lo muestre.

**Solución**: Siempre usar `new SimpleGrantedAuthority("ROLE_" + role)`.

**Aplicación futura**: Validar authorities en logs durante desarrollo.

---

### 5. Debugging sistemático es esencial

**Lección**: Problema complejo requería logs en múltiples puntos del filter chain.

**Logs utilizados**:
```java
System.out.println("🔍 JWT Filter - Email: " + email + ", Role: " + role);
System.out.println("✅ Authentication guardada: " + authentication);
System.out.println("🚀 Antes de continuar filter chain, auth = " + context);
```

**Aplicación futura**: Logs estructurados con MDC para producción.

---

## Preparación para Sprint 4

### Infraestructura de seguridad completa:
✅ JWT authentication funcional  
✅ Role-based authorization operativa  
✅ Distinción correcta 401 vs 403  
✅ CORS configurado para desarrollo  
✅ Exception handling robusto

### Próximo Sprint: Course Management

**Día 1**: Category domain (entity, repository, use cases)  
**Día 2**: Course domain (entity con validaciones)  
**Día 3**: Module & Lesson domain (estructura jerárquica)  
**Día 4**: CreateCourseUseCase + PublishCourseUseCase  
**Día 5**: Course endpoints (INSTRUCTOR protegidos)

**Seguridad lista**: Endpoints `/instructor/courses` ya protegidos por rol

---

## Comandos Útiles

### Testing con cURL

```bash
# Login para obtener token
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"instructor@test.com","password":"Instructor1"}' \
  | jq -r '.token')

# Usar token en request protegido
curl -X POST http://localhost:8080/instructor/courses \
  -H "Authorization: Bearer $TOKEN"

# Verificar 403
curl -X POST http://localhost:8080/admin/users \
  -H "Authorization: Bearer $TOKEN"
```

### Verificar JWT en jwt.io

```bash
# Copiar token
echo $TOKEN

# Pegar en https://jwt.io
# Verificar payload:
{
  "sub": "instructor@test.com",
  "role": "INSTRUCTOR",
  "iat": 1234567890,
  "exp": 1234654290
}
```

---

## Verificación Sprint Completado

- [x] JwtAuthenticationFilter funciona correctamente
- [x] SecurityConfig con role-based authorization
- [x] RequestAttributeSecurityContextRepository configurado
- [x] Distinción correcta entre 401 y 403
- [x] CORS habilitado para desarrollo
- [x] UnauthorizedException y AccessDeniedException creadas
- [x] GlobalExceptionHandler extendido
- [x] Tests manuales completos (9/9)
- [x] TestSecurityController implementado
- [x] Logs de debug añadidos
- [x] Arquitectura hexagonal respetada
- [x] Sin dependencias de Spring Security en dominio

---

**Sprint 3 completado exitosamente - Sistema de autenticación y autorización JWT completamente funcional**