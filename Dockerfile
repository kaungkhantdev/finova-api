FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /usr/src/app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-jammy AS runner

WORKDIR /usr/src/app

RUN groupadd --system appgroup && useradd --system -g appgroup appuser

COPY --chown=appuser:appgroup --from=builder /usr/src/app/target/your-artifact-id-*.jar /usr/src/app/app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]