# 🏦 Retirement Calculator API Project Brief V3

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
- **Front-End:** 🌐 Angular 19 (built using Node.js and NPM)
- **Tooling:** 🛠️ Node.js (v18+), NPM (v9+), Angular CLI

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

6. 🌐 Build a front-end Angular application:
   - Uses form-based input to collect retirement calculation parameters
   - Communicates with the REST API using HttpClient
   - Outputs results dynamically
   - Can be built into static files (`index.html`, `.css`, `.js`) for web hosting

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
- Comprehensive unit tests with 70% coverage
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

### Day 5: 🌐 Angular Front-End Development

> _Objective: Build a simple web-based front end using Angular 19 to consume the Retirement Calculator API. The final output should be static files (`index.html`, `styles.css`, `main.js`) suitable for hosting on any web server._

**Morning Tasks:**

1. 🧰 **Install Prerequisites:**
   - Install Node.js (v18+) and NPM (v9+)
   - Verify installations with `node -v` and `npm -v`
   - Install Angular CLI globally using: `npm install -g @angular/cli`

2. 🏗️ **Initialize Angular Project:**
   - Generate a new Angular project: `ng new retirement-calculator-web`
      - Choose SCSS or CSS for styling
      - Enable Angular routing (optional for future routing work)
   - Navigate into project directory and run dev server: `ng serve`
   - Verify the app is running on `http://localhost:4200`

3. 🧪 **Understand Angular Basics:**
   - Explore structure: `src/app/`, `main.ts`, `index.html`, `angular.json`
   - Learn about modules, components, and services
   - Modify default component to include "Welcome to the Retirement Calculator"

**Afternoon Tasks:**

4. 🔌 **Create HTTP Client Service:**
   - Use Angular HttpClientModule to call the back-end API
   - Create a service to send user input and receive future value results
   - Handle observable data and errors using RxJS

5. 🎛️ **Build Input UI and Results Display:**
   - Create a new component (`calculator-form`)
   - Form should capture:
      - Current Age
      - Retirement Age
      - Interest Rate
      - Lifestyle Type (dropdown)
   - Display calculated results on form submission

6. 🛠️ **Build Static Files for Hosting:**
   - Run Angular production build: `ng build --configuration=production`
   - Locate build output in `dist/retirement-calculator-web/`
   - Confirm presence of:
      - `index.html`
      - `styles.[hash].css`
      - `main.[hash].js`

7. 🌍 **Optional: Test Hosting Locally**
   - Use a simple server (e.g., `npx serve dist/retirement-calculator-web`)
   - Confirm static site works and interacts with backend

**Deliverable:**
- An Angular front-end project that accepts user input and calls the Retirement Calculator API
- Responsive, styled form interface
- Build output containing static deployable files (`index.html`, CSS, JS)
- Understanding of Node, NPM, Angular CLI, and client-side HTTP calls

### Day 6: Update API and Add Secondary Redis Cache for Rates
#### **Morning Tasks**
1. **Refactor API Input Handling**:
   - Remove the `interest rate` field from the API input DTO and validation annotations.
   - Update the API documentation to reflect these changes (Swagger/OpenAPI).

2. **Design and Configure Secondary Redis Cache**:
   - Plan a secondary Redis cache to store interest rates based on `lifestyle type`.
   - Add configuration properties in `application.yaml` for this secondary cache connection.
   - Modify the existing `RedisConfiguration` class (or create a new one) to handle the secondary cache.

3. **CSV Parsing Logic**:
   - Implement a service to parse the CSV file located in the `resources` directory.
   - Add validation to ensure the file adheres to the expected format (columns: `lifestyleType`, `interestRate`).
   - Write logic to populate the cache with data parsed from the CSV file on application startup.

#### **Afternoon Tasks**
1. **Integrate Rate Lookup with Calculator Service**:
   - Update the `calculator service` to fetch the interest rate data from the newly implemented Redis cache based on the `lifestyle type`.
   - Add error handling for missing or invalid cache data (`RateNotFoundException`).

2. **Write Unit and Integration Tests**:
   - Unit test the CSV parsing logic, verifying correct mapping of rows to cache entries.
   - Test the Redis service responsible for fetching the interest rates.
   - Update the tests for the calculator service to validate integration with the rate lookup.

3. **Test Container Setup for Multiple Caches**:
   - Use TestContainers or an embedded Redis instance to test both caches in an isolated environment.
   - Create integration tests to ensure both caches are working correctly.

4. **Performance Optimization (Stretch Goal)**:
   - Improve rate lookup implementation by ensuring low-latency cache access.
   - Validate fallback mechanisms (e.g., reloading cached data if Redis is empty).

#### **Deliverable**:
- API updated to remove rate input and include automated rate lookup based on `lifestyle type`.
- CSV parsing logic to populate the secondary Redis cache on startup, integrated with the calculator service.
- Comprehensive test coverage for CSV parsing, rate lookup, and service integration.
- Proper documentation updates reflecting the changes (OpenAPI and inline comments).


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

9. 🌐 **Web Front-End Development with Angular**:
   - Node.js and NPM fundamentals
   - Angular project structure, components, and services
   - HttpClient and Observables
   - Angular CLI usage and production builds

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
- 📚 [Node.js Documentation](https://nodejs.org/en/docs)
- 📚 [Angular Documentation](https://angular.io/docs)
- 📚 [Angular CLI Overview](https://angular.io/cli)
- 📚 [NPM Documentation](https://docs.npmjs.com/)

🥚 <- just for you Jess