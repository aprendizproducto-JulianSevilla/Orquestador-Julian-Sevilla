package com.certicamara.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RequiredHeadersGatewayFilter
        extends AbstractGatewayFilterFactory<RequiredHeadersGatewayFilter.Config> {

    public RequiredHeadersGatewayFilter() {
        super(Config.class);
    }

    public static class Config {
        public String[] headers;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            for (String header : config.headers) {

                if (!exchange.getRequest().getHeaders().containsKey(header)) {

                    exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                    return exchange.getResponse().setComplete();
                }
            }

            return chain.filter(exchange);
        };
    }
}