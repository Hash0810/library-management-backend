package com.lib.service;

import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public static void sendOTP(String to, String otp) {
        // Sender's email ID and credentials
        String fromEmail = "unnamsiva9963@gmail.com";
        final String username = "unnamsiva9963@gmail.com"; // Your email address
        final String appPassword = "tjrv gsct rtpd qlsw"; // Your Google App Password

        // Gmail SMTP server configuration
        String host = "smtp.gmail.com";

        // Configure SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Ensure TLS 1.2
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Get the Session object
        Session session = Session.getInstance(props,
                new jakarta.mail.Authenticator() {
                    @Override
					protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, appPassword);
                    }
                });

        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);

            // Set From header
            message.setFrom(new InternetAddress(fromEmail, "Library Management System"));

            // Set To header
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject
            message.setSubject("Your OTP Code");

            // Set the message body
            message.setText("Dear user,\n\nYour OTP code is: " + otp);

            // Send message
            Transport.send(message);

//            System.out.println("OTP email sent successfully....");

        } catch (MessagingException e) {
            System.err.println("Error while sending email: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
