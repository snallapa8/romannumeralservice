# RomanNumeralService

## Overview

RomanNumeralService is a Spring Boot application that converts integers to Roman numerals. The application is containerized using Docker and can be easily deployed and run in any environment that supports Docker. Additionally, the application includes metrics monitoring with Spring Boot Actuator, which can be integrated with Prometheus and Grafana for advanced monitoring and visualization.

## Features

- Converts integers to Roman numerals via a RESTful API.
- Supports range conversions.
- Dockerized for easy deployment.
- Supports Java 21 runtime.
- Integrated with Spring Boot Actuator for metrics.
- Optional integration with Prometheus and Grafana.

## Requirements

- **Java 21** (for building the application)
- **Maven 3.6+** (for building the application)
- **Docker** (for containerization)
- **Prometheus** (optional, for monitoring)
- **Grafana** (optional, for visualization)

## Getting Started

### Building the Application

1. **Clone the Repository**

   ```bash
   git clone https://github.com/your-repo/romannumeralservice.git
   cd romannumeralservice
   ```

2. **Build the Application**

   Ensure that you have Java 21 and Maven installed, then run:

   ```bash
   mvn clean package
   ```

   This will create an executable JAR file in the `target` directory.

### Running the Application locally

   run the below command for running it wihout jmx exporter:

   ```bash
   java -jar target/romannumeralservice-1.0.0.jar
   ```

   or

   ```bash
   mvn spring-boot:run
   ```
   
### Running the Application with Docker

1. **Build the Docker Image**

   Ensure Docker is installed and running, then build the Docker image:

   ```bash
   docker build -t roman-numeral-service .
   ```

2. **Run the Docker Container**

   Run the container, exposing the application on port `8080`:

   ```bash
   docker run -d -p 8080:8080 -p 8090:8090 --name roman-numeral-service-container roman-numeral-service
   ```

**Access the Application**

   Open your browser and navigate to:

   ```text
   http://localhost:8080
   ```

   **Example Endpoint**:
   - Convert an integer to a Roman numeral:

     ```text
     http://localhost:8080/romannumeral?query=10
     ```

   This will return a JSON response with the Roman numeral equivalent of `10`.

   - Convert an integer to a Roman numeral with range:

     ```text
     http://localhost:8080/romannumeral?min=6&max=8
     ```

   This will return a JSON response with the Roman numeral equivalent of `6`, `7` and `8`.
### Metrics Monitoring with Spring Boot Actuator

#### Step 1: Check the Actuator Endpoint configurations

Check `application.properties` file

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
```

#### Step 2: Access Actuator Endpoints

- **Health Endpoint**:

  ```bash
  curl http://localhost:8080/actuator/health
  ```

- **Metrics Endpoint**:

  ```bash
  curl http://localhost:8080/actuator/metrics
  ```

- **Example of Metrics Endpoint**:

  ```bash
  curl http://localhost:8080/actuator/metrics/jvm.memory.used
  ```

### Additional(Optional): Integrating with Prometheus and Grafana

#### Step 1: Check Prometheus Config

Check [./config.yaml](./config.yaml) configuration:

```yaml
# Using default rules so we can scrape all metrics
rules:
- pattern: ".*"
```

#### Step 2: Start Prometheus

We are leveraging [Jmx_exporter](https://github.com/prometheus/jmx_exporter/tree/release-1.0.1/docs) from prometheys to help us with exposing metrics at `localhost:8090/metrics`

* JMX exporter is running as a javaagent and it needs to be added as an arg the java command.

#### Step 3: Prometheus needs to scrape metrics from `/metrics`

1. Setup Prometheus (assumption the application will run in Kuberentes and an existing prometheus is running in the kuberentes cluster)
2. Define scrape configs rules via Servicemonitor or podmonitor in kuberentes with labels set to the deployment of the applicaiton.
3. The ServiceMonitor/PodMonitor will scrape the metrics from `:8090/metrics` location and publish to Prometheus
4. Levereage Prometheus and Grafana to visualize the metrics.
### Logs

Monitor the application logs for any errors or warnings related to Actuator or Prometheus scraping:

```bash
tail -f logs/application.log
```

* Stdout Logging is also getting done and assuming it is running kuberentes all stdout can be shipped to ElasticSearch or Splunk (Assumption a daemonset for splunk/logstash is running in kubenetes to push the logs to the corresponding server)
### Testing the Application

You can use `curl` or any REST client to test the API:

```bash
curl http://localhost:8080/romannumeral?query=10
```


#### Fetch the jmx metrics via 

```bash
curl https://localhost:8090/metrics
```

### Accessing the Application from Another Device

To access the application from another device on the same network, find the IP address of the machine running Docker and replace `localhost` with that IP:

```text
http://<your-ip-address>:8080
```

## Project Structure

```
.
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── adobe
│   │   │           └── romannumeralservice
│   │   │               ├── RomannumeralserviceApplication.java
│   │   │               └── controllers
│   │   │                   └── RomanNumeralController.java
│   │   │               └── services
│   │   │                   └── RomanNumeralService.java
│   │   └── resources
│   │       └── application.properties
├── Dockerfile
├── pom.xml
├── prometheus.yml
└── README.md
```

## Dockerfile

The [Dockerfile](./Dockerfile) is used to create a Docker image for the application:


## Troubleshooting

### Common Issues

- **Java Version Compatibility**: Ensure you are using Java 21, as this project is built with Java 21. If you encounter a `UnsupportedClassVersionError`, check your Java version.
- **Docker Issues**: Ensure Docker is running and accessible. If you encounter issues while building or running the Docker container, try restarting Docker and rebuilding the image.

## Notes

Scenarios Considered

1. 0 is considered as valid input but out of range
2. 01 (pre-fixed zeros) are considered as invalid inputs

## References
For more information on Roman numerals, see the specification on Wikipedia:

Roman Numerals - Wikipedia:
https://en.wikipedia.org/wiki/Roman_numerals
## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
