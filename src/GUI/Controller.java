package GUI;

import GUI.Maze.MapDrawer;
import GUI.Windows.AboutWindow;
import GUI.Windows.LegendWindow;
import GUI.Windows.WarningWindow;
import MazeLogic.MazeSolver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @author Patrick Shinn
 * @author Brandon Duke
 * @author Claire Wallace
 * @version Alpha 0.5
 *
 * This is the controller class for the GUI.fxml. This is the code for the fx Thread.
 */
public class Controller{
    // data structures, counters, booleans, all that good stuff.
    private char[][][] masterMaze; // the map as a 3d array for coordinate purposes.
    private boolean run = false;
    private boolean reRun = false; // used to determine if we need to load a fresh copy of a map.
    private int currentLevel = 0; // used for changing the level that is currently displayed.

    // Theme changing stuff, just for fun :)
    private String mapTheme = "Default";
    private String theme = "Default";


    //GUI elements, can be called by using @FXML then defining an object with the same name as the FXid.
    //--------------------------------------------------------------------------------
    private Stage primaryStage; // the primary stage of the GUI, obtained through setStage method.
    private Scene primaryScene;

    //Buttons
    @FXML private Button lvlUp;
    @FXML private Button lvlDown;
    @FXML private Button start;
    @FXML private Button load;

    // Labels
    @FXML private Label statusLbl;

    // Slider
    @FXML private Slider slider;

    // Map GUI Stuff
    @FXML private Canvas canvas;
    @FXML private Pane spritePane;

    // custom class used for drawing and displaying the map.
    private MapDrawer drawer;

    // used to extract the primary stage from the Main Class.
    void setStage(Stage stage, Scene scene){
        this.primaryStage = stage;
        this.primaryScene = scene;
    }

    // Load method, loads the maps into a 3d array.
    public void load() {
        if (reRun) spritePane.getChildren().remove(1); // if we ran, clear the screen of the sprite.
        FileChooser chooser = new FileChooser();
        Scanner scanner;
        char[][] maze = new char[0][0];
        char[][][] masterMazeHolder = {maze};
        int mazeCounter = 0;
        int masterCounter = 0;

        chooser.setTitle("Open a Map");
        // sets the extension filter types
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Map Files", "*.map") // custom extension, just for fun.
        );

        File map = chooser.showOpenDialog(primaryStage);// opens file chooser box to main stage.
        statusLbl.setText("Status: Loading " + map.getName() + "...");

        masterMaze = new char[0][0][0]; // ensures that the previous maze has been dumped.

        try { // builds the array from the file
            scanner = new Scanner(map);
            

            while (scanner.hasNextLine()){ // so while we still have lines to read
                String line = scanner.nextLine();
                if (line.contains("--")){ // if we have reached the end of a maze
                    masterMazeHolder = Arrays.copyOf(masterMazeHolder, masterCounter + 1); // expand the masterMazeHolder array
                    masterMazeHolder[masterCounter] = maze; // insert maze
                    maze = Arrays.copyOf(maze, 0); // dump maze
                    masterCounter ++; // increase masterCounter
                    mazeCounter = 0; //reset mazeCounter for next run.


                }else{ // so if we are in a level of the maze
                    char[] row = line.toCharArray(); // split each line of the maze into an array.
                    maze = Arrays.copyOf(maze, mazeCounter + 1); // expand the maze array
                    maze[mazeCounter] = row; // insert into the maze array
                    mazeCounter ++; // increase the counter.

                }
            }
            masterMazeHolder = Arrays.copyOf(masterMazeHolder, masterCounter + 1); // expand the masterMazeHolder array one last time
            masterMazeHolder[masterCounter] = maze; // get the last maze.
        }catch (FileNotFoundException e){
            //congrats, this doesnt matter... simply appeasing the compiler.
        }

        this.masterMaze = masterMazeHolder; //makes the newly made 3D array available to other methods.

        // draws the newly loaded map.
        drawer = new MapDrawer(canvas, masterMaze, mapTheme);
        drawer.displayLevel(0);

        // updates the gui buttons
        currentLevel = 0;
        lvlDown.setDisable(true);
        lvlUp.setDisable(false);

        // Status updates
        statusLbl.setText("Loaded " + map.getName() + ".");
        run = true; // we loaded a file, so now we can run through the maze.
        start.setDisable(false);
        reRun = false;
    }

    // Starts the search algorithm.
    public void start() {

        if (!run) {
            new WarningWindow(primaryStage, "Nothing Loaded", "There is nothing loaded so there is nothing to run!", theme).display();
        }else {
            if (reRun) { // id we need to load a fresh map for a re-run, do it.
                drawer = new MapDrawer(canvas, masterMaze, mapTheme);
                drawer.displayLevel(0);
                spritePane.getChildren().remove(1);
            }
            reRun = true; // lets the controller know that the next run of this map is a re-run.
            //solve the maze.
            statusLbl.setText("Running maze...");
            MazeSolver mazeSolver = new MazeSolver(masterMaze, slider, canvas, drawer, statusLbl, mapTheme, spritePane);

            // disables buttons so the user cant screw stuff up.
            start.setDisable(true);
            load.setDisable(true);
            lvlDown.setDisable(true);
            lvlUp.setDisable(true);

            Thread one = new Thread() {
                public void run() { // logic thread!
                    currentLevel = mazeSolver.getCurrentLocation()[0];
                    drawer.displayLevel(mazeSolver.getCurrentLocation()[0]); // displays the location of start on the map.

                    // start solving
                    synchronized (mazeSolver) {  // syncronize the solver in hopes that this will end the level breakage
                        mazeSolver.startExploration();
                        currentLevel = mazeSolver.getCurrentLocation()[0];
                    }

                    // updates the buttons for new usage, this runs in the fx thread.
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() { // re enable all the buttons and update labels!
                            String solved;
                            if (mazeSolver.isSolved()) solved = "Maze solvable, finish at: " + Arrays.toString(mazeSolver.getCurrentLocation());
                            else solved = "Maze unsolvable";
                            statusLbl.setText("Map Status: " + solved + " | Current floor " + mazeSolver.getCurrentLocation()[0] +
                                    " | Moves made: " + mazeSolver.getMovesMade());
                            if (currentLevel == 0) {
                                lvlDown.setDisable(true);
                                lvlUp.setDisable(false);
                            } else if (currentLevel == masterMaze.length - 1) {
                                lvlUp.setDisable(true);
                                lvlDown.setDisable(false);
                            } else {
                                lvlUp.setDisable(false);
                                lvlDown.setDisable(false);
                            }
                            start.setDisable(false);
                            load.setDisable(false);
                        }
                    });

                }
            };
            one.start(); // start the logic thread.
        }
    }

    //terminated program
    public void exit(){
        System.exit(1);
    }

    //clears the map from the gui. Has a keyboard shortcut of ctrl + shift + c
    public void clearScreen(){
        // code to clear GUI
        if (!run){
            WarningWindow warning = new WarningWindow(primaryStage, "Unable to Clear",
                    "There is nothing loaded, so there is nothing to clear.", theme);
            warning.display();
        }else{
            drawer.clearMap();
            statusLbl.setText("Status: Nothing Loaded.");
            masterMaze = new char[0][0][0];
            spritePane.getChildren().remove(1);
            run = false;
            reRun = false;
            lvlDown.setDisable(true);
            lvlUp.setDisable(true);
            start.setDisable(true);
        }
    }

    // displays the next level down
    public void displayLevelDown(){
        currentLevel --;
        drawer.displayLevel(currentLevel);
        lvlUp.setDisable(false);
        if (currentLevel == 0)lvlDown.setDisable(true); // if we are at the bottom, make it impossible to go lower.
    }

    // displays the next level up
    public void displayLevelUp(){
        currentLevel ++;
        drawer.displayLevel(currentLevel);
        lvlDown.setDisable(false);
        if(currentLevel == masterMaze.length -1)lvlUp.setDisable(true); // if we are at the top level, make it impossible to go up again.

    }

    //Shows about info screen from help menu
    public void about(){
        AboutWindow aboutWindow = new AboutWindow("About", "aMAZEing Solver", "1.0",
                "This is the most amazing maze solver that world will ever know!\n" +
                        "Let it be known that this is a fact and has been scientifically proven.",
                "Awesome Team", "https://github.com/shinn16/MazeSolver", theme);
        aboutWindow.display();
    }

    public void legend(){
        LegendWindow legendWindow = new LegendWindow(primaryStage, "Legend", mapTheme, theme);
        legendWindow.display();
    }

    public void settings(){ // builds and opens a settings window.
        Stage window = new Stage();
        window.setTitle("Settings");
        window.initModality(Modality.APPLICATION_MODAL); // means that while this window is open, you can't interact with the main program.;

        // labels
        Label themePacks = new Label("Map Packs");

        // drop down menus



        ComboBox<String> mapBox = new ComboBox<>();
        mapBox.getItems().addAll("Default", "MineCraft", "Penguin", "Pokemon");
        mapBox.setValue(mapTheme);
        mapBox.setPrefSize(125,25);

        // layout defining.
        HBox layout = new HBox(50);
        VBox right = new VBox(10);

        // layout building

        right.getChildren().addAll(themePacks, mapBox);
        right.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(right);
        layout.setAlignment(Pos.CENTER);

        // build and display window
        Scene scene = new Scene(layout);
        scene.getStylesheets().addAll("/graphics/css/"+theme+".css");
        window.setScene(scene);
        window.setWidth(350);
        window.setHeight(225);
        window.setResizable(false);
        window.getIcons().add(new Image(getClass().getResourceAsStream("/graphics/AppIcon.png")));
        window.show();

        // this is here because nested methods aren't a thing in java...
        class settingsMethods{
            private settingsMethods(){
                // does nothing..
            }



            private void mapPackMethod(){
                mapTheme = mapBox.getValue();
                if (run)drawer.reDraw(mapTheme); // if we are able to run, that means we need to redraw the map.
            }
        }

        // changes the themes on click!
        mapBox.setOnAction(e -> new settingsMethods().mapPackMethod());
    }

}