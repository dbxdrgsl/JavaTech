# Testing Secured Endpoints - Section 6 Compulsory

This guide shows how to test the Spring Security integration and secured REST endpoints.

## Overview

After implementing Spring Security:
- **All endpoints are now protected** by default
- **Only /login and /api/login** endpoints allow unauthenticated access
- **HTTP Basic Authentication** is enabled for API access
- Two test users are configured: `user:password` and `admin:admin`

## Testing Authentication

### 1. Test Unauthenticated Access (Should Fail)

Try accessing any endpoint without authentication:

```bash
curl -X GET http://localhost:8080/api/students
```

**Expected Response (401 Unauthorized):**
```json
{
  "timestamp": "...",
  "status": 401,
  "error": "Unauthorized",
  "path": "/api/students"
}
```

### 2. Test Login Endpoint (Public Access)

The `/api/login` endpoint is publicly accessible:

```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "password"
  }'
```

**Expected Response (200 OK):**
```json
{
  "message": "Login successful for user: user",
  "timestamp": "2025-12-01T15:00:00",
  "token": "mock-token-1234567890"
}
```

### 3. Test Authenticated Access with Basic Auth

Access endpoints using HTTP Basic Authentication:

```bash
# Get all students with authentication
curl -X GET http://localhost:8080/api/students \
  -u user:password
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "code": "S2025-001",
    "year": 2
  }
]
```

### 4. Get Current User Information

```bash
curl -X GET http://localhost:8080/api/me \
  -u user:password
```

**Expected Response (200 OK):**
```json
{
  "username": "user",
  "authorities": "[ROLE_USER]",
  "authenticated": true
}
```

### 5. Test with Admin User

```bash
curl -X GET http://localhost:8080/api/students \
  -u admin:admin
```

**Expected Response (200 OK):**
Same as regular user (role-based access control will be in homework)

## Complete CRUD Test Sequence with Authentication

### Create a Student
```bash
curl -X POST http://localhost:8080/api/students \
  -u user:password \
  -H "Content-Type: application/json" \
  -d '{
    "code": "S2025-100",
    "name": "Test Student",
    "email": "test@example.com",
    "year": 3
  }'
```

### Get All Students
```bash
curl -X GET http://localhost:8080/api/students \
  -u user:password
```

### Get Student by ID
```bash
curl -X GET http://localhost:8080/api/students/1 \
  -u user:password
```

### Update Student
```bash
curl -X PUT http://localhost:8080/api/students/6 \
  -u user:password \
  -H "Content-Type: application/json" \
  -d '{
    "code": "S2025-100",
    "name": "Updated Student",
    "email": "updated@example.com",
    "year": 4
  }'
```

### Delete Student
```bash
curl -X DELETE http://localhost:8080/api/students/6 \
  -u user:password
```

## Testing Student Preferences with Authentication

### Create Preference
```bash
curl -X POST http://localhost:8080/api/preferences \
  -u user:password \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "courseId": 2,
    "rankOrder": 1
  }'
```

### Get All Preferences
```bash
curl -X GET http://localhost:8080/api/preferences \
  -u user:password
```

### Get Preferences by Student
```bash
curl -X GET http://localhost:8080/api/preferences/student/1 \
  -u user:password
```

### Get Preference with ETag Support
```bash
# First request - get the ETag
curl -v -X GET http://localhost:8080/api/preferences/1 \
  -u user:password
  
# Note the ETag header in response, e.g., ETag: "0"

# Second request with If-None-Match
curl -v -X GET http://localhost:8080/api/preferences/1 \
  -u user:password \
  -H 'If-None-Match: "0"'
```

**Expected Response (304 Not Modified):**
No body returned, status code 304

## Testing Content Negotiation with Authentication

### Request JSON (default)
```bash
curl -X GET http://localhost:8080/api/preferences \
  -u user:password \
  -H "Accept: application/json"
```

### Request XML
```bash
curl -X GET http://localhost:8080/api/preferences \
  -u user:password \
  -H "Accept: application/xml"
```

**Expected Response (XML):**
```xml
<List>
  <item>
    <id>1</id>
    <studentId>1</studentId>
    <studentName>John Doe</studentName>
    ...
  </item>
</List>
```

## Testing with Postman

### Setup Authentication in Postman

1. Create a new request
2. Go to the **Authorization** tab
3. Select **Type: Basic Auth**
4. Enter **Username:** `user`
5. Enter **Password:** `password`
6. Send the request

### Environment Variables

Create environment variables for easier testing:
- `baseUrl`: `http://localhost:8080`
- `username`: `user`
- `password`: `password`

Then use: `{{baseUrl}}/api/students`

## Swagger UI Access with Authentication

1. Start the application: `mvn spring-boot:run`
2. Open: http://localhost:8080/swagger-ui.html
3. Click the **Authorize** button (lock icon)
4. Enter credentials:
   - **Username:** `user`
   - **Password:** `password`
5. Click **Authorize**
6. Now you can test all endpoints through Swagger UI

## Default Test Users

The application comes with two pre-configured test users:

| Username | Password | Roles |
|----------|----------|-------|
| user     | password | USER  |
| admin    | admin    | ADMIN |

## Security Configuration Summary

- **Protected Endpoints:** All API endpoints (`/api/**`)
- **Public Endpoints:** `/login`, `/api/login`
- **Authentication Method:** HTTP Basic Auth
- **Password Encoding:** BCrypt
- **CSRF:** Disabled (for API testing convenience)

## Troubleshooting

### 401 Unauthorized
- Check you're including the `-u user:password` flag in curl
- Verify the username and password are correct
- Make sure you're not accessing a protected endpoint without auth

### 403 Forbidden
- Will be relevant in Section 6 Homework when role-based access is implemented
- Indicates you're authenticated but don't have the required role

### Invalid Credentials
- The default users are: `user:password` and `admin:admin`
- Passwords are case-sensitive

## Next Steps

In **Section 6 Homework**, we'll add:
- JWT-based authentication (tokens instead of Basic Auth)
- Role-based access control (RBAC)
- Database-backed users
- Password encryption with BCrypt
- User registration flow
- Method-level security with @PreAuthorize
- Actuator endpoint security

## Testing Checklist

- [ ] Verify unauthenticated access returns 401
- [ ] Verify /api/login is publicly accessible
- [ ] Verify authenticated access works with Basic Auth
- [ ] Test all CRUD operations with authentication
- [ ] Test current user endpoint (/api/me)
- [ ] Verify both test users (user and admin) can authenticate
- [ ] Test Swagger UI with authentication
- [ ] Verify ETag conditional requests still work with auth
- [ ] Verify content negotiation (JSON/XML) works with auth
