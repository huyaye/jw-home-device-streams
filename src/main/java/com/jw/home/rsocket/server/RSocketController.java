package com.jw.home.rsocket.server;

import com.jw.home.dto.DeviceState;
import com.jw.home.dto.ResponseDto;
import com.jw.home.exception.CustomBusinessException;
import com.jw.home.kafka.interactivequery.StateStoreManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RSocketController {
    private final StateStoreManager stateStoreManager;

    @MessageMapping("get.device.{deviceId}")
    public Mono<ResponseDto<DeviceState>> getDeviceState(@DestinationVariable String deviceId) {
        return stateStoreManager.getDeviceState(deviceId)
                .map(deviceState -> ResponseDto.<DeviceState>builder().resultData(deviceState).build());
    }

    @MessageExceptionHandler
    public Mono<ResponseDto<?>> handleException(CustomBusinessException e){
        return Mono.just(ResponseDto.builder().errorCode(e.getErrorCode()).errorMessage(e.getErrorMessage()).build());
    }
}
