package com.jw.home.client;

import com.jw.home.dto.DeviceState;
import com.jw.home.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;

@FeignClient(name = "streams", configuration = StreamsClientErrorDecoder.class)
public interface StreamsClient {
    @GetMapping(value = "/api/v1/device/{deviceId}")
    ResponseEntity<ResponseDto<DeviceState>> getDeviceState(URI baseUri, @PathVariable String deviceId);
}
