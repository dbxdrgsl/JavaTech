# Testing REST Endpoints - Section 5 Compulsory

This guide shows how to test the CRUD REST endpoints for students using curl commands.

## Prerequisites

1. Start the application:
```bash
cd project/PrefSchedule
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## REST Endpoints

All student endpoints are under `/api/students`

### 1. CREATE - Add a New Student

**Request:**
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "code": "S2025-100",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "year": 3
  }'
```

**Expected Response (201 Created):**
```json
{
  "id": 6,
  "code": "S2025-100",
  "name": "John Doe",
  "email": "john.doe@example.com",
  "year": 3
}
```

### 2. READ - Get All Students

**Request:**
```bash
curl -X GET http://localhost:8080/api/students
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "code": "S2025-001",
    "name": "Miss Ira Kulas",
    "email": "...",
    "year": 2
  },
  {
    "id": 2,
    "code": "S2025-002",
    "name": "Mirtha Stokes",
    "email": "...",
    "year": 2
  }
  // ... more students
]
```

### 3. READ - Get Student by ID

**Request:**
```bash
curl -X GET http://localhost:8080/api/students/1
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "code": "S2025-001",
  "name": "Miss Ira Kulas",
  "email": "...",
  "year": 2
}
```

**If student not found (404 Not Found):**
```bash
curl -X GET http://localhost:8080/api/students/999
```
Returns empty response with 404 status.

### 4. READ - Get Student by Code

**Request:**
```bash
curl -X GET http://localhost:8080/api/students/code/S2025-001
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "code": "S2025-001",
  "name": "Miss Ira Kulas",
  "email": "...",
  "year": 2
}
```

### 5. READ - Get Students by Year

**Request:**
```bash
curl -X GET http://localhost:8080/api/students/year/2
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "code": "S2025-001",
    "name": "Miss Ira Kulas",
    "email": "...",
    "year": 2
  },
  {
    "id": 2,
    "code": "S2025-002",
    "name": "Mirtha Stokes",
    "email": "...",
    "year": 2
  }
  // ... all year 2 students
]
```

### 6. UPDATE - Update an Existing Student

**Request:**
```bash
curl -X PUT http://localhost:8080/api/students/6 \
  -H "Content-Type: application/json" \
  -d '{
    "code": "S2025-100",
    "name": "John Smith",
    "email": "john.smith@example.com",
    "year": 4
  }'
```

**Expected Response (200 OK):**
```json
{
  "id": 6,
  "code": "S2025-100",
  "name": "John Smith",
  "email": "john.smith@example.com",
  "year": 4
}
```

**If student not found (404 Not Found):**
```bash
curl -X PUT http://localhost:8080/api/students/999 \
  -H "Content-Type: application/json" \
  -d '{...}'
```
Returns empty response with 404 status.

### 7. DELETE - Delete a Student

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/students/6
```

**Expected Response (204 No Content):**
Empty response with status 204.

**If student not found (404 Not Found):**
```bash
curl -X DELETE http://localhost:8080/api/students/999
```
Returns empty response with 404 status.

### 8. READ - Get Student Count

**Request:**
```bash
curl -X GET http://localhost:8080/api/students/count
```

**Expected Response (200 OK):**
```
5
```

## Complete Test Sequence

Here's a complete test sequence that demonstrates all CRUD operations:

```bash
# 1. Get all students (should show 5 students from DataLoader)
curl -X GET http://localhost:8080/api/students

# 2. Get count
curl -X GET http://localhost:8080/api/students/count

# 3. Create a new student
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "code": "S2025-100",
    "name": "Test Student",
    "email": "test@example.com",
    "year": 1
  }'

# 4. Get the new student by ID (assuming ID is 6)
curl -X GET http://localhost:8080/api/students/6

# 5. Get student by code
curl -X GET http://localhost:8080/api/students/code/S2025-100

# 6. Update the student
curl -X PUT http://localhost:8080/api/students/6 \
  -H "Content-Type: application/json" \
  -d '{
    "code": "S2025-100",
    "name": "Updated Student",
    "email": "updated@example.com",
    "year": 2
  }'

# 7. Verify the update
curl -X GET http://localhost:8080/api/students/6

# 8. Get all year 2 students (should include our updated student)
curl -X GET http://localhost:8080/api/students/year/2

# 9. Delete the student
curl -X DELETE http://localhost:8080/api/students/6

# 10. Verify deletion (should return 404)
curl -X GET http://localhost:8080/api/students/6

# 11. Get count again (should be back to 5)
curl -X GET http://localhost:8080/api/students/count
```

## Using curl with verbose output

To see full HTTP response details, add `-v` flag:

```bash
curl -v -X GET http://localhost:8080/api/students/1
```

## Using curl with pretty-printed JSON

To format JSON output, pipe to `jq` (if installed):

```bash
curl -X GET http://localhost:8080/api/students | jq
```

Or use Python:

```bash
curl -X GET http://localhost:8080/api/students | python -m json.tool
```

## Testing with Postman

If you prefer Postman:

1. Import this collection or create requests manually
2. Base URL: `http://localhost:8080`
3. Create a collection with requests for each endpoint
4. Use environment variables for dynamic IDs

### Postman Collection Example

```json
{
  "info": {
    "name": "PrefSchedule Students API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Get All Students",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/students",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "students"]
        }
      }
    },
    {
      "name": "Create Student",
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
          "raw": "{\n  \"code\": \"S2025-100\",\n  \"name\": \"Test Student\",\n  \"email\": \"test@example.com\",\n  \"year\": 1\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/students",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "students"]
        }
      }
    }
  ]
}
```

## Expected HTTP Status Codes

- `200 OK` - Successful GET, PUT
- `201 Created` - Successful POST
- `204 No Content` - Successful DELETE
- `404 Not Found` - Resource not found
- `400 Bad Request` - Invalid request body (if validation is added)

## Troubleshooting

### Application not running
If you get "Connection refused", make sure the application is running:
```bash
cd project/PrefSchedule
mvn spring-boot:run
```

### Port already in use
If port 8080 is in use, you can change it in `application.properties`:
```properties
server.port=8081
```

### JSON formatting issues
Make sure to escape quotes properly in your curl commands, or use single quotes around the JSON data.

### H2 Console
While the application is running, you can also check the database via H2 console:
1. Open: http://localhost:8080/h2-console
2. JDBC URL: (check console output for exact URL)
3. Username: SA
4. Password: (empty)

## Summary

All CRUD operations are now available via REST endpoints:
- ✅ CREATE: POST /api/students
- ✅ READ: GET /api/students, /api/students/{id}, /api/students/code/{code}, /api/students/year/{year}
- ✅ UPDATE: PUT /api/students/{id}
- ✅ DELETE: DELETE /api/students/{id}

The endpoints use standard HTTP methods and return appropriate status codes.
