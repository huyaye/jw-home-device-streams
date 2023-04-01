package com.jw.home.rsocket.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class ConnectionHandler {
    @ConnectMapping
    public Mono<Void> onConnect(RSocketRequester rSocketRequester){
        log.info("Connection setup +++++++++++++++");   // Connection 요청이 왔을 때,
        rSocketRequester.rsocket()
                // Connection 이 끊어질 때
                .onClose()
                .doOnSuccess(d -> log.info("doOnSuccess : {}", d))
                .doOnTerminate(() -> log.info("doOnTerminate +++++++++++++++"))
                .doOnCancel(() -> log.info("doOnCancel +++++++++++++++"))
                .doOnError(e -> log.error("doOnError +++++++++++++++", e))
                .subscribe();
        return Mono.empty();
    }
}
