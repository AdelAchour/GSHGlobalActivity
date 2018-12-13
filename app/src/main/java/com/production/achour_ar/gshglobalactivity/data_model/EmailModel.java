package com.production.achour_ar.gshglobalactivity.data_model;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailModel {

    String from, to, host, subject, content;

    public EmailModel(String from, String to, String subject, String content) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.host = "ssl0.ovh.net";
    }

    public void sendMessage(){

        Properties properties = System.getProperties();

        // Setup mail server

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "587");

        // Get the default Session object.
        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getDefaultInstance(properties,auth);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(content);

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");

        } catch (MessagingException mex) { mex.printStackTrace(); }

    }

    private class SMTPAuthenticator extends Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = from;
            String password = "69i8yC9PsA^W";
            return new PasswordAuthentication(username, password);
        }
    }
}
