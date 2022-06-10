package at.spengergasse.emailclient.application.view;

import at.spengergasse.emailclient.application.controller.Controller;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * @author David Mazurek
 * @version 1.0.0
 */
public class DialogSendMail extends Stage {

    private final Controller ctrl;

    private GridPane gp;
    private VBox vBox;

    private TextField toEmail, ccEmail, bccEmail, subject;

    private HTMLEditor htmlEditor;

    private Button attachmentButton, sendButton;

    private Label attachments;

    public DialogSendMail(Controller ctrl) {
        this.ctrl = ctrl;

        initGUI();

        setScene(new Scene(vBox, 1020, 720));
        initModality(Modality.WINDOW_MODAL);
        setTitle("E-Mail versenden");
        getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/at/spengergasse/emailclient/icon.png"))));
    }

    private void initGUI() {
        initComponents();
        addComponents();
        handle();
    }

    private void initComponents() {
        // VBox
        vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        // GridPane
        gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        // TextField
        toEmail = new TextField();
        toEmail.setPrefWidth(500);

        ccEmail = new TextField();
        ccEmail.setPrefWidth(500);

        bccEmail = new TextField();
        bccEmail.setPrefWidth(500);

        subject = new TextField();
        subject.setPrefWidth(500);

        // Button
        attachmentButton = new Button("Anhänge hinzufügen");
        attachmentButton.setUserData("addAttachment");

        sendButton = new Button("Senden");
        sendButton.setUserData("send");

        // HTMLEditor
        htmlEditor = new HTMLEditor();

        // Label
        attachments = new Label();
    }

    private void addComponents() {
        gp.addRow(0, new Label("Empfänger:"), toEmail);
        gp.addRow(1, new Label("CC:"), ccEmail);
        gp.addRow(2, new Label("BCC:"), bccEmail);
        gp.addRow(3, new Label("Betreff:"), subject);
        gp.addRow(4, new Label("Anhänge:"), attachmentButton);
        gp.addRow(5, new Label("Nachricht:"));

        vBox.getChildren().addAll(gp, htmlEditor, sendButton, attachments);
    }

    private void handle() {
        // Button
        attachmentButton.setOnAction(ctrl);
        sendButton.setOnAction(ctrl);
    }

    public String getToEmail() {
        return toEmail.getText();
    }

    public String getCCEmail() {
        return ccEmail.getText();
    }

    public String getBCCEmail() {
        return bccEmail.getText();
    }

    public String getSubject() {
        return subject.getText();
    }

    public String getMessage() {
        return htmlEditor.getHtmlText();
    }

    public String getLabelAttachments() {
        return attachments.getText();
    }

    public void setLabelAttachments(String fileName) {
        if (fileName == null)
            attachments.setText("");
        else if (attachments.getText() == null || attachments.getText().isEmpty())
            attachments.setText("Anhänge: " + fileName);
        else
            attachments.setText(attachments.getText() + ", " + fileName);
    }
}
