# Kafka Streams Skeleton (Multi-Module Project)

This is a multi-module Maven project providing tools and templates for building Kafka Streams applications with Spring Boot and Java 21.

## Project Structure

This project consists of:

### Maven Modules
1. **kafka-streams-template** - A reusable Spring Boot template for building Kafka Streams applications with a sample topology, metrics, and externalized configuration
2. **kafka-streams-archetype** - Maven archetype for quickly generating new Kafka Streams projects based on the template

### Tooling Folder (Not a Maven Module)
3. **kafka-broker-tooling** - Docker Compose stack and scripts for running a local Kafka development environment with KCM (Kafka Cluster Manager), PostgreSQL, and Redis. This is NOT a Maven module - it contains only Docker Compose files and helper scripts.

## Building the Project

To build all modules:
```bash
mvn clean install
```

To build a specific module:
```bash
cd kafka-streams-template
mvn clean install
```
---

## Module 1: kafka-streams-template

### Features
- Spring Boot 3.3 (via `spring-boot-starter-parent`)
- Java 21
- Kafka Streams 3.9 integrated through Spring Kafka
- Example topology that transforms messages to upper case
- Centralized Kafka Streams configuration
- Application metrics via Spring Boot Actuator and Micrometer Prometheus
- Configuration overridden via environment variables (suitable for containers and cloud platforms)
### Module structure
- `kafka-streams-template/pom.xml` – Maven configuration and dependencies
- `kafka-streams-template/src/main/java/io/kcmhub/KafkaStreamsApplication.java` – Spring Boot entry point
- `kafka-streams-template/src/main/java/io/kcmhub/streams/StreamsTopologyConfig.java` – Kafka Streams configuration and topology wiring
- `kafka-streams-template/src/main/java/io/kcmhub/streams/UppercaseTopologyBuilder.java` – example stream processing topology
- `kafka-streams-template/src/main/java/io/kcmhub/streams/KafkaStreamsStarter.java` – lifecycle management for the Kafka Streams instance
- `kafka-streams-template/src/main/resources/application.yml` – default Spring Boot and application configuration
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
---

## Tooling Folder: kafka-broker-tooling

This is a **tooling folder** (NOT a Maven module) that provides a complete Docker Compose setup for local Kafka development.

⚠️ **Important**: This folder contains NO Java code and NO `pom.xml` - it's purely for infrastructure setup.

### What's Included
- **Docker Compose stack** with:
  - Kafka Broker (port 9092) - KRaft mode (no Zookeeper required)
  - **KCM UI** - Web interface for Kafka management (http://localhost:3000)
  - **KCM API** - Backend for Kafka management (http://localhost:8080)
  - PostgreSQL (port 5432) - Database for KCM
  - Redis (port 6379) - Cache for KCM
- **Helper scripts**:
  - `docker.ps1` - PowerShell script for Windows
  - `Makefile` - Make commands for Linux/macOS
  - `DOCKER.md` - Detailed documentation

### Quick Start

#### Windows (PowerShell)
```powershell
cd kafka-broker-tooling
.\docker.ps1 up
```

#### Linux/macOS
```bash
cd kafka-broker-tooling
make up
# or
docker compose up -d
```

### Using KCM (Kafka Cluster Manager)

**KCM** is a powerful web UI for managing Kafka clusters, included in this tooling stack.

🔗 **GitHub**: [kcmhub/KCM](https://github.com/kcmhub/KCM.git)

#### Features
- Create, view, and manage topics
- Produce and consume messages
- Monitor consumer groups and lag
- View broker and partition information

#### Access
1. Start the environment (see Quick Start above)
2. Open http://localhost:3000 in your browser
3. The Kafka cluster connection is pre-configured

### Documentation

See `kafka-broker-tooling/README.md` and `kafka-broker-tooling/DOCKER.md` for complete documentation.

---

## Module 2: kafka-streams-archetype

Maven archetype for quickly generating new Kafka Streams projects.

### Usage

First, install the archetype locally:
```bash
cd kafka-streams-archetype
mvn clean install
```

Then generate a new project from the archetype:
```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.example \
  -DartifactId=my-kafka-streams-app \
  -Dversion=1.0-SNAPSHOT \
  -Dpackage=com.example.streams
```

This will create a new Kafka Streams project based on the template with your specified group, artifact, and package names.

---

## License and contributions
This project is licensed under the Apache License 2.0. See the [`LICENSE`](LICENSE) file for details.
Contributions are welcome. Please read [`CONTRIBUTING.md`](CONTRIBUTING.md) for guidelines on how to propose changes.
