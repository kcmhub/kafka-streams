package ${package}.api;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyDescription;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API controller for Kafka Streams topology information
 */
@RestController
@RequestMapping("/api/topology")
public class TopologyController {

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    public TopologyController(StreamsBuilderFactoryBean streamsBuilderFactoryBean) {
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
    }

    /**
     * Get the topology description
     *
     * @return topology description as JSON
     */
    @GetMapping("/description")
    public Map<String, Object> getTopologyDescription() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Get topology from StreamsBuilder
            Topology topology = streamsBuilderFactoryBean.getTopology();

            if (topology == null) {
                response.put("status", "NOT_AVAILABLE");
                response.put("message", "Topology not initialized");
                return response;
            }

            TopologyDescription topologyDesc = topology.describe();

            response.put("status", "SUCCESS");
            response.put("description", topologyDesc.toString());
            response.put("subtopologies", topologyDesc.subtopologies().size());

            // Add detailed subtopology information
            Map<String, Object> subtopologies = new HashMap<>();
            topologyDesc.subtopologies().forEach(subtopology -> {
                Map<String, Object> subtopologyInfo = new HashMap<>();
                subtopologyInfo.put("id", subtopology.id());
                subtopologyInfo.put("nodes", subtopology.nodes().size());
                subtopologyInfo.put("nodeNames", subtopology.nodes().stream()
                        .map(TopologyDescription.Node::name)
                        .toList());
                subtopologies.put("subtopology-" + subtopology.id(), subtopologyInfo);
            });
            response.put("subtopologyDetails", subtopologies);

        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
        }

        return response;
    }

    /**
     * Get the Kafka Streams state
     *
     * @return current state of Kafka Streams
     */
    @GetMapping("/state")
    public Map<String, Object> getStreamsState() {
        Map<String, Object> response = new HashMap<>();

        try {
            KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();

            if (kafkaStreams == null) {
                response.put("status", "NOT_AVAILABLE");
                response.put("state", "NOT_INITIALIZED");
                return response;
            }

            response.put("status", "SUCCESS");
            response.put("state", kafkaStreams.state().name());
            response.put("isRunning", kafkaStreams.state().isRunningOrRebalancing());

        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
        }

        return response;
    }

    /**
     * Get topology as plain text (for debugging)
     *
     * @return topology description as plain text
     */
    @GetMapping(value = "/description/text", produces = "text/plain")
    public String getTopologyDescriptionText() {
        try {
            Topology topology = streamsBuilderFactoryBean.getTopology();

            if (topology == null) {
                return "Topology not initialized";
            }

            return topology.describe().toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}

