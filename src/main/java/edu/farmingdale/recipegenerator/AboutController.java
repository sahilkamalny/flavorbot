package edu.farmingdale.recipegenerator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class AboutController {

    @FXML
    public StackPane stackPane;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private Label appDetails;

    @FXML
    public void initialize() {
        // Set the background image
        Image image = new Image(getClass().getResourceAsStream("/images/b6.png"));
        backgroundImage.setImage(image);

        // Set the text for the application details
        appDetails.setText("The Recipe Generator App is a modern, AI-powered desktop application built using JavaFX and integrates with the OpenAI API to help users generate creative and personalized recipes based on their preferences and available ingredients.");
    }
}
