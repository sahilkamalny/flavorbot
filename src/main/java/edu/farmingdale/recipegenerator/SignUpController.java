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

import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpController {

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Za-z0-9_]{3,25}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PASSWORD_PATTERN =
            // At least 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$");

    @FXML
    public TextField emailTextField;

    @FXML
    public Button loginButton;

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
        String email = emailTextField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // 1) Basic non-empty check
        if (username.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Input Error",
                    "Please fill in all fields.",
                    Alert.AlertType.ERROR);
            return;
        }

        // 2) Username format
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            showAlert("Invalid Username",
                    "Username must be 3–25 characters and contain only letters, digits, or underscores.",
                    Alert.AlertType.ERROR);
            return;
        }

        // 3) Email format
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showAlert("Invalid Email",
                    "Please enter a valid email address.",
                    Alert.AlertType.ERROR);
            return;
        }

        // 4) Password strength
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showAlert("Weak Password",
                    "Password must be at least 8 characters long and include uppercase, lowercase, a digit, and a special character.",
                    Alert.AlertType.ERROR);
            return;
        }

        // 5) Password confirmation
        if (!password.equals(confirmPassword)) {
            showAlert("Password Mismatch",
                    "Passwords do not match.",
                    Alert.AlertType.ERROR);
            return;
        }

        // 6) Attempt to insert into the database
        AzureDBConnector connector = new AzureDBConnector();
        if(connector.usernameExists(username)) {
            showAlert("Error",
                    "Username already exists.",
                    Alert.AlertType.ERROR);
            usernameField.clear();
            return;
        }
        try {
            connector.insertUser(username, email, password);
            showAlert("Success",
                    "Account created successfully!",
                    Alert.AlertType.INFORMATION);
            connector.authenticateAndSetSession(username, password);
            openPreferencesWindow();
        } catch (Exception ex) {
            ex.printStackTrace();    // optional logging
            showAlert("Unexpected Error",
                    "An unexpected error occurred: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void openMainWindow() {
        try {
            // Close the current (sign-up) stage
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            stage.close();

            // Load the main scene (hello-view.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/farmingdale/recipegenerator/login.fxml"));
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

    private void openPreferencesWindow() {
        try {
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/farmingdale/recipegenerator/preferences.fxml"));
            Scene preferencesScene = new Scene(loader.load());

            Stage prefStage = new Stage();
            prefStage.setTitle("User Preferences");

            // Let JavaFX handle fullscreen properly
            prefStage.setScene(preferencesScene);
            prefStage.setMaximized(true); // This keeps better proportions
            prefStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load preferences window.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void handleAlreadyHaveAccountButtonAction() {
        openMainWindow();
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
