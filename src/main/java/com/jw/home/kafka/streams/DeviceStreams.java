package com.jw.home.kafka.streams;

import com.jw.home.dto.DeviceState;
import com.jw.home.kafka.topic.DeviceEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

@Component
public class DeviceStreams {
    @Value("${spring.kafka.topics.device-state}")
    private String deviceStateTopicName;
    @Value("${spring.kafka.streams.state-stores.device-state}")
    private String stateStoreName;

    private static final Serde<DeviceEvent> deviceEventSerde = new Serdes.WrapperSerde<>(new JsonSerializer<>(), new JsonDeserializer<>(DeviceEvent.class));
    private static final Serde<DeviceState> deviceStateSerde = new Serdes.WrapperSerde<>(new JsonSerializer<>(), new JsonDeserializer<>(DeviceState.class));

    @Autowired
    public void buildTopology(StreamsBuilder streamsBuilder) {
        KeyValueBytesStoreSupplier storeSupplier = Stores.inMemoryKeyValueStore(stateStoreName);
        StoreBuilder<KeyValueStore<String, DeviceState>> storeBuilder
                = Stores.keyValueStoreBuilder(storeSupplier, Serdes.String(), deviceStateSerde);
        streamsBuilder.addStateStore(storeBuilder);

        KStream<String, DeviceState> stream = streamsBuilder.stream(deviceStateTopicName, Consumed.with(Serdes.String(), deviceEventSerde))
                .transformValues(() -> new DeviceStateTransformer(stateStoreName), stateStoreName);
        stream.print(Printed.<String, DeviceState>toSysOut().withLabel("Event-Stream"));
    }
}
