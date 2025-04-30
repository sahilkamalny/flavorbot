package edu.farmingdale.recipegenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class PreferencesController {

    @FXML
    private ComboBox<String> foodStyleComboBox;

    @FXML
    private TextField ingredientField;

    @FXML
    private TextField quantityField;

    @FXML
    private TableView<Ingredient> ingredientsTable;

    @FXML
    private TableColumn<Ingredient, String> ingredientColumn;

    @FXML
    private TableColumn<Ingredient, String> quantityColumn;

    private final ObservableList<Ingredient> ingredientList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        ingredientColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        ingredientsTable.setItems(ingredientList);
    }

    @FXML
    private void handleAddIngredient() {
        String name = ingredientField.getText().trim();
        String quantity = quantityField.getText().trim();

        if (name.isEmpty() || quantity.isEmpty()) {
            showAlert("Input Error", "Please enter both ingredient name and quantity.");
            return;
        }

        ingredientList.add(new Ingredient(name, quantity));
        ingredientField.clear();
        quantityField.clear();
    }

    @FXML
    private void handleContinueButtonAction() {
        String selectedFoodStyle = foodStyleComboBox.getValue();

        if (selectedFoodStyle == null || selectedFoodStyle.isEmpty() || ingredientList.isEmpty()) {
            showAlert("Error", "Please select a food style and add at least one ingredient.");
            return;
        }

        // Save preferences if needed

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

    // Simple POJO for ingredient
    public static class Ingredient {
        private final String name;
        private final String quantity;

        public Ingredient(String name, String quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public String getQuantity() {
            return quantity;
        }
    }
}
