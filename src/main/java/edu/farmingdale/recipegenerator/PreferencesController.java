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
import javafx.scene.effect.GaussianBlur;
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

/**
 * Controller class for managing the user's preferences in the Recipe Generator application.
 * This class allows users to set and save their food preferences, dietary restrictions,
 * and other cooking preferences, such as skill level, meal type, and spice level.
 */
public class PreferencesController {


    @FXML
    Button updateButton;
    @FXML
    private ComboBox<String> foodStyleComboBox;
    @FXML
    private ComboBox<String> dietaryPreferencesComboBox;
    @FXML
    private ComboBox<String> mealTypeComboBox;
    @FXML
    private TextArea allergiesTextArea,notesTextArea;

    @FXML
    private ComboBox<String> skillsComboBox,portionSizeComboBox,cokingTimeComboBox,flavorComboBox,messComboBox;
    @FXML
    private Label dietaryPreferencesLabel,mealTypeLabel,spiceLevelLabel,skillsLabel,foodStyleLabel,allergiesLabel,portionSizeLabel,numServingLabel,cokingTimeLabel,flavorLabel,messLabel,notesLabel;
    @FXML
    private ComboBox<Integer> spiceLevelSlider,numServingComboBox;

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


    /**
     * Initializes the UI elements with the user's saved preferences and default options.
     * This method is called automatically when the scene is loaded.
     */
    @FXML
    public void initialize() {
        // 1) Parse the prefs JSON
        String json = SessionManager.getInstance()
                .getCurrentUser()
                .getPreferencesJson();
        JSONObject prefs = new JSONObject(json);

        // 2) Populate your controls
        foodStyleComboBox.getItems().addAll("None","Italian","Chinese","Mexican","Indian","American","Japanese");
        dietaryPreferencesComboBox.getItems().addAll("None","Vegetarian","Vegan","Gluten-Free","Dairy-Free");
        mealTypeComboBox.getItems().addAll("None","Breakfast","Lunch","Dinner","Snack");
        // Assuming spiceLevelSlider is actually a ComboBox<Integer>—otherwise use a Slider.setValue(...)
        spiceLevelSlider.getItems().addAll(0,1,2,3,4,5,6,7,8,9,10);
        skillsComboBox.getItems().addAll("None","Beginner","Intermediate","Advanced");
        portionSizeComboBox.getItems().addAll("None","Small","Medium","Large");
        numServingComboBox.getItems().addAll(1,2,3,4,5,6,7,8,9,10);
        cokingTimeComboBox.getItems().addAll("None","15min","30min","1hour","2hours");
        flavorComboBox.getItems().addAll("None","sweet","salty","sour","bitter","umami");
        messComboBox.getItems().addAll("None","Minimal","Medium","Doesn’t matter");

        // 3) Read out the saved values (with defaults)
        String savedFoodStyle         = prefs.optString("foodStyle",        "None");
        String savedDietaryPreference = prefs.optString("dietaryPreference","None");
        String savedMealType          = prefs.optString("mealType",         "None");
        int    savedSpiceLevel        = prefs.optInt   ("spiceLevel",        0);
        String savedCookingSkill      = prefs.optString("cookingSkill",     "None");
        String savedPortionSize       = prefs.optString("portionSize",      "None");
        int    savedNumberOfServings  = prefs.optInt   ("numberOfServings",  1);
        String savedCookingTime       = prefs.optString("cookingTime",      "None");
        String savedFlavorProfile     = prefs.optString("flavorProfile",    "None");
        String savedCleanupEffort     = prefs.optString("cleanupEffort",    "None");
        String savedAllergies         = prefs.optString("allergies",        "");
        String savedAdditionalNotes   = prefs.optString("additionalNotes",  "");

        // 4) Apply them to the UI
        foodStyleComboBox.getSelectionModel().select(savedFoodStyle);
        dietaryPreferencesComboBox.getSelectionModel().select(savedDietaryPreference);
        mealTypeComboBox.getSelectionModel().select(savedMealType);
        spiceLevelSlider.getSelectionModel().select(Integer.valueOf(savedSpiceLevel));
        skillsComboBox.getSelectionModel().select(savedCookingSkill);
        portionSizeComboBox.getSelectionModel().select(savedPortionSize);
        numServingComboBox.getSelectionModel().select(Integer.valueOf(savedNumberOfServings));
        cokingTimeComboBox.getSelectionModel().select(savedCookingTime);
        flavorComboBox.getSelectionModel().select(savedFlavorProfile);
        messComboBox.getSelectionModel().select(savedCleanupEffort);

        allergiesTextArea.setText(savedAllergies);
        notesTextArea.setText(savedAdditionalNotes);

        try {
            Image img = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/images/b6.png")));
            backgroundImageView.setImage(img);

            // Add a slight blur effect to the background
            GaussianBlur blur = new GaussianBlur(2);
            backgroundImageView.setEffect(blur);

            // Set up background image properties
            backgroundImageView.setPreserveRatio(false);
            backgroundImageView.setSmooth(true);
            backgroundImageView.setOpacity(0.9);

            // Bind the image size to the anchor pane size
            backgroundImageView.fitWidthProperty().bind(anchorPane.widthProperty());
            backgroundImageView.fitHeightProperty().bind(anchorPane.heightProperty());

            // Set the anchor constraints
            AnchorPane.setTopAnchor(backgroundImageView, 0.0);
            AnchorPane.setBottomAnchor(backgroundImageView, 0.0);
            AnchorPane.setLeftAnchor(backgroundImageView, 0.0);
            AnchorPane.setRightAnchor(backgroundImageView, 0.0);

            // Make sure the background image is behind other elements
            backgroundImageView.toBack();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the background image.");
        }
    }

    /**
     * Handles the action of the "Continue" button.
     * This method retrieves the user's selections and updates their preferences in the database.
     * After the preferences are saved, it opens the main window for the Recipe Generator application.
     */
    @FXML
    private void handleContinueButtonAction() {
        // Retrieve user selections, defaulting to empty string or safe value if null
        String selectedFoodStyle = foodStyleComboBox.getValue() != null ? foodStyleComboBox.getValue() : "";
        String selectedDietaryPreference = dietaryPreferencesComboBox.getValue() != null ? dietaryPreferencesComboBox.getValue() : "";
        String selectedMealType = mealTypeComboBox.getValue() != null ? mealTypeComboBox.getValue() : "";
        int spiceLevel = spiceLevelSlider.getValue() != null ? spiceLevelSlider.getValue() : 0;
        String selectedSkills = skillsComboBox.getValue() != null ? skillsComboBox.getValue() : "";
        String portionSelected = portionSizeComboBox.getValue() != null ? portionSizeComboBox.getValue() : "";
        String cookingTimeSelected = cokingTimeComboBox.getValue() != null ? cokingTimeComboBox.getValue() : "";
        String flavorSelected = flavorComboBox.getValue() != null ? flavorComboBox.getValue() : "";
        String messSelected = messComboBox.getValue() != null ? messComboBox.getValue() : "";
        int numberOfServingsSelected = numServingComboBox.getValue() != null ? numServingComboBox.getValue() : 1;
        String allergiesSelected = allergiesTextArea.getText() != null ? allergiesTextArea.getText() : "";
        String notesSelected = notesTextArea.getText() != null ? notesTextArea.getText() : "";

         //Validate selections
        if (selectedFoodStyle == null || selectedDietaryPreference == null || selectedMealType == null) {
            showAlert("Missing Selection", "Please select food style, dietary preference, and meal type.");
            return;
        }

        // Build full JSON with all preferences
        String json = String.format(
                "{" +
                        "\"foodStyle\":\"%s\"," +
                        "\"dietaryPreference\":\"%s\"," +
                        "\"mealType\":\"%s\"," +
                        "\"spiceLevel\":%d," +
                        "\"cookingSkill\":\"%s\"," +
                        "\"portionSize\":\"%s\"," +
                        "\"cookingTime\":\"%s\"," +
                        "\"flavorProfile\":\"%s\"," +
                        "\"cleanupEffort\":\"%s\"," +
                        "\"numberOfServings\":%d," +
                        "\"allergies\":\"%s\"," +
                        "\"additionalNotes\":\"%s\"" +
                        "}",
                selectedFoodStyle, selectedDietaryPreference, selectedMealType, spiceLevel,
                selectedSkills, portionSelected, cookingTimeSelected, flavorSelected,
                messSelected, numberOfServingsSelected, allergiesSelected, notesSelected
        );

        // Update preferences in database
        AzureDBConnector connector = new AzureDBConnector();
        connector.updateUserPreferences(
                SessionManager.getInstance().getCurrentUser().getUserID(), json
        );

        // Open next window
        openMainWindow();
    }

    /**
     * Opens the main window of the Recipe Generator application.
     * This method loads the FXML for the main window, applies an external CSS, and shows the window.
     */
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
            Scene scene = new Scene(root, screenWidth * 1, screenHeight * 0.98);
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
    /**
     * Displays an alert with the specified title and message.
     *
     * @param title The title of the alert.
     * @param message The message to be displayed.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Represents an ingredient in the recipe generator.
     * This class is used for binding data to the ingredients table in the UI.
     */
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
