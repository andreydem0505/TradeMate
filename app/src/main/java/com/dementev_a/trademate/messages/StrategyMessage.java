package com.dementev_a.trademate.messages;

public class StrategyMessage {
    public final String to;
    public final String subject;
    public final String text;
    public final String merchandiser;

    public StrategyMessage(String to, String subject, String text, String merchandiser) {
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.merchandiser = merchandiser;
    }
}
