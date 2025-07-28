# Stage 1: Build
FROM eclipse-temurin:18-jdk as build

WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy only the pom.xml first and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the rest of the source code
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:18-jre

WORKDIR /app

COPY --from=build /app/target/Lib_Man-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
