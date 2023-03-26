package com.jw.home.rest;

import com.jw.home.dto.DeviceState;
import com.jw.home.dto.ResponseDto;
import com.jw.home.exception.CustomBusinessException;
import com.jw.home.kafka.interactivequery.StateStoreManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeviceRestController {
    private final StateStoreManager stateStoreManager;

    @GetMapping("/api/v1/device/{deviceId}")
    public ResponseDto<DeviceState> getDeviceState(@PathVariable String deviceId) {
        DeviceState deviceState = stateStoreManager.getDeviceState(deviceId);
        return ResponseDto.<DeviceState>builder().resultData(deviceState).build();
    }

    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<ResponseDto<Object>> handleNoSuchElementFoundException(CustomBusinessException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseDto.builder().errorCode(e.getErrorCode()).errorMessage(e.getErrorMessage()).build());
    }
}
