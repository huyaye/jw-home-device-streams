package com.jw.home.kafka.interactivequery;

import com.jw.home.dto.DeviceState;
import com.jw.home.exception.NotFoundDeviceException;
import com.jw.home.exception.SystemException;
import com.jw.home.rsocket.client.RSocketClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

import static org.apache.kafka.common.serialization.Serdes.String;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateStoreManager {
    @Value("${spring.kafka.streams.state-stores.device-state}")
    private String stateStoreName;
    @Value(value = "${spring.kafka.streams.app.server}")
    private String hostInfo;

    private final StreamsBuilderFactoryBean factoryBean;
    private final RSocketClient streamsCaller;

    public Mono<DeviceState> getDeviceState(String deviceId) {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        KeyQueryMetadata queryMetadata = kafkaStreams.queryMetadataForKey(stateStoreName, deviceId, String().serializer()); // TODO KIP-562
        if (queryMetadata == null) {
            log.warn("queryMetadata is null.");
            return Mono.error(SystemException.INSTANCE);
        }
        if (isAlive(queryMetadata.activeHost())) {
            return query(deviceId, kafkaStreams, queryMetadata.activeHost(), queryMetadata.partition());
        } else {
            Set<HostInfo> standbyHosts = queryMetadata.standbyHosts();
            for (HostInfo standbyHost : standbyHosts) { // TODO Filter acceptable offset lag, sort by smallest lag
                return query(deviceId, kafkaStreams, standbyHost, queryMetadata.partition());
            }
        }
        return Mono.error(SystemException.INSTANCE);
    }

    private Mono<DeviceState> query(String deviceId, KafkaStreams kafkaStreams, HostInfo hostInfo, int partition) {
        ReadOnlyKeyValueStore<String, DeviceState> stateStore =
                kafkaStreams.store(StoreQueryParameters.<ReadOnlyKeyValueStore<String, DeviceState>>fromNameAndType(stateStoreName, QueryableStoreTypes.keyValueStore())
                        .enableStaleStores().withPartition(partition));
        if (isLocalHost(hostInfo)) {
            DeviceState deviceState = stateStore.get(deviceId);
            return deviceState != null ? Mono.just(deviceState) : Mono.error(NotFoundDeviceException.INSTANCE);
        } else {
            return streamsCaller.getDeviceState(hostInfo.host(), hostInfo.port(), deviceId);
        }
    }

    private boolean isAlive(HostInfo hostInfo) {
        return KeyQueryMetadata.NOT_AVAILABLE.activeHost().equals(hostInfo) == false;
    }

    private boolean isLocalHost(HostInfo hostInfo) {
        return this.hostInfo.equals(hostInfo.host() + ":" + hostInfo.port());
    }
}