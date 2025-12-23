# PrefSchedule - 4. Compulsory Implementation

## Overview
This project implements the **4. Java Persistence API (Compulsory)** requirements for the PrefSchedule system. It's a Spring Boot application that manages students, instructors, packs, and courses with support for Spring Data JPA and PostgreSQL.

## Features Implemented
✅ Spring Boot Project with Spring Data JPA support  
✅ PostgreSQL and H2 database support  
✅ SQL schema with tables for students, instructors, packs, and courses  
✅ Student entity class with JPA annotations  
✅ StudentRepository interface extending JpaRepository  
✅ CommandLineRunner for testing database operations  
✅ Complete database schema with foreign key relationships  

## Technologies Used
- **Java**: 17
- **Spring Boot**: 3.5.7
- **Spring Data JPA**: For database operations
- **PostgreSQL**: Production database (configurable)
- **H2 Database**: In-memory database for development/testing
- **Lombok**: For reducing boilerplate code
- **Maven**: Build tool

## Project Structure
```
project/PrefSchedule/
├── pom.xml                                    # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/ro/uaic/dbxdrgsl/prefschedule/
│   │   │   ├── PrefScheduleApplication.java  # Main Spring Boot application
│   │   │   ├── DataLoader.java               # CommandLineRunner for testing
│   │   │   ├── model/
│   │   │   │   └── Student.java              # Student entity
│   │   │   └── repository/
│   │   │       └── StudentRepository.java    # JPA repository
│   │   └── resources/
│   │       ├── application.properties         # Application configuration
│   │       └── schema.sql                     # Database schema
│   └── test/
│       └── java/ro/uaic/dbxdrgsl/prefschedule/
│           └── PrefScheduleApplicationTests.java  # Basic test
└── .gitignore                                 # Git ignore file
```

## Database Schema
The application creates the following tables:

### students
- `id` (BIGINT, PRIMARY KEY, AUTO-INCREMENT)
- `code` (VARCHAR, UNIQUE, NOT NULL) - Student code
- `name` (VARCHAR, NOT NULL) - Student name
- `email` (VARCHAR) - Student email
- `student_year` (INT, NOT NULL) - Year of study

### instructors
- `id` (BIGINT, PRIMARY KEY, AUTO-INCREMENT)
- `name` (VARCHAR, NOT NULL) - Instructor name
- `email` (VARCHAR) - Instructor email

### packs
- `id` (BIGINT, PRIMARY KEY, AUTO-INCREMENT)
- `pack_year` (INT, NOT NULL) - Academic year
- `semester` (INT, NOT NULL) - Semester
- `name` (VARCHAR, NOT NULL) - Pack name

### courses
- `id` (BIGINT, PRIMARY KEY, AUTO-INCREMENT)
- `type` (VARCHAR, NOT NULL) - Course type ('COMPULSORY' or 'OPTIONAL')
- `code` (VARCHAR, NOT NULL) - Course code
- `abbr` (VARCHAR) - Course abbreviation
- `name` (VARCHAR, NOT NULL) - Course name
- `instructor_id` (BIGINT, FOREIGN KEY) - Reference to instructors table
- `pack_id` (BIGINT, FOREIGN KEY) - Reference to packs table
- `group_count` (INT) - Number of groups
- `description` (TEXT) - Course description

## How to Test the Implementation

### Prerequisites
- Java 17 or higher installed
- Maven installed (or use the included Maven wrapper)

### Method 1: Run Tests
The simplest way to verify the implementation is to run the unit tests:

```bash
cd project/PrefSchedule
mvn test
```

**Expected Output:**
- Tests should pass successfully
- You'll see the DataLoader output showing a sample student being created
- Console output will display: "Saved sample student" and "All students in database"

### Method 2: Run the Application
Run the Spring Boot application to test it interactively:

```bash
cd project/PrefSchedule
mvn spring-boot:run
```

**Expected Output:**
```
...
Started PrefScheduleApplication in X.XXX seconds
--- DataLoader starting ---
Saved sample student: Student(id=1, code=S2025-001, name=Ion Popescu, email=ion.popescu@example.com, year=2)
All students in database:
Student(id=1, code=S2025-001, name=Ion Popescu, email=ion.popescu@example.com, year=2)
--- DataLoader finished ---
```

The application will:
1. Start a Tomcat server on port 8080
2. Initialize an H2 in-memory database
3. Execute the schema.sql script to create tables
4. Run the CommandLineRunner (DataLoader) which:
   - Checks if any students exist
   - Creates a sample student if none exist
   - Displays all students in the database

### Method 3: Access H2 Console (Optional)
When the application is running, you can access the H2 database console:

1. Start the application: `mvn spring-boot:run`
2. Open a browser and navigate to: `http://localhost:8080/h2-console`
3. Use the connection details shown in the console output (look for "Database available at...")
   - JDBC URL: `jdbc:h2:mem:XXXXXXXXX` (shown in console)
   - Username: `SA`
   - Password: (leave empty)
4. Click "Connect"
5. You can now run SQL queries:
   ```sql
   SELECT * FROM students;
   SELECT * FROM instructors;
   SELECT * FROM packs;
   SELECT * FROM courses;
   ```

### Method 4: Verify Database Schema
To verify the schema was created correctly:

1. Start the application
2. Connect to H2 console (see Method 3)
3. Run: `SHOW TABLES;`
4. Verify that tables exist: `STUDENTS`, `INSTRUCTORS`, `PACKS`, `COURSES`
5. Check table structure: `SHOW COLUMNS FROM STUDENTS;`

### Method 5: Build and Package
To build the project and create a JAR file:

```bash
cd project/PrefSchedule
mvn clean package
```

This will:
- Compile the code
- Run all tests
- Create a JAR file in `target/PrefSchedule-0.0.1-SNAPSHOT.jar`

You can then run the JAR:
```bash
java -jar target/PrefSchedule-0.0.1-SNAPSHOT.jar
```

## Configuration

### Using H2 (Default - for development)
The application is configured to use H2 by default. No configuration needed!

### Switching to PostgreSQL (Optional)
To use PostgreSQL instead:

1. Install and start PostgreSQL
2. Create a database: `CREATE DATABASE prefschedule;`
3. Edit `src/main/resources/application.properties`:
   ```properties
   # Uncomment and configure these lines:
   spring.datasource.url=jdbc:postgresql://localhost:5432/prefschedule
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.hibernate.ddl-auto=none
   ```
4. Restart the application

## Verification Checklist
✅ Project builds successfully (`mvn compile`)  
✅ Tests pass (`mvn test`)  
✅ Application starts without errors (`mvn spring-boot:run`)  
✅ DataLoader executes and creates sample student  
✅ Database tables are created from schema.sql  
✅ Student entity is persisted correctly  
✅ StudentRepository operations work  

## Next Steps
This implementation covers the **Compulsory (1p)** requirements. The next steps would include:
- Creating entity classes for Instructor, Pack, and Course
- Adding more repositories
- Implementing CRUD operations
- Adding more comprehensive tests

## Troubleshooting

### Port 8080 is already in use
If you see an error about port 8080 being in use:
- Stop any other applications running on port 8080
- Or change the port in `application.properties`: `server.port=8081`

### Build fails with "release version X not supported"
- Verify Java version: `java -version`
- Ensure Java 17 or higher is installed
- Update `pom.xml` if using a different Java version

### Tests fail
- Check console output for error messages
- Verify all dependencies are downloaded: `mvn clean install`
- Ensure no other instance of the application is running

## Author
Database and XML Repository Systems Laboratory (dbxdrgsl)  
Faculty of Computer Science, "Alexandru Ioan Cuza" University of Iași

## License
See LICENSE file in repository root.
