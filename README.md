# User Management API

A comprehensive Spring Boot application providing CRUD operations for user management with H2 database integration, comprehensive testing, and monitoring capabilities.

## Features

- **Complete CRUD Operations**: Create, Read, Update, Delete users
- **H2 Database Integration**: In-memory database for development and testing
- **Comprehensive Testing**: Unit tests with >85% coverage, BDD tests with Cucumber
- **API Documentation**: Well-documented REST endpoints
- **Monitoring Ready**: Splunk queries for performance and error monitoring
- **Postman Collection**: Ready-to-use API testing collection

## Technology Stack

- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Data JPA**
- **H2 Database**
- **JUnit 5 & Mockito** for unit testing
- **Cucumber** for BDD testing
- **Maven** for dependency management
- **JaCoCo** for test coverage

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone the repository:
```bash
git clone https://github.com/Srikanth764/spring-developer.git
cd spring-developer
```

2. Build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

3. The application will start on `http://localhost:8080`

4. Access H2 Console at `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: `password`

## API Endpoints

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create a new user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Request/Response Examples

#### Create User
```bash
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "age": 25
}
```

#### Response
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "age": 25
}
```

## Testing

### Unit Tests
Run unit tests with coverage:
```bash
mvn test
mvn jacoco:report
```

Coverage report will be available at `target/site/jacoco/index.html`

### BDD Tests
Run Cucumber BDD tests:
```bash
mvn test -Dtest=CucumberTestRunner
```

### Test Coverage
The application maintains >85% test coverage across all layers:
- Controller Layer
- Service Layer
- Repository Layer
- Mapper Layer

## Project Structure

```
src/
├── main/
│   ├── java/com/example/crudapp/
│   │   ├── controller/          # REST Controllers
│   │   ├── service/             # Business Logic
│   │   ├── repository/          # Data Access Layer
│   │   ├── entity/              # JPA Entities
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── mapper/              # Entity-DTO Mappers
│   │   ├── exception/           # Custom Exceptions
│   │   └── CrudappApplication.java
│   └── resources/
│       └── application.yml      # Application Configuration
└── test/
    ├── java/com/example/crudapp/
    │   ├── controller/          # Controller Tests
    │   ├── service/             # Service Tests
    │   ├── repository/          # Repository Tests
    │   ├── mapper/              # Mapper Tests
    │   └── bdd/                 # BDD Step Definitions
    └── resources/
        ├── features/            # Cucumber Feature Files
        └── cleanup.sql          # Test Data Cleanup
```

## Error Handling

The application provides comprehensive error handling with appropriate HTTP status codes:

- **400 Bad Request**: Validation errors
- **404 Not Found**: User not found
- **409 Conflict**: Duplicate email
- **500 Internal Server Error**: Unexpected errors

## Logging

The application uses SLF4J with Logback for logging with the following levels:
- **INFO**: Business operations and API calls
- **DEBUG**: Detailed execution flow
- **ERROR**: Exception handling and error scenarios

Logger count is optimized to not exceed 7 loggers per request.

## Monitoring and Analytics

### Postman Collection
Import `postman_collection.json` into Postman for comprehensive API testing including:
- Success scenarios
- Error scenarios
- Validation testing
- Performance testing

### Splunk Queries
Use queries from `splunk_queries.md` for:
- API performance monitoring
- Error rate analysis
- User activity tracking
- Security auditing
- Business metrics

## Configuration

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

### Logging Configuration
```yaml
logging:
  level:
    com.example.crudapp: INFO
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
```

## Development Guidelines

### Code Quality
- Comprehensive JavaDoc documentation
- Clean code principles
- SOLID design patterns
- Proper exception handling
- Input validation

### Testing Strategy
- Unit tests for all business logic
- Integration tests for data layer
- BDD tests for user scenarios
- Mock external dependencies
- Maintain high test coverage

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement changes with tests
4. Ensure test coverage >85%
5. Submit a pull request

## License

This project is licensed under the MIT License.
