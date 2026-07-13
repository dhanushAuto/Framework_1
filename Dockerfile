# AI-Driven Automation Framework

FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .

# Default command to run tests
CMD ["mvn", "test"]
