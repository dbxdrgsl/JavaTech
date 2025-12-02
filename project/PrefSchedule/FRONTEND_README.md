# PrefSchedule Frontend Documentation

## Overview

The PrefSchedule frontend is a comprehensive single-page application (SPA) that provides a user-friendly interface to interact with all features of the PrefSchedule backend system. It demonstrates all requirements from the project specification including JPA, REST services, Security, Messaging, and Microservices integration.

## Features Implemented

### 1. Authentication & Authorization (Lab 6 - Security)
- **JWT-based Login**: Secure authentication using JSON Web Tokens
- **User Registration**: Self-service registration with role selection (STUDENT, INSTRUCTOR, ADMIN)
- **Role-based Access Control**: Different permissions based on user roles
- **Session Management**: Token storage in localStorage for persistent sessions

### 2. Students Management (Lab 4 & 5)
- **View All Students**: Browse complete list of students with search functionality
- **Add Students**: Create new student records (ADMIN/INSTRUCTOR only)
- **Delete Students**: Remove student records (ADMIN only)
- **Search**: Filter students by code, name, or email
- **Student Details**: View code, name, email, year, and ID

### 3. Courses Management (Lab 4)
- **View Courses**: Browse all courses in the system
- **Filter by Type**: Filter between COMPULSORY and OPTIONAL courses
- **Course Details**: View code, abbreviation, name, description, and group count
- **Type Badges**: Visual indicators for course types

### 4. Course Packs Management (Lab 4)
- **View Packs**: Browse all course packs
- **Filter by Year**: Filter packs by academic year (1-4)
- **Pack Details**: View pack name, year, semester, and associated courses

### 5. Student Preferences (Lab 5 - RESTful Services)
- **Create Preferences**: Students can submit course preferences with rank ordering
- **View Preferences**: Browse all preferences with student and course details
- **Filter by Student**: View preferences for specific students
- **Delete Preferences**: Remove preference records (ADMIN/STUDENT)
- **Rank Ordering**: Support for partial ordering with ties between courses

### 6. Grades from QuickGrade (Lab 7 - Messaging)
- **View Grades**: Display grades received from QuickGrade messaging service
- **Filter by Student**: View grades for specific students
- **Grade Details**: Show student code, course code, grade value (0-10), and timestamp
- **Visual Indicators**: Color-coded badges for passing/failing grades
- **Real-time Updates**: Grades populated via RabbitMQ messaging

### 7. Microservices Integration (Lab 8)
- **Service Health Check**: Monitor QuickGrade service availability
- **Grade Statistics**: Fetch statistics from QuickGrade microservice
- **Service Status Display**: Visual indicators for UP/DOWN status
- **Error Handling**: Graceful handling of unavailable services

### 8. System Monitoring (Lab 6 - Actuator)
- **Health Endpoint**: Check application health status
- **Info Endpoint**: View application information
- **Public Access**: Health and info endpoints accessible without authentication
- **Metrics**: Access to system metrics (authenticated users only)

## Technology Stack

### Frontend Technologies
- **HTML5**: Semantic markup structure
- **CSS3**: Modern styling with flexbox and grid layouts
- **Vanilla JavaScript**: No frameworks, pure ES6+ JavaScript
- **Fetch API**: RESTful API communication
- **LocalStorage**: Client-side token storage

### Backend Integration
- **Spring Boot**: Backend REST API
- **Spring Security**: JWT-based authentication
- **Spring Data JPA**: Database persistence
- **Spring AMQP**: RabbitMQ messaging
- **Spring Actuator**: System monitoring
- **PostgreSQL/H2**: Database (H2 for development)

## Architecture

### Single Page Application (SPA)
The frontend is built as a SPA with section-based navigation:
- Login/Register sections for authentication
- Dashboard for overview and quick access
- Individual sections for each feature (Students, Courses, Packs, Preferences, Grades, Services, Monitor)
- Dynamic content loading without page refreshes

### API Communication
All communication with the backend uses the Fetch API:
- **GET requests**: Public endpoints for read operations
- **POST/PUT/DELETE**: Authenticated requests with JWT token in Authorization header
- **Error Handling**: User-friendly error messages
- **Loading States**: Visual feedback during API calls

### State Management
Simple state management using JavaScript variables:
- `authToken`: JWT token for authenticated requests
- `currentUser`: Current logged-in username
- `currentRole`: User role (ADMIN, INSTRUCTOR, STUDENT)
- `all*`: Cached data arrays for each entity type

## User Interface

### Design Principles
- **Clean & Modern**: Purple gradient theme with card-based layout
- **Responsive**: Mobile-friendly design with adaptive layouts
- **Intuitive**: Clear navigation and action buttons
- **Accessible**: Semantic HTML and proper ARIA labels
- **Visual Feedback**: Color-coded badges, hover effects, and animations

### Color Scheme
- **Primary**: Purple gradient (#667eea to #764ba2)
- **Success**: Green (#28a745)
- **Danger**: Red (#dc3545)
- **Warning**: Yellow (#ffc107)
- **Info**: Blue (#d1ecf1)

### Navigation
Persistent header with role-based navigation buttons:
- Dashboard: Overview and quick stats
- Students: Student management
- Courses: Course catalog
- Packs: Course pack management
- Preferences: Student preference management
- Grades: Grade viewing
- Services: Microservices integration
- Monitor: System health and info
- Logout: End session

## Security Features

### Authentication
- JWT token-based authentication
- Token stored in localStorage
- Automatic token inclusion in API requests
- Session persistence across page refreshes

### Authorization
- Role-based access control (RBAC)
- Method-level security via @PreAuthorize
- Different UI elements shown/hidden based on roles:
  - **ADMIN**: Full access to all features
  - **INSTRUCTOR**: Can manage students and view all data
  - **STUDENT**: Can manage own preferences, view data

### Password Security
- BCrypt password hashing
- Passwords never stored in plain text
- Secure password transmission via HTTPS (in production)

## REST API Integration

### Endpoints Used
- **Auth**: `/api/auth/login`, `/api/auth/register`, `/api/auth/me`
- **Students**: `/api/students` (GET, POST, PUT, DELETE)
- **Courses**: `/api/courses` (GET)
- **Packs**: `/api/packs` (GET)
- **Preferences**: `/api/preferences` (GET, POST, PUT, DELETE)
- **Grades**: `/api/grades` (GET)
- **Microservices**: `/api/microservices/quickgrade/*` (GET)
- **Actuator**: `/actuator/health`, `/actuator/info` (GET)

### Content Negotiation
The Preferences API supports both JSON and XML:
- Accepts: `application/json`, `application/xml`
- Content-Type: `application/json`, `application/xml`

### ETags
The Preferences API supports ETag-based conditional requests:
- `If-None-Match` header for cache validation
- Returns 304 Not Modified when content unchanged

## Project Requirements Coverage

### Lab 4 - Java Persistence API ✅
- Entity management through REST API
- CRUD operations for all entities
- Repository pattern implementation
- Data persistence visualization

### Lab 5 - RESTful Services ✅
- CRUD endpoints for students
- Student preferences with DTOs
- Custom exception handling
- ETag support (Preferences API)
- Content negotiation JSON/XML (Preferences API)
- OpenAPI/Swagger integration (accessible at `/swagger-ui.html`)

### Lab 6 - Security ✅
- JWT-based authentication
- Role-based access control (ADMIN, INSTRUCTOR, STUDENT)
- BCrypt password encryption
- Method-level security (@PreAuthorize)
- Public GET endpoints, protected POST/PUT/DELETE
- Actuator endpoints (health/info public, metrics protected)

### Lab 7 - Messaging ✅
- Grades received from QuickGrade via RabbitMQ
- Display of message-based data
- DLQ handling (backend)
- Real-time grade updates

### Lab 8 - Microservices ✅
- QuickGrade service integration
- Service health monitoring
- Statistics API consumption
- Resilience patterns (backend: Retry, Fallback, Timeout)

## File Structure

```
src/main/resources/static/
├── index.html          # Main HTML file with all sections
├── css/
│   └── styles.css      # Complete styling
└── js/
    └── app.js          # Application logic and API integration
```

## Running the Application

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL (optional, H2 used by default)
- RabbitMQ (optional, for messaging features)

### Start Backend
```bash
cd project/PrefSchedule
mvn clean package
java -jar target/PrefSchedule-0.0.1-SNAPSHOT.jar
```

### Access Frontend
Open browser and navigate to:
```
http://localhost:8080
```

The frontend is automatically served by Spring Boot from the `static` directory.

## Testing the Application

### 1. Register a User
- Click "Register" button
- Fill in username, password, name, email
- Select role (ADMIN, INSTRUCTOR, or STUDENT)
- Click "Register"

### 2. Login
- Enter credentials
- Click "Login"
- Dashboard will appear with user information

### 3. Explore Features
- **Dashboard**: View statistics and quick access to all features
- **Students**: Browse students, search, add new (if authorized)
- **Courses**: View and filter courses by type
- **Packs**: Browse course packs, filter by year
- **Preferences**: View and manage student preferences
- **Grades**: View grades received from QuickGrade
- **Services**: Check QuickGrade service health and statistics
- **Monitor**: View application health and info

## Screenshots

### Login Page
![Login](https://github.com/user-attachments/assets/...)
- Clean authentication interface
- Link to registration

### Dashboard
![Dashboard](https://github.com/user-attachments/assets/...)
- User welcome message with role
- Quick access cards with statistics
- Navigation to all features

### Students Management
![Students](https://github.com/user-attachments/assets/...)
- List of all students
- Search functionality
- Add/Delete actions (role-based)

### Microservices Integration
![Microservices](https://github.com/user-attachments/assets/d1cb163b-c427-404b-8846-ed7b4755a7bb)
- QuickGrade service health check
- Service status display (UP/DOWN)
- Grade statistics fetching

### System Monitoring
![Actuator](https://github.com/user-attachments/assets/59182ee0-08cf-45c5-9b91-bc437bf7ecaf)
- Health endpoint monitoring
- Application info display
- System metrics access

## API Documentation

Swagger UI is available at: `http://localhost:8080/swagger-ui.html`

This provides:
- Interactive API documentation
- Try-it-out functionality for all endpoints
- Request/response schemas
- Authentication testing

## Future Enhancements

Potential improvements for the frontend:
1. **Real-time Updates**: WebSocket integration for live data updates
2. **Advanced Filtering**: Multi-criteria search and filtering
3. **Data Visualization**: Charts and graphs for statistics
4. **Bulk Operations**: Import/export via CSV
5. **Pagination**: Server-side pagination for large datasets
6. **Internationalization**: Multi-language support
7. **Progressive Web App**: Offline capability and mobile app features
8. **Dark Mode**: Theme switcher for dark/light modes

## Troubleshooting

### Common Issues

**Issue**: "Failed to load resource: 500"
- **Solution**: Check backend logs, ensure database is running

**Issue**: "Bad credentials" on login
- **Solution**: Verify user is registered, check username/password

**Issue**: "QuickGrade service DOWN"
- **Solution**: This is expected if QuickGrade is not running, demonstrates error handling

**Issue**: Grades not showing
- **Solution**: Ensure RabbitMQ is running and QuickGrade is publishing events

**Issue**: Courses showing JSON parsing error
- **Solution**: Check for circular references in Course entity relationships

## Conclusion

This frontend provides a complete, user-friendly interface to demonstrate all features of the PrefSchedule system, covering all requirements from Labs 4-8 including JPA, REST services, Security, Messaging, and Microservices integration. The application is production-ready with proper error handling, security, and user experience considerations.
