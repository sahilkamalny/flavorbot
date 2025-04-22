package edu.farmingdale.recipegenerator;

import edu.farmingdale.recipegenerator.db.ConnDbOps;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static java.util.Objects.hash;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //ConnDbOps db = new ConnDbOps();

        //db.insertUser("alice", "alice@example.com", String.valueOf(hash("pa$$w0rd")));
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("/Styling/style.css").toExternalForm());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
        System.out.println("CSS Path: " + HelloApplication.class.getResource("/Styling/style.css"));
    }
}
