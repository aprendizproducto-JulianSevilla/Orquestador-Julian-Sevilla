package com.certicamara.gateway.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver(
            @NonNull @Value("${gateway.ratelimit.header:X-Forwarded-For}") String forwardedForHeader,
            @NonNull @Value("${gateway.ratelimit.default-key:unknown}") String defaultKey) {
            
        return exchange -> {
            String ip = exchange.getRequest()
                    .getHeaders()
                    .getFirst(forwardedForHeader);

            if (ip == null) {
                InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();

                if (remoteAddress == null) {
                    return Mono.just(defaultKey);
                }

                ip = remoteAddress.getAddress().getHostAddress();
            }

            return Mono.just(ip);
        };
    }
}