package edu.farmingdale.recipegenerator;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;


/**
 * Controller class for managing the user's fridge items.
 * Provides functionality to add, delete, and modify ingredients,
 * as well as drag-and-drop and help tutorial features.
 */
public class FridgeController {

    @FXML
    private Button modifyButton,deleteButton,addButton,questionButton;
    @FXML
    private ImageView imageView;
    @FXML
    private Text welcomeLabel;
    @FXML
    private TextField textField;
    @FXML
    private ListView<String> listView;

    private AzureDBConnector connector;
    private SessionManager session;

    /**
     * Initializes the controller after the FXML elements have been loaded.
     * Sets up drag-and-drop, populates the fridge list, and configures button graphics.
     */
    @FXML
    private void initialize() {
        dragAndDrop();

        session = SessionManager.getInstance();
        connector = new AzureDBConnector();

        UpdateFridge();

        listView.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null) {textField.setText(newSelection);}});

//        imageView.setImage(new Image(getClass().getResourceAsStream("/images/fridge.png")));
        ImageView buttonImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/light-bulb.png")));
        buttonImageView.setFitWidth(25);
        buttonImageView.setFitHeight(28);
        questionButton.setGraphic(buttonImageView);

    }
    /**
     * Adds a new item to the fridge list and updates the database.
     */
    @FXML
    private void addButton(){

        String item = textField.getText();
        if(item.isEmpty()){
            return;
        }
        int userId = session.getCurrentUser().getUserID(); //gets userID
        connector.addFridgeItem(userId, item); // add item to the database

        listView.getItems().add(item);
        textField.clear();

    }

    /**
     * Deletes the selected item from the fridge list and database.
     */
    @FXML
    private void deleteButton(){

        String selectedItem = listView.getSelectionModel().getSelectedItem();//item selected in the tableview

        if (selectedItem != null) {

            int userId = session.getCurrentUser().getUserID(); //gets userID
            connector.deleteFridgeItemByName(userId,selectedItem);  // Delete the item by name from the database

            // Remove item from ListView
            listView.getItems().remove(listView.getSelectionModel().getSelectedIndex());
        }
    }

    /**
     * Modifies the selected item in the fridge list and updates the database.
     */
    @FXML
    private void modifyButton() {
        String newItem = textField.getText();

        int selectedIndex = listView.getSelectionModel().getSelectedIndex();


        if (selectedIndex >= 0 && !newItem.isEmpty()) {
            // Get the old item (name)
            String oldItem = listView.getSelectionModel().getSelectedItem();

            // Update item in the database
            int userId = session.getCurrentUser().getUserID(); // gets user ID
            connector.updateFridgeItemByName(userId, oldItem, newItem); // now includes userId

            // Update item in ListView
            listView.getItems().set(selectedIndex, newItem);
        }


    }

    /**
     * Displays a help popup with a tutorial GIF for using the fridge.
     */
    @FXML
    private void questionButton(){

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Fridge Tutorial");


        Image gifImage = new Image(getClass().getResourceAsStream("/images/help.gif"));
        ImageView imageView = new ImageView(gifImage);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);


        VBox layout = new VBox(imageView);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        popup.setScene(scene);
        popup.setResizable(false);
        popup.show();

    }

    /**
     * Refreshes the fridge list with items from the database for the current user.
     */
    public void UpdateFridge(){
        int userId = session.getCurrentUser().getUserID();
        List<String> updatedList = connector.getFridgeItems(userId);
        listView.getItems().setAll(updatedList);

    }

    /**
     * Enables drag-and-drop functionality for the fridge item list.
     */
    public void dragAndDrop(){
        listView.setOnDragDetected(event -> {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) return;

            Dragboard db = listView.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString(selectedItem);
            db.setContent(content);

            event.consume();
        });
    }



}
