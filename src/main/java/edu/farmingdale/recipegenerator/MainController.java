package edu.farmingdale.recipegenerator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainController {
    @FXML
    private ListView<String> ingredientListView;
    @FXML
    private Button generateButton;
    @FXML
    private Button addIngredientButton;
    @FXML
    private Button preferencesButton;
    @FXML
    private TextField ingredientField;
    @FXML
    private ImageView backimageView;
    @FXML
    private ImageView backgroundImage;

    @FXML
    private BorderPane mainPane;

    @FXML
    private StackPane stackPane;

    @FXML
    public void initialize() {
        // Clear any existing items
        ingredientListView.getItems().clear();

        try {
            // Load the background image
            Image image = new Image(getClass().getResourceAsStream("/images/b6.png"));
            backgroundImage.setImage(image);
            backgroundImage.setPreserveRatio(true);
            backgroundImage.setSmooth(true);


            backgroundImage.fitWidthProperty().bind(stackPane.widthProperty());
            backgroundImage.fitHeightProperty().bind(stackPane.heightProperty());

        } catch (Exception e) {
            System.out.println("Background image load failed: " + e.getMessage());
        }

        // Wire up button handlers
        addIngredientButton.setOnAction(e -> handleAddIngredient());
        preferencesButton.setOnAction(e -> openPreferencesWindow());
        generateButton.setOnAction(e -> {
            try {
                handleGenerateRecipe();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * Reads the text field and adds the ingredient to the list.
     */
    @FXML
    private void handleAddIngredient() {
        String text = ingredientField.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        ingredientListView.getItems().add(text);
        ingredientField.clear();
    }

    /**
     * Placeholder: handle recipe generation logic or display a simple alert.
     */
    @FXML
    private void handleGenerateRecipe() throws Exception {
        OpenAI.getTextResponse("");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Generate Recipe");
        alert.setHeaderText(null);
        alert.setContentText("Generating recipe for: " + ingredientListView.getItems());
        alert.showAndWait();
    }

    /**
     * Opens the user preferences window, closing the current main window.
     */
    @FXML
    private void openPreferencesWindow() {
        try {
            Stage currentStage = (Stage) preferencesButton.getScene().getWindow();

            currentStage.close();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/farmingdale/recipegenerator/preferences.fxml")
            );
            Parent root = loader.load();

            // Get the screen bounds
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();

            // Add the external CSS stylesheet
            Scene scene = new Scene(root, screenWidth * 1, screenHeight * 0.98);
            scene.getStylesheets().add(getClass().getResource("/Styling/preference.css").toExternalForm());

            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Flavor Bot");
            newStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load preferences window.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Generic alert helper.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
