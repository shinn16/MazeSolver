package GUI.Windows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * WarningWindow Class
 * This displays a waring when the user tries to run the program with an unloaded map.
 * @author Patrick Shinn
 * @version 9/15/16
 */
public class WarningWindow {
    private Stage primaryStage;
    private String windowTitle;
    private String warning;

    public WarningWindow(Stage primaryStage, String windowTitle, String warning){
        this.primaryStage = primaryStage;
        this.windowTitle = windowTitle;
        this.warning = warning;
    }

    public void display(){
        // displays the window
        Stage window = new Stage();
        window.setTitle(windowTitle);
        window.initModality(Modality.APPLICATION_MODAL); // means that while this window is open, you can't interact with the main program.

        // labels
        Label warningLabel = new Label(warning);

        // images
        ImageView warningImage = new ImageView(new Image(getClass().getResourceAsStream("/graphics/warning.png")));

        // Building the window
        HBox layout = new HBox(10);
        layout.getChildren().addAll(warningImage, warningLabel);
        layout.setAlignment(Pos.CENTER);

        // Showing the window
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.setHeight(200.00);
        window.setWidth(500.00);
        window.show();
    }
}
