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

## Conclusion

You have successfully implemented and tested the messaging functionality for Section 7. Compulsory:
- ✅ RabbitMQ broker installed and running
- ✅ QuickGrade publishes grade events
- ✅ PrefSchedule consumes and prints grade events to console
- ✅ Message persistence and reliable delivery verified

For Section 7. Homework, we'll extend this to store grades in the database, filter compulsory courses, and implement Dead-Letter Queue handling.
