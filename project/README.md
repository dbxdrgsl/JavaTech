# PrefSchedule - Student Course Preference Scheduler

## Overview

**PrefSchedule** is a comprehensive multi-module project developed as part of Java Technologies Labs 4–8. This application serves as a student course preference scheduler, allowing students to select optional courses from predefined packs and manage their academic schedules.

The project demonstrates modern Java development practices using Spring Boot, JPA, PostgreSQL, Thymeleaf, and will expand to include REST APIs, security, messaging, microservices, and more throughout the lab series.

## Project Purpose

PrefSchedule addresses the academic need for managing student course preferences in a structured way:
- Students can view and select optional courses from various packs
- Instructors can be assigned to courses
- Courses are organized by type (compulsory/optional), packs, and academic details
- The system ensures data consistency and provides both API and UI access

## Project Structure

```
project/
├── backend/
│   └── pref-schedule/              # Spring Boot backend application
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/           # Java source code
│       │   │   │   └── ro/uaic/dbxdrgsl/prefschedule/
│       │   │   │       ├── controller/    # REST & UI Controllers
│       │   │   │       ├── model/         # JPA Entities
│       │   │   │       ├── repository/    # Data repositories
│       │   │   │       ├── service/       # Business logic
│       │   │   │       ├── DataLoader.java
│       │   │   │       └── PrefScheduleApplication.java
│       │   │   └── resources/
│       │   │       ├── application.yaml   # Configuration
│       │   │       ├── schema.sql         # Database schema
│       │   │       └── templates/         # Thymeleaf templates
│       │   └── test/                # Unit tests
│       ├── pom.xml                  # Maven dependencies
│       └── README.md                # Backend-specific documentation
│
├── frontend/
│   └── templates/                   # Shared UI templates
│       └── students.html            # Student list view
│
├── README.md                        # This file - main documentation
└── .copilot/
    └── pref-schedule.md             # Development guidelines for GitHub Copilot
```

## Technologies Used

### Current (Labs 4-5)
- **Java 21** - Modern Java features
- **Spring Boot 3.5.7** - Application framework
- **Spring Data JPA** - Database persistence
- **PostgreSQL** - Production database
- **H2 Database** - In-memory database for testing
- **Lombok** - Reduce boilerplate code
- **Thymeleaf** - Server-side template engine
- **Datafaker** - Generate realistic test data
- **Maven** - Dependency management and build tool

### Future Labs (6-8)
- **Spring Security** - Authentication and authorization (Lab 6)
- **Spring Messaging/WebSocket** - Real-time communication (Lab 7)
- **Microservices Architecture** - Service decomposition (Lab 8)
- **Docker** - Containerization
- **Spring Cloud** - Distributed systems

## Domain Model

The application manages four core entities:

1. **Students** - Users of the system who select courses
   - Code, Name, Email, Year

2. **Instructors** - Teachers assigned to courses
   - Name, Email

3. **Packs** - Groups of optional courses for specific year/semester
   - Pack Year, Semester, Name

4. **Courses** - Academic courses (compulsory or optional)
   - Type, Code, Abbreviation, Name, Description
   - Associated Instructor and Pack
   - Group Count

## Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **PostgreSQL 14+** (or use H2 for quick testing)
- **Git**

### Database Setup

#### Option 1: PostgreSQL (Recommended for Production)

1. Install PostgreSQL
2. Create database:
```sql
CREATE DATABASE prefschedule;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE prefschedule TO postgres;
```

3. Update `application.yaml` if needed (default config points to localhost:5432)

#### Option 2: H2 In-Memory Database (Quick Testing)

Update `application.yaml` to use H2:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:prefschedule
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
```

### Running the Backend

1. Clone the repository:
```bash
git clone https://github.com/dbxdrgsl/JavaTech.git
cd JavaTech/project/backend/pref-schedule
```

2. Build the project:
```bash
./mvnw clean install
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8072`

### Accessing the Application

#### Web UI
- **Student List**: http://localhost:8072/ui/students
  - View all students in a formatted table
  - Access API links

#### REST API Endpoints
- **GET** `/api/students` - List all students
- **GET** `/api/students/{id}` - Get student by ID
- **POST** `/api/students` - Create new student
- **PUT** `/api/students/{id}` - Update existing student
- **DELETE** `/api/students/{id}` - Delete student

See [backend/pref-schedule/README.md](backend/pref-schedule/README.md) for detailed API documentation.

## Data Generation

The application includes a `DataLoader` component that automatically generates 10 sample students using the Datafaker library when the database is empty. This provides immediate test data for development and demonstration.

## Development Workflow

### Lab 4: Persistence Layer
- ✅ Project structure setup
- ✅ JPA entities (Student, Instructor, Pack, Course)
- ✅ Database schema
- ✅ Basic CRUD operations
- ✅ Data loader with Faker

### Lab 5: REST API
- ✅ REST Controllers
- ✅ Service layer
- ✅ Exception handling
- 🔄 Advanced queries and filtering
- 🔄 Validation

### Lab 6: Security (Coming Soon)
- 🔜 Spring Security configuration
- 🔜 User authentication
- 🔜 Role-based authorization
- 🔜 JWT tokens

### Lab 7: Messaging (Coming Soon)
- 🔜 WebSocket support
- 🔜 Real-time notifications
- 🔜 Message queues
- 🔜 Event-driven architecture

### Lab 8: Microservices (Coming Soon)
- 🔜 Service decomposition
- 🔜 API Gateway
- 🔜 Service discovery
- 🔜 Distributed configuration

## Testing

Run tests with:
```bash
./mvnw test
```

## Building for Production

Create a production JAR:
```bash
./mvnw clean package -DskipTests
java -jar target/PrefSchedule-0.0.1-SNAPSHOT.jar
```

## Contributing

This project is part of the Java Technologies course. For development guidelines and coding conventions, see [.copilot/pref-schedule.md](.copilot/pref-schedule.md).

## License

This project is developed for educational purposes as part of the Java Technologies course at UAIC.

## Authors

- **DBXDRGSL** - Initial work and lab implementations

## Acknowledgments

- Java Technologies course materials
- Spring Boot documentation
- Thymeleaf documentation
- Datafaker library
