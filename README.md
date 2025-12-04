# Financial Management API

A comprehensive REST API for personal financial management built with Spring Boot, providing features for tracking accounts, transactions, budgets, and financial analytics.

##  Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Usage Examples](#usage-examples)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Features

### Core Features
-  **User Management** - User registration, authentication, and profile management
-  **Account Management** - Multiple account types (checking, savings, credit cards)
-  **Transaction Tracking** - Income, expenses, transfers with categorization
-  **Categories** - Organize transactions with custom categories
-  **Financial Analytics** - Spending analysis, trends, and reports
-  **RESTful API** - Clean, documented API endpoints

### Advanced Features
-  **Dashboard Analytics** - Financial overview and insights
-  **Security** - JWT authentication and authorization
-  **Rate Limiting** - Prevent abuse and protect against DDoS attacks

##  Tech Stack

### Backend
- **Java 21+** - Programming language
- **Spring Boot 3.x** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
- **JWT** - JSON Web Tokens for authentication
- **Mail & Template** - Send emails with templates using Thymeleaf
- **MySQL 8.0+** - Primary database
- **Flyway** - Database migration management
- **Maven** - Dependency management and build tool

### Libraries & Tools
- **Lombok** - Reduce boilerplate code
- **MapStruct** - Entity-DTO mapping
- **Swagger/OpenAPI 3** - API documentation
- **JUnit 5** - Unit testing
- **Testcontainers** - Integration testing
- **Docker** - Containerization
- **SLF4J + Logback** - Logging

## Prerequisites

Before running this application, ensure you have:

-  **Java 21 or higher**
-  **Maven 3.6+**
-  **MySQL 8.0+** (or Docker)
-  **Docker** (optional, for containerized setup)
-  **IDE** (IntelliJ IDEA, Eclipse, or VS Code)

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/finova-api.git
cd finova-api
```

### 2. Database Setup
Install MySQL 8.0+ and create a database named `finova`.
```sql
CREATE DATABASE finova;
```

### 2. Set Up Environment Variables
Copy the `.env.example` file to `.env` and update the values:
```bash
cp .env.example .env
```

### 3. Install Dependencies
```bash
mvn clean install
```

### 4. Run Migrations
```bash
mvn flyway:migrate
```

## Running the Application

### Development Mode
```bash
mvn spring-boot:run
```

### Production Mode
```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/finova-api-1.0.0.jar
```

## Running with Docker
```bash
# copy .env
cp .env.example .env

# Build image
docker build -t finova-api .

# Run container
docker run -p 8080:8080 --env-file .env finova-api
```

The API will be available at: `http://localhost:8080`

## API Documentation

### Swagger UI
Once the application is running, access the interactive API documentation:
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spec:** `http://localhost:8080/v3/api-docs`

### Key Endpoints

#### Authentication
```http
POST /api/v1/auth/register    # User registration
POST /api/v1/auth/login       # User login
POST /api/v1/auth/refresh     # Refresh token
```

## Project Structure

```
financial-management-api/
├── src/
│   ├── main/
│   │   ├── java/com/financial/api/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── exception/        # Custom exceptions
│   │   │   ├── mapper/           # MapStruct mappers
│   │   │   ├── repository/       # JPA repositories
│   │   │   ├── security/         # Security configuration
│   │   │   ├── service/          # Business logic
│   │   │   └── util/             # Utility classes
│   │   └── resources/
│   │       ├── db/migration/     # Flyway migrations
│   │       ├── application.yml   # Configuration
│   │       └── logback-spring.xml
│   └── test/                     # Test classes
├── docker/                       # Docker configurations
├── docs/                         # Documentation
├── .env                          # Environment variables
├── docker-compose.yml            # Docker Compose
├── Dockerfile                    # Docker build
├── pom.xml                       # Maven configuration
└── README.md
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Categories
```bash
# Unit tests only
mvn test -Dtest="*Test"

# Integration tests only
mvn test -Dtest="*IT"

# With coverage report
mvn test jacoco:report
```

### Test Coverage
Coverage reports are generated in `target/site/jacoco/index.html`

## Deployment

### Production Checklist
- [ ] Set secure JWT secret
- [ ] Configure production database
- [ ] Enable HTTPS/SSL
- [ ] Set up monitoring and logging
- [ ] Configure backup strategy
- [ ] Review security settings
- [ ] Set up CI/CD pipeline

## Contributing

1. **Fork the repository**
2. **Create a feature branch:** `git checkout -b feature/amazing-feature`
3. **Commit changes:** `git commit -m 'Add amazing feature'`
4. **Push to branch:** `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### Development Guidelines
- Follow Java naming conventions
- Write unit tests for new features
- Update documentation
- Ensure all tests pass
- Follow the existing code style

##  License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

##  Support

### Common Issues
- **Database Connection Issues:** Check MySQL is running and credentials are correct
- **Migration Failures:** Ensure database is clean or run `mvn flyway:repair`
- **JWT Errors:** Verify JWT secret is properly configured
- **Port Conflicts:** Change server.port in application.yml

### Getting Help
-  **Email:** support@financialapi.com
-  **Discord:** [Join our community](https://discord.gg/financialapi)
-  **Issues:** [GitHub Issues](https://github.com/your-username/financial-api/issues)
-  **Documentation:** [Full Documentation](https://docs.financialapi.com)

##  Acknowledgments

- Spring Boot team for the amazing framework
- MySQL for robust database support
- All contributors and users of this project

---

**Made with Love for better finova** 