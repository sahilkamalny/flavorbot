package edu.farmingdale.recipegenerator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private TextArea recipeTextArea;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private BorderPane mainPane;

    @FXML
    private StackPane stackPane;

    private AzureDBConnector connector;


    @FXML
    public void initialize() {
        connector = new AzureDBConnector();
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

        String preferences = connector.getUserPreferences(SessionManager.getInstance().getCurrentUser().getUserID());

        StringBuilder ingredientsList = new StringBuilder();
        for (String ingredient : ingredientListView.getItems()) {
            ingredientsList.append(ingredient).append(", ");
        }

        if (ingredientsList.length() > 0) {
            ingredientsList.setLength(ingredientsList.length() - 2); // Remove last comma
        }

        // Create the prompt for the OpenAI API (you can adjust this format as needed)
        String prompt = "You are a professional chef. Using the following ingredients: "
                + ingredientsList.toString() + ", and based on the user's preferences: "
                + preferences + ", please generate a recipe. The recipe should include:\n"
                + "The name of the dish (If it is possible)\n"
                + "1. A list of ingredients.\n"
                + "2. Clear, step-by-step instructions on how to prepare the recipe, with specific actions for each step.\n"
                + "3. Cooking tips or suggestions where necessary.\n"
                + "4. Serving suggestions to make the dish even better.\n"
                + "Make sure to format the recipe with each step clearly numbered and include any necessary cooking times.";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Generate Recipe");
        alert.setHeaderText(null);
        alert.setContentText("Generating recipe.....");
        alert.showAndWait();

        // Call OpenAI's API to get the recipe
        String recipe = OpenAI.getTextResponse(prompt, preferences);

        recipeTextArea.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: black;");

        recipeTextArea.setText(recipe);

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
            // Get the screen bounds
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();

            // window size to a percentage of screen size
            double windowWidth = screenWidth * 1;
            double windowHeight = screenHeight * 0.98;

            Scene scene = new Scene(loader.load(),windowWidth,windowHeight);
            Stage prefStage = new Stage();
            prefStage.setTitle("User Preferences");
            prefStage.setScene(scene);
//            prefStage.setMaximized(true);
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
