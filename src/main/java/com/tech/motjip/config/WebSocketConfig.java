package com.tech.motjip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 웹소켓 메시지 브로커를 켭니다!
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. 안드로이드가 처음 서버랑 "연결할게요!" 하고 찾아오는 주소(Endpoint)입니다.
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*") // 코르스(CORS) 에러 방지 (일단 다 열어둡니다)
                .withSockJS(); // 구형 환경에서도 돌아가게 돕는 녀석입니다.
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 2. 메시지를 "받을 때" (구독) 쓰는 경로 (안드로이드가 이 주소를 쳐다보고 있습니다)
        // 예: /sub/chat/room/1 (1번 방 메시지 받을래)
        registry.enableSimpleBroker("/sub");

        // 3. 메시지를 "보낼 때" (발송) 쓰는 경로 (안드로이드가 이 주소로 편지를 보냅니다)
        // 예: /pub/chat/message (서버야 이거 좀 전달해 줘)
        registry.setApplicationDestinationPrefixes("/pub");
    }
}