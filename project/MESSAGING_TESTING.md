# Messaging Testing Guide - Section 7. Compulsory

## Overview

This guide explains how to test the messaging functionality between **QuickGrade** (grade publisher) and **PrefSchedule** (grade consumer) using RabbitMQ.

## Architecture

```
QuickGrade (Port 8081)     RabbitMQ Broker          PrefSchedule (Port 8080)
     [Publisher]      →    [grade.queue]      →         [Consumer]
                                                     (Prints to console)
```

## Prerequisites

### 1. Install RabbitMQ

#### Option A: Docker (Recommended)
```bash
# Run RabbitMQ with management UI
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management

# Access RabbitMQ Management UI at: http://localhost:15672
# Default credentials: guest / guest
```

#### Option B: Manual Installation

**Ubuntu/Debian:**
```bash
sudo apt-get update
sudo apt-get install rabbitmq-server
sudo service rabbitmq-server start
```

**macOS:**
```bash
brew install rabbitmq
brew services start rabbitmq
```

**Windows:**
Download and install from: https://www.rabbitmq.com/download.html

### 2. Verify RabbitMQ is Running

```bash
# Check RabbitMQ status
sudo rabbitmqctl status

# Or check if port 5672 is listening
netstat -an | grep 5672
```

## Testing Steps

### Step 1: Start PrefSchedule (Consumer)

```bash
cd project/PrefSchedule
mvn clean install
mvn spring-boot:run
```

**Expected Output:**
```
Started PrefScheduleApplication in X seconds
Waiting for grade events from QuickGrade...
```

The application will connect to RabbitMQ and wait for messages.

### Step 2: Start QuickGrade (Publisher)

Open a new terminal:

```bash
cd project/QuickGrade
mvn clean install
mvn spring-boot:run
```

**Expected Output:**
```
Started QuickGradeApplication in X seconds
QuickGrade ready to publish grade events
```

### Step 3: Publish Test Grade Events

#### Method 1: Using the Test Endpoint

```bash
# Publish 3 sample grade events
curl http://localhost:8081/api/grades/test
```

**Expected Response:**
```
3 sample grade events published successfully
```

#### Method 2: Publishing Custom Grade Events

```bash
# Publish a single custom grade event
curl -X POST http://localhost:8081/api/grades/publish \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "S001",
    "courseCode": "MATH101",
    "grade": 9.5
  }'
```

**Expected Response:**
```
Grade event published successfully
```

### Step 4: Verify Messages in PrefSchedule Console

Switch back to the PrefSchedule terminal. You should see:

```
========================================
GRADE EVENT RECEIVED:
  Student: S001
  Course:  MATH101
  Grade:   9.5
========================================
```

## Testing Scenarios

### Scenario 1: Multiple Grade Events

Publish multiple grades in sequence:

```bash
# Publish first grade
curl -X POST http://localhost:8081/api/grades/publish \
  -H "Content-Type: application/json" \
  -d '{"studentCode": "S001", "courseCode": "MATH101", "grade": 9.5}'

# Publish second grade
curl -X POST http://localhost:8081/api/grades/publish \
  -H "Content-Type: application/json" \
  -d '{"studentCode": "S002", "courseCode": "CS101", "grade": 8.7}'

# Publish third grade
curl -X POST http://localhost:8081/api/grades/publish \
  -H "Content-Type: application/json" \
  -d '{"studentCode": "S003", "courseCode": "PHY101", "grade": 7.9}'
```

All three messages should appear in the PrefSchedule console.

### Scenario 2: Consumer Offline Testing

1. Stop PrefSchedule (Ctrl+C)
2. Publish several grade events using QuickGrade
3. Start PrefSchedule again
4. All queued messages should be delivered immediately

This demonstrates **message persistence** - messages are not lost even when the consumer is offline.

### Scenario 3: Using RabbitMQ Management UI

1. Open browser: http://localhost:15672
2. Login with: guest / guest
3. Go to "Queues" tab
4. You should see `grade.queue`
5. Click on the queue to see:
   - Message rate
   - Ready messages count
   - Unacknowledged messages
6. Publish messages and watch the statistics update in real-time

## Troubleshooting

### Problem: Connection Refused to RabbitMQ

**Solution:**
```bash
# Check if RabbitMQ is running
sudo service rabbitmq-server status

# Or with Docker
docker ps | grep rabbitmq

# Restart if needed
docker restart rabbitmq
```

### Problem: Messages Not Being Received

**Solution:**
1. Check PrefSchedule logs for connection errors
2. Verify both applications are using the same queue name
3. Check RabbitMQ Management UI to see if messages are in the queue
4. Ensure both applications use the same message format (JSON)

### Problem: Port Already in Use

**Error:** "Port 8081 already in use" (QuickGrade)

**Solution:**
```bash
# Find process using port 8081
lsof -i :8081

# Kill the process
kill -9 <PID>

# Or change port in application.properties
server.port=8082
```

### Problem: Maven Build Fails

**Solution:**
```bash
# Clean and rebuild
mvn clean
mvn install

# If dependencies issues
mvn dependency:purge-local-repository
mvn clean install
```

## Understanding the Flow

1. **QuickGrade** receives a POST request to publish a grade
2. **GradePublisher** service converts GradeEvent to JSON and sends to RabbitMQ
3. **RabbitMQ** stores the message in `grade.queue`
4. **PrefSchedule's GradeConsumer** receives the message via `@RabbitListener`
5. Message is deserialized from JSON to GradeEvent object
6. **Console output** displays the grade information

## Advanced Testing with Postman

### Import Collection

Create a Postman collection:

```json
{
  "info": {
    "name": "QuickGrade API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Publish Grade Event",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"studentCode\": \"S001\",\n  \"courseCode\": \"MATH101\",\n  \"grade\": 9.5\n}"
        },
        "url": {
          "raw": "http://localhost:8081/api/grades/publish",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["api", "grades", "publish"]
        }
      }
    },
    {
      "name": "Publish Test Grades",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:8081/api/grades/test",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["api", "grades", "test"]
        }
      }
    }
  ]
}
```

## Performance Testing

### Load Testing Script

```bash
#!/bin/bash
# Publish 100 grade events rapidly

for i in {1..100}
do
  curl -X POST http://localhost:8081/api/grades/publish \
    -H "Content-Type: application/json" \
    -d "{\"studentCode\": \"S$(printf %03d $i)\", \"courseCode\": \"CS101\", \"grade\": $((RANDOM % 10)).5}" \
    --silent > /dev/null
  echo "Published grade $i/100"
done

echo "Load test complete!"
```

Save as `load_test.sh`, make executable, and run:
```bash
chmod +x load_test.sh
./load_test.sh
```

Watch PrefSchedule console to see all 100 messages being processed.

## Quick Reference

### Key URLs
- QuickGrade API: http://localhost:8081/api/grades
- PrefSchedule: http://localhost:8080
- RabbitMQ Management: http://localhost:15672

### Key Commands
```bash
# Start RabbitMQ (Docker)
docker start rabbitmq

# Stop RabbitMQ (Docker)
docker stop rabbitmq

# View RabbitMQ logs
docker logs -f rabbitmq

# Test endpoint
curl http://localhost:8081/api/grades/test

# Publish custom grade
curl -X POST http://localhost:8081/api/grades/publish \
  -H "Content-Type: application/json" \
  -d '{"studentCode":"S001","courseCode":"MATH101","grade":9.5}'
```

## Conclusion - Section 7. Compulsory

You have successfully implemented and tested the messaging functionality for Section 7. Compulsory:
- ✅ RabbitMQ broker installed and running
- ✅ QuickGrade publishes grade events
- ✅ PrefSchedule consumes and prints grade events to console
- ✅ Message persistence and reliable delivery verified

---

# Section 7. Homework - Advanced Messaging Features

## Overview

Section 7. Homework extends the basic messaging with:
1. **Database persistence** - Store received grades in the database
2. **Course type filtering** - Only accept grades for COMPULSORY courses
3. **Business logic validation** - Verify student and course exist
4. **Dead-Letter Queue (DLQ)** - Handle failed messages gracefully
5. **Retry mechanism** - Exponential backoff for transient failures
6. **REST API** - View stored grades via endpoints

## New Architecture

```
QuickGrade (Port 8081)
      ↓
  [Publishes to grade.queue]
      ↓
RabbitMQ Broker
   ├── grade.queue (main queue with DLX config)
   ├── grade.dlx (Dead-Letter Exchange)
   └── grade.dlq (Dead-Letter Queue)
      ↓
PrefSchedule (Port 8080)
   ├── GradeConsumer (listens to grade.queue)
   │    ├── Validates: Student exists?
   │    ├── Validates: Course exists?
   │    ├── Filters: Is COMPULSORY?
   │    └── Saves to database (grades table)
   │
   ├── GradeDLQConsumer (monitors grade.dlq)
   │    └── Logs failed messages for manual review
   │
   └── GradeController (REST API)
        └── GET /api/grades/* (view stored grades)
```

## Homework Features

### 1. Database Persistence

Grades are stored in a new `grades` table:
```sql
CREATE TABLE grades (
    id BIGINT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    grade DOUBLE PRECISION NOT NULL,
    received_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    notes VARCHAR(1000)
);
```

### 2. Business Logic Validation

The `GradeService` performs validation:
- ✅ Student must exist in database (by student code)
- ✅ Course must exist in database (by course code)
- ✅ Course type must be "COMPULSORY" (OPTIONAL courses are rejected)
- ✅ Grade must be between 0.0 and 10.0 (validated by DTO)

### 3. Retry Mechanism

Failed messages are retried with exponential backoff:
- **Attempt 1**: Immediate processing
- **Attempt 2**: 2 seconds delay
- **Attempt 3**: 4 seconds delay (2 × 2s)
- **After 3 attempts**: Message sent to Dead-Letter Queue

### 4. Dead-Letter Queue

Failed messages (after 3 retries) go to the DLQ:
- Logged with detailed error information
- Monitored by `GradeDLQConsumer`
- Requires manual review and intervention

## Testing Section 7. Homework

### Prerequisites

Ensure RabbitMQ is running (same as Section 7. Compulsory).

### Step 1: Start PrefSchedule with Database

```bash
cd project/PrefSchedule
mvn clean install
mvn spring-boot:run
```

**Expected Output:**
```
Started PrefScheduleApplication in X seconds
Hibernate: create table grades (...)
GradeConsumer ready to receive messages
GradeDLQConsumer monitoring dead-letter queue
```

### Step 2: Verify Database Schema

The application creates the `grades` table automatically. You can verify:

```bash
# If using H2 database (default), access H2 console at:
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Username: sa
# Password: (leave empty)
```

### Step 3: Test Successful Grade Processing

Publish a grade for a **COMPULSORY** course with existing student/course:

```bash
# First, check what students and courses exist in the database
curl http://localhost:8080/api/students

# Then publish a grade (adjust codes based on your data)
curl -X POST http://localhost:8081/api/grades/publish \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "S001",
    "courseCode": "PA",
    "grade": 9.5
  }'
```

**Expected Output in PrefSchedule Console:**
```
========================================
✓ GRADE PROCESSED SUCCESSFULLY:
  Student: S001
  Course:  PA
  Grade:   9.5
  Status:  Stored in database
========================================
```

**Verify in Database:**
```bash
# View all stored grades
curl http://localhost:8080/api/grades

# View grades for specific student
curl http://localhost:8080/api/grades/student/S001

# View grades for specific course
curl http://localhost:8080/api/grades/course/PA

# View specific grade
curl http://localhost:8080/api/grades/student/S001/course/PA
```

### Step 4: Test Course Type Filtering

Publish a grade for an **OPTIONAL** course (should be rejected):

```bash
curl -X POST http://localhost:8081/api/grades/publish \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "S001",
    "courseCode": "OPTIONAL_COURSE_CODE",
    "grade": 8.0
  }'
```

**Expected Output in PrefSchedule Console:**
```
========================================
✗ GRADE VALIDATION FAILED:
  Student: S001
  Course:  OPTIONAL_COURSE_CODE
  Error:   Grade rejected: Course is not compulsory (type=OPTIONAL)
  Action:  Message will be retried with exponential backoff
========================================
```

After 3 retries, the message goes to the Dead-Letter Queue:

```
╔════════════════════════════════════════╗
║ ⚠️  DEAD-LETTER QUEUE - FAILED MESSAGE ⚠️  ║
╚════════════════════════════════════════╝
Grade event FAILED after max retries:
  Student: S001
  Course:  OPTIONAL_COURSE_CODE
  Grade:   8.0

⚠️  MANUAL REVIEW REQUIRED ⚠️
Possible reasons:
  - Course is not COMPULSORY
╚════════════════════════════════════════╝
```

### Step 5: Test Student Not Found

```bash
curl -X POST http://localhost:8081/api/grades/publish \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "NONEXISTENT",
    "courseCode": "PA",
    "grade": 7.5
  }'
```

**Expected:** Message retried 3 times, then sent to DLQ with error "Student not found with code: NONEXISTENT"

### Step 6: Test Course Not Found

```bash
curl -X POST http://localhost:8081/api/grades/publish \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "S001",
    "courseCode": "NONEXISTENT",
    "grade": 8.5
  }'
```

**Expected:** Message retried 3 times, then sent to DLQ with error "Course not found with code: NONEXISTENT"

### Step 7: Monitor RabbitMQ Queues

Access RabbitMQ Management UI at http://localhost:15672 (guest/guest):

1. **Queues Tab**: View all queues
   - `grade.queue` - Main queue (should show 0 messages when all processed)
   - `grade.dlq` - Dead-Letter Queue (shows failed messages)

2. **Exchanges Tab**: View exchanges
   - `grade.dlx` - Dead-Letter Exchange

3. **Get Messages**: Click on `grade.dlq` → Get messages to see failed events

### Step 8: Batch Testing

Test multiple grades at once:

```bash
# Send 5 grades (mix of valid and invalid)
curl http://localhost:8081/api/grades/test
```

Watch the PrefSchedule console for:
- ✅ Successful processing (compulsory courses with valid student/course)
- ✗ Validation failures (optional courses, nonexistent student/course)
- ⚠️ DLQ messages (after max retries)

## REST API Endpoints for Grades

### GET /api/grades
Get all stored grades.

```bash
curl http://localhost:8080/api/grades
```

**Response:**
```json
[
  {
    "id": 1,
    "student": {"id": 1, "code": "S001", "name": "John Doe"},
    "course": {"id": 1, "code": "PA", "name": "Advanced Programming"},
    "grade": 9.5,
    "receivedAt": "2025-12-01T19:00:00",
    "processedAt": "2025-12-01T19:00:01",
    "notes": "Received from QuickGrade"
  }
]
```

### GET /api/grades/student/{studentCode}
Get all grades for a specific student.

```bash
curl http://localhost:8080/api/grades/student/S001
```

### GET /api/grades/course/{courseCode}
Get all grades for a specific course.

```bash
curl http://localhost:8080/api/grades/course/PA
```

### GET /api/grades/student/{studentCode}/course/{courseCode}
Get a specific grade for a student-course combination.

```bash
curl http://localhost:8080/api/grades/student/S001/course/PA
```

## Monitoring and Debugging

### View Application Logs

```bash
# PrefSchedule logs
cd project/PrefSchedule
mvn spring-boot:run | grep -E "(GRADE|ERROR|DLQ)"
```

### View RabbitMQ Logs

```bash
# Docker
docker logs -f rabbitmq

# Local installation
tail -f /var/log/rabbitmq/rabbit@localhost.log
```

### Check Database

```bash
# Count total grades
curl http://localhost:8080/api/grades | jq length

# Check specific student's grades
curl http://localhost:8080/api/grades/student/S001 | jq
```

## Troubleshooting - Homework Edition

### Issue: Grades not being stored

**Solution:**
1. Check database connection in application.properties
2. Verify student and course exist in database
3. Ensure course type is "COMPULSORY"
4. Check PrefSchedule logs for validation errors

### Issue: All messages going to DLQ

**Solution:**
1. Verify students exist: `curl http://localhost:8080/api/students`
2. Verify courses exist and are COMPULSORY
3. Check GradeService logs for specific validation errors

### Issue: Retry mechanism not working

**Solution:**
1. Ensure `@EnableRetry` is present in PrefScheduleApplication
2. Check `spring-retry` dependency in pom.xml
3. Verify `@Retryable` annotation on consumeGrade method

### Issue: DLQ consumer not receiving messages

**Solution:**
1. Check RabbitMQ Management UI → Exchanges → grade.dlx → Bindings
2. Verify grade.dlq queue exists and is bound to grade.dlx
3. Restart PrefSchedule application

## Load Testing

Test system resilience with high message volume:

```bash
# Send 100 grade events
for i in {1..100}; do
  curl -X POST http://localhost:8081/api/grades/publish \
    -H "Content-Type: application/json" \
    -d "{\"studentCode\":\"S001\",\"courseCode\":\"PA\",\"grade\":$((RANDOM % 10 + 1)).$((RANDOM % 10))}" &
done
wait

# Check results
curl http://localhost:8080/api/grades | jq length
```

## Conclusion - Section 7. Homework

You have successfully implemented and tested advanced messaging features:
- ✅ Grades persisted in database
- ✅ Course type filtering (COMPULSORY only)
- ✅ Business logic validation (student/course existence)
- ✅ Retry mechanism with exponential backoff
- ✅ Dead-Letter Queue for failed messages
- ✅ DLQ monitoring and logging
- ✅ REST API for viewing stored grades
- ✅ Comprehensive error handling

### Key Benefits

1. **Reliability**: Retry mechanism handles transient failures
2. **Data Integrity**: Validation ensures only valid grades are stored
3. **Observability**: DLQ provides visibility into failed messages
4. **Maintainability**: Clear separation of concerns (service/repository/consumer)
5. **Scalability**: Asynchronous processing decouples services

### Production Considerations

For production deployment, consider:
1. **DLQ Monitoring**: Set up alerts for messages in DLQ (e.g., DataDog, Sentry)
2. **Manual Retry**: Implement admin interface to retry failed messages from DLQ
3. **Database Indexes**: Add indexes on student_code and course_code for performance
4. **Message TTL**: Set time-to-live for messages to prevent queue buildup
5. **Rate Limiting**: Implement rate limiting on QuickGrade publisher
6. **Distributed Tracing**: Add correlation IDs for end-to-end request tracking

## Next Steps

- **Section 8**: Implement microservices architecture with service discovery
- **Add more validations**: Grade value ranges, duplicate detection
- **Implement audit log**: Track all grade changes
- **Add notification system**: Email students when grades are posted
