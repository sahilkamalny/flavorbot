package edu.farmingdale.recipegenerator;

import com.mysql.cj.xdevapi.JsonArray;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class PreferencesController {


    @FXML
    Button updateButton;
    @FXML
    private ComboBox<String> foodStyleComboBox;
    @FXML
    private ComboBox<String> dietaryPreferencesComboBox;
//    @FXML
//    private Slider spiceLevelSlider;
    @FXML
    private ComboBox<String> mealTypeComboBox;
    @FXML
    private ComboBox<Integer> spiceLevelSlider;

    @FXML
    private TextField ingredientsAvailableField;

    @FXML
    private TableView<Ingredient> ingredientsTable;

    @FXML
    private HBox hBoxholder;

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
        // 1) Parse the prefs JSON as an object, not an array
        String json = SessionManager.getInstance()
                .getCurrentUser()
                .getPreferencesJson();
        JSONObject prefs = new JSONObject(json);

        // 2) Populate your controls
        foodStyleComboBox.getItems().addAll("Italian", "Chinese", "Mexican", "Indian", "American");
        dietaryPreferencesComboBox.getItems().addAll("Vegetarian", "Vegan", "Gluten-Free", "None");
        mealTypeComboBox.getItems().addAll("Breakfast", "Lunch", "Dinner", "Snack");
        spiceLevelSlider.getItems().addAll(1,2,3,4,5,6,7,8,9,10);

//        spiceLevelSlider.setMin(0);
//        spiceLevelSlider.setMax(10);
//        spiceLevelSlider.setShowTickLabels(true);
//        spiceLevelSlider.setShowTickMarks(true);
//        spiceLevelSlider.setMajorTickUnit(1);
//        spiceLevelSlider.setSnapToTicks(true);

        // 3) Read out the saved values
        String savedFoodStyle = prefs.optString("foodStyle", null);
        String savedDietaryPreference = prefs.optString("dietaryPreference", null);
        String savedMealType = prefs.optString("mealType", null);
        int savedSpiceLevel = prefs.optInt("spiceLevel", 5);

        // 4) Apply them to the UI
        if (savedFoodStyle != null) {
            foodStyleComboBox.getSelectionModel().select(savedFoodStyle);
        }
        if (savedDietaryPreference != null) {
            dietaryPreferencesComboBox.getSelectionModel().select(savedDietaryPreference);
        }
        if (savedMealType != null) {
            mealTypeComboBox.getSelectionModel().select(savedMealType);
        }
        spiceLevelSlider.setValue(savedSpiceLevel);

        // 5) (Optional) load background image as before
        Image image = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/images/preferences.png")));

        backgroundImageView.setImage(image);
//        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setFitWidth(anchorPane.getWidth());
        backgroundImageView.setFitHeight(anchorPane.getHeight());

        anchorPane.widthProperty().addListener((obs, o, n) ->
                backgroundImageView.setFitWidth(n.doubleValue()));
        anchorPane.heightProperty().addListener((obs, o, n) ->
                backgroundImageView.setFitHeight(n.doubleValue()));


    }

//    @FXML
//    private void handleAddIngredient() {
//        String ingredient = ingredientsAvailableField.getText().trim();
//        if (!ingredient.isEmpty()) {
//            Label ingredientLabel = new Label(ingredient);
//            ingredientLabel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-padding: 8 12; -fx-background-radius: 10; -fx-text-fill: black;");
//
//            ingredientsGrid.add(ingredientLabel, column, row);
//
//            column++;
//            if (column == 4) { // adjust grid width as needed
//                column = 0;
//                row++;
//            }
//
//            ingredientsAvailableField.clear();
//        }
//    }

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
            Stage stage = (Stage) mealTypeComboBox.getScene().getWindow();
            stage.close();  // Close the login window

            // Load the main scene (your fridge management window)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/farmingdale/recipegenerator/hello-view.fxml"));
            Parent root = loader.load();

            // Get the screen bounds
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();

            // Add the external CSS stylesheet
            Scene scene = new Scene(root, screenWidth * 1, screenHeight * 0.95);
            scene.getStylesheets().add(getClass().getResource("/Styling/frosted-glass.css").toExternalForm());

            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Flavor Bot");
            newStage.show();

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

    //This method will change the text on the updateButton Button if the screen is loaded from the sigUp page
    //If user is logged in it will save "Update" instead of "Save"
    public void setContextFromSignup(boolean fromSignup) {
        if (fromSignup) {
            updateButton.setText("Save Preferences");
        }
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
