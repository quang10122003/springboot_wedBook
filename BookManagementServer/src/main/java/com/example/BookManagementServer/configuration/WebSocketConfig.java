package com.example.BookManagementServer.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Cấu hình broker để gửi thông báo
        config.enableSimpleBroker("/topic"); // Kênh gửi thông báo
        config.setApplicationDestinationPrefixes("/app");
    }   

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint để frontend kết nối WebSocket
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*").withSockJS();
    }
}
