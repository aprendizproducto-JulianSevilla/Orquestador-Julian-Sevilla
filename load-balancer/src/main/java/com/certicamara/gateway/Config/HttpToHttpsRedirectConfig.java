package com.certicamara.gateway.Config;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.server.HttpServer;

@Configuration
public class HttpToHttpsRedirectConfig implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    @Override
    public void customize(NettyReactiveWebServerFactory factory) {

        factory.addServerCustomizers(httpServer -> httpServer.port(8443));

        HttpServer.create()
                .port(8080)
                .handle((request, response) -> {

                    String host = request.requestHeaders().get("Host");
                    String uri = request.uri();

                    String redirectUrl = "https://" + host.replace(":8080", ":8443") + uri;

                    return response
                            .status(301)
                            .header("Location", redirectUrl)
                            .send();
                })
                .bindNow();
    }
}