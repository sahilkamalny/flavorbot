package edu.farmingdale.recipegenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class PreferencesController {

    @FXML
    private ComboBox<String> foodStyleComboBox;
    @FXML
    private ComboBox<String> dietaryPreferencesComboBox;
    @FXML
    private Slider spiceLevelSlider;
    @FXML
    private ComboBox<String> mealTypeComboBox;

    @FXML
    private TextField ingredientsAvailableField;

    @FXML
    private TableView<Ingredient> ingredientsTableView;
    @FXML
    private TableColumn<Ingredient, String> ingredientColumn;
    @FXML
    private AnchorPane anchorPane; // The AnchorPane from the FXML
    @FXML
    ImageView backgroundImageView;

    private ObservableList<Ingredient> ingredientList;

    @FXML
    public void initialize() {
        // Populate combo boxes
        foodStyleComboBox.getItems().addAll("Italian", "Chinese", "Mexican", "Indian", "American");
        dietaryPreferencesComboBox.getItems().addAll("Vegetarian", "Vegan", "Gluten-Free", "None");
        spiceLevelSlider.setMin(0);
        spiceLevelSlider.setMax(10);
        spiceLevelSlider.setValue(5); // Default value
        spiceLevelSlider.setShowTickLabels(true);
        spiceLevelSlider.setShowTickMarks(true);
        spiceLevelSlider.setMajorTickUnit(1);
        spiceLevelSlider.setSnapToTicks(true);
        mealTypeComboBox.getItems().addAll("Breakfast", "Lunch", "Dinner", "Snack");

        // Initialize TableView
        ingredientList = FXCollections.observableArrayList();
        ingredientColumn.setCellValueFactory(cellData -> cellData.getValue().ingredientProperty());
        ingredientsTableView.setItems(ingredientList);

        // Set the background image and make it resize proportionally
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/b4.jpg")));

        backgroundImageView.setImage(image);

        // Make the image resize with the window
        backgroundImageView.setPreserveRatio(true); // Maintain aspect ratio
        backgroundImageView.setFitWidth(anchorPane.getWidth());
        backgroundImageView.setFitHeight(anchorPane.getHeight());

        // Bind the ImageView width and height to the AnchorPane's size
        anchorPane.widthProperty().addListener((obs, oldVal, newVal) ->
                backgroundImageView.setFitWidth(newVal.doubleValue()));

        anchorPane.heightProperty().addListener((obs, oldVal, newVal) ->
                backgroundImageView.setFitHeight(newVal.doubleValue()));
    }


    @FXML
    private void handleAddIngredient() {
        String input = ingredientsAvailableField.getText().trim();
        if (!input.isEmpty()) {
            ingredientList.add(new Ingredient(input));
            ingredientsAvailableField.clear();
        }
    }

    @FXML
    private void handleContinueButtonAction() {
        String selectedFoodStyle = foodStyleComboBox.getValue();
        String selectedDietaryPreference = dietaryPreferencesComboBox.getValue();
        String selectedMealType = mealTypeComboBox.getValue();


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

    // Inner Ingredient class
    public static class Ingredient {
        private final StringProperty ingredient;

        public Ingredient(String ingredient) {
            this.ingredient = new SimpleStringProperty(ingredient);
        }

        public String getIngredient() {
            return ingredient.get();
        }

        public void setIngredient(String ingredient) {
            this.ingredient.set(ingredient);
        }

        public StringProperty ingredientProperty() {
            return ingredient;
        }
    }
}
