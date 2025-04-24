package com.lib.service;

import org.springframework.beans.factory.annotation.Value;
import java.util.Properties;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileTypeMap;
import jakarta.activation.MimeType;
import jakarta.activation.MimeTypeParseException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.util.ByteArrayDataSource;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
@Value("${spring.mail.fromEmail}")
private String fromEmail;
	
@Value("${spring.mail.username}")
private String username;
	
@Value("${spring.mail.password}")
private String appPassword;
		
public void sendOTP(String to, String otp) {
	        
	String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
			protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, "Library Management System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Your OTP Code");
            message.setText("Dear user,\n\nYour OTP code is: " + otp);
            Transport.send(message);
        } catch (Exception e) {
            System.err.println("Error while sending OTP email: " + e.getMessage());
            e.printStackTrace();
        }
    }
	public void sendReceiptWithPDF(String to, byte[] pdfBytes, String fileName) {
		
		String host = "smtp.gmail.com"
	        Properties props = new Properties();
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.smtp.starttls.required", "true");
	        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
	        props.put("mail.smtp.host", host);
	        props.put("mail.smtp.port", "587");
	
	        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
	            @Override
				protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(username, appPassword);
	            }
	        });
	
	        try {
	            MimeMessage message = new MimeMessage(session);
	            message.setFrom(new InternetAddress(fromEmail, "Library Management System"));
	            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
	            message.setSubject("Your Book Receipt");
	
	            // Create email content
	            MimeBodyPart textPart = new MimeBodyPart();
	            textPart.setText("Dear user,\n\nPlease find attached your receipt for the borrowed book.\n\nRegards,\nLibrary Management System");
	
	            // Create PDF attachment
	            MimeBodyPart attachmentPart = new MimeBodyPart();
	            DataSource source = new ByteArrayDataSource(pdfBytes, "application/pdf");
	            attachmentPart.setDataHandler(new DataHandler(source));
	            attachmentPart.setFileName(fileName);
	
	            // Combine parts
	            MimeMultipart multipart = new MimeMultipart();
	            multipart.addBodyPart(textPart);
	            multipart.addBodyPart(attachmentPart);
	
	            message.setContent(multipart);
	
	            Transport.send(message);
	        } catch (Exception e) {
	            System.err.println("Error while sending PDF email: " + e.getMessage());
	            e.printStackTrace();
	        }
	}
	public void sendFinePDF(String to, byte[] pdfBytes, String fileName) {
	    String host = "smtp.gmail.com";
	    Properties props = new Properties();
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.starttls.required", "true");
	    props.put("mail.smtp.ssl.protocols", "TLSv1.2");
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.port", "587");
	
	    Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
	        @Override
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(username, appPassword);
	        }
	    });
	
	    try {
	        MimeMessage message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(fromEmail, "Library Management System"));
	        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
	        message.setSubject("Fine Notice - Library Management System");
	
	        // Email body
	        MimeBodyPart textPart = new MimeBodyPart();
	        textPart.setText("Dear user,\n\nYou have an overdue fine. Please find the attached fine receipt.\n\nRegards,\nLibrary Management System");
	
	        // PDF attachment
	        MimeBodyPart attachmentPart = new MimeBodyPart();
	        DataSource source = new ByteArrayDataSource(pdfBytes, "application/pdf");
	        attachmentPart.setDataHandler(new DataHandler(source));
	        attachmentPart.setFileName(fileName);
	
	        // Combine parts
	        MimeMultipart multipart = new MimeMultipart();
	        multipart.addBodyPart(textPart);
	        multipart.addBodyPart(attachmentPart);
	
	        message.setContent(multipart);
	
	        Transport.send(message);
	    } catch (Exception e) {
	        System.err.println("Error while sending fine PDF email: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
}
