package at.spengergasse.emailclient.application.view;

import at.spengergasse.emailclient.application.controller.Controller;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * @author David Mazurek
 * @version 1.0.0
 */
public class DialogLogin extends Stage {

    private final Controller ctrl;

    private GridPane gp;

    private final String[] secureConnection = {"ssl", "tls", "never"};
    private final String[] protocol = {"imap", "pop3"};

    private ChoiceBox<String> secureConnectionChoiceBox, protocolChoiceBox;

    private TextField smtpHost, smtpPort, receiveHost, receivePort, username;
    private PasswordField password;

    private Button loginButton;

    public DialogLogin(Controller ctrl) {
        this.ctrl = ctrl;

        initGUI();

        initModality(Modality.WINDOW_MODAL);
        setResizable(false);
        setTitle("Login");
        setScene(new Scene(gp));
        getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/at/spengergasse/emailclient/icon.png"))));
        setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private void initGUI() {
        initComponents();
        addComponents();
        handle();
    }

    private void initComponents() {
        // GridPane
        gp = new GridPane();
        gp.setVgap(10);
        gp.setHgap(10);
        gp.setPadding(new Insets(10, 10, 10, 10));

        // ChoiceBox
        secureConnectionChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(secureConnection));
        protocolChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(protocol));

        // TextField
        smtpHost = new TextField();
        smtpPort = new TextField();
        receiveHost = new TextField();
        receivePort = new TextField();
        username = new TextField();

        // PasswordField
        password = new PasswordField();

        // Button
        loginButton = new Button("Bestätigen");
        loginButton.setUserData("login");
    }

    private void addComponents() {
        // GridPane
        gp.addRow(0, new Label("Verbindungseinstellungen"));
        gp.addRow(1, new Label("E-Mail Adresse:"), username);
        gp.addRow(2, new Label("Passwort:"), password);
        gp.addRow(3, new Label("SMTP-Server"), smtpHost);
        gp.addRow(4, new Label("SMTP-Port"), smtpPort);
        gp.addRow(5, new Label("Sichere Verbindung:"), secureConnectionChoiceBox);
        gp.addRow(6, new Label("Protokoll:"), protocolChoiceBox);
        gp.addRow(7, new Label("IMAP/POP3-Host"), receiveHost);
        gp.addRow(8, new Label("IMAP/POP3-Port"), receivePort);
        gp.addRow(9, loginButton);
    }

    private void handle() {
        // Button
        loginButton.setOnAction(ctrl);
    }

    public String getUsername() {
        return username.getText();
    }

    public String getPassword() {
        return password.getText();
    }

    public String getSmtpHost() {
        return smtpHost.getText();
    }

    public String getSmtpPort() {
        return smtpPort.getText();
    }

    public String getSecureConnection() {
        return secureConnectionChoiceBox.getSelectionModel().getSelectedItem();
    }

    public String getProtocol() {
        return protocolChoiceBox.getSelectionModel().getSelectedItem();
    }

    public String getReceiveHost() {
        return receiveHost.getText();
    }

    public String getReceivePort() {
        return receivePort.getText();
    }
}
