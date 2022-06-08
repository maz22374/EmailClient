package at.spengergasse.emailclient.application;

import at.spengergasse.emailclient.application.view.View;
import jakarta.mail.MessagingException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * @author David Mazurek
 * @version 1.0.0
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        View root = new View();
        primaryStage.setTitle("Email Client");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/at/spengergasse/emailclient/icon.png"))));
        primaryStage.setScene(new Scene(root, 1020, 720));
        primaryStage.show();
    }
}
