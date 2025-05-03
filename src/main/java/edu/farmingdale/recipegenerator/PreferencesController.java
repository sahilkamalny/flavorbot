package edu.farmingdale.recipegenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private TableView<Ingredient> ingredientsTable;

    @FXML
    private AnchorPane anchorPane; // The AnchorPane from the FXML
    @FXML
    ImageView backgroundImageView;

    @FXML
    private GridPane ingredientsGrid;

    @FXML
    private TableColumn<Ingredient, String> ingredientNameColumn;
    @FXML
    private TableColumn<Ingredient, String> ingredientQuantityColumn;
    @FXML
    private TableColumn<Ingredient, String> ingredientCategoryColumn;

    private final ObservableList<Ingredient> ingredientData = FXCollections.observableArrayList();

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
        ObservableList<Ingredient> ingredientList = FXCollections.observableArrayList();
        ingredientNameColumn.setCellValueFactory(cellData -> cellData.getValue().ingredientProperty());
        ingredientsTable.setItems(ingredientList);

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

        ingredientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ingredientQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        ingredientCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        ingredientsTable.setItems(ingredientData);
    }


    private int column = 0;
    private int row = 0;

    @FXML
    private void handleAddIngredient() {
        String ingredient = ingredientsAvailableField.getText().trim();
        if (!ingredient.isEmpty()) {
            Label ingredientLabel = new Label(ingredient);
            ingredientLabel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-padding: 8 12; -fx-background-radius: 10; -fx-text-fill: black;");

            ingredientsGrid.add(ingredientLabel, column, row);

            column++;
            if (column == 4) { // adjust grid width as needed
                column = 0;
                row++;
            }

            ingredientsAvailableField.clear();
        }
    }

    @FXML
    private void handleContinueButtonAction() {
        // Retrieve user selections
        String selectedFoodStyle = foodStyleComboBox.getValue();
        String selectedDietaryPreference = dietaryPreferencesComboBox.getValue();
        String selectedMealType = mealTypeComboBox.getValue();
        int spiceLevel = (int) spiceLevelSlider.getValue();

        // Validate selections
        if (selectedFoodStyle == null || selectedDietaryPreference == null || selectedMealType == null) {
            showAlert("Missing Selection", "Please select food style, dietary preference, and meal type.");
            return;
        }

        // Build JSON with only required preferences
        String json = String.format(
                "{\"foodStyle\":\"%s\",\"dietaryPreference\":\"%s\",\"mealType\":\"%s\",\"spiceLevel\":%d}",
                selectedFoodStyle, selectedDietaryPreference, selectedMealType, spiceLevel
        );

        // Update preferences in database
        AzureDBConnector connector = new AzureDBConnector();
        connector.updateUserPreferences(
                SessionManager.getInstance().getCurrentUser().getUserID(), json
        );

        // Open next window
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
