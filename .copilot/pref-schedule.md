# PrefSchedule - GitHub Copilot Development Guidelines

This document provides instructions and conventions for GitHub Copilot to assist with the ongoing development of the PrefSchedule project across Labs 4-8.

## Project Context

PrefSchedule is a student course preference scheduling system built with Spring Boot, JPA, and Thymeleaf. The project evolves through multiple labs:
- **Lab 4**: Persistence layer with JPA entities
- **Lab 5**: REST API development
- **Lab 6**: Security and authentication
- **Lab 7**: Messaging and real-time features
- **Lab 8**: Microservices architecture

## Code Style Conventions

### General Guidelines
- Use **Java 21** features where appropriate
- Follow **Spring Boot best practices**
- Maintain **clean code** principles
- Write **self-documenting code** with clear naming
- Add comments only when necessary to explain complex logic

### Naming Conventions

#### Packages
- Base package: `ro.uaic.dbxdrgsl.prefschedule`
- Subpackages: `controller`, `service`, `repository`, `model`, `dto`, `config`, `exception`, etc.

#### Classes
- **Entities**: Singular nouns (e.g., `Student`, `Course`, `Instructor`)
- **Controllers**: `{Entity}Controller` (REST) or `{Entity}UIController` (Thymeleaf)
- **Services**: `{Entity}Service`
- **Repositories**: `{Entity}Repository`
- **DTOs**: `{Entity}DTO` or `{Purpose}Request`/`{Purpose}Response`

#### Methods
- **CRUD operations**: `findAll()`, `findById()`, `create()`, `update()`, `delete()`
- **Query methods**: Use descriptive names (e.g., `findByYear()`, `findByInstructorId()`)
- **Boolean methods**: Start with `is`, `has`, `can` (e.g., `isOptional()`, `hasPrerequisites()`)

#### Variables
- Use **camelCase** for all variables
- Use **meaningful names** (avoid single letters except in loops)
- Database column names: Use **snake_case** (e.g., `student_year`, `pack_id`)

### File Organization

#### Controller Layer
```java
@RestController
@RequestMapping("/api/{resource}")
@RequiredArgsConstructor
public class {Entity}Controller {
    private final {Entity}Service service;
    
    @GetMapping
    public List<{Entity}> getAll() { ... }
    
    @GetMapping("/{id}")
    public {Entity} getById(@PathVariable Long id) { ... }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public {Entity} create(@RequestBody {Entity} entity) { ... }
    
    @PutMapping("/{id}")
    public {Entity} update(@PathVariable Long id, @RequestBody {Entity} entity) { ... }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { ... }
}
```

#### Service Layer
```java
@Service
@RequiredArgsConstructor
public class {Entity}Service {
    private final {Entity}Repository repository;
    
    public List<{Entity}> findAll() { ... }
    public {Entity} findById(Long id) { ... }
    public {Entity} create({Entity} entity) { ... }
    public {Entity} update(Long id, {Entity} entity) { ... }
    public void delete(Long id) { ... }
}
```

#### Entity Layer
```java
@Entity
@Table(name = "{table_name}")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class {Entity} {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Additional fields with appropriate JPA annotations
}
```

### Dependency Injection
- Always use **constructor injection** via `@RequiredArgsConstructor` (Lombok)
- Avoid field injection (`@Autowired` on fields)
- Never use setter injection

### Exception Handling
- Create custom exceptions in `exception` package
- Use `@ControllerAdvice` for global exception handling
- Return appropriate HTTP status codes

### Validation
- Use **Bean Validation** annotations (`@NotNull`, `@NotBlank`, `@Email`, etc.)
- Validate at the controller layer with `@Valid`
- Custom validators should implement `ConstraintValidator`

## Database Conventions

### Schema Design
- Use `IDENTITY` generation strategy for primary keys (compatible with both PostgreSQL and H2)
- Foreign keys: `{entity}_id` (e.g., `instructor_id`, `pack_id`)
- Boolean columns: Prefix with `is_` (e.g., `is_active`)
- Timestamps: Use `created_at`, `updated_at`

### JPA Annotations
- Use `@Column(name = "...")` for fields that differ from property names
- Mark nullable fields explicitly: `@Column(nullable = false)` or `nullable = true`
- Use `@ManyToOne`, `@OneToMany`, `@ManyToMany` appropriately with `fetch` strategies

## Testing Guidelines

### Unit Tests
- Test classes: `{Class}Test`
- Use **JUnit 5** (`@Test`, `@BeforeEach`, `@AfterEach`)
- Use **Mockito** for mocking dependencies
- Test naming: `methodName_condition_expectedResult`

Example:
```java
@Test
void findById_existingId_returnsStudent() {
    // Arrange
    // Act
    // Assert
}
```

### Integration Tests
- Use `@SpringBootTest` for full context
- Use `@DataJpaTest` for repository tests
- Use `@WebMvcTest` for controller tests

## Adding New Entities

When adding a new entity (e.g., `Instructor`, `Pack`, `Course`):

1. **Create Entity Class** in `model` package
   - Add JPA annotations
   - Use Lombok annotations (@Data, @Builder, etc.)
   - Define relationships with other entities

2. **Create Repository Interface** in `repository` package
   - Extend `JpaRepository<Entity, Long>`
   - Add custom query methods if needed

3. **Create Service Class** in `service` package
   - Implement business logic
   - Handle exceptions

4. **Create Controller Class** in `controller` package
   - REST endpoints: `@RestController`
   - UI endpoints: `@Controller` with `@RequestMapping("/ui")`

5. **Update schema.sql**
   - Add table definition
   - Define constraints and relationships

6. **Update DataLoader** (if applicable)
   - Add sample data generation using Faker

7. **Create Thymeleaf Template** (for UI)
   - Place in `src/main/resources/templates/`
   - Follow existing template structure

## Continuing Lab Implementation

### Lab 5: REST API Expansion
When implementing Lab 5 features:
- Add remaining entities (Instructor, Pack, Course)
- Implement CRUD endpoints for all entities
- Add query methods in repositories
- Implement validation
- Add exception handling with `@ControllerAdvice`
- Create DTOs for complex requests/responses
- Update documentation

### Lab 6: Security
When adding security:
- Add Spring Security dependency
- Create `SecurityConfig` in `config` package
- Implement user authentication
- Add JWT token generation/validation
- Secure endpoints with role-based access
- Create `User` entity and `UserRepository`

### Lab 7: Messaging
When adding messaging:
- Add Spring WebSocket dependency
- Create `WebSocketConfig` in `config` package
- Implement message handlers
- Add notification service
- Create WebSocket endpoints
- Update UI with real-time features

### Lab 8: Microservices
When decomposing to microservices:
- Split into separate modules (student-service, course-service, etc.)
- Add Spring Cloud dependencies
- Create API Gateway
- Implement service discovery (Eureka)
- Add distributed configuration
- Implement circuit breakers

## UI Development Guidelines

### Thymeleaf Templates
- Place in `src/main/resources/templates/`
- Use Thymeleaf namespace: `xmlns:th="http://www.thymeleaf.org"`
- Follow existing styling patterns
- Include navigation and links to API endpoints

### Template Structure
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>PrefSchedule - {Page Title}</title>
    <style>
        /* Inline styles or link to CSS */
    </style>
</head>
<body>
    <h1>{Page Title}</h1>
    <!-- Content here -->
</body>
</html>
```

### UI Controllers
- Use `@Controller` (not `@RestController`)
- Return view names (String)
- Add data to `Model`
- Place in `controller` package with suffix `UIController`

## Configuration Management

### application.yaml Structure
```yaml
spring:
  application:
    name: pref-schedule
  datasource:
    url: jdbc:postgresql://localhost:5432/prefschedule
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  thymeleaf:
    cache: false
    prefix: classpath:/templates/
    suffix: .html

server:
  port: 8072

# Custom application properties
app:
  name: PrefSchedule
  version: 1.0.0
```

## Documentation Requirements

When adding new features:
1. Update `project/README.md` with high-level changes
2. Update `project/backend/pref-schedule/README.md` with API details
3. Add JavaDoc comments for public methods
4. Include curl examples for new endpoints
5. Update this file with new conventions

## Git Workflow

### Commit Messages
- Use imperative mood: "Add feature" not "Added feature"
- Start with verb: Add, Update, Fix, Remove, Refactor
- Be specific: "Add Student CRUD endpoints" not "Update code"

### Branch Naming
- Feature branches: `feature/description`
- Bug fixes: `fix/description`
- Lab work: `lab/lab-number-description`

## Common Patterns

### Error Response
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
```

### DTO Mapping
```java
public class StudentMapper {
    public static StudentDTO toDTO(Student student) {
        return StudentDTO.builder()
            .id(student.getId())
            .code(student.getCode())
            .name(student.getName())
            .build();
    }
    
    public static Student toEntity(StudentDTO dto) {
        return Student.builder()
            .code(dto.getCode())
            .name(dto.getName())
            .build();
    }
}
```

## Quick Reference

### Add New REST Endpoint
1. Add method to Service
2. Add method to Controller
3. Update README with endpoint documentation
4. Test with curl/Postman

### Add New UI Page
1. Create Thymeleaf template in `templates/`
2. Create or update UIController method
3. Copy template to `project/frontend/templates/` (for reference)
4. Test in browser

### Add New Dependency
1. Add to `pom.xml` dependencies section
2. Run `./mvnw clean install`
3. Update documentation
4. Commit changes

## Tips for Copilot

- Always follow the established package structure
- Use Lombok to reduce boilerplate
- Maintain consistency with existing code
- Prioritize readability over cleverness
- When in doubt, follow Spring Boot conventions
- Test new features thoroughly
- Update documentation alongside code changes
