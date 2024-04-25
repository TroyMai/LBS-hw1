package com.team.demo.websocket;

import org.springframework.context.ApplicationEvent;

public class WinnerEvent extends ApplicationEvent {
    private String winnerMessage;

    public WinnerEvent(Object source, String winnerMessage) {
        super(source);
        this.winnerMessage = winnerMessage;
    }

    public String getWinnerMessage() {
        return winnerMessage;
    }
}
