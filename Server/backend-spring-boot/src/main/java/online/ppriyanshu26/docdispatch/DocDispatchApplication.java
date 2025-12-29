/*
 * DocDispatchApplication.java - Main Entry Point for DocDispatch Backend Server
 * 
 * PURPOSE:
 * This is the main Spring Boot application class that bootstraps the entire server.
 * When this application runs, it starts an embedded Tomcat server on port 8081 and
 * initializes all Spring components (controllers, services, repositories).
 * 
 * FUNCTIONALITY:
 * - Serves as the entry point for the Spring Boot application
 * - Auto-configures Spring components via @SpringBootApplication annotation
 * - Scans and registers all controllers, services, and repositories in the package
 * - Connects to MySQL database for patient query management
 * - Enables REST API endpoints for the Flutter mobile app
 * 
 * SERVER ARCHITECTURE:
 * The application follows MVC (Model-View-Controller) pattern with:
 * - Controllers: Handle HTTP requests and responses
 * - Services: Implement business logic
 * - Repositories: Manage database operations
 * - DTOs: Transfer data between layers
 * - Entities: Represent database tables
 */
package online.ppriyanshu26.docdispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DocDispatchApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DocDispatchApplication.class, args);
    }
}
