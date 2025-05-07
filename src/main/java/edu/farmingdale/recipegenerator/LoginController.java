package edu.farmingdale.recipegenerator;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Controller for the login screen of the application.
 * Handles user authentication, loading of the main window,
 * and managing the user interface elements (login, sign-up, background image, etc.).
 */
public class LoginController {

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private Text titleText;

    @FXML
    private Text titlePhrase;


    /**
     * Initializes the controller, loading the background image,
     * binding the background image to the scene dimensions,
     * and applying fade-in effects to the title texts.
     */
    @FXML
    private void initialize() {
        try {
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/b6.png")));
            backgroundImageView.setImage(backgroundImage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the background image.", AlertType.ERROR);
        }

        // Dynamically bind image view to scene width and height while preserving aspect ratio
        backgroundImageView.setPreserveRatio(false);
        backgroundImageView.setSmooth(true);
        backgroundImageView.setCache(true);

        backgroundImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImageView.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImageView.fitHeightProperty().bind(newScene.heightProperty());
            }
    });

        // Fade in animations
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

    /**
     * Handles the login button click event. Attempts to authenticate the user
     * and opens the main window if successful. Displays an error message if authentication fails.
     */
    @FXML
    private void handleLoginButtonAction() {
        String username = usernameTextField.getText().trim();
        String password = passwordField.getText().trim();

        // 1) Check for empty fields
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Please enter both username and password.", Alert.AlertType.ERROR);
            return;
        }

        AzureDBConnector connector = new AzureDBConnector();
        boolean authenticated;

        try {
            // 2) Attempt authentication
            authenticated = connector.authenticateAndSetSession(username, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Unexpected Error",
                    "An unexpected error occurred:\n" + ex.getMessage(),
                    Alert.AlertType.ERROR);
            return;
        }

        // 3) Check authentication result
        if (authenticated) {
            // Success: proceed to preferences
            //System.out.println(SessionManager.getInstance().getCurrentUser().getPreferencesJson());
            openMainWindow();
        } else {
            // Failure: inform the user
            showAlert("Authentication Failed",
                    "Invalid username or password. Please try again.",
                    Alert.AlertType.WARNING);
            // Optionally clear password field
            passwordField.clear();
        }
    }

    /**
     * Handles the sign-up button click event. Opens the sign-up window.
     */
    @FXML
    private void handleSignUpButtonAction() {
        // Open the Sign Up window
        openSignUpWindow();
    }

    /**
     * Displays an alert with the given title, message, and alert type.
     *
     * @param title   the title of the alert
     * @param message the content message of the alert
     * @param type    the type of alert (e.g., ERROR, WARNING, etc.)
     */
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Opens the main window (the fridge management window) after a successful login.
     */
    private void openMainWindow() {
        try {


            // Load the main scene (your fridge management window)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/farmingdale/recipegenerator/hello-view.fxml"));
            Parent root = loader.load();

            // Get the screen bounds
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();

            // Add the external CSS stylesheet
            Scene scene = new Scene(root, screenWidth * 1, screenHeight * 0.98);
            scene.getStylesheets().add(getClass().getResource("/Styling/frosted-glass.css").toExternalForm());

            Stage newStage = new Stage();
            newStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.png"))));
            newStage.setScene(scene);
            newStage.setTitle("Flavor Bot");
            newStage.show();
            ((Stage) loginButton.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the main window.", AlertType.ERROR);
        }
    }



    /**
     * Opens the sign-up window.
     */
    private void openSignUpWindow() {
        try {
            // Close the current (login) stage
            Stage currentStage = (Stage) signUpButton.getScene().getWindow();
            currentStage.close();


            // Get screen dimensions
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();

            // Load the sign-up FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/farmingdale/recipegenerator/sign-up.fxml"));
            Scene signUpScene = new Scene(loader.load());

            Stage signUpStage = new Stage();
            signUpStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.png"))));
            signUpStage.setTitle("Flavor Bot");

            // Set to full screen dimensions
            signUpStage.setX(screenBounds.getMinX());
            signUpStage.setY(screenBounds.getMinY());
            signUpStage.setWidth(screenWidth);
            signUpStage.setHeight(screenHeight);

            signUpStage.setScene(signUpScene);
            signUpStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the sign-up window.", AlertType.ERROR);
        }
    }

}
