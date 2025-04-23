package edu.farmingdale.recipegenerator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Get screen bounds
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Apply external CSS
        //scene.getStylesheets().add(getClass().getResource("/Styling/style.css").toExternalForm());

        // Set up the stage
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenWidth);
        stage.setHeight(screenHeight);

        // Connect to the Azure Database
        AzureDBConnector dbConnector = new AzureDBConnector();
        boolean isConnected = dbConnector.connectToDatabase();

        // Show success or error message depending on the connection result
        if (isConnected) {
            // Show a success message
            showAlert("Connection Successful", "Successfully connected to the Azure Database.", AlertType.INFORMATION);
        } else {
            // Show an error message
            showAlert("Connection Failed", "Failed to connect to the Azure Database.", AlertType.ERROR);
        }

        stage.show();
    }

    // Method to show alerts
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
