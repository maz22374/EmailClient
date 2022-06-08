package at.spengergasse.emailclient.application.model;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author David Mazurek
 * @version 1.0.0
 */
public class EmailSender {

    private Properties properties;
    private Session session;

    public void login(String host, String port, String secureConnection, String username, String password) throws MessagingException {
        properties = System.getProperties();

        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.debug", "false");
        properties.setProperty("mail.smtp.port", port);

        if (secureConnection.equalsIgnoreCase("tls")) {
            properties.setProperty("mail.smtp.starttls.enable", "true");
        } else if (secureConnection.equalsIgnoreCase("ssl")) {
            properties.setProperty("mail.smtp.startssl.enable", "true");  // make this true if port=465
        } else {
            properties.setProperty("mail.smtp.starttls.enable", "false");
            properties.setProperty("mail.smtp.startssl.enable", "false");
        }

        properties.setProperty("mail.smtp.socketFactory.port", port);
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");

        session = Session.getInstance(properties,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        session.setDebug(true);

        Transport transport = session.getTransport();
        transport.connect(host, Integer.parseInt(port), username, password);
        transport.close();
    }

    public void sendMessage(String fromEmail, String toEmail, String ccEmail, String bccEmail, String subject, String msg, ArrayList<String> attachFiles) throws MessagingException, IOException {
        if (properties == null) {
            throw new IllegalStateException("You have to log in first (login-method())!");
        }

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));

        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

        if (ccEmail != null && !ccEmail.isEmpty())
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccEmail));
        if (bccEmail != null && !bccEmail.isEmpty())
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccEmail));

        message.setSubject(subject);

        Multipart multipart = new MimeMultipart();
        MimeBodyPart messageBodyPart = new MimeBodyPart();

        messageBodyPart.setContent(msg, "text/html");
        multipart.addBodyPart(messageBodyPart);

        if (attachFiles != null && attachFiles.size() > 0) {
            for (String filePath : attachFiles) {
                MimeBodyPart attachPart = new MimeBodyPart();
                attachPart.attachFile(filePath);
                multipart.addBodyPart(attachPart);
            }
        }
        message.setContent(multipart);

        Transport.send(message);
    }

    public static void main(String[] args) throws Exception {
        String serverName = "smtp.gmail.com";
        String port = "465";                            // 465 , 587 , 25
        String secureConnection = "ssl";               // ssl , tls , never
        String username = "your_email_address";
        String password = "your_password";
        String toEmail = "receiver_email_address";
        String subject = "New Assessment mail";
        String msg = "<h1> This is test mail please ignore... </h1>";

        // attachments
        ArrayList<String> attachFiles = new ArrayList<>();
        attachFiles.add("D:/test1.html");
        attachFiles.add("D:/test2.txt");
        attachFiles.add("D:/test3.txt");

        EmailSender emailSender = new EmailSender();
        emailSender.login(serverName, port, secureConnection, username, password);
        emailSender.sendMessage(username, toEmail, null, null, subject, msg, attachFiles);
    }
}
