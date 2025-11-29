package ${package}.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.kafka.KafkaStreamsMetrics;
import org.apache.kafka.streams.KafkaStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration for Kafka Streams metrics integration with Micrometer/Prometheus
 *
 * This configuration registers Kafka Streams metrics with Micrometer to ensure
 * they are properly exported to the Prometheus endpoint.
 *
 * Based on Micrometer best practices for Kafka Streams integration.
 */
@Configuration
public class KafkaStreamsMetricsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsMetricsConfig.class);

    private final Map<String, KafkaStreamsMetrics> metricsMap = new ConcurrentHashMap<>();

    public KafkaStreamsMetricsConfig(StreamsBuilderFactoryBean factoryBean,
                                     MeterRegistry meterRegistry,
                                     @Value("$" + "{spring.application.name:kafka-streams-app}") String applicationName) {

        factoryBean.addListener(new StreamsBuilderFactoryBean.Listener() {
            @Override
            public void streamsAdded(String id, KafkaStreams streams) {
                LOGGER.info("🔧 Binding Kafka Streams metrics to Micrometer for instance: {}", id);
                try {
                    Iterable<Tag> tags = Collections.singletonList(
                            Tag.of("application", applicationName)
                    );

                    KafkaStreamsMetrics kafkaMetrics = new KafkaStreamsMetrics(streams, tags);
                    kafkaMetrics.bindTo(meterRegistry);

                    metricsMap.put(id, kafkaMetrics);

                    LOGGER.info("✅ Kafka Streams metrics successfully registered for instance: {}", id);
                    LOGGER.info("📊 Metrics will be available at /actuator/prometheus (kafka_stream* / kafka_streams*).");
                } catch (Exception e) {
                    LOGGER.error("❌ Failed to register Kafka Streams metrics for instance: {}", id, e);
                }
            }

            @Override
            public void streamsRemoved(String id, KafkaStreams streams) {
                LOGGER.info("🔌 Removing Kafka Streams metrics for instance: {}", id);
                KafkaStreamsMetrics metrics = metricsMap.remove(id);
                if (metrics != null) {
                    try {
                        metrics.close();
                        LOGGER.info("✅ Kafka Streams metrics closed for instance: {}", id);
                    } catch (Exception e) {
                        LOGGER.warn("⚠️ Error closing Kafka Streams metrics for instance: {}", id, e);
                    }
                }
            }
        });
    }
}

