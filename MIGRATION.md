# Flyway Database Migration Guide

This project uses Flyway with Maven for database version control.
Migration scripts are located in:

```bash
src/main/resources/db/migration
```


Each script must follow Flyway’s naming convention:
```bash

V<version_number>__<description>.sql


Example:

V1__init.sql
V2__add_indexes.sql
V3__seed_data.sql
```

### Common Commands
Run migrations
```bash
mvn flyway:migrate

# alternative
mvn flyway:migrate \
  -Dflyway.url=jdbc:mysql://localhost:3306/financial \
  -Dflyway.user=root \
  -Dflyway.password=

```


### Applies all pending migrations in order.

Keeps track of applied migrations in the flyway_schema_history table.

Validate migrations
```bash
mvn flyway:validate

# alternative 
mvn flyway:validate \
  -Dflyway.url=jdbc:mysql://localhost:3306/financial \
  -Dflyway.user=root \
  -Dflyway.password=

```


### Ensures applied migrations match the files on disk (no accidental edits).

Clean database ⚠️ (Dangerous!)
```bash
mvn flyway:clean

# alternative 
mvn flyway:clean \
  -Dflyway.url=jdbc:mysql://localhost:3306/financial \
  -Dflyway.user=root \
  -Dflyway.password=

```


### Drops all tables, views, and data in the database.

Use only in local/dev environments, never in production.

Repair migration history
```bash
mvn flyway:repair

# alternative
mvn flyway:repair \
  -Dflyway.url=jdbc:mysql://localhost:3306/financial \
  -Dflyway.user=root \
  -Dflyway.password=

```


### Fixes issues in the flyway_schema_history table (e.g., if a checksum mismatch happens).

Info
```bash
mvn flyway:info

# alternative
mvn flyway:info \
  -Dflyway.url=jdbc:mysql://localhost:3306/financial \
  -Dflyway.user=root \
  -Dflyway.password=

```


### Shows which migrations were applied, pending, or failed.

Configuration

Flyway database connection is configured in your application.properties or application.yml (Spring Boot):

```bash

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.datasource.url=jdbc:mysql://localhost:3306/financial_db
spring.datasource.username=your_user
spring.datasource.password=your_password
```