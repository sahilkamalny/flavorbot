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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

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

    @FXML
    private void initialize() {
        session = SessionManager.getInstance();
        connector = new AzureDBConnector();


        UpdateFridge();

        listView.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null) {textField.setText(newSelection);}});

        imageView.setImage(new Image(getClass().getResourceAsStream("/images/fridge.png")));
        ImageView buttonImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/light-bulb.png")));
        buttonImageView.setFitWidth(25);
        buttonImageView.setFitHeight(28);
        questionButton.setGraphic(buttonImageView);

    }
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

    @FXML
    private void questionButton(){

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Help / Info");


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

    public void UpdateFridge(){
        int userId = session.getCurrentUser().getUserID();
        List<String> updatedList = connector.getFridgeItems(userId);
        listView.getItems().setAll(updatedList);

    }



}
