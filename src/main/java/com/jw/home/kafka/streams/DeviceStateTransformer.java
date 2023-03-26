package com.jw.home.kafka.streams;

import com.jw.home.dto.DeviceState;
import com.jw.home.kafka.topic.DeviceEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

@RequiredArgsConstructor
public class DeviceStateTransformer implements ValueTransformerWithKey<String, DeviceEvent, DeviceState> {
    private final String stateStoreName;
    private KeyValueStore<String, DeviceState> stateStore;

    @Override
    public void init(ProcessorContext context) {
        stateStore = context.getStateStore(stateStoreName);
    }

    @Override
    public DeviceState transform(String deviceId, DeviceEvent event) {
        DeviceState deviceState = stateStore.get(deviceId);
        if (deviceState != null) {
            updateDeviceState(deviceState, event);
        } else {
            deviceState = DeviceState.builder().online(event.getOnline()).traits(event.getTraits()).build();
        }
        stateStore.put(deviceId, deviceState);
        return deviceState;
    }

    @Override
    public void close() {}

    private void updateDeviceState(DeviceState deviceState, DeviceEvent event) {
        Boolean online = event.getOnline();
        if (online != null && !online) {
            deviceState.setOnline(false);
            return;
        }
        deviceState.setOnline(true);
        if (event.getTraits() != null) {
            event.getTraits().forEach(trait -> {
                deviceState.updateTrait(trait);
            });
        }
    }
}
