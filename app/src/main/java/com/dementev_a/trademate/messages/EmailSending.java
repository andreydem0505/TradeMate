package com.dementev_a.trademate.messages;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSending implements SendingMethod {
    @Override
    public void send(StrategyMessage message) {
        String host = "smtp.gmail.com";
        String port = "587";
        String from = "andreydem42@gmail.com";
        Properties props = System.getProperties();
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        Session session = Session.getDefaultInstance(props);
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(message.to));
            mimeMessage.setSubject(message.subject);
            mimeMessage.setText(message.text);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, "xxx");
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
