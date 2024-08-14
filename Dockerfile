# Use an official OpenJDK 21 runtime as a parent image
FROM openjdk:21-slim

# Set the working directory in the container
WORKDIR /app

# Copy the project's jar file into the container at /app
COPY target/romannumeralservice-1.0.0.jar /app/app.jar

# Copy Promethues jmx exporter configs and jar.
COPY jmx_prometheus_javaagent-1.0.1.jar /app/jmx_prometheus_javaagent-1.0.1.jar
COPY ./config.yaml /app/prometheus_config.yaml

# Ensure the jar file has execute permissions
RUN chmod +x /app/app.jar

# Expose the application port
EXPOSE 8080
EXPOSE 8090
# Run the jar file
# jmx agent is set to 8090 in the docker file instead the port can also be added as an environement variable.
ENTRYPOINT ["java","-javaagent:./jmx_prometheus_javaagent-1.0.1.jar=8090:prometheus_config.yaml", "-jar", "/app/app.jar"]