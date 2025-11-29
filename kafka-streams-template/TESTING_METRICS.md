# Testing Metrics

This document provides instructions to verify that Kafka Streams metrics are correctly exposed via Micrometer and Prometheus.

## Prerequisites

1. Kafka broker running (see `kafka-broker-tooling/` folder)
2. Application running

## Quick Test

### 1. Start Kafka (if not running)

```powershell
cd ..\kafka-broker-tooling
.\docker.ps1 up
```

Wait 30-60 seconds for Kafka to be ready.

### 2. Start the application

```powershell
cd kafka-streams-template
mvn spring-boot:run
```

Wait for the log message:
```
Kafka Streams instance kafka-streams-skeleton started successfully
Kafka Streams state: RUNNING
```

### 3. Test metrics endpoints

#### Windows (PowerShell)
```powershell
# Test health endpoint
Invoke-WebRequest http://localhost:8080/actuator/health | Select-Object -ExpandProperty Content

# Test Prometheus endpoint (all metrics)
Invoke-WebRequest http://localhost:8080/actuator/prometheus | Select-Object -ExpandProperty Content

# Test Kafka Streams metrics specifically
(Invoke-WebRequest http://localhost:8080/actuator/prometheus).Content -split "`n" | Select-String "kafka_stream"
```

#### Linux/macOS (Bash)
```bash
# Test health endpoint
curl http://localhost:8080/actuator/health | jq

# Test Prometheus endpoint (all metrics)
curl http://localhost:8080/actuator/prometheus

# Test Kafka Streams metrics specifically
curl http://localhost:8080/actuator/prometheus | grep kafka_stream
```

## Expected Results

### Health endpoint

```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Prometheus metrics (sample)

```
# HELP kafka_stream_state The state of the Kafka Stream
# TYPE kafka_stream_state gauge
kafka_stream_state{application="kafka-streams-skeleton",instance="local",spring_application_name="kafka-streams-skeleton"} 2.0

# HELP kafka_stream_thread_commit_latency_avg  
# TYPE kafka_stream_thread_commit_latency_avg gauge
kafka_stream_thread_commit_latency_avg{application="kafka-streams-skeleton",instance="local",kafka_version="3.9.0",thread_id="kafka-streams-skeleton-StreamThread-1"} 0.0

# HELP kafka_stream_thread_poll_latency_avg  
# TYPE kafka_stream_thread_poll_latency_avg gauge
kafka_stream_thread_poll_latency_avg{application="kafka-streams-skeleton",instance="local",kafka_version="3.9.0",thread_id="kafka-streams-skeleton-StreamThread-1"} 0.0

# HELP kafka_stream_thread_process_latency_avg  
# TYPE kafka_stream_thread_process_latency_avg gauge
kafka_stream_thread_process_latency_avg{application="kafka-streams-skeleton",instance="local",kafka_version="3.9.0",thread_id="kafka-streams-skeleton-StreamThread-1"} 0.0
```

### Metric state values

The `kafka_stream_state` metric returns numeric values representing the Kafka Streams state:

| Value | State | Description |
|-------|-------|-------------|
| 0 | CREATED | Stream created but not started |
| 1 | REBALANCING | Consumer group rebalancing in progress |
| 2 | RUNNING | Stream is running and processing records |
| 3 | PENDING_SHUTDOWN | Shutdown initiated |
| 4 | PENDING_ERROR | Error occurred, shutdown pending |
| 5 | NOT_RUNNING | Stream stopped |
| 6 | ERROR | Stream in error state |

## Verify Auto-Configuration

To verify that `KafkaStreamsMicrometerListener` was auto-configured, enable DEBUG logging:

### Temporary (command line)
```bash
mvn spring-boot:run -Dlogging.level.org.springframework.boot.autoconfigure=DEBUG
```

### Permanent (application.yml)
```yaml
logging:
  level:
    org.springframework.boot.autoconfigure: DEBUG
    org.springframework.kafka: DEBUG
```

Look for log entries like:
```
KafkaStreamsMetricsAutoConfiguration matched
KafkaStreamsMicrometerListener auto-configured
```

## Integration with Prometheus Server

If you want to scrape these metrics with an actual Prometheus server:

### 1. Add Prometheus configuration

Create `prometheus.yml`:

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'kafka-streams-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

### 2. Start Prometheus

```bash
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus
```

### 3. Access Prometheus UI

Open http://localhost:9090 and query for `kafka_stream_*` metrics.

## Integration with Grafana

### 1. Start Grafana

```bash
docker run -d \
  --name grafana \
  -p 3001:3000 \
  grafana/grafana
```

### 2. Add Prometheus data source

1. Open http://localhost:3001 (default credentials: admin/admin)
2. Go to Configuration > Data Sources
3. Add Prometheus data source pointing to http://localhost:9090

### 3. Create dashboard

Import a Kafka Streams dashboard or create custom panels with queries like:

```promql
# Stream state
kafka_stream_state

# Commit latency (95th percentile)
kafka_stream_thread_commit_latency_p95

# Process rate
rate(kafka_stream_thread_process_total[5m])

# Error rate
rate(kafka_stream_thread_process_errors_total[5m])
```

## Troubleshooting

### No metrics appearing

1. **Check actuator is enabled**:
   ```yaml
   management:
     endpoints:
       web:
         exposure:
           include: prometheus
   ```

2. **Check dependencies**:
   ```bash
   mvn dependency:tree | grep -E "(actuator|micrometer|spring-kafka)"
   ```

3. **Check application logs** for errors during startup

### Metrics show zero values

This is normal if:
- No messages have been processed yet
- Application just started
- No topics assigned to the consumer

To generate metrics:
1. Produce test messages to the input topic
2. Watch metrics update in real-time

## Load Testing

To generate meaningful metrics, produce test data:

### Using kafka-console-producer

```bash
# Connect to Kafka container
docker exec -it kafka-tooling-broker bash

# Produce test messages
kafka-console-producer --bootstrap-server localhost:9092 --topic input-topic
> hello world
> test message
> kafka streams
```

### Using KCM UI

1. Open http://localhost:3000
2. Navigate to Topics > input-topic
3. Use the "Produce Message" feature to send test data

### Watch metrics change

```powershell
# Windows - Poll metrics every 2 seconds
while ($true) { 
    (Invoke-WebRequest http://localhost:8080/actuator/prometheus).Content -split "`n" | Select-String "kafka_stream_thread_process_total"
    Start-Sleep -Seconds 2
}
```

```bash
# Linux/macOS - Poll metrics every 2 seconds
watch -n 2 "curl -s http://localhost:8080/actuator/prometheus | grep kafka_stream_thread_process_total"
```

## References

- [Micrometer Kafka Streams Metrics](https://micrometer.io/docs/ref/kafka-streams)
- [Spring Kafka Metrics](https://docs.spring.io/spring-kafka/reference/kafka/micrometer.html)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)

