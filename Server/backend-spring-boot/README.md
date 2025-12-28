# DocDispatch Spring Boot Backend

## Migration from Servlet to Spring Boot

This is the Spring Boot version of the DocDispatch backend, migrated from the traditional servlet-based architecture.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

## Setup Instructions

### 1. Database Configuration

Update the database credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/yourdbname
spring.datasource.username=root
spring.datasource.password=password
```

### 2. Database Schema

The application will auto-create tables using JPA. Ensure your MySQL database exists:

```sql
CREATE DATABASE yourdbname;
```

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/docdispatch-1.0.0.jar
```

The server will start on `http://localhost:8080`

## API Endpoints

All endpoints maintain the same URLs as the servlet version:

### 1. Add Query (Create new patient query)
- **URL**: `POST /api/patient`
- **Body**:
```json
{
  "contact": "1234567890",
  "name": "John Doe",
  "age": 30,
  "gender": "Male",
  "temperature": 98,
  "days": 3,
  "contagious": "No"
}
```

### 2. Get Queries (Fetch queries by contact)
- **URL**: `GET /api/queries?contact=1234567890`
- **Response**: Array of query objects with attended details if applicable

### 3. Attend Query (Mark query as attended)
- **URL**: `POST /api/attend`
- **Body**:
```json
{
  "qid": 1,
  "contact": "1234567890",
  "doctor": "Dr. Smith",
  "treatment": "Rest and medication",
  "remarks": "Follow up in 3 days"
}
```

## Key Improvements

1. **Dependency Injection**: Spring's IoC container manages all beans
2. **Auto Configuration**: Minimal configuration needed
3. **Spring Data JPA**: Eliminates manual JDBC code
4. **Transaction Management**: Automatic transaction handling
5. **Exception Handling**: Better error management
6. **RESTful Design**: Cleaner controller layer
7. **Built-in CORS**: Configured for Flutter app
8. **Lombok**: Reduces boilerplate code

## Project Structure

```
src/main/java/online/ppriyanshu26/docdispatch/
├── DocDispatchApplication.java       # Main application class
├── controller/
│   └── QueryController.java          # REST endpoints
├── service/
│   └── QueryService.java             # Business logic
├── repository/
│   ├── QueryRepository.java          # Data access for queries
│   └── AttendedRepository.java       # Data access for attended
├── entity/
│   ├── Query.java                    # Query entity
│   └── Attended.java                 # Attended entity
└── dto/
    ├── AddQueryRequest.java          # Request DTO
    ├── AttendQueryRequest.java       # Request DTO
    └── QueryResponse.java            # Response DTO
```

## Development

### Testing

Run tests with:
```bash
mvn test
```

### Packaging

Create a production-ready JAR:
```bash
mvn clean package
```

The JAR will be in `target/docdispatch-1.0.0.jar`

## Migration Notes

- All servlet functionality has been preserved
- API endpoints remain the same for compatibility with the Flutter app
- Database schema is managed by JPA (auto-creates/updates tables)
- No need for web.xml or servlet configurations
- Better performance with connection pooling (HikariCP)
- Production-ready with embedded Tomcat server
