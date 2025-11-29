package io.kcmhub.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.KafkaStreamsInfrastructureCustomizer;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StreamsTopologyConfig {

    private final String inputTopic;

    private final String outputTopic;

    @Autowired
    public StreamsTopologyConfig(@Value("${app.kafka.input-topic}") String inputTopic,
                                 @Value("${app.kafka.output-topic}") String outputTopic) {
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
    }

    /**
     * Default Kafka Streams configuration bean.
     *
     * @param kafkaProperties the Spring Boot Kafka properties used to build Streams configuration
     * @return a configured {@link KafkaStreamsConfiguration} instance
     */
    @Bean(name = "defaultKafkaStreamsConfig")
    @ConditionalOnMissingBean
    public KafkaStreamsConfiguration kafkaStreamsConfiguration(KafkaProperties kafkaProperties) {
        if (kafkaProperties == null || kafkaProperties.getStreams() == null) {
            throw new IllegalStateException("Kafka Streams properties are not configured properly.");
        }

        if (kafkaProperties.getStreams().getApplicationId() == null) {
            throw new IllegalStateException("Kafka Streams application ID must be set.");
        }

        if (kafkaProperties.getStreams().getBootstrapServers() == null
                && kafkaProperties.getBootstrapServers() == null) {
            throw new IllegalStateException("Kafka bootstrap servers must be set.");
        }

        Map<String, Object> props = new HashMap<>(kafkaProperties.getStreams().buildProperties(null));

        props.putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
                Serdes.StringSerde.class);
        props.putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
                Serdes.StringSerde.class);
        props.putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        return new KafkaStreamsConfiguration(props);
    }

    /**
     * StreamsBuilderFactoryBean that uses UppercaseTopologyBuilder to configure the topology.
     *
     * @param kafkaStreamsConfiguration the Kafka Streams configuration
     * @return a configured {@link StreamsBuilderFactoryBean} instance
     */
    @Bean
    public StreamsBuilderFactoryBean streamsBuilderFactoryBean(KafkaStreamsConfiguration kafkaStreamsConfiguration,
                                                               UppercaseTopologyBuilder uppercaseTopologyBuilder) {
        StreamsBuilderFactoryBean factoryBean = new StreamsBuilderFactoryBean(kafkaStreamsConfiguration);
        factoryBean.setInfrastructureCustomizer(new KafkaStreamsInfrastructureCustomizer() {
            @Override
            public void configureBuilder(StreamsBuilder builder) {
                uppercaseTopologyBuilder.configure(builder, inputTopic, outputTopic);
            }
        });
        return factoryBean;
    }
}
