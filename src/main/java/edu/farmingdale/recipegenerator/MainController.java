package edu.farmingdale.recipegenerator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
    public void initialize() {
        // Clear any existing items
        ingredientListView.getItems().clear();

        // Wire up button handlers (in case not set in FXML)
        addIngredientButton.setOnAction(e -> handleAddIngredient());
        preferencesButton.setOnAction(e -> openPreferencesWindow());
        generateButton.setOnAction(e -> handleGenerateRecipe());
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
    private void handleGenerateRecipe() {
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
            Scene scene = new Scene(loader.load());
            Stage prefStage = new Stage();
            prefStage.setTitle("User Preferences");
            prefStage.setScene(scene);
            prefStage.setMaximized(true);
            prefStage.show();
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
