package com.dementev_a.trademate.messages;

public class StrategyMessage {
    public final String to;
    public final String subject;
    public final String text;

    public StrategyMessage(String to, String subject, String text) {
        this.to = to;
        this.subject = subject;
        this.text = text;
    }
}
