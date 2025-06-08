# API Flow Diagram - User Management CRUD Operations

This document provides comprehensive flow diagrams for all User Management APIs, showing the complete request-response flow through the application layers.

## Overall Architecture Flow

```mermaid
graph TB
    Client[Client Application] --> Controller[UserController]
    Controller --> Service[UserService]
    Service --> Repository[UserRepository]
    Repository --> Database[(H2 Database)]
    
    Controller --> GlobalHandler[GlobalExceptionHandler]
    Service --> GlobalHandler
    Repository --> GlobalHandler
    
    GlobalHandler --> ErrorResponse[Error Response]
    Controller --> SuccessResponse[Success Response]
```

## 1. Create User API - POST /api/users

```mermaid
sequenceDiagram
    participant Client
    participant Controller as UserController
    participant Service as UserService
    participant Repository as UserRepository
    participant DB as H2 Database
    participant Handler as GlobalExceptionHandler

    Client->>Controller: POST /api/users<br/>{name, email, age}
    Controller->>Controller: Log request
    Controller->>Service: createUser(userDto)
    
    alt Email already exists
        Service->>Repository: existsByEmail(email)
        Repository->>DB: SELECT COUNT(*) WHERE email=?
        DB-->>Repository: count > 0
        Repository-->>Service: true
        Service->>Service: throw UserAlreadyExistsException
        Service-->>Handler: UserAlreadyExistsException
        Handler->>Handler: Log error
        Handler-->>Controller: 409 Conflict Response
        Controller-->>Client: 409 {error, message, timestamp}
    else Email is unique
        Service->>Repository: existsByEmail(email)
        Repository->>DB: SELECT COUNT(*) WHERE email=?
        DB-->>Repository: count = 0
        Repository-->>Service: false
        Service->>Service: Map DTO to Entity
        Service->>Repository: save(user)
        Repository->>DB: INSERT INTO users
        DB-->>Repository: User with ID
        Repository-->>Service: Saved User Entity
        Service->>Service: Map Entity to DTO
        Service-->>Controller: UserDto
        Controller->>Controller: Log success
        Controller-->>Client: 201 Created {id, name, email, age}
    end
```

## 2. Get All Users API - GET /api/users

```mermaid
sequenceDiagram
    participant Client
    participant Controller as UserController
    participant Service as UserService
    participant Repository as UserRepository
    participant DB as H2 Database

    Client->>Controller: GET /api/users
    Controller->>Controller: Log request
    Controller->>Service: getAllUsers()
    Service->>Repository: findAll()
    Repository->>DB: SELECT * FROM users
    DB-->>Repository: List<User>
    Repository-->>Service: List<User>
    Service->>Service: Map Entities to DTOs
    Service-->>Controller: List<UserDto>
    Controller->>Controller: Log success
    Controller-->>Client: 200 OK [users array]
```

## 3. Get User by ID API - GET /api/users/{id}

```mermaid
sequenceDiagram
    participant Client
    participant Controller as UserController
    participant Service as UserService
    participant Repository as UserRepository
    participant DB as H2 Database
    participant Handler as GlobalExceptionHandler

    Client->>Controller: GET /api/users/{id}
    Controller->>Controller: Log request
    Controller->>Service: getUserById(id)
    
    alt User exists
        Service->>Repository: findById(id)
        Repository->>DB: SELECT * FROM users WHERE id=?
        DB-->>Repository: Optional<User> (present)
        Repository-->>Service: User Entity
        Service->>Service: Map Entity to DTO
        Service-->>Controller: UserDto
        Controller->>Controller: Log success
        Controller-->>Client: 200 OK {id, name, email, age}
    else User not found
        Service->>Repository: findById(id)
        Repository->>DB: SELECT * FROM users WHERE id=?
        DB-->>Repository: Optional<User> (empty)
        Repository-->>Service: Optional.empty()
        Service->>Service: throw UserNotFoundException
        Service-->>Handler: UserNotFoundException
        Handler->>Handler: Log error
        Handler-->>Controller: 404 Not Found Response
        Controller-->>Client: 404 {error, message, timestamp}
    end
```

## 4. Update User API - PUT /api/users/{id}

```mermaid
sequenceDiagram
    participant Client
    participant Controller as UserController
    participant Service as UserService
    participant Repository as UserRepository
    participant DB as H2 Database
    participant Handler as GlobalExceptionHandler

    Client->>Controller: PUT /api/users/{id}<br/>{name, email, age}
    Controller->>Controller: Log request
    Controller->>Service: updateUser(id, userDto)
    
    alt User exists
        Service->>Repository: findById(id)
        Repository->>DB: SELECT * FROM users WHERE id=?
        DB-->>Repository: Optional<User> (present)
        Repository-->>Service: Existing User Entity
        
        alt Email changed and already exists
            Service->>Repository: existsByEmail(newEmail)
            Repository->>DB: SELECT COUNT(*) WHERE email=? AND id!=?
            DB-->>Repository: count > 0
            Repository-->>Service: true
            Service->>Service: throw UserAlreadyExistsException
            Service-->>Handler: UserAlreadyExistsException
            Handler->>Handler: Log error
            Handler-->>Controller: 409 Conflict Response
            Controller-->>Client: 409 {error, message, timestamp}
        else Email is unique or unchanged
            Service->>Service: Update entity fields
            Service->>Repository: save(updatedUser)
            Repository->>DB: UPDATE users SET ... WHERE id=?
            DB-->>Repository: Updated User Entity
            Repository-->>Service: Updated User Entity
            Service->>Service: Map Entity to DTO
            Service-->>Controller: UserDto
            Controller->>Controller: Log success
            Controller-->>Client: 200 OK {id, name, email, age}
        end
    else User not found
        Service->>Repository: findById(id)
        Repository->>DB: SELECT * FROM users WHERE id=?
        DB-->>Repository: Optional<User> (empty)
        Repository-->>Service: Optional.empty()
        Service->>Service: throw UserNotFoundException
        Service-->>Handler: UserNotFoundException
        Handler->>Handler: Log error
        Handler-->>Controller: 404 Not Found Response
        Controller-->>Client: 404 {error, message, timestamp}
    end
```

## 5. Delete User API - DELETE /api/users/{id}

```mermaid
sequenceDiagram
    participant Client
    participant Controller as UserController
    participant Service as UserService
    participant Repository as UserRepository
    participant DB as H2 Database
    participant Handler as GlobalExceptionHandler

    Client->>Controller: DELETE /api/users/{id}
    Controller->>Controller: Log request
    Controller->>Service: deleteUser(id)
    
    alt User exists
        Service->>Repository: existsById(id)
        Repository->>DB: SELECT COUNT(*) FROM users WHERE id=?
        DB-->>Repository: count = 1
        Repository-->>Service: true
        Service->>Repository: deleteById(id)
        Repository->>DB: DELETE FROM users WHERE id=?
        DB-->>Repository: Success
        Repository-->>Service: void
        Service-->>Controller: void
        Controller->>Controller: Log success
        Controller-->>Client: 204 No Content
    else User not found
        Service->>Repository: existsById(id)
        Repository->>DB: SELECT COUNT(*) FROM users WHERE id=?
        DB-->>Repository: count = 0
        Repository-->>Service: false
        Service->>Service: throw UserNotFoundException
        Service-->>Handler: UserNotFoundException
        Handler->>Handler: Log error
        Handler-->>Controller: 404 Not Found Response
        Controller-->>Client: 404 {error, message, timestamp}
    end
```

## Exception Handling Flow

```mermaid
graph TB
    RuntimeEx[Runtime Exceptions] --> GlobalHandler[GlobalExceptionHandler]
    
    subgraph "Exception Types"
        NPE[NullPointerException]
        IOOBE[IndexOutOfBoundsException]
        IAE[IllegalArgumentException]
        ISE[IllegalStateException]
        NFE[NumberFormatException]
        CCE[ClassCastException]
        DIVE[DataIntegrityViolationException]
        UNFE[UserNotFoundException]
        UAEE[UserAlreadyExistsException]
        GE[Generic Exception]
    end
    
    NPE --> GlobalHandler
    IOOBE --> GlobalHandler
    IAE --> GlobalHandler
    ISE --> GlobalHandler
    NFE --> GlobalHandler
    CCE --> GlobalHandler
    DIVE --> GlobalHandler
    UNFE --> GlobalHandler
    UAEE --> GlobalHandler
    GE --> GlobalHandler
    
    GlobalHandler --> ErrorLog[Error Logging]
    GlobalHandler --> ErrorResponse[Structured Error Response]
    
    subgraph "HTTP Status Codes"
        Status400[400 Bad Request]
        Status404[404 Not Found]
        Status409[409 Conflict]
        Status500[500 Internal Server Error]
    end
    
    ErrorResponse --> Status400
    ErrorResponse --> Status404
    ErrorResponse --> Status409
    ErrorResponse --> Status500
```

## Data Validation Flow

```mermaid
graph TB
    Request[API Request] --> Validation{Validation}
    
    Validation -->|Valid| Controller[Controller Processing]
    Validation -->|Invalid| ValidationError[MethodArgumentNotValidException]
    
    ValidationError --> GlobalHandler[GlobalExceptionHandler]
    GlobalHandler --> ValidationResponse[400 Bad Request<br/>with field errors]
    
    Controller --> Service[Service Layer]
    Service --> BusinessValidation{Business Rules}
    
    BusinessValidation -->|Valid| Repository[Repository Layer]
    BusinessValidation -->|Invalid| BusinessError[Business Exception]
    
    BusinessError --> GlobalHandler
    Repository --> Database[(H2 Database)]
```

## API Response Formats

### Success Responses

```json
// Create User (201 Created)
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "age": 30
}

// Get All Users (200 OK)
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "age": 30
  }
]

// Delete User (204 No Content)
// Empty response body
```

### Error Responses

```json
// User Not Found (404)
{
  "timestamp": "2025-06-08T21:46:00.323",
  "status": 404,
  "error": "User Not Found",
  "message": "User not found with ID: 1"
}

// User Already Exists (409)
{
  "timestamp": "2025-06-08T21:46:00.323",
  "status": 409,
  "error": "User Already Exists",
  "message": "User with email john@example.com already exists"
}

// Validation Error (400)
{
  "timestamp": "2025-06-08T21:46:00.323",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "validationErrors": {
    "email": "Email is required",
    "age": "Age must be greater than 0"
  }
}
```

## Performance Considerations

- **Database Connections**: H2 in-memory database for fast operations
- **Transaction Management**: `@Transactional` annotations for data consistency
- **Logging**: Strategic logging at INFO/WARN/ERROR levels (max 7 per request)
- **Exception Handling**: Centralized error handling for consistent responses
- **Validation**: Early validation to prevent unnecessary processing

## Security Considerations

- **Input Validation**: Bean validation annotations on DTOs
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries
- **Error Information**: Sanitized error messages to prevent information leakage
- **Exception Logging**: Detailed logging for debugging without exposing sensitive data
