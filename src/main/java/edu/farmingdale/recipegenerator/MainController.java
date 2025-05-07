package edu.farmingdale.recipegenerator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static edu.farmingdale.recipegenerator.OpenAI.getDefaultIngredients;

public class MainController {
    @FXML
    public Button Exit;
    @FXML
    public Button logOut;
    @FXML
    private ListView<String> ingredientListView,fridgeView;
    @FXML
    private Button generateButton;
    @FXML
    private Button addIngredientButton;
    @FXML
    private Button preferencesButton,openFridgeButton,showFridgeButton,questionButton;
    @FXML
    private TextField ingredientField;
    @FXML
    private Hyperlink pdfHyperlink;

//    @FXML
//    private TextArea recipeTextArea;

    @FXML
    private TextFlow recipeTextArea;

    @FXML
    private ImageView backgroundImage,fridgeImageView;

    @FXML
    private BorderPane mainPane;
    @FXML
    private StackPane stackPane;

    private AzureDBConnector connector;

    private Image fridgeImg;
    private int fridgeNum;



    @FXML
    public void initialize() {

        hyperlinkTXT();

        //Sets the lighbulb icon for the button
        ImageView buttonImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/light-bulb.png")));
        buttonImageView.setFitWidth(25);
        buttonImageView.setFitHeight(28);
        questionButton.setGraphic(buttonImageView);


        fridgeImageView = new ImageView();
        fridgeImg = new Image(getClass().getResourceAsStream("/images/fridge.png"));

        connector = new AzureDBConnector();

        // Clear any existing items
        ingredientListView.getItems().clear();
        fridgeView.getItems().clear();

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

        setupDragAndDrop();

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
        if (!text.matches("^[a-zA-Z\\s]+$")) {
            showAlert("Invalid Input", "Only letters are allowed in ingredient names.", Alert.AlertType.ERROR);
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
            newStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.png"))));
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
       stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.png"))));


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/farmingdale/recipegenerator/fridge.fxml"));
       Scene fridgeScene = new Scene(loader.load());
       fridgeScene.getStylesheets().add(getClass().getResource("/Styling/fridge.css").toExternalForm());

       stage.setScene(fridgeScene);
       stage.setTitle("Flavor Bot");
       stage.show();

    }

    @FXML
    private void showFridge() throws Exception {
        fridgeView.getItems().clear();

        List<String> commonItems = getDefaultIngredients();

        fridgeView.getItems().addAll(commonItems);


    }

    @FXML
    private void showTutorial(){
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Dasboard Tutorial");

        Image gifImage = new Image(getClass().getResourceAsStream("/images/dashboardHelp.gif"));
        ImageView imageView = new ImageView(gifImage);
        imageView.setFitWidth(800);
        imageView.setFitHeight(600);
//        imageView.setPreserveRatio(true);

        VBox layout = new VBox(imageView);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        popup.setScene(scene);
        popup.setResizable(false);
        popup.show();

    }
    private void setupDragAndDrop() {

        // Drag source set Up
        fridgeView.setOnDragDetected(event -> {
            String selectedItem = fridgeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) return;

            Dragboard db = fridgeView.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString(selectedItem);
            db.setContent(content);

            event.consume();
        });

        // Accept the drag
        ingredientListView.setOnDragOver(event -> {
            if (event.getGestureSource() != ingredientListView &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        // Drop handling
        ingredientListView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String ingredient = db.getString();
                if (!ingredientListView.getItems().contains(ingredient)) {
                    ingredientListView.getItems().add(ingredient);
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void hyperlinkTXT() {
        pdfHyperlink.setOnAction(e -> {
            try {
                // Get the path to the user's Downloads folder
                String userHome = System.getProperty("user.home");
                String downloadsPath = "";

                // Check if we're on Windows or Mac/Linux
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    // Windows "C:/Users/username/Downloads"
                    downloadsPath = userHome + "\\Downloads\\recipe.txt";
                } else {
                    // Mac/Linux
                    downloadsPath = userHome + "/Downloads/recipe.txt";
                }

                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                downloadsPath += timestamp + ".txt";

                try (PrintWriter writer = new PrintWriter(downloadsPath)) {
                    for (Node node : recipeTextArea.getChildren()) {
                        if (node instanceof Text text) {
                            writer.println(text.getText());
                        }
                    }
                    // Show success alert
                    showAlert("TXT Created", "Recipe saved to Downloads as recipe.txt.", Alert.AlertType.INFORMATION);
                }
            } catch (Exception ex) {
                showAlert("Error", "Failed to generate TXT file. " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
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

    @FXML
    public void SignInWindow(ActionEvent actionEvent) {
        try {
            Parent signInRoot = FXMLLoader.load(getClass().getResource("/edu/farmingdale/recipegenerator/login.fxml"));
            Scene signInScene = new Scene(signInRoot);

            // Get current stage
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(signInScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void CloseApplication(ActionEvent actionEvent) {
        System.exit(0);
    }
}
