package edu.farmingdale.recipegenerator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class PreferencesController {

    @FXML
    private ComboBox<String> foodStyleComboBox;

    @FXML
    private TextArea ingredientsTextArea;

    @FXML
    private void handleContinueButtonAction() {
        String selectedFoodStyle = foodStyleComboBox.getValue();
        String ingredients = ingredientsTextArea.getText().trim();

        if (selectedFoodStyle == null || selectedFoodStyle.isEmpty() || ingredients.isEmpty()) {
            showAlert("Error", "Please select a food style and enter your ingredients.");
            return;
        }

        // You can save user preferences here if you want (e.g., in a model)

        openMainWindow();
    }

    private void openMainWindow() {
        try {
            Stage currentStage = (Stage) foodStyleComboBox.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/farmingdale/recipegenerator/hello-view.fxml"));
            Stage mainStage = new Stage();
            mainStage.setScene(new Scene(loader.load()));
            mainStage.setTitle("Fridge Management");
            mainStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the main window.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
