package com.team.demo.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WinnerEventListener {
    private SimpMessagingTemplate messagingTemplate;

    public WinnerEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWinnerEvent(WinnerEvent event) {
        String destination = "/topic/getWinner"; // 目标地址，与@SendTo注解中的目标地址相对应
        String winnerMessage = event.getWinnerMessage();

        messagingTemplate.convertAndSend(destination, winnerMessage);
    }
}
