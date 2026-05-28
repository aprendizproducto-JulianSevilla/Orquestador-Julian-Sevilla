package com.certicamara.gateway.filters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestIdFilter implements GlobalFilter, Ordered {

    @NonNull
    private final String requestIdHeader;

    public RequestIdFilter(@NonNull @Value("${gateway.request-id.header:X-Request-Id}") String requestIdHeader) {
        this.requestIdHeader = requestIdHeader;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String requestId = exchange.getRequest().getHeaders().getFirst(this.requestIdHeader);

        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        ServerWebExchange mutated = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header(this.requestIdHeader, requestId)
                        .build())
                .build();

        return chain.filter(mutated);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}