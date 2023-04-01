package com.jw.home.rsocket.client;

import com.jw.home.dto.DeviceState;
import com.jw.home.dto.ResponseDto;
import com.jw.home.exception.NotFoundDeviceException;
import com.jw.home.exception.SystemException;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RSocketClient {
    final Map<String, RSocketRequester> requesterMap = new HashMap<>();

    @Autowired
    private RSocketRequester.Builder builder;

    public Mono<DeviceState> getDeviceState(String host, int port, String deviceId) {
        RSocketRequester requester = getRSocketRequest(host, port);
        return requester.route("get.device." + deviceId)
                .retrieveMono(new ParameterizedTypeReference<ResponseDto<DeviceState>>() {
                })
                .map(res -> {
                    if (res.getResultData() == null) {
                        if (res.getErrorCode() == 401) {
                            throw NotFoundDeviceException.INSTANCE;
                        }
                        throw SystemException.INSTANCE;
                    }
                    return res.getResultData();
                });
    }

    private RSocketRequester getRSocketRequest(String host, int port) {
        RSocketRequester requester = requesterMap.get(host + ":" + port);
        if (requester != null) {
            return requester;
        }
        requester = builder.rsocketConnector(c -> c.reconnect(Retry.fixedDelay(5, Duration.ofSeconds(2))
                        .doBeforeRetry(s -> log.info("retrying " + s.totalRetriesInARow()))))
                .transport(TcpClientTransport.create(host, port));
        requesterMap.put(host + ":" + port, requester);
        return requester;
    }
}
