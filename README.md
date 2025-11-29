# Kafka Streams Skeleton (Spring Boot Template)
This repository is a reusable starter template for building Kafka Streams applications with Spring Boot and Java 21.
It provides a minimal, production-oriented setup with a sample topology, metrics, and externalized configuration
through Spring Boot properties and environment variables.
## Features
- Spring Boot 3.3 (via `spring-boot-starter-parent`)
- Java 21
- Kafka Streams 3.9 integrated through Spring Kafka
- Example topology that transforms messages to upper case
- Centralized Kafka Streams configuration
- Application metrics via Spring Boot Actuator and Micrometer Prometheus
- Configuration overridden via environment variables (suitable for containers and cloud platforms)
## Project structure
- `pom.xml` – Maven configuration and dependencies
- `src/main/java/io/kcmhub/KafkaStreamsApplication.java` – Spring Boot entry point
- `src/main/java/io/kcmhub/streams/StreamsTopologyConfig.java` – Kafka Streams configuration and topology wiring
- `src/main/java/io/kcmhub/streams/UppercaseTopologyBuilder.java` – example stream processing topology
- `src/main/java/io/kcmhub/streams/KafkaStreamsStarter.java` – lifecycle management for the Kafka Streams instance
- `src/main/resources/application.yml` – default Spring Boot and application configuration
## Prerequisites
- Java 21 (JDK 21) installed  
  - Verify with:
    ```powershell
    java -version
    ```
- Maven 3.x installed  
  - Verify with:
    ```powershell
    mvn -v
    ```
- A running Kafka cluster (default: `localhost:9092`)
## Configuration
Default configuration is defined in `src/main/resources/application.yml`. Relevant properties include:
```yaml
spring:
  application:
    name: kafka-streams-skeleton
  kafka:
    bootstrap-servers: localhost:9092
    streams:
      application-id: kafka-streams-skeleton
      replication-factor: 1
management:
  metrics:
    tags:
      application: ${spring.application.name}
      instance: ${METRICS_INSTANCE_ID:local}
app:
  kafka:
    input-topic: ${APP_KAFKA_INPUT_TOPIC:input-topic}
    output-topic: ${APP_KAFKA_OUTPUT_TOPIC:output-topic}
```
You can override these values at runtime using environment variables.
### Overriding Spring Kafka properties via environment variables
Spring Boot automatically maps environment variables to configuration properties by:
- Uppercasing the property name
- Replacing dots (`.`) with underscores (`_`)
- Using the result as the environment variable name
Some common mappings in this project:
- `spring.kafka.bootstrap-servers` ? `SPRING_KAFKA_BOOTSTRAP_SERVERS`
- `spring.kafka.streams.application-id` ? `SPRING_KAFKA_STREAMS_APPLICATION_ID`
- `app.kafka.input-topic` ? `APP_KAFKA_INPUT_TOPIC`
- `app.kafka.output-topic` ? `APP_KAFKA_OUTPUT_TOPIC`
#### Examples

##### Windows (PowerShell)
Open a PowerShell in the project directory and set environment variables before starting the app.

Override Kafka bootstrap servers:
```powershell
$env:SPRING_KAFKA_BOOTSTRAP_SERVERS = "broker1:9092,broker2:9092"
```

Override the Kafka Streams application id:
```powershell
$env:SPRING_KAFKA_STREAMS_APPLICATION_ID = "my-streams-service"
```

Override input/output topics:
```powershell
$env:APP_KAFKA_INPUT_TOPIC  = "orders-input"
$env:APP_KAFKA_OUTPUT_TOPIC = "orders-output"
```

Then run the application using Maven:
```powershell
mvn spring-boot:run
```

##### Linux/macOS (Bash)
Open a terminal in the project directory and set environment variables before starting the app.

Override Kafka bootstrap servers:
```bash
export SPRING_KAFKA_BOOTSTRAP_SERVERS="broker1:9092,broker2:9092"
```

Override the Kafka Streams application id:
```bash
export SPRING_KAFKA_STREAMS_APPLICATION_ID="my-streams-service"
```

Override input/output topics:
```bash
export APP_KAFKA_INPUT_TOPIC="orders-input"
export APP_KAFKA_OUTPUT_TOPIC="orders-output"
```

Then run the application using Maven:
```bash
mvn spring-boot:run
```

All of the above environment variables will override the values from `application.yml`.
> Note: Any property defined in `application.yml` can be overridden in the same way:  
> `some.property.path` → `SOME_PROPERTY_PATH`.
## Build and run
From the root of the project:
### Build
```powershell
mvn clean package
```
This will compile the code and create a JAR file under `target/`.
### Run with Maven
```powershell
mvn spring-boot:run
```
Make sure to set any environment variables in the same PowerShell session **before** running this command.
### Run the packaged JAR
After packaging, run:
```powershell
java -jar target/kafka-streams-skeleton-1.0-SNAPSHOT.jar
```
Environment-variable-based overrides work the same way when using the JAR.
## Local development workflow
1. Start a local Kafka broker (for example on `localhost:9092`).
2. Create the input and output topics:
   - Input topic name: taken from `app.kafka.input-topic` (default `input-topic`)
   - Output topic name: taken from `app.kafka.output-topic` (default `output-topic`)
3. Run the application:
   ```powershell
   mvn spring-boot:run
   ```
4. Produce messages to the input topic and observe transformed messages (uppercased values) on the output topic.
## Working with Kafka using KCM
You can use [KCM](https://github.com/kcmhub/KCM) to interact with your Kafka cluster and topics while developing with this template.
KCM provides a simple CLI to:
- List topics
- Create or delete topics
- Produce messages to a topic
- Consume messages from a topic
This is particularly useful to send test messages to the input topic and inspect the output topic used by this Kafka Streams application.
Basic workflow with KCM:
1. Install and configure KCM following the instructions in the KCM repository:  
   https://github.com/kcmhub/KCM
2. Use KCM to create or inspect the topics configured in `application.yml`:
   - Input topic: `app.kafka.input-topic` (default `input-topic`)
   - Output topic: `app.kafka.output-topic` (default `output-topic`)
3. Use KCM commands to:
   - Produce messages to the input topic (for example JSON or plain text)
   - Consume messages from the output topic to verify that your topology processes records as expected
## Extending this template
To use this repository as a template for your own Kafka Streams service:
1. Clone or copy the project.
2. Change the Maven coordinates (`groupId`, `artifactId`, `version`) in `pom.xml` as needed.
3. Update the application name and Kafka Streams configuration in `application.yml`.
4. Create your own topology classes under `io.kcmhub.streams` (or another package) and wire them in `StreamsTopologyConfig`.
5. Add or adjust configuration properties and override them via environment variables following the pattern described above.
## License and contributions
This project is licensed under the Apache License 2.0. See the [`LICENSE`](LICENSE) file for details.
Contributions are welcome. Please read [`CONTRIBUTING.md`](CONTRIBUTING.md) for guidelines on how to propose changes.
