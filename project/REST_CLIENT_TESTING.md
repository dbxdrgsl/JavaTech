# Section 8. Compulsory: REST Client Communication Between Microservices

This guide demonstrates synchronous REST communication between the PrefSchedule and QuickGrade microservices.

## Overview

**Section 8. Compulsory** implements REST client communication allowing PrefSchedule to fetch data from QuickGrade synchronously, complementing the existing asynchronous RabbitMQ messaging.

### Communication Patterns

1. **Asynchronous (Section 7)**: QuickGrade → RabbitMQ → PrefSchedule (grade events)
2. **Synchronous (Section 8)**: PrefSchedule → REST → QuickGrade (statistics queries)

## Architecture

```
┌─────────────────┐                    ┌─────────────────┐
│   PrefSchedule  │                    │   QuickGrade    │
│   (Port 8080)   │                    │   (Port 8081)   │
├─────────────────┤                    ├─────────────────┤
│                 │  REST GET Request  │                 │
│ Microservice    │───────────────────>│ GradeController │
│ Controller      │                    │ /statistics     │
│                 │<───────────────────│                 │
│                 │  JSON Response     │                 │
│                 │                    │                 │
│ QuickGrade      │                    │ Statistics      │
│ ClientService   │                    │ Service         │
│ (RestTemplate)  │                    │ (In-Memory)     │
└─────────────────┘                    └─────────────────┘
```

## Components

### QuickGrade (Producer)

1. **GradeStatisticsService**: Tracks grade statistics in memory
   - Total grades published
   - Average, min, max grades
   - Last published student/course

2. **GradeController**: REST endpoints
   - `GET /api/grades/statistics` - Get current statistics
   - `DELETE /api/grades/statistics` - Reset statistics

3. **GradeStatistics DTO**: Data transfer object for statistics

### PrefSchedule (Consumer)

1. **RestClientConfig**: Configures RestTemplate with timeouts

2. **QuickGradeClientService**: REST client service
   - Calls QuickGrade API using RestTemplate
   - Error handling for service unavailability

3. **MicroserviceController**: Exposes REST endpoints
   - `GET /api/microservices/quickgrade/statistics` - Fetch statistics
   - `GET /api/microservices/quickgrade/health` - Check service health

4. **GradeStatisticsDTO**: DTO for receiving statistics

## Prerequisites

1. **RabbitMQ** must be running (for Section 7 messaging)
2. **QuickGrade** service running on port 8081
3. **PrefSchedule** service running on port 8080

## Testing Guide

### Step 1: Start RabbitMQ

```bash
# Using Docker
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# Or use local RabbitMQ installation
sudo systemctl start rabbitmq-server
```

### Step 2: Start QuickGrade Service

```bash
cd project/QuickGrade
mvn clean install
mvn spring-boot:run
```

Expected output:
```
Started QuickGradeApplication in X seconds
Tomcat started on port 8081
```

### Step 3: Start PrefSchedule Service

```bash
cd project/PrefSchedule
mvn clean install
mvn spring-boot:run
```

Expected output:
```
Started PrefScheduleApplication in X seconds
Tomcat started on port 8080
```

### Step 4: Publish Some Grades (Optional)

This creates statistics data in QuickGrade:

```bash
# Publish sample grades
curl -X GET http://localhost:8081/api/grades/test
```

Or publish individual grades:

```bash
curl -X POST http://localhost:8081/api/grades/publish \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "S001",
    "courseCode": "MATH101",
    "grade": 9.5
  }'

curl -X POST http://localhost:8081/api/grades/publish \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "S002",
    "courseCode": "CS101",
    "grade": 8.7
  }'
```

### Step 5: Test REST Client Communication

#### 5.1 Check QuickGrade Service Health

```bash
curl http://localhost:8080/api/microservices/quickgrade/health
```

Expected response:
```json
{
  "service": "QuickGrade",
  "available": true,
  "status": "UP"
}
```

#### 5.2 Fetch Statistics from QuickGrade via PrefSchedule

```bash
curl http://localhost:8080/api/microservices/quickgrade/statistics
```

Expected response (with data):
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

Expected response (no data yet):
```json
{
  "totalGradesPublished": 0,
  "averageGrade": 0.0,
  "minGrade": 0.0,
  "maxGrade": 0.0,
  "lastPublishedStudent": "N/A",
  "lastPublishedCourse": "N/A"
}
```

#### 5.3 Direct Access to QuickGrade Statistics

You can also access QuickGrade directly:

```bash
curl http://localhost:8081/api/grades/statistics
```

### Step 6: Test Service Unavailability Handling

Stop QuickGrade service and test error handling:

```bash
# Stop QuickGrade (Ctrl+C in QuickGrade terminal)

# Try to fetch statistics
curl http://localhost:8080/api/microservices/quickgrade/statistics
```

Expected response (503 Service Unavailable):
```json
{
  "error": "QuickGrade service is unavailable",
  "message": "Unable to fetch statistics from QuickGrade microservice"
}
```

Health check response:
```bash
curl http://localhost:8080/api/microservices/quickgrade/health
```

Response:
```json
{
  "service": "QuickGrade",
  "available": false,
  "status": "DOWN"
}
```

### Step 7: Reset Statistics

```bash
curl -X DELETE http://localhost:8081/api/grades/statistics
```

Response:
```
Statistics reset successfully
```

## Testing Scenarios

### Scenario 1: Basic Flow

1. Publish 5 grades to QuickGrade
2. Fetch statistics via PrefSchedule REST client
3. Verify statistics are correct

```bash
# Publish grades
for i in {1..5}; do
  curl -X POST http://localhost:8081/api/grades/publish \
    -H "Content-Type: application/json" \
    -d "{\"studentCode\":\"S00$i\",\"courseCode\":\"COURSE$i\",\"grade\":$((RANDOM % 5 + 6))}"
done

# Fetch via REST client
curl http://localhost:8080/api/microservices/quickgrade/statistics | jq
```

### Scenario 2: Service Discovery

1. Check health of QuickGrade
2. If available, fetch statistics
3. If unavailable, handle gracefully

```bash
# Health check
HEALTH=$(curl -s http://localhost:8080/api/microservices/quickgrade/health | jq -r '.available')

if [ "$HEALTH" = "true" ]; then
  echo "QuickGrade is available, fetching statistics..."
  curl http://localhost:8080/api/microservices/quickgrade/statistics | jq
else
  echo "QuickGrade is not available"
fi
```

### Scenario 3: Load Testing

Test REST client under load:

```bash
#!/bin/bash
# Load test script

echo "Publishing 100 grades..."
for i in {1..100}; do
  curl -s -X POST http://localhost:8081/api/grades/publish \
    -H "Content-Type: application/json" \
    -d "{\"studentCode\":\"S$(printf %03d $i)\",\"courseCode\":\"COURSE$((i % 10))\",\"grade\":$((RANDOM % 51 + 50))/10}" &
done
wait

echo "Fetching statistics via REST client..."
curl http://localhost:8080/api/microservices/quickgrade/statistics | jq
```

## Swagger UI Testing

Access Swagger UI to test REST endpoints interactively:

1. **PrefSchedule**: http://localhost:8080/swagger-ui.html
   - Navigate to "Microservice Communication" section
   - Try `/api/microservices/quickgrade/statistics`
   - Try `/api/microservices/quickgrade/health`

2. **QuickGrade**: http://localhost:8081/swagger-ui.html (if configured)

## Configuration

### PrefSchedule application.properties

```properties
# QuickGrade Microservice Configuration
quickgrade.service.url=http://localhost:8081

# RestTemplate timeouts
# Connect timeout: 5 seconds
# Read timeout: 5 seconds
```

### Custom Configuration

You can override the QuickGrade URL:

```bash
# Run with custom URL
java -jar prefschedule.jar --quickgrade.service.url=http://quickgrade-service:8081
```

Or use environment variable:
```bash
export QUICKGRADE_SERVICE_URL=http://quickgrade-service:8081
mvn spring-boot:run
```

## Troubleshooting

### Issue: "QuickGrade service is unavailable"

**Cause**: QuickGrade service is not running or not reachable

**Solutions**:
1. Verify QuickGrade is running: `curl http://localhost:8081/api/grades/statistics`
2. Check network connectivity
3. Verify port 8081 is not blocked by firewall
4. Check QuickGrade logs for errors

### Issue: "Connection timeout"

**Cause**: Network latency or QuickGrade is slow to respond

**Solutions**:
1. Increase timeout in RestClientConfig
2. Check network performance
3. Verify QuickGrade service health

### Issue: "404 Not Found"

**Cause**: Endpoint URL is incorrect

**Solutions**:
1. Verify URL in application.properties
2. Check QuickGrade controller mappings
3. Ensure QuickGrade is running correct version

## Comparison: Async vs Sync Communication

| Aspect | RabbitMQ (Async) | REST Client (Sync) |
|--------|------------------|-------------------|
| **Direction** | QuickGrade → PrefSchedule | PrefSchedule → QuickGrade |
| **Pattern** | Event-driven, fire-and-forget | Request-response |
| **Latency** | Non-blocking, eventual consistency | Blocking, immediate response |
| **Use Case** | Grade events notification | Query statistics |
| **Error Handling** | DLQ, retries | HTTP status codes, timeouts |
| **Coupling** | Loose coupling | Tighter coupling |

## Best Practices

1. **Timeout Configuration**: Always set connect and read timeouts
2. **Error Handling**: Handle service unavailability gracefully
3. **Circuit Breaker**: Consider adding Resilience4j for production (Section 8. Homework)
4. **Service Discovery**: Use Eureka/Consul for dynamic service URLs (Section 8. Homework)
5. **Load Balancing**: Use client-side load balancing for multiple instances
6. **Monitoring**: Log all REST calls for debugging
7. **Security**: Add authentication/authorization for inter-service calls

## Advanced Features (Section 8. Homework)

The homework section may include:
- Circuit Breaker pattern with Resilience4j
- Service Registry with Eureka
- Client-side load balancing with Ribbon
- API Gateway with Spring Cloud Gateway
- Distributed tracing with Sleuth and Zipkin

## Summary

Section 8. Compulsory demonstrates:
✅ RestTemplate configuration with timeouts
✅ REST client service for inter-service communication
✅ Error handling for service unavailability
✅ Synchronous request-response pattern
✅ Health check endpoints
✅ Clean separation of concerns
✅ Comprehensive testing guide

Both asynchronous (RabbitMQ) and synchronous (REST) communication patterns are now implemented, providing a complete microservices architecture.
