package io.kcmhub.streams;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class KafkaStreamsStarter {

    private final StreamsBuilderFactoryBean factoryBean;
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsStarter.class);

    public KafkaStreamsStarter(StreamsBuilderFactoryBean factoryBean) {
        this.factoryBean = factoryBean;
    }

    @PostConstruct
    public KafkaStreams start() {
        StreamsBuilderFactoryBean.Listener listener = new StreamsBuilderFactoryBean.Listener() {
            @Override
            public void streamsAdded(String id, KafkaStreams streams) {
                LOGGER.info("Kafka Streams instance {} started", id);
            }

            @Override
            public void streamsRemoved(String id, KafkaStreams streams) {
                LOGGER.warn("Kafka Streams instance {} stopped", id);
            }
        };

        factoryBean.addListener(listener);
        factoryBean.setStreamsUncaughtExceptionHandler(exception -> {
            LOGGER.error("Unhandled exception in Kafka Streams", exception);
            return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_APPLICATION;
        });

        factoryBean.setAutoStartup(true);

        if (!factoryBean.isRunning()) {
            factoryBean.start();
        }

        KafkaStreams streams = factoryBean.getKafkaStreams();

        if (streams == null) {
            throw new IllegalStateException("Kafka Streams did not start correctly");
        }
        return streams;
    }

    @PreDestroy
    public void stop() {
        KafkaStreams streams = factoryBean.getKafkaStreams();
        if (streams != null) {
            LOGGER.info("Shutting down Kafka Streams instance");
            factoryBean.stop();
        }
    }
}
