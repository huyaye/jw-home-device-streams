package com.jw.home;

import com.jw.home.dto.DeviceState;
import com.jw.home.dto.Trait;
import com.jw.home.kafka.config.KafkaConfig;
import com.jw.home.kafka.streams.DeviceStreams;
import com.jw.home.kafka.topic.DeviceEvent;
import com.jw.home.kafka.topic.TriggerType;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {KafkaConfig.class, DeviceStreams.class})
public class DeviceStateStoreTest {
    @Autowired
    private DeviceStreams deviceStreams;
    private static final Serde<DeviceEvent> deviceEventSerde = new Serdes.WrapperSerde<>(new JsonSerializer<>(), new JsonDeserializer<>(DeviceEvent.class));

    @Test
    void queryDeviceStateStore() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        deviceStreams.buildTopology(streamsBuilder);
        Topology topology = streamsBuilder.build();

        try (TopologyTestDriver testDriver = new TopologyTestDriver(topology, new Properties())) {
            KeyValueStore<String, DeviceState> stateStore = testDriver.getKeyValueStore("device-state-store");
            TestInputTopic<String, DeviceEvent> inputTopic = testDriver
                    .createInputTopic("jwhome.dev.event.device.json", Serdes.String().serializer(), deviceEventSerde.serializer());
            inputTopic.pipeInput("0", makeRegisterEvent());
            inputTopic.pipeInput("0", makeChangedOnOffEvent(false));
            inputTopic.pipeInput("1", makeRegisterEvent());
            inputTopic.pipeInput("1", makeChangedBrightnessEvent(90));
            inputTopic.pipeInput("1", makeConnectionOffEvent());

            DeviceState state = stateStore.get("0");
            assertEquals(state.getOnline(), true);
            assertEquals(state.getTraits().get(0).getState().get("on"), false);
            assertEquals(state.getTraits().get(1).getState().get("brightness"), 50);
            state = stateStore.get("1");
            assertEquals(state.getOnline(), false);
            assertEquals(state.getTraits().get(0).getState().get("on"), true);
            assertEquals(state.getTraits().get(1).getState().get("brightness"), 90);
        }
    }

    private static DeviceEvent makeRegisterEvent() {
        DeviceEvent event = new DeviceEvent();
        event.setTrigger(TriggerType.register);
        event.setOnline(true);
        Trait onOffTrait = new Trait("OnOff", Collections.singletonMap("on", true));
        Trait brightnessTrait = new Trait("Brightness", Collections.singletonMap("brightness", 50));
        Trait colorsettingTrait = new Trait("ColorSetting", Collections.singletonMap("color", Collections.singletonMap("temperatureK", 5000)));
        event.setTraits(Arrays.asList(onOffTrait, brightnessTrait, colorsettingTrait));
        return event;
    }

    private static DeviceEvent makeChangedOnOffEvent(boolean on) {
        DeviceEvent event = new DeviceEvent();
        event.setTrigger(TriggerType.app);
        Trait onOffTrait = new Trait("OnOff", Collections.singletonMap("on", on));
        event.setTraits(Arrays.asList(onOffTrait));
        return event;
    }

    private static DeviceEvent makeChangedBrightnessEvent(int brightness) {
        DeviceEvent event = new DeviceEvent();
        event.setTrigger(TriggerType.app);
        Trait brightnessTrait = new Trait("Brightness", Collections.singletonMap("brightness", brightness));
        event.setTraits(Arrays.asList(brightnessTrait));
        return event;
    }

    private static DeviceEvent makeConnectionOffEvent() {
        DeviceEvent event = new DeviceEvent();
        event.setOnline(false);
        return event;
    }
}
