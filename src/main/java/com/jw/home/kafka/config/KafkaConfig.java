package com.jw.home.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.*;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value(value = "${spring.kafka.streams.app.id}")
    private String streamsAppId;
    @Value(value = "${spring.kafka.streams.app.server}")
    private String hostInfo;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kafkaStreamsConfig() throws UnknownHostException {
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, streamsAppId);
        props.put(APPLICATION_SERVER_CONFIG, hostInfo);
        props.put(NUM_STANDBY_REPLICAS_CONFIG, 1);
        props.put(COMMIT_INTERVAL_MS_CONFIG, 5000);
        props.put(NUM_STREAM_THREADS_CONFIG, 1);
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, new Serdes.WrapperSerde<>(new JsonSerializer<>(), new JsonDeserializer<>(DeviceState.class)).getClass().getName());
        return new KafkaStreamsConfiguration(props);
    }
}
