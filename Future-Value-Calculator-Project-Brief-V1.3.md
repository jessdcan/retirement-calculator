# 🏦 Retirement Calculator API Project Brief V1.3

## 📝 Project Description

You will develop a comprehensive Retirement Savings Calculator API that allows users to calculate their future retirement savings based on their current age, retirement age, interest rate, and lifestyle preferences. This project will give you hands-on experience with modern Java development, Spring Boot, database integration, caching, containerization, and RESTful API design. 


## 🛠️ Technical Stack

- **Language:** ☕ Java 17
- **Framework:** 🌱 Spring Boot 3
- **API:** 🔌 RESTful endpoints with JSON and XML support
- **Database:** 🐘 PostgreSQL (containerized)
- **Caching:** 🔄 Redis (containerized)
- **Containerization:** 🐳 Docker/Podman & Docker Compose/Podman Compose (interchangeable)
- **Configuration:** ⚙️ YAML configuration
- **Documentation:** 📚 OpenAPI 3 with Swagger UI
- **Test Coverage:** ✅ JaCoCo (minimum 80% for Controllers and Services)
- **Code Reduction:** 🧩 Lombok
- **Error Handling:** ⚠️ Global exception handling with appropriate HTTP status codes

> **Note on Containerization:** Throughout this document, references to Docker and Docker Compose are interchangeable with Podman and Podman Compose respectively. Both tools provide similar functionality and can be used to containerize this application. Choose the tool that best fits your environment and requirements.

## 📋 Project Requirements

1. 📡 Create a RESTful API that accepts:
   - Current Age (years)
   - Retirement Age (years)
   - Interest Rate (percentage)
   - Lifestyle Type (simple/fancy)

2. 🧮 Implement a calculator service that:
   - Performs future value calculations
   - Retrieves periodic deposit amounts from Redis cache based on lifestyle type
   - Returns comprehensive results

3. 💾 Database and caching:
   - PostgreSQL database storing lifestyle data
   - Redis cache populated at startup from the database
   - Efficient cache retrieval

4. ✅ Proper error handling, validation, and testing
5. 📚 Documentation and containerization

## 📅 Daily Implementation Plan

### Day 1: 🌅 Project Setup and Database Integration

**Morning Tasks:**
1. 🚀 Initialize Spring Boot project with necessary dependencies:
   - Spring Web
   - Spring Data JPA
   - PostgreSQL Driver
   - Spring Boot DevTools
   - Lombok (required)
   - Spring Validation
   - Spring Boot Test
   - JaCoCo for test coverage
   - Initialize project as a git repository

2. Set up container environment:
   - Install Docker/Podman and Docker Compose/Podman Compose if not already available
   - Create a compose file for PostgreSQL
   - Configure PostgreSQL container with appropriate environment variables
   - Learn to start and stop the containerized database

3. Configure application using YAML:
   - Create `application.yaml` with database configuration
   - Configure database connection properties for the dockerized PostgreSQL
   - Set up profiles for development and testing environments

4. Design database schema:
   - Plan the `lifestyle_deposits` table structure with appropriate fields
   - Prepare database initialization scripts

**Afternoon Tasks:**
5. Implement entity classes:
   - Create entity models using Lombok annotations
   - Add validation annotations where appropriate
   - Document the classes with appropriate comments

6. Create repository interfaces:
   - Define JPA repository interfaces with appropriate query methods
   - Document the repository with Javadoc

7. Implement database initialization:
   - Create SQL scripts for initial data
   - Configure Flyway or Liquibase for database migration (optional stretch goal)

8. Write unit tests for repository layer:
   - Set up test configuration for the repository layer
   - Write comprehensive tests using Spring Boot Test
   - Configure an H2 in-memory database for testing

9. Configure JaCoCo:
   - Set up JaCoCo Maven plugin
   - Configure coverage rules for controller and service classes (80% minimum)
   - Create a test report goal

**Deliverable:** 
- A Spring Boot project with proper Docker setup for PostgreSQL
- YAML configuration for the application
- Database schema created with initial data
- Repository layer implemented with comprehensive tests
- JaCoCo configured for test coverage enforcement

### Day 2: Redis Cache Implementation

**Morning Tasks:**
1. Set up Redis with containers:
   - Extend the compose file to include Redis
   - Configure appropriate ports and volumes for Redis
   - Learn to inspect Redis data using Redis CLI or RedisInsight

2. Add Redis dependencies to the project:
   - Add Spring Data Redis dependency
   - Configure Redis properties in application.yaml

3. Create Redis configuration:
   - Create a configuration class for Redis connection
   - Define appropriate serialization strategy
   - Configure cache settings and TTL values

**Afternoon Tasks:**
4. Implement cache service:
   - Design a service to interact with Redis
   - Implement cache initialization logic on application startup
   - Create methods to fetch, update, and delete cached data
   - Implement a cache refresh mechanism

5. Implement unit tests for cache service:
   - Write comprehensive tests using TestContainers or embedded Redis
   - Test cache hits and misses
   - Test cache initialization and refresh functionality
   - Ensure test coverage meets the 80% threshold for service classes

6. Add cache monitoring endpoints:
   - Create endpoints to check cache status
   - Implement an endpoint to manually refresh the cache
   - Add appropriate security measures

**Deliverable:**
- Working Redis container integrated with the application
- Cache service that initializes on startup
- Comprehensive unit tests with good coverage
- Cache monitoring and management endpoints

### Day 3: Calculator Service Implementation

**Morning Tasks:**
1. Design calculator service components:
   - Define service interfaces for the calculator functionality
   - Design appropriate DTOs for request and response data
   - Document the interfaces with clear Javadoc

2. Create custom exception hierarchy:
   - Design an exception hierarchy for the application
   - Create specific exceptions for various error scenarios
   - Ensure exceptions contain appropriate details for troubleshooting

3. Implement the calculator service:
   - Create the service implementation with proper retirement calculation logic
   - Implement validation logic for input parameters
   - Integrate with the cache service to retrieve lifestyle data
   - Apply appropriate mathematical formulas for future value calculations
   - Follow best practices for handling BigDecimal arithmetic

**Afternoon Tasks:**
4. Implement comprehensive unit tests:
   - Write tests for all calculator service methods
   - Test happy path scenarios
   - Test edge cases and error conditions
   - Use mocking frameworks to isolate service from dependencies
   - Ensure 80% test coverage as required by JaCoCo

5. Implement logging:
   - Add appropriate logging throughout the service
   - Log method entry/exit points
   - Log exceptions and error conditions
   - Configure appropriate log levels

**Deliverable:**
- Calculator service with future value calculation logic
- Integration with the Redis cache service
- Custom exception hierarchy for error handling
- Comprehensive unit tests with 80% coverage
- Proper logging implementation

### Day 4: REST API Development

**Morning Tasks:**
1. Design REST API structure:
   - Plan API endpoints and HTTP methods
   - Design request/response DTOs with appropriate validation annotations
   - Create API versioning strategy
   - Document API design decisions

2. Configure OpenAPI 3 with Swagger UI:
   - Add OpenAPI 3 dependencies
   - Configure Swagger UI interface
   - Learn how to document API endpoints using OpenAPI annotations
   - Set up security schemes if needed

3. Implement XML support:
   - Configure application to support both JSON and XML formats
   - Set up appropriate message converters
   - Test content negotiation

**Afternoon Tasks:**
4. Implement REST controllers:
   - Create controller classes following RESTful principles
   - Implement endpoints for calculation functionality
   - Add appropriate request validation
   - Document endpoints with OpenAPI annotations

5. Implement global exception handling:
   - Create a global exception handler using @RestControllerAdvice
   - Define specific exception handlers for different error cases
   - Create standardized error response DTOs
   - Ensure appropriate HTTP status codes are returned

6. Write controller tests:
   - Create unit tests for controller methods
   - Implement integration tests for API endpoints
   - Test error handling and validation
   - Verify test coverage meets JaCoCo requirements (80%)

**Deliverable:**
- RESTful API with JSON and XML support
- OpenAPI 3 documentation with Swagger UI interface
- Global exception handling
- Input validation
- Controller unit and integration tests with 80% coverage

## 📚 Technical Concepts to Learn

Through this project, you will learn:

1. 🌱 **Spring Boot Fundamentals**:
   - Application setup and configuration
   - Dependency injection
   - Bean management
   - Properties configuration

2. 🔌 **RESTful API Design**:
   - Resource modeling
   - HTTP methods
   - Status codes
   - Content negotiation (JSON/XML)
   - API documentation with Swagger/OpenAPI

3. 💾 **Database Integration**:
   - Spring Data JPA
   - Entity design
   - Repository pattern
   - Transaction management

4. 🔄 **Caching with Redis**:
   - Cache configuration
   - Data serialization
   - Cache population strategies
   - Cache invalidation

5. 🧮 **Business Logic Implementation**:
   - Financial calculations
   - Service layer design
   - Error handling
   - Input validation

6. ✅ **Testing Strategies**:
   - Unit testing
   - Integration testing
   - Mocking and stubbing
   - Test configurations

7. 🐳 **Containerization**:
   - Container basics (Docker/Podman)
   - Multi-container applications
   - Environment configuration
   - Volume management

8. 📄 **XML Configuration and Processing**:
   - XML request/response formatting
   - XML validation
   - Jackson XML conversion

## 📊 Assessment Criteria

Each day's deliverable will be assessed based on:

1. ⚡ **Functionality**: Does it work as expected?
2. 💎 **Code Quality**: Is the code well-structured, readable, and maintainable?
3. ✅ **Testing**: Are there appropriate tests with good coverage?
4. 📝 **Documentation**: Is the code well-documented with comments and API documentation?
5. 🏆 **Best Practices**: Does it follow Java and Spring Boot best practices?

## 🔗 Resources

- 📚 [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- 📚 [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- 📚 [Spring Data Redis Documentation](https://docs.spring.io/spring-data/redis/docs/current/reference/html/)
- 📚 [Docker Documentation](https://docs.docker.com/)
- 📚 [Podman Documentation](https://docs.podman.io/)
- 📚 [Podman Compose Documentation](https://docs.podman.io/en/latest/markdown/podman-compose.1.html)
- 📚 [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- 📚 [Redis Documentation](https://redis.io/documentation)