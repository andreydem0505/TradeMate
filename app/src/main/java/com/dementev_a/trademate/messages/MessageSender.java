package com.dementev_a.trademate.messages;

public class MessageSender {
    private SendingMethod sendingMethod;

    public void setMethod(SendingMethod sendingMethod) {
        this.sendingMethod = sendingMethod;
    }

    public void send(StrategyMessage message) {
        this.sendingMethod.send(message);
    }
}
