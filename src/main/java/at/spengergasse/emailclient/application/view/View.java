package at.spengergasse.emailclient.application.view;

import at.spengergasse.emailclient.application.controller.Controller;
import at.spengergasse.emailclient.application.model.EmailReceiver;
import at.spengergasse.emailclient.application.model.EmailSender;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author David Mazurek
 * @version 1.0.0
 */
public class View extends BorderPane {

    private final Controller ctrl;

    private final EmailReceiver emailReceiver;

    private MenuBar menuBar;
    private MenuItem sendMailMenuItem, closeMenuItem, receiveMenuItem;

    private TableView<Message> tableView;

    private ObservableList<Message> messages;

    private WebView webView;

    private SplitPane splitPane;

    public View() {
        EmailSender emailSender = new EmailSender();
        this.emailReceiver = new EmailReceiver();
        this.ctrl = new Controller(this, emailSender, emailReceiver);

        initGUI();
    }

    private void initGUI() {
        initComponents();
        addComponents();
        handle();
    }

    private void initComponents() {
        // MenuBar
        sendMailMenuItem = new MenuItem("Neue E-Mail");
        sendMailMenuItem.setUserData("sendMail");

        closeMenuItem = new MenuItem("Schließen");
        closeMenuItem.setUserData("close");

        receiveMenuItem = new MenuItem("Ordner Empfangen");
        receiveMenuItem.setUserData("receiveFolder");

        Menu file = new Menu("Datei");
        file.getItems().addAll(sendMailMenuItem, receiveMenuItem, new SeparatorMenuItem(), closeMenuItem);

        menuBar = new MenuBar(file);

        // TableView
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Message, String> subjectColumn = new TableColumn<>("Betreff");
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));

        TableColumn<Message, String> sentDateColumn = new TableColumn<>("Datum");
        sentDateColumn.setCellValueFactory(messageStringCellDataFeatures -> {
            try {
                Date date = messageStringCellDataFeatures.getValue().getSentDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return new ReadOnlyObjectWrapper<>(dateFormat.format(date));
            } catch (MessagingException e) {
                showMessage("Fehler!", "Ein fehler ist aufgetreten", e.getLocalizedMessage());
                return null;
            }
        });

        TableColumn<Message, String> fromColumn = new TableColumn<>("Absender");
        fromColumn.setCellValueFactory(messageStringCellDataFeatures -> {
            try {
                Address[] addresses = messageStringCellDataFeatures.getValue().getFrom();
                String email = addresses == null ? null : ((InternetAddress) addresses[0]).getAddress();
                return new ReadOnlyObjectWrapper<>(email);
            } catch (MessagingException e) {
                showMessage("Fehler!", "Ein fehler ist aufgetreten", e.getLocalizedMessage());
                return null;
            }
        });

        tableView.setItems(fetchMessages());
        tableView.getColumns().addAll(fromColumn, subjectColumn, sentDateColumn);

        // WebView
        webView = new WebView();

        // SplitPane
        splitPane = new SplitPane(tableView, webView);
        splitPane.setOrientation(Orientation.VERTICAL);
    }

    private void addComponents() {
        // Top
        this.setTop(menuBar);

        // Center
        this.setCenter(splitPane);
    }

    private void handle() {
        // MenuBar
        sendMailMenuItem.setOnAction(ctrl);
        closeMenuItem.setOnAction(ctrl);
        receiveMenuItem.setOnAction(ctrl);

        // TableView
        tableView.setOnMouseClicked(mouseEvent -> {
            try {
                Message message = tableView.getSelectionModel().getSelectedItem();
                if (message != null)
                    webView.getEngine().loadContent(emailReceiver.getText(message));
            } catch (MessagingException | IOException e) {
                showMessage("Fehler!", "Ein fehler ist aufgetreten", e.getLocalizedMessage());
            }
        });
    }

    public void showMessage(String title, String header, String content) {
        Alert alert;
        if (title.startsWith("Fehler"))
            alert = new Alert(Alert.AlertType.ERROR);
        else
            alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    private ObservableList<Message> fetchMessages() {
        messages = FXCollections.observableArrayList(emailReceiver.getMessages());
        return messages;
    }

    public void updateList() {
        messages.setAll(emailReceiver.getMessages());
    }
}
