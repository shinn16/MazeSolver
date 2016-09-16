package GUI.Windows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * AboutWindow
 * This is a customizable about window.
 * @author Patrick Shinn
 * @version 9/2/16
 */

// // TODO: 9/5/16 FORMAT THIS SHIT TO MAKE IT LOOK NICE 
public class AboutWindow {
    private String windowName ,
            developer,
            version,
            appName,
            website,
            aboutApp;
    public AboutWindow(String windowName, String appName,
                       String version, String aboutApp,
                       String developer, String website){
        this.windowName = windowName;
        this.appName = appName;
        this.version = version;
        this.aboutApp = aboutApp;
        this.developer = developer;
        this.website = website;
    }
    public void display(){
        // Stage setup
        Stage window = new Stage();
        window.setTitle(windowName);
        window.initModality(Modality.APPLICATION_MODAL); // means that while this window is open, you can't interact with the main program.

        
        // Labels
        Label appNameLabel = new Label(appName);
        Label websiteLabel = new Label("Website: " + website);
        Label aboutAppLabel = new Label(aboutApp);
        Label developerLabel = new Label("Developers: " + developer);
        Label versionLabel = new Label("Version: " + version);

        // Images
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/graphics/AppIcon.png")));
        
        // Layout type
        VBox layout = new VBox(10);
        layout.getChildren().addAll(imageView, appNameLabel, developerLabel, versionLabel, aboutAppLabel, websiteLabel);
        layout.setAlignment(Pos.CENTER);

        // Building scene and displaying.
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.setHeight(400);
        window.setWidth(500);
        window.show();
    }
}
