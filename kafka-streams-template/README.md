# Kafka Streams Template Module

This module is a reusable Spring Boot template for building Kafka Streams applications with Java 21.
It provides a minimal, production-oriented setup with a sample topology, metrics, and externalized configuration.

## Features
- Spring Boot 3.3 (via `spring-boot-starter-parent`)
- Java 21
- Kafka Streams 3.9 integrated through Spring Kafka
- Example topology that transforms messages to upper case
- Centralized Kafka Streams configuration
- Application metrics via Spring Boot Actuator and Micrometer Prometheus
- Configuration overridden via environment variables (suitable for containers and cloud platforms)

## Prerequisites
- Java 21 (JDK 21) installed  
  - Verify with:
    ```bash
    java -version
    ```
- Maven 3.x installed  
  - Verify with:
    ```bash
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
- `spring.kafka.bootstrap-servers` → `SPRING_KAFKA_BOOTSTRAP_SERVERS`
- `spring.kafka.streams.application-id` → `SPRING_KAFKA_STREAMS_APPLICATION_ID`
- `app.kafka.input-topic` → `APP_KAFKA_INPUT_TOPIC`
- `app.kafka.output-topic` → `APP_KAFKA_OUTPUT_TOPIC`

#### Examples

##### Windows (PowerShell)
```powershell
$env:SPRING_KAFKA_BOOTSTRAP_SERVERS = "broker1:9092,broker2:9092"
$env:SPRING_KAFKA_STREAMS_APPLICATION_ID = "my-streams-service"
$env:APP_KAFKA_INPUT_TOPIC = "orders-input"
$env:APP_KAFKA_OUTPUT_TOPIC = "orders-output"
```

##### Linux/macOS (Bash)
```bash
export SPRING_KAFKA_BOOTSTRAP_SERVERS="broker1:9092,broker2:9092"
export SPRING_KAFKA_STREAMS_APPLICATION_ID="my-streams-service"
export APP_KAFKA_INPUT_TOPIC="orders-input"
export APP_KAFKA_OUTPUT_TOPIC="orders-output"
```

## Build and run
From the module directory:

### Build
```bash
mvn clean package
```

### Run with Maven
```bash
mvn spring-boot:run
```

### Run the packaged JAR
```bash
java -jar target/kafka-streams-template-1.0-SNAPSHOT.jar
```

## Local development workflow
1. Start a local Kafka broker (for example on `localhost:9092`).
2. Create the input and output topics:
   - Input topic name: taken from `app.kafka.input-topic` (default `input-topic`)
   - Output topic name: taken from `app.kafka.output-topic` (default `output-topic`)
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Produce messages to the input topic and observe transformed messages (uppercased values) on the output topic.

## Working with Kafka using KCM
You can use [KCM](https://github.com/kcmhub/KCM) to interact with your Kafka cluster and topics while developing with this template.

## Topology REST API

This template exposes REST endpoints to inspect the Kafka Streams topology at runtime:

### Available endpoints

- `GET /api/topology/description` - Get topology description as JSON
- `GET /api/topology/description/text` - Get topology description as plain text
- `GET /api/topology/state` - Get current Kafka Streams state

### Quick test

```bash
# Get topology description
curl http://localhost:8080/api/topology/description | jq

# Get topology as text
curl http://localhost:8080/api/topology/description/text

# Get streams state
curl http://localhost:8080/api/topology/state | jq
```

For complete documentation, see [TOPOLOGY_API.md](TOPOLOGY_API.md).

---

## Metrics and Monitoring

This template includes **Kafka Streams metrics** via Micrometer and Prometheus:

⚠️ **Important**: Kafka Streams metrics require manual configuration in Spring Boot 3.3.x. This template includes the required `KafkaStreamsMetricsConfig.java` configuration.

### Access metrics endpoint
```bash
# All metrics
curl http://localhost:8080/actuator/prometheus

# Kafka Streams metrics only
curl http://localhost:8080/actuator/prometheus | grep kafka_stream
```

### Available metrics
- `kafka_stream_state` - Stream state (RUNNING, REBALANCING, etc.)
- `kafka_stream_thread_*` - Thread-level metrics (commit latency, poll latency, etc.)
- `kafka_stream_task_*` - Task-level metrics
- And many more...

### Configuration

This template includes **manual configuration** (`KafkaStreamsMetricsConfig.java`) to bind Kafka Streams metrics to Micrometer.

**Required dependencies** (already included):
- ✅ `spring-boot-starter-actuator`
- ✅ `micrometer-registry-prometheus`
- ✅ `spring-kafka`

**Manual configuration** (`src/main/java/io/kcmhub/config/KafkaStreamsMetricsConfig.java`):
- Binds Kafka Streams JMX metrics to Micrometer
- Adds application tags for better metric organization
- Manages metric lifecycle (registration and cleanup)

For detailed information, see [METRICS.md](METRICS.md).

### Troubleshooting

If you don't see `kafka_stream_*` metrics, see:
- [SOLUTION_METRIQUES.md](SOLUTION_METRIQUES.md) - Quick fix guide
- [TROUBLESHOOTING_METRICS.md](TROUBLESHOOTING_METRICS.md) - Detailed troubleshooting
- [TEST_FINAL.md](TEST_FINAL.md) - Testing guide

## Extending this template
To use this module as a template for your own Kafka Streams service:

1. Copy the module to a new project.
2. Change the Maven coordinates (`groupId`, `artifactId`, `version`) in `pom.xml` as needed.
3. Update the application name and Kafka Streams configuration in `application.yml`.
4. Create your own topology classes under `io.kcmhub.streams` (or another package) and wire them in `StreamsTopologyConfig`.
5. Add or adjust configuration properties and override them via environment variables following the pattern described above.

