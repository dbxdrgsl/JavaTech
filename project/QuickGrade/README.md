# QuickGrade - Student Grades Management System

## Overview

QuickGrade is a Spring Boot application that publishes student grade events to a RabbitMQ message queue. It serves as the grade entry system that communicates with the PrefSchedule application.

## Features

- **Grade Event Publishing**: Publishes student grades to RabbitMQ
- **REST API**: Endpoints for publishing individual or test grade events
- **Grade Statistics**: Tracks and exposes statistics about published grades (Section 8)
- **REST Endpoints**: Provides statistics for other microservices to query
- **JSON Message Format**: Structured grade events with student code, course code, and grade
- **RabbitMQ Integration**: Uses Spring AMQP for reliable message delivery

## Architecture

```
REST API → GradeController → GradePublisher → RabbitMQ (grade.queue)
                           ↓
                    Statistics Service (Section 8)
                           ↓
                    REST API (statistics endpoint)
```

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring AMQP (RabbitMQ)**
- **Lombok**
- **Maven**

## Project Structure

```
QuickGrade/
├── src/
│   ├── main/
│   │   ├── java/ro/uaic/dbxdrgsl/quickgrade/
│   │   │   ├── QuickGradeApplication.java
│   │   │   ├── config/
│   │   │   │   └── RabbitMQConfig.java
│   │   │   ├── controller/
│   │   │   │   └── GradeController.java
│   │   │   ├── dto/
│   │   │   │   ├── GradeEvent.java
│   │   │   │   └── GradeStatistics.java
│   │   │   └── service/
│   │   │       ├── GradePublisher.java
│   │   │       └── GradeStatisticsService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

1. **Java 17** or higher
2. **Maven 3.6+**
3. **RabbitMQ** running on localhost:5672

### Install RabbitMQ with Docker

```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

### Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on **port 8081**.

## API Endpoints

### 1. Publish Grade Event

Publishes a single grade event to RabbitMQ.

**Endpoint:** `POST /api/grades/publish`

**Request Body:**
```json
{
  "studentCode": "S001",
  "courseCode": "MATH101",
  "grade": 9.5
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8081/api/grades/publish \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "S001",
    "courseCode": "MATH101",
    "grade": 9.5
  }'
```

**Response:**
```
Grade event published successfully
```

### 2. Test Publish (Sample Data)

Publishes 3 sample grade events for testing.

**Endpoint:** `GET /api/grades/test`

**cURL Example:**
```bash
curl http://localhost:8081/api/grades/test
```

**Response:**
```
3 sample grade events published successfully
```

### 3. Get Grade Statistics (Section 8. Compulsory)

Get statistics about published grades. This endpoint is called by PrefSchedule microservice.

**Endpoint:** `GET /api/grades/statistics`

**cURL Example:**
```bash
curl http://localhost:8081/api/grades/statistics
```

**Response:**
```json
{
  "totalGradesPublished": 3,
  "averageGrade": 8.7,
  "minGrade": 7.9,
  "maxGrade": 9.5,
  "lastPublishedStudent": "S003",
  "lastPublishedCourse": "PHY101"
}
```

### 4. Reset Statistics

Reset the statistics (for testing purposes).

**Endpoint:** `DELETE /api/grades/statistics`

**cURL Example:**
```bash
curl -X DELETE http://localhost:8081/api/grades/statistics
```

**Response:**
```
Statistics reset successfully
```

### 2. Publish Test Grades

Publishes 3 sample grade events for testing purposes.

**Endpoint:** `GET /api/grades/test`

**cURL Example:**
```bash
curl http://localhost:8081/api/grades/test
```

**Response:**
```
3 sample grade events published successfully
```

**Sample Events Published:**
- Student: S001, Course: MATH101, Grade: 9.5
- Student: S002, Course: CS101, Grade: 8.7
- Student: S003, Course: PHY101, Grade: 7.9

## Configuration

Configuration is in `src/main/resources/application.properties`:

```properties
# Application
spring.application.name=quickgrade
server.port=8081

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

## Grade Event Structure

```java
public class GradeEvent {
    private String studentCode;  // e.g., "S001"
    private String courseCode;   // e.g., "MATH101"
    private Double grade;        // e.g., 9.5
}
```

## RabbitMQ Configuration

- **Queue Name:** `grade.queue`
- **Durable:** Yes (survives broker restarts)
- **Message Format:** JSON
- **Message Converter:** Jackson2JsonMessageConverter

## Testing

### Run Unit Tests

```bash
mvn test
```

### Manual Testing

1. Start RabbitMQ
2. Start QuickGrade application
3. Start PrefSchedule application (consumer)
4. Use curl or Postman to publish grades
5. Check PrefSchedule console for received messages

### Using RabbitMQ Management UI

1. Open: http://localhost:15672
2. Login: guest / guest
3. Navigate to Queues → `grade.queue`
4. View message rates and queue statistics

## Integration with PrefSchedule

QuickGrade publishes grade events that are consumed by the PrefSchedule application:

```
QuickGrade (8081) → RabbitMQ (5672) → PrefSchedule (8080)
```

PrefSchedule receives the messages and prints them to the console.

## Development

### Adding New Features

1. **New DTO**: Add to `dto/` package
2. **New Service**: Add to `service/` package
3. **New Endpoint**: Add to `controller/` package

### Logging

Logging is configured in `application.properties`:

```properties
logging.level.ro.uaic.dbxdrgsl.quickgrade=INFO
```

Change to `DEBUG` for more detailed logs:

```properties
logging.level.ro.uaic.dbxdrgsl.quickgrade=DEBUG
```

## Troubleshooting

### Port 8081 Already in Use

Change the port in `application.properties`:

```properties
server.port=8082
```

### Cannot Connect to RabbitMQ

1. Verify RabbitMQ is running:
   ```bash
   docker ps | grep rabbitmq
   ```

2. Check RabbitMQ logs:
   ```bash
   docker logs rabbitmq
   ```

3. Restart RabbitMQ:
   ```bash
   docker restart rabbitmq
   ```

### Build Failures

Clean and rebuild:

```bash
mvn clean
mvn install -U
```

## Implementation Details

### Publisher Service

```java
@Service
public class GradePublisher {
    private final RabbitTemplate rabbitTemplate;
    
    public void publishGrade(GradeEvent gradeEvent) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.GRADE_QUEUE, 
            gradeEvent
        );
    }
}
```

### Message Conversion

Jackson2JsonMessageConverter automatically converts:
- Java objects → JSON (when publishing)
- JSON → Java objects (when consuming)

## Related Documentation

- **[MESSAGING_TESTING.md](../MESSAGING_TESTING.md)** - Complete testing guide
- **[PrefSchedule README](../PrefSchedule/readme.md)** - Consumer application

## License

This project is part of the JavaTech course assignment.

## Author

PrefSchedule Project - Section 7. Compulsory Implementation
