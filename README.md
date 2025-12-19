# E-Learning Platform - Hexagonal Architecture

Plataforma MVP de venta y consumo de cursos online construida bajo Arquitectura Hexagonal.

## Objetivo

Proyecto de aprendizaje enfocado en dominar los principios de arquitectura hexagonal (puertos y adaptadores), separación de capas y dependencias invertidas.

## Stack Técnico

- **Java**: 21 (LTS)
- **Spring Boot**: 3.2.x
- **Spring Data JPA**: Persistencia
- **Spring Security**: Autenticación/Autorización
- **H2 Database**: Desarrollo (in-memory)
- **PostgreSQL**: Producción (futuro)
- **JWT**: Autenticación con tokens
- **RabbitMQ**: Mensajería asíncrona (futuro)
- **Stripe**: Procesamiento de pagos (futuro)
- **Lombok**: Reducción de boilerplate
- **JUnit 5 + Mockito**: Testing

## Requisitos

- JDK 21 o superior
- Maven 3.8+
- IDE recomendado: IntelliJ IDEA o Eclipse

## Instalación y Ejecución

1. **Clonar el repositorio**:
```bash
   git clone https://github.com/AlexIzquierdo21/elearning-platform.git
   cd elearning-platform
```

2. **Compilar el proyecto**:
```bash
   ./mvnw clean install
```

3. **Ejecutar la aplicación**:
```bash
   ./mvnw spring-boot:run
```

4. **Acceder a la aplicación**:
    - API: `http://localhost:8080`
    - H2 Console: `http://localhost:8080/h2-console`
        - JDBC URL: `jdbc:h2:mem:elearning`
        - Username: `sa`
        - Password: _(vacío)_

## Estructura del Proyecto

Arquitectura hexagonal organizada en bounded contexts:
```
src/main/java/com/elearning/elearning_platform/
├── shared/               # Código compartido entre contextos
│   ├── domain/          # Excepciones y Value Objects
│   └── infrastructure/  # Configuración global, seguridad, manejo de errores
│
├── user/                # Bounded Context: Usuarios y Autenticación
│   ├── domain/         # Modelo de dominio, excepciones, ports OUT
│   ├── application/    # Casos de uso, ports IN
│   └── infrastructure/ # Adaptadores (web, persistencia, config)
│
├── course/             # Bounded Context: Cursos, Módulos, Lecciones
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
└── enrollment/         # Bounded Context: Compras y Acceso a Contenido
    ├── domain/
    ├── application/
    └── infrastructure/
```

### Principios de Arquitectura Hexagonal

- **Independencia de frameworks**: El dominio no depende de Spring, JPA ni HTTP
- **Puertos y Adaptadores**:
    - **Puertos IN** (application/port/in): Definen casos de uso
    - **Puertos OUT** (domain/port/out): Contratos con infraestructura
- **Flujo de dependencias**: `Infrastructure → Application → Domain` (nunca al revés)

## Ejecutar Tests
```bash
# Todos los tests
./mvnw test

# Tests de un módulo específico
./mvnw test -Dtest=JwtUtilTest
```

## Estado del Proyecto

### Sprint 1 - Fundación Hexagonal (COMPLETADO)
- [x] Setup proyecto y estructura de carpetas
- [x] Excepciones de dominio (DomainException, NotFoundException, ValidationException)
- [x] Value Objects (Email, Money)
- [x] GlobalExceptionHandler con ErrorResponse
- [x] JwtUtil para generación y validación de tokens
- [x] SecurityConfig básico con PasswordEncoder

### Sprint 2 - User Domain & Auth (EN PROGRESO)
- [ ] User domain model
- [ ] RegisterUserUseCase y LoginUseCase
- [ ] AuthController (POST /auth/register, POST /auth/login)
- [ ] Persistencia JPA
- [ ] Tests de integración

### Próximos Sprints
- **Sprint 3**: Security por roles con JWT
- **Sprint 4**: Course Domain y catálogo
- **Sprint 5**: Módulos y lecciones
- **Sprint 6**: Sistema de compras con Stripe
- **Sprint 7**: Acceso a contenido (gatekeeper)
- **Sprint 8**: RabbitMQ y notificaciones
- **Sprint 9**: Admin panel

## Documentación

- **Requisitos funcionales**: Ver `FUNCTIONAL.md`
- **Roadmap detallado**: Sprints definidos en `FUNCTIONAL.md` sección 10

## Configuración

La configuración principal está en `src/main/resources/application.properties`:

- Base de datos H2 en memoria
- JWT secret key (cambiar en producción)
- Logging habilitado para debugging

## Contribuir

Este es un proyecto de aprendizaje personal. Si encuentras algo interesante o tienes sugerencias, ¡son bienvenidas!

## Licencia

Proyecto educativo sin licencia específica.

## Autor

Alex Izquierdo - [GitHub](https://github.com/AlexIzquierdo21)