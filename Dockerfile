# Use an official Eclipse Temurin (OpenJDK) image as the base image
FROM eclipse-temurin:18-jdk AS build

# Set the working directory in the container
WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy the project files
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Copy the packaged JAR file into the container
# Use a lightweight base image for the final stage
FROM eclipse-temurin:18-jre

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file from the build stage
COPY --from=build /app/target/Lib_Man-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]