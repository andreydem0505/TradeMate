package com.dementev_a.trademate.messages;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSending implements SendingMethod {
    private final String password = "xxx";
    private final String host = "smtp.gmail.com";
    private final String from = "andreydem42@gmail.com";
    private final String protocol = "smtp";

    @Override
    public void send(StrategyMessage message) {
        Session session = createSession();
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(message.to));
            mimeMessage.setSubject(message.subject);
            mimeMessage.setText(message.text + "\n" + message.merchandiser);
            Transport transport = session.getTransport(protocol);
            transport.connect(host, from, password);
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendPhotos(String[] uris, String to, String reportName) {
        Session session = createSession();
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setSubject(reportName);
            Multipart multipart = new MimeMultipart();
            try {
                for (String uri : uris) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(new File(uri));
                    multipart.addBodyPart(attachmentPart);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mimeMessage.setContent(multipart);
            Transport transport = session.getTransport(protocol);
            transport.connect(host, from, password);
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private Session createSession() {
        String port = "587";
        Properties props = System.getProperties();
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        return Session.getDefaultInstance(props);
    }
}
