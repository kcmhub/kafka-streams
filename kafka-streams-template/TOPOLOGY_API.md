# Topology REST API

This document describes the REST API endpoints available for inspecting the Kafka Streams topology.

## Overview

The application exposes REST endpoints to retrieve information about the Kafka Streams topology at runtime. This is useful for:
- Debugging topology configuration
- Monitoring topology structure
- Understanding data flow
- Troubleshooting issues

## Endpoints

### Base URL

```
http://localhost:8080/api/topology
```

---

### 1. Get Topology Description (JSON)

Retrieves the complete topology description as a structured JSON response.

**Endpoint**: `GET /api/topology/description`

**Response Example**:
```json
{
  "status": "SUCCESS",
  "description": "Topologies:\n   Sub-topology: 0\n    Source: KSTREAM-SOURCE-0000000000 (topics: [input-topic])\n      --> KSTREAM-PEEK-0000000001\n    Processor: KSTREAM-PEEK-0000000001 (stores: [])\n      --> KSTREAM-MAPVALUES-0000000002\n      <-- KSTREAM-SOURCE-0000000000\n    Processor: KSTREAM-MAPVALUES-0000000002 (stores: [])\n      --> KSTREAM-SINK-0000000003\n      <-- KSTREAM-PEEK-0000000001\n    Sink: KSTREAM-SINK-0000000003 (topic: output-topic)\n      <-- KSTREAM-MAPVALUES-0000000002\n\n",
  "subtopologies": 1,
  "subtopologyDetails": {
    "subtopology-0": {
      "id": 0,
      "nodes": 4,
      "nodeNames": [
        "KSTREAM-SOURCE-0000000000",
        "KSTREAM-PEEK-0000000001",
        "KSTREAM-MAPVALUES-0000000002",
        "KSTREAM-SINK-0000000003"
      ]
    }
  }
}
```

**Response Fields**:
- `status`: Status of the request (`SUCCESS`, `ERROR`, `NOT_AVAILABLE`)
- `description`: Full text description of the topology
- `subtopologies`: Number of subtopologies
- `subtopologyDetails`: Detailed information about each subtopology
  - `id`: Subtopology identifier
  - `nodes`: Number of nodes in the subtopology
  - `nodeNames`: List of node names

**Error Response**:
```json
{
  "status": "NOT_AVAILABLE",
  "message": "Topology not initialized"
}
```

---

### 2. Get Topology Description (Plain Text)

Retrieves the topology description as plain text, easier to read for debugging.

**Endpoint**: `GET /api/topology/description/text`

**Response Example**:
```
Topologies:
   Sub-topology: 0
    Source: KSTREAM-SOURCE-0000000000 (topics: [input-topic])
      --> KSTREAM-PEEK-0000000001
    Processor: KSTREAM-PEEK-0000000001 (stores: [])
      --> KSTREAM-MAPVALUES-0000000002
      <-- KSTREAM-SOURCE-0000000000
    Processor: KSTREAM-MAPVALUES-0000000002 (stores: [])
      --> KSTREAM-SINK-0000000003
      <-- KSTREAM-PEEK-0000000001
    Sink: KSTREAM-SINK-0000000003 (topic: output-topic)
      <-- KSTREAM-MAPVALUES-0000000002
```

**Content-Type**: `text/plain`

---

### 3. Get Kafka Streams State

Retrieves the current state of the Kafka Streams application.

**Endpoint**: `GET /api/topology/state`

**Response Example**:
```json
{
  "status": "SUCCESS",
  "state": "RUNNING",
  "isRunning": true
}
```

**Response Fields**:
- `status`: Status of the request (`SUCCESS`, `ERROR`, `NOT_AVAILABLE`)
- `state`: Current Kafka Streams state (see [States](#kafka-streams-states) below)
- `isRunning`: Boolean indicating if the stream is running or rebalancing

**Possible States**:
- `CREATED`: Stream created but not started
- `REBALANCING`: Consumer group rebalancing in progress
- `RUNNING`: Stream is running and processing records
- `PENDING_SHUTDOWN`: Shutdown initiated
- `PENDING_ERROR`: Error occurred, shutdown pending
- `NOT_RUNNING`: Stream stopped
- `ERROR`: Stream in error state

---

## Usage Examples

### Using curl (Linux/macOS/Git Bash)

```bash
# Get topology description (JSON)
curl http://localhost:8080/api/topology/description | jq

# Get topology description (plain text)
curl http://localhost:8080/api/topology/description/text

# Get Kafka Streams state
curl http://localhost:8080/api/topology/state | jq
```

### Using PowerShell (Windows)

```powershell
# Get topology description (JSON)
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/topology/description"
$response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10

# Get topology description (plain text)
(Invoke-WebRequest -Uri "http://localhost:8080/api/topology/description/text").Content

# Get Kafka Streams state
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/topology/state"
$response.Content | ConvertFrom-Json
```

### Using a Web Browser

Simply navigate to:
- http://localhost:8080/api/topology/description
- http://localhost:8080/api/topology/description/text
- http://localhost:8080/api/topology/state

---

## Understanding the Topology Description

### Node Types

1. **Source**: Entry point, reads from Kafka topics
   - Example: `KSTREAM-SOURCE-0000000000 (topics: [input-topic])`

2. **Processor**: Intermediate processing step
   - Example: `KSTREAM-MAPVALUES-0000000002 (stores: [])`
   - Can have state stores (shown in `stores: [...]`)

3. **Sink**: Exit point, writes to Kafka topics
   - Example: `KSTREAM-SINK-0000000003 (topic: output-topic)`

### Subtopologies

A topology can be split into multiple subtopologies for parallel processing. Each subtopology is independent and can be executed on different threads.

### Arrows

- `-->` : Data flows from this node to the next
- `<--` : This node receives data from the previous node

---

## Configuration

### Change Server Port

The REST API runs on port 8080 by default. To change it:

**Via environment variable**:
```bash
export SERVER_PORT=9090
```

**Via application.yml**:
```yaml
server:
  port: 9090
```

---

## Troubleshooting

### Endpoint returns "NOT_AVAILABLE"

**Problem**: Topology not initialized

**Solutions**:
1. Wait for the application to fully start
2. Check application logs for startup errors
3. Verify Kafka broker is accessible
4. Check `spring.kafka.bootstrap-servers` configuration

### Connection refused

**Problem**: Server not running or wrong port

**Solutions**:
1. Verify application is running: `ps aux | grep java` (Linux) or Task Manager (Windows)
2. Check the configured port in `application.yml`
3. Verify no firewall is blocking the port

### 404 Not Found

**Problem**: Wrong endpoint URL

**Solutions**:
1. Verify the base URL: `http://localhost:8080/api/topology`
2. Check spelling of the endpoint path
3. Ensure `spring-boot-starter-web` dependency is present

---

## Integration with Monitoring Tools

### Prometheus/Grafana

While this API provides topology information, for metrics use:
- `/actuator/prometheus` - Prometheus metrics endpoint (all metrics including Kafka Streams)
- `/actuator/metrics` - Spring Boot metrics endpoint

#### Verifying Kafka Streams Metrics

To check if Kafka Streams metrics are properly exported:

**Windows PowerShell**:
```powershell
(Invoke-WebRequest -Uri "http://localhost:8080/actuator/prometheus").Content -split "`n" | Select-String "kafka_stream"
```

**Linux/macOS**:
```bash
curl -s http://localhost:8080/actuator/prometheus | grep kafka_stream
```

**Expected output** (sample):
```
kafka_stream_state{...} 2.0
kafka_stream_thread_commit_latency_avg{...} 0.0
kafka_stream_thread_poll_latency_avg{...} 0.0
kafka_stream_task_active_process_ratio{...} 0.0
```

#### Troubleshooting Missing Kafka Streams Metrics

If you don't see any `kafka_stream_*` metrics:

1. **Verify KafkaStreamsMicrometerListener is configured**
   - Check that `KafkaStreamsMetricsConfig.java` exists
   - Verify the bean is created: Check application logs for bean creation

2. **Check Kafka Streams is running**
   ```bash
   curl http://localhost:8080/api/topology/state
   ```
   Should return `"state": "RUNNING"`

3. **Verify dependencies**
   - `spring-boot-starter-actuator` 
   - `micrometer-registry-prometheus`
   - `spring-kafka`

4. **Enable DEBUG logging**
   ```yaml
   logging:
     level:
       org.springframework.kafka: DEBUG
       io.micrometer: DEBUG
   ```

5. **Restart the application**
   - Metrics listener is registered at startup
   - Changes to configuration require a restart

### Logging

Enable DEBUG logging for detailed topology information at startup:

```yaml
logging:
  level:
    org.apache.kafka.streams: DEBUG
    io.kcmhub.streams: DEBUG
```

---

## Security Considerations

⚠️ **Important**: These endpoints expose internal application structure.

**Recommendations**:
- Do not expose in production without authentication
- Use Spring Security to protect endpoints
- Consider adding API keys or OAuth2
- Restrict access to internal networks only

### Example: Adding Security

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/topology/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        );
        return http.build();
    }
}
```

---

## Development Tips

### Live Reload

Use Spring Boot DevTools for automatic restart when code changes:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

### Testing the API

Create a simple test:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TopologyControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testTopologyDescription() {
        var response = restTemplate.getForEntity(
            "/api/topology/description", 
            Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("status");
    }
}
```

---

## Additional Resources

- [Kafka Streams Documentation](https://kafka.apache.org/documentation/streams/)
- [Spring Kafka Documentation](https://docs.spring.io/spring-kafka/reference/)
- [Topology DSL](https://kafka.apache.org/documentation/streams/developer-guide/dsl-api)

