# Use an official Java runtime as a parent image
FROM openjdk:18-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container
COPY target/*.jar app.jar

# Expose port 8080 (or the port your app runs on)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
