package com.jw.home.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jw.home.dto.ResponseDto;
import com.jw.home.exception.NotFoundDeviceException;
import com.jw.home.exception.SystemException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class StreamsClientErrorDecoder implements ErrorDecoder {
    @SneakyThrows
    @Override
    public Exception decode(String methodKey, Response response) {
        log.warn("Failed to call remote : {}, {}", response.status(), response.reason());
        switch (response.status()) {
            case 409:
                Reader reader = response.body().asReader(StandardCharsets.UTF_8);
                String errorResult = IOUtils.toString(reader);
                ObjectMapper objectMapper = new ObjectMapper();
                ResponseDto errorResponse = objectMapper.readValue(errorResult, ResponseDto.class);
                switch (errorResponse.getErrorCode()) {
                    case 101:
                        return SystemException.INSTANCE;
                    case 401:
                        return NotFoundDeviceException.INSTANCE;
                }
        }
        return new Exception(response.reason());
    }
}
