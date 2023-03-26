package com.jw.home.client;

import com.jw.home.dto.DeviceState;
import com.jw.home.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class StreamsAPICaller {
    private final StreamsClient streamsClient;

    public DeviceState getDeviceState(String host, int port, String deviceId) {
        URI uri = URI.create("http://" + host + ":" + port);
        ResponseEntity<ResponseDto<DeviceState>> response = streamsClient.getDeviceState(uri, deviceId);
        return response.getBody().getResultData();
    }
}
