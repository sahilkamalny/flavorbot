package edu.farmingdale.recipegenerator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    private ListView<String> ingredientListView,fridgeView;
    @FXML
    private Button generateButton;
    @FXML
    private Button addIngredientButton;
    @FXML
    private Button preferencesButton,openFridgeButton,showFridgeButton;
    @FXML
    private TextField ingredientField;

//    @FXML
//    private TextArea recipeTextArea;

    @FXML
    private TextFlow recipeTextArea;

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


//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("FlavorBot is thinking a recipe");
//        alert.setHeaderText(null);
//        alert.setContentText("Generating recipe.....");
//        alert.show();

        // Call OpenAI's API to get the recipe
        String recipe = OpenAI.getTextResponse(prompt, preferences);

//        recipeTextArea.setText(recipe);

        recipeTextArea.getChildren().clear();
        Text recipeText = new Text(recipe); // 'recipe' is your generated text

        recipeTextArea.getChildren().add(recipeText);

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
    @FXML
    private void openFridge() throws IOException {
       Stage stage = new Stage();

       FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/farmingdale/recipegenerator/fridge.fxml"));
       Scene fridgeScene = new Scene(loader.load());
       fridgeScene.getStylesheets().add(getClass().getResource("/Styling/fridge.css").toExternalForm());

       stage.setScene(fridgeScene);
       stage.setTitle("Fridge");
       stage.show();

    }

    @FXML
    private void showFridge() {


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
