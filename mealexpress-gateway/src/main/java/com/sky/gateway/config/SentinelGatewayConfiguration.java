package com.sky.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Registers Sentinel filters and fallback responses for Gateway traffic limiting.
 */
@Configuration
public class SentinelGatewayConfiguration {

    @PostConstruct
    public void initGatewayFallback() {
        GatewayCallbackManager.setBlockHandler((exchange, throwable) -> {
            Map<String, Object> body = new HashMap<>();
            body.put("code", HttpStatus.TOO_MANY_REQUESTS.value());
            body.put("msg", "系统繁忙，请稍后再试");
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body));
        });
    }
}
