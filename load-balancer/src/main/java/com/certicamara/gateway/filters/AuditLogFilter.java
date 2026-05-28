package com.certicamara.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuditLogFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(AuditLogFilter.class);
    
    @NonNull
    private final String requestIdHeader;

    public AuditLogFilter(@NonNull @Value("${gateway.request-id.header:X-Request-Id}") String requestIdHeader) {
        this.requestIdHeader = requestIdHeader;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        long start = System.currentTimeMillis();

        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    long time = System.currentTimeMillis() - start;

                    log.info("RequestId={} path={} status={} time={}ms",
                            exchange.getRequest().getHeaders().getFirst(this.requestIdHeader),
                            exchange.getRequest().getURI(),
                            exchange.getResponse().getStatusCode(),
                            time);
                })
        );
    }
}