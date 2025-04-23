package edu.farmingdale.recipegenerator;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button signUpButton;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private Text titleText;

    @FXML
    private Text titlePhrase;

    @FXML
    private void initialize() {
        try {
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/b4.jpg")));
            backgroundImageView.setImage(backgroundImage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the background image.", Alert.AlertType.ERROR);
        }

        backgroundImageView.setPreserveRatio(false);
        backgroundImageView.setSmooth(true);
        backgroundImageView.setCache(true);

        backgroundImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImageView.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImageView.fitHeightProperty().bind(newScene.heightProperty());
            }
        });

        FadeTransition fade = new FadeTransition(Duration.seconds(2), titleText);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        FadeTransition fade2 = new FadeTransition(Duration.seconds(2), titlePhrase);
        fade2.setFromValue(0);
        fade2.setToValue(1);
        fade2.play();

        titleText.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((obsWidth, oldWidth, newWidth) -> {
                    // Center titleText
                    titleText.setLayoutX((newWidth.doubleValue() - titleText.getBoundsInLocal().getWidth()) / 2);

                    // Get the position of the "e" in "Recipe"
                    double titleTextWidth = titleText.getBoundsInLocal().getWidth();
                    String titleTextContent = titleText.getText();
                    double widthOfRecipeUpToE = titleText.getLayoutBounds().getWidth() * (titleTextContent.indexOf("e") + 1) / titleTextContent.length();

                    // Set titlePhrase to start where "e" ends in "Recipe"
                    titlePhrase.setLayoutX((newWidth.doubleValue() - titleTextWidth) / 2 + widthOfRecipeUpToE);
                });

                // Initial centering when scene is first shown
                newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                    if (newWin != null) {
                        // Center titleText
                        titleText.setLayoutX((newScene.getWidth() - titleText.getBoundsInLocal().getWidth()) / 2);

                        // Get the position of the "e" in "Recipe"
                        double titleTextWidth = titleText.getBoundsInLocal().getWidth();
                        String titleTextContent = titleText.getText();
                        double widthOfRecipeUpToE = titleText.getLayoutBounds().getWidth() * (titleTextContent.indexOf("e") + 1) / titleTextContent.length();

                        // Set titlePhrase to start where "e" ends in "Recipe"
                        titlePhrase.setLayoutX((newScene.getWidth() - titleTextWidth) / 2 + widthOfRecipeUpToE);
                    }
                });
            }
        });

    }

    @FXML
    private void handleSignUpButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.", Alert.AlertType.ERROR);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Password Mismatch", "Passwords do not match.", Alert.AlertType.ERROR);
            return;
        }

        // Show success alert
        showAlert("Success", "Account created successfully!", Alert.AlertType.INFORMATION);

        // Open the main application window
        openMainWindow();
    }

    private void openMainWindow() {
        try {
            // Close the current (sign-up) stage
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            stage.close();

            // Load the main scene (hello-view.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/farmingdale/recipegenerator/hello-view.fxml"));
            Scene mainScene = new Scene(loader.load());

            // Get the primary screen's bounds
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            Stage mainStage = new Stage();
            mainStage.setTitle("Main App Window");
            mainStage.setScene(mainScene);

            // Set the window size to match the screen resolution
            mainStage.setX(screenBounds.getMinX());
            mainStage.setY(screenBounds.getMinY());
            mainStage.setWidth(screenBounds.getWidth());
            mainStage.setHeight(screenBounds.getHeight());

            // Optional: make the window maximized (not necessary if you set bounds)
            // mainStage.setMaximized(true);

            mainStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the main window.", Alert.AlertType.ERROR);
        }
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
