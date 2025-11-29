package ${package}.streams;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class KafkaStreamsStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsStarter.class);

    public KafkaStreamsStarter(StreamsBuilderFactoryBean factoryBean) {
        // Configure listener to track Kafka Streams lifecycle
        factoryBean.addListener(new StreamsBuilderFactoryBean.Listener() {
            @Override
            public void streamsAdded(String id, KafkaStreams streams) {
                LOGGER.info("Kafka Streams instance {} started successfully", id);
                LOGGER.info("Kafka Streams state: {}", streams.state());
            }

            @Override
            public void streamsRemoved(String id, KafkaStreams streams) {
                LOGGER.warn("Kafka Streams instance {} stopped", id);
            }
        });

        // Configure exception handler
        factoryBean.setStreamsUncaughtExceptionHandler(exception -> {
            LOGGER.error("Unhandled exception in Kafka Streams", exception);
            return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_APPLICATION;
        });

        // Ensure auto-startup is enabled (default is true, but explicit for clarity)
        factoryBean.setAutoStartup(true);

        LOGGER.info("Kafka Streams starter configured successfully");
    }
}

