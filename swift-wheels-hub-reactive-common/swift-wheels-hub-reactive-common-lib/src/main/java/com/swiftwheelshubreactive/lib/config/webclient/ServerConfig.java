package com.swiftwheelshubreactive.lib.config.webclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    @Bean
    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> serverFactoryCustomizer() {
        return factory -> {
            int connectionTimeout = 60000;
            int writeTimeout = 60000;

            factory.addServerCustomizers(
                    server -> server.tcpConfiguration(
                            tcp -> tcp.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                                    .doOnConnection(connection -> connection.addHandlerLast(new ReadTimeoutHandler(writeTimeout)))
                    )
            );
        };
    }

}