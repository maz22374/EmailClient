package at.spengergasse.emailclient.application.controller;

import at.spengergasse.emailclient.application.model.EmailReceiver;
import at.spengergasse.emailclient.application.model.EmailSender;
import at.spengergasse.emailclient.application.view.DialogLogin;
import at.spengergasse.emailclient.application.view.DialogSendMail;
import at.spengergasse.emailclient.application.view.View;
import jakarta.mail.MessagingException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author David Mazurek
 * @version 1.0.0
 */
public class Controller implements EventHandler<ActionEvent> {

    private final View view;
    private DialogLogin dialogLogin;
    private DialogSendMail dialogSendMail;

    private final EmailSender emailSender;
    private final EmailReceiver emailReceiver;

    private ArrayList<String> attachments;

    public Controller(View view, EmailSender emailSender, EmailReceiver emailReceiver) {
        this.view = view;
        this.emailSender = emailSender;
        this.emailReceiver = emailReceiver;

        login();
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        String s = "";

        if (actionEvent.getSource() instanceof MenuItem)
            s = (String) ((MenuItem) actionEvent.getSource()).getUserData();
        if (actionEvent.getSource() instanceof Button)
            s = (String) ((Button) actionEvent.getSource()).getUserData();

        switch (s) {
            case "login" -> loginCallback();
            case "close" -> close();
            case "addAttachment" -> addAttachment();
            case "sendMail" -> sendMail();
            case "send" -> sendMailCallback();
            case "receiveFolder" -> updateList();
            default -> view.showMessage("Fehler!", "Funktion nicht unterstützt!", "\"" + s + "\"");
        }
    }

    private void login() {
        dialogLogin = new DialogLogin(this);
        dialogLogin.showAndWait();
    }

    private void loginCallback() {
        String username = dialogLogin.getUsername();
        String password = dialogLogin.getPassword();
        String smtpHost = dialogLogin.getSmtpHost();
        String smtpPort = dialogLogin.getSmtpPort();
        String receiveHost = dialogLogin.getReceiveHost();
        String receivePort = dialogLogin.getReceivePort();
        String protocol = dialogLogin.getProtocol();
        String secureConnection = dialogLogin.getSecureConnection();

        if (username == null || username.isEmpty()) {
            view.showMessage("Fehler!", "Fehler beim Login", "Bitte gebe deine E-Mail Adresse an!");
        } else if (password == null || password.isEmpty()) {
            view.showMessage("Fehler!", "Fehler beim Login", "Bitte gebe dein Passwort an!");
        } else if (smtpHost == null || smtpHost.isEmpty()) {
            view.showMessage("Fehler!", "Fehler beim Login", "Bitte gebe den SMTP-Host an!");
        } else if (smtpPort == null || smtpPort.isEmpty()) {
            view.showMessage("Fehler!", "Fehler beim Login", "Bitte gebe den SMTP-Port an!");
        } else if (receiveHost == null || receiveHost.isEmpty()) {
            view.showMessage("Fehler!", "Fehler beim Login", "Bitte gebe den IMAP/POP3-Host an!");
        } else if (receivePort == null || receivePort.isEmpty()) {
            view.showMessage("Fehler!", "Fehler beim Login", "Bitte gebe den IMAP-POP3-Port an!");
        } else if (protocol == null || protocol.isEmpty()) {
            view.showMessage("Fehler!", "Fehler beim Login", "Bitte gebe das Protokoll an!");
        } else if (secureConnection == null || secureConnection.isEmpty()) {
            view.showMessage("Fehler!", "Fehler beim Login", "Bitte gebe die Verschlüsselungsart an!");
        } else {
            try {
                emailSender.login(smtpHost, smtpPort, secureConnection, username, password);
                emailReceiver.login(protocol, receiveHost, receivePort, username, password);

                emailReceiver.check();

                dialogLogin.close();
            } catch (MessagingException | NumberFormatException e) {
                view.showMessage("Fehler!", "Fehler beim Login", e.getLocalizedMessage());
            }
        }
    }

    private void sendMail() {
        dialogSendMail = new DialogSendMail(this);
        dialogSendMail.show();
    }

    private void addAttachment() {
        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(view.getScene().getWindow());

        attachments = new ArrayList<>();

        if (files != null && files.size() > 0) {
            for (File file : files) {
                attachments.add(file.getPath());
                dialogSendMail.setLabelAttachments(file.getName());
            }
        }
    }

    private void sendMailCallback() {
        String fromEmail = dialogLogin.getUsername();
        String toEmail = dialogSendMail.getToEmail();
        String ccEmail = dialogSendMail.getCCEmail();
        String bccEmail = dialogSendMail.getBCCEmail();
        String subject = dialogSendMail.getSubject();
        String message = dialogSendMail.getMessage();


        if (toEmail == null || toEmail.isEmpty()) {
            view.showMessage("Fehler!", "Fehler beim senden der E-Mail", "Bitte gebe den Empfänger an!");
        } else if (subject == null || subject.isEmpty()) {
            view.showMessage("Fehler!", "Fehler beim senden der E-Mail", "Bitte gebe den Betreff an!");
        } else if (message == null || message.isEmpty()) {
            view.showMessage("Fehler!", "Fehler beim senden der E-Mail", "Bitte gebe die Nachricht an!");
        } else {
            try {
                emailSender.sendMessage(fromEmail, toEmail, ccEmail, bccEmail, subject, message, attachments);
                dialogSendMail.close();
            } catch (MessagingException | IOException e) {
                view.showMessage("Fehler!", "Fehler beim senden der E-Mail", e.getLocalizedMessage());
            }
        }
    }

    private void updateList() {
        try {
            emailReceiver.check();
            view.updateList();
        } catch (MessagingException e) {
            view.showMessage("Fehler!", "Ein Fehler ist aufgetreten", e.getLocalizedMessage());
        }
    }

    private void close() {
        Platform.exit();
        System.exit(0);
    }
}
