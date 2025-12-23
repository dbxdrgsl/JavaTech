# PrefSchedule Backend - API Documentation

## Overview

This is the backend module of PrefSchedule, built with Spring Boot 3.5.7. It provides REST API endpoints for managing students, instructors, packs, and courses, along with a simple Thymeleaf-based UI.

## Domain Entities

### Student
Represents students who use the system to select courses.

**Fields:**
- `id` (Long) - Auto-generated primary key
- `code` (String) - Unique student code (e.g., "S2025-001")
- `name` (String) - Full name
- `email` (String) - Email address
- `year` (Integer) - Academic year (1-4)

**Table:** `students`

### Instructor
Represents teachers assigned to courses.

**Fields:**
- `id` (Long) - Auto-generated primary key
- `name` (String) - Full name
- `email` (String) - Email address

**Table:** `instructors`

### Pack
Represents groups of optional courses for a specific year and semester.

**Fields:**
- `id` (Long) - Auto-generated primary key
- `packYear` (Integer) - Academic year
- `semester` (Integer) - Semester (1 or 2)
- `name` (String) - Pack name

**Table:** `packs`

### Course
Represents academic courses (compulsory or optional).

**Fields:**
- `id` (Long) - Auto-generated primary key
- `type` (String) - "COMPULSORY" or "OPTIONAL"
- `code` (String) - Course code
- `abbr` (String) - Abbreviation
- `name` (String) - Full course name
- `instructorId` (Long) - Foreign key to instructor
- `packId` (Long) - Foreign key to pack (for optional courses)
- `groupCount` (Integer) - Number of groups
- `description` (String) - Course description

**Table:** `courses`

## API Endpoints

### Student Endpoints

#### List All Students
```http
GET /api/students
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "code": "S2025-001",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "year": 2
  },
  ...
]
```

#### Get Student by ID
```http
GET /api/students/{id}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "code": "S2025-001",
  "name": "John Doe",
  "email": "john.doe@example.com",
  "year": 2
}
```

**Error:** `404 Not Found` if student doesn't exist

#### Create Student
```http
POST /api/students
Content-Type: application/json

{
  "code": "S2025-011",
  "name": "Jane Smith",
  "email": "jane.smith@example.com",
  "year": 3
}
```

**Response:** `201 Created`
```json
{
  "id": 11,
  "code": "S2025-011",
  "name": "Jane Smith",
  "email": "jane.smith@example.com",
  "year": 3
}
```

#### Update Student
```http
PUT /api/students/{id}
Content-Type: application/json

{
  "code": "S2025-001",
  "name": "John Updated",
  "email": "john.updated@example.com",
  "year": 3
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "code": "S2025-001",
  "name": "John Updated",
  "email": "john.updated@example.com",
  "year": 3
}
```

#### Delete Student
```http
DELETE /api/students/{id}
```

**Response:** `204 No Content`

## Sample curl Commands

### List all students
```bash
curl -X GET http://localhost:8072/api/students
```

### Get specific student
```bash
curl -X GET http://localhost:8072/api/students/1
```

### Create new student
```bash
curl -X POST http://localhost:8072/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "code": "S2025-999",
    "name": "Test Student",
    "email": "test@example.com",
    "year": 1
  }'
```

### Update student
```bash
curl -X PUT http://localhost:8072/api/students/1 \
  -H "Content-Type: application/json" \
  -d '{
    "code": "S2025-001",
    "name": "Updated Name",
    "email": "updated@example.com",
    "year": 4
  }'
```

### Delete student
```bash
curl -X DELETE http://localhost:8072/api/students/1
```

## Postman Collection

You can import these requests into Postman by creating a new collection with the following requests:

1. **Get All Students**
   - Method: GET
   - URL: `http://localhost:8072/api/students`

2. **Get Student by ID**
   - Method: GET
   - URL: `http://localhost:8072/api/students/1`

3. **Create Student**
   - Method: POST
   - URL: `http://localhost:8072/api/students`
   - Body (raw JSON):
   ```json
   {
     "code": "S2025-999",
     "name": "Test Student",
     "email": "test@example.com",
     "year": 1
   }
   ```

4. **Update Student**
   - Method: PUT
   - URL: `http://localhost:8072/api/students/1`
   - Body (raw JSON):
   ```json
   {
     "code": "S2025-001",
     "name": "Updated Name",
     "email": "updated@example.com",
     "year": 4
   }
   ```

5. **Delete Student**
   - Method: DELETE
   - URL: `http://localhost:8072/api/students/1`

## UI Endpoints

### Student List View
```http
GET /ui/students
```

Displays a formatted HTML table with all students, including:
- ID, Code, Name, Email, Year
- Link to JSON API endpoint
- Styled with CSS for better readability

## Database Setup

### Schema Creation

The application uses `schema.sql` to initialize the database. The schema is automatically executed on startup if the tables don't exist.

**Tables created:**
- `students` - Student information
- `instructors` - Instructor information
- `packs` - Course pack groupings
- `courses` - Course details with foreign keys to instructors and packs

### Configuration

Database settings are in `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/prefschedule
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
server:
  port: 8072
```

**Key settings:**
- `ddl-auto: update` - Hibernate updates schema automatically
- `show-sql: true` - SQL queries are logged to console
- `port: 8072` - Application runs on port 8072

### Initial Data Loader

The `DataLoader` component automatically runs on startup and:
1. Checks if the database is empty
2. Generates 10 sample students using Datafaker
3. Saves them to the database
4. Logs all students to console

**Sample output:**
```
--- DataLoader starting ---
Saved 10 sample students using Faker
All students in database:
Student(id=1, code=S2025-001, name=John Smith, email=john.smith@example.com, year=2)
...
--- DataLoader finished ---
```

## Running the Application

### Development Mode
```bash
./mvnw spring-boot:run
```

### With Custom Profile
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build and Run JAR
```bash
./mvnw clean package
java -jar target/PrefSchedule-0.0.1-SNAPSHOT.jar
```

## Testing

Run all tests:
```bash
./mvnw test
```

Run specific test class:
```bash
./mvnw test -Dtest=PrefScheduleApplicationTests
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── ro/uaic/dbxdrgsl/prefschedule/
│   │       ├── controller/
│   │       │   ├── StudentController.java       # REST API
│   │       │   └── StudentUIController.java     # Thymeleaf UI
│   │       ├── model/
│   │       │   └── Student.java                 # JPA Entity
│   │       ├── repository/
│   │       │   └── StudentRepository.java       # Data access
│   │       ├── service/
│   │       │   └── StudentService.java          # Business logic
│   │       ├── DataLoader.java                  # Initial data
│   │       └── PrefScheduleApplication.java     # Main class
│   └── resources/
│       ├── application.yaml                     # Configuration
│       ├── schema.sql                           # Database schema
│       └── templates/
│           └── students.html                    # Thymeleaf view
└── test/
    └── java/
        └── ro/uaic/dbxdrgsl/prefschedule/
            └── PrefScheduleApplicationTests.java
```

## Dependencies

Key dependencies (see `pom.xml` for full list):
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Thymeleaf
- PostgreSQL Driver
- H2 Database
- Lombok
- Datafaker 2.0.2

## Future Enhancements

### Lab 5 Additions
- Instructor, Pack, and Course entities
- Complete CRUD endpoints for all entities
- Advanced query methods
- Input validation
- Custom exception handling

### Lab 6-8 Plans
- Security with JWT authentication
- Real-time updates with WebSocket
- Microservices architecture
- API Gateway
- Service discovery

## Troubleshooting

### Database Connection Issues
- Ensure PostgreSQL is running
- Check credentials in `application.yaml`
- Verify database exists: `psql -U postgres -c "\l"`

### Port Already in Use
Change port in `application.yaml`:
```yaml
server:
  port: 8073
```

### Lombok Not Working
Ensure your IDE has Lombok plugin installed and annotation processing enabled.

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Reference](https://spring.io/projects/spring-data-jpa)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Datafaker Documentation](https://www.datafaker.net/)
