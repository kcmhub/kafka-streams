package ${package}.streams;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ${topologyBuilderClass} {

    private static final Logger LOGGER = LoggerFactory.getLogger(${topologyBuilderClass}.class);

    public void configure(StreamsBuilder streamsBuilder, String inputTopic, String outputTopic) {
        KStream<String, String> stream = streamsBuilder.stream(inputTopic);

        stream.peek((key, value) -> LOGGER.debug("Processing record key={} value={}", key, value))
                .to(outputTopic);

        LOGGER.info("Topology configured using input topic '{}' and output topic '{}'", inputTopic, outputTopic);
    }
}

