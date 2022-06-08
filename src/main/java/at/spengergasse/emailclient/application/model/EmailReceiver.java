package at.spengergasse.emailclient.application.model;

import jakarta.mail.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/**
 * @author David Mazurek
 * @version 1.0.0
 */
public class EmailReceiver {

    private Store store;
    private ArrayList<Message> messages;

    public void login(String protocol, String host, String port, String username, String password) throws MessagingException {
        Properties properties = System.getProperties();

        // server settings
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);

        // SSL settings
        properties.setProperty(String.format("mail.%s.socketFactory.class", protocol), "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
        properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), String.valueOf(port));

        Session session = Session.getDefaultInstance(properties);
        session.setDebug(true);

        store = session.getStore(protocol);
        store.connect(host, Integer.parseInt(port), username, password);
    }

    public void check() throws MessagingException {
        if (store == null) {
            throw new IllegalStateException("You have to log in first (login-method())!");
        }

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages(1, inbox.getMessageCount());

        this.messages = new ArrayList<>();
        Collections.addAll(this.messages, messages);

        //inbox.close(false);    // Sollte nicht so sein ...
        //store.close();        // Sollte nicht so sein ...
    }

    public String getText(Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            return (String) p.getContent();
        }

        if (p.isMimeType("multipart/alternative")) {
            Multipart mp = (Multipart) p.getContent();
            String text = null;

            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);

                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();

            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;
    }

    public ArrayList<Message> getMessages() {
        messages.sort((o1, o2) -> {
            try {
                return -o1.getSentDate().compareTo(o2.getSentDate());
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });

        return messages;
    }

    public static void main(String[] args) throws MessagingException {
        EmailReceiver emailReceiver = new EmailReceiver();
        //emailReceiver.login("imap", "imap.gmail.com", "993", "your_email_address", "your_password");
        emailReceiver.login("imap", "imap.gmail.com", "993", "your_email_address", "your_password");
        emailReceiver.check();
    }
}
