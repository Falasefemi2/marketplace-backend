# Use OpenJDK 21 runtime
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built jar into the container
COPY target/marketplace-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
