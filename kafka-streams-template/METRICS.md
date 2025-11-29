# Kafka Streams Metrics with Micrometer

## Question: Do I need to manually add `KafkaStreamsMicrometerListener`?

**Short answer: NO** ❌

With Spring Boot 3.x + Spring Kafka 3.x + Micrometer, the `KafkaStreamsMicrometerListener` is **automatically configured** by Spring Boot's auto-configuration mechanism.

---

## Auto-Configuration Details

### What's included in this project

✅ **Dependencies** (in `pom.xml`):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

✅ **Configuration** (in `application.yml`):
```yaml
management:
  metrics:
    distribution:
      percentiles-histogram:
        kafka.streams: true
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
```

### How auto-configuration works

When Spring Boot detects:
1. `spring-boot-starter-actuator` on the classpath
2. `micrometer-registry-prometheus` on the classpath
3. `spring-kafka` on the classpath
4. A `KafkaStreams` bean in the context

**Then** Spring Boot's `KafkaStreamsMetricsAutoConfiguration` automatically:
- Creates a `KafkaStreamsMicrometerListener` bean
- Registers it with your `StreamsBuilderFactoryBean`
- Configures it to publish metrics to the Micrometer registry

### Verification

You can verify auto-configuration is working by:

1. **Check logs at startup** (DEBUG level):
   ```yaml
   logging:
     level:
       org.springframework.boot.autoconfigure: DEBUG
   ```
   Look for: `KafkaStreamsMetricsAutoConfiguration matched`

2. **Check actuator endpoint**:
   ```bash
   curl http://localhost:8080/actuator/prometheus | grep kafka_stream
   ```

3. **Expected metrics**:
   - `kafka_stream_state` - Current state of Kafka Streams
   - `kafka_stream_thread_commit_latency_*` - Commit latency percentiles
   - `kafka_stream_thread_poll_latency_*` - Poll latency percentiles
   - `kafka_stream_thread_process_latency_*` - Process latency percentiles
   - `kafka_stream_task_*` - Task-level metrics
   - And many more...

---

## When to manually add KafkaStreamsMicrometerListener

You should **only** manually configure `KafkaStreamsMicrometerListener` if:

❗ You're **NOT using Spring Boot** (plain Spring Framework)
❗ You're using an **old version** of Spring Kafka (< 2.8)
❗ You want **custom tags or filters** beyond what auto-configuration provides
❗ You want to **disable auto-configuration** and configure manually

### Example: Manual configuration (NOT needed in this project)

```java
@Configuration
public class CustomMetricsConfig {
    
    @Bean
    public KafkaStreamsMicrometerListener kafkaStreamsMicrometerListener(
            MeterRegistry meterRegistry) {
        return new KafkaStreamsMicrometerListener(meterRegistry, 
            Collections.singletonList(Tag.of("custom-tag", "custom-value")));
    }
}
```

---

## Current Configuration Summary

### ✅ What's automatically configured

| Component | Status | Configured by |
|-----------|--------|---------------|
| `KafkaStreamsMicrometerListener` | ✅ Auto | Spring Boot |
| Prometheus metrics endpoint | ✅ Auto | Spring Boot Actuator |
| Kafka Streams metrics | ✅ Auto | Micrometer |
| Health checks | ✅ Auto | Spring Boot Actuator |

### ✅ What's manually configured

| Component | Status | Configured in |
|-----------|--------|---------------|
| Kafka Streams topology | ✅ Manual | `StreamsTopologyConfig.java` |
| Topology builder | ✅ Manual | `UppercaseTopologyBuilder.java` |
| Streams lifecycle listener | ✅ Manual | `KafkaStreamsStarter.java` |

---

## Testing Metrics

### 1. Start the application

```bash
mvn spring-boot:run
```

### 2. Access Prometheus metrics

```bash
# Get all metrics
curl http://localhost:8080/actuator/prometheus

# Filter Kafka Streams metrics only
curl http://localhost:8080/actuator/prometheus | grep kafka_stream
```

### 3. Expected output (sample)

```
# HELP kafka_stream_state The state of the Kafka Stream
# TYPE kafka_stream_state gauge
kafka_stream_state{application="kafka-streams-skeleton",spring_application_name="kafka-streams-skeleton"} 2.0

# HELP kafka_stream_thread_commit_latency_avg  
# TYPE kafka_stream_thread_commit_latency_avg gauge
kafka_stream_thread_commit_latency_avg{application="kafka-streams-skeleton"} 0.0

# HELP kafka_stream_thread_poll_records  
# TYPE kafka_stream_thread_poll_records gauge
kafka_stream_thread_poll_records{application="kafka-streams-skeleton"} 0.0
```

---

## Additional Configuration Options

### Custom metric tags

If you want to add custom tags globally to all metrics:

```yaml
management:
  metrics:
    tags:
      application: ${spring.application.name}
      environment: ${ENVIRONMENT:dev}
      datacenter: ${DATACENTER:local}
```

### Percentiles and histograms

Already configured in `application.yml`:

```yaml
management:
  metrics:
    distribution:
      percentiles-histogram:
        kafka.streams: true  # Enables histogram for Kafka Streams metrics
      percentiles:
        kafka.streams: 0.5,0.95,0.99  # Track 50th, 95th, 99th percentiles
```

---

## Troubleshooting

### Metrics not appearing?

1. **Check actuator endpoint is exposed**:
   ```yaml
   management:
     endpoints:
       web:
         exposure:
           include: prometheus  # Must include 'prometheus'
   ```

2. **Check Kafka Streams is running**:
   ```bash
   curl http://localhost:8080/actuator/health
   # Should show Kafka Streams health indicator
   ```

3. **Enable debug logging**:
   ```yaml
   logging:
     level:
       org.springframework.kafka: DEBUG
       io.micrometer: DEBUG
   ```

---

## References

- [Spring Kafka Metrics Documentation](https://docs.spring.io/spring-kafka/reference/kafka/micrometer.html)
- [Micrometer Documentation](https://micrometer.io/docs)
- [Spring Boot Actuator Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)

---

## Conclusion

✅ **No manual configuration needed** - Spring Boot handles everything automatically!

The project is already correctly configured for Kafka Streams metrics with Micrometer and Prometheus.

