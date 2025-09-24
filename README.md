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

## ✨ Features

### Core Features
-  **User Management** - User registration, authentication, and profile management
-  **Account Management** - Multiple account types (checking, savings, credit cards)
-  **Transaction Tracking** - Income, expenses, transfers with categorization
-  **Categories & Tags** - Organize transactions with custom categories
-  **Multi-Currency Support** - Handle multiple currencies with conversion
-  **Financial Analytics** - Spending analysis, trends, and reports
-  **Budget Management** - Set and track budgets by category
-  **RESTful API** - Clean, documented API endpoints

### Advanced Features
-  **Dashboard Analytics** - Financial overview and insights
-  **Advanced Search** - Filter transactions by date, amount, category
-  **Export Data** - Export transactions to CSV/PDF
-  **Security** - JWT authentication and authorization
-  **Audit Trail** - Track all financial changes
-  **Internationalization** - Multi-language support

##  Tech Stack

### Backend
- **Java 21+** - Programming language
- **Spring Boot 3.x** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
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
git clone https://github.com/your-username/financial-api.git
cd financial-api
```

### 2. Set Up Environment Variables
Create a `.env` file in the root directory:
```env
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=financial_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-here
JWT_EXPIRATION=86400000

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
```

### 3. Configure Application Properties
Update `src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:financial_db}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# Server Configuration
server.port=${SERVER_PORT:8080}

# JWT Configuration
jwt.secret=${JWT_SECRET:defaultSecretKey}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Logging Configuration
logging.level.com.financial.api=INFO
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Spring Boot Configuration
spring.application.name=Financial Management API
spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}

# Jackson Configuration (JSON serialization)
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.time-zone=UTC

# Validation Messages
spring.messages.basename=validation

# Actuator Configuration (for monitoring)
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

### 4. Install Dependencies
```bash
mvn clean install
```

## 🗄️ Database Setup

### Option 1: Local MySQL Setup
1. **Install MySQL 8.0+**
2. **Create Database:**
```sql
CREATE DATABASE financial_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'finapp'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON financial_db.* TO 'finapp'@'localhost';
FLUSH PRIVILEGES;
```

3. **Run Migrations:**
```bash
mvn flyway:migrate
```

### Option 2: Docker Setup
```bash
# Start MySQL container
docker run --name financial-mysql \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=financial_db \
  -e MYSQL_USER=finapp \
  -e MYSQL_PASSWORD=finapp123 \
  -p 3306:3306 \
  -d mysql:8.0

# Run migrations
mvn flyway:migrate
```

### Option 3: Docker Compose
```bash
# Start all services
docker-compose up -d

# Check status
docker-compose ps
```

## 🏃‍♂️ Running the Application

### Development Mode
```bash
mvn spring-boot:run
```

### Production Mode
```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/financial-api-1.0.0.jar
```

### Docker
```bash
# Build image
docker build -t financial-api .

# Run container
docker run -p 8080:8080 financial-api
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

#### Users
```http
GET    /api/v1/users/profile     # Get user profile
PUT    /api/v1/users/profile     # Update profile
DELETE /api/v1/users/profile     # Delete account
```

#### Accounts
```http
GET    /api/v1/accounts          # Get all accounts
POST   /api/v1/accounts          # Create account
GET    /api/v1/accounts/{id}     # Get account by ID
PUT    /api/v1/accounts/{id}     # Update account
DELETE /api/v1/accounts/{id}     # Delete account
```

#### Transactions
```http
GET    /api/v1/transactions      # Get all transactions
POST   /api/v1/transactions      # Create transaction
GET    /api/v1/transactions/{id} # Get transaction by ID
PUT    /api/v1/transactions/{id} # Update transaction
DELETE /api/v1/transactions/{id} # Delete transaction
```

#### Analytics
```http
GET /api/v1/analytics/dashboard     # Dashboard summary
GET /api/v1/analytics/spending      # Spending analysis
GET /api/v1/analytics/income        # Income analysis
GET /api/v1/analytics/trends        # Financial trends
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

## Configuration

### Environment Profiles
- **`dev`** - Development environment
- **`test`** - Testing environment
- **`prod`** - Production environment

### Key Properties
```properties
# Security
jwt.secret=Your JWT secret key
jwt.expiration=Token expiration time

# Database
spring.datasource.url=Database connection URL
spring.jpa.hibernate.ddl-auto=validate

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# Logging
logging.level.com.financial.api=INFO
logging.level.org.springframework.security=DEBUG
```

## Usage Examples

### Create a User Account
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "username": "johndoe",
    "password": "securePassword123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "securePassword123"
  }'
```

### Create an Account
```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Checking Account",
    "description": "Main checking account",
    "amount": 1000.00,
    "categoryId": 1,
    "currencyId": 1
  }'
```

### Record a Transaction
```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Grocery Shopping",
    "description": "Weekly groceries",
    "amount": -87.50,
    "accountId": 1,
    "categoryId": 5,
    "transactionTypeId": 2
  }'
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

### Docker Production Deployment
```bash
# Build production image
docker build -f Dockerfile.prod -t financial-api:prod .

# Run with production settings
docker run -d \
  --name financial-api-prod \
  -p 8080:8080 \
  --env-file .env.prod \
  financial-api:prod
```

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

**Made with ❤️ for better financial management**