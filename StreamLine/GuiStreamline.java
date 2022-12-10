/**
 * Author: Julian Wai San Yan
 * CSE8B Login: cs8bwapf
 * Email: jwyan@ucsd.edu
 * Date: 3/6/19
 * File: GuiStreamline.java
 * Sources of Help: CSE 8B Piazza, PSA6 Write Up, Discussion Slides, Lecture
 *                  Slides, Tutors, Path Documentation, PathTransition
 *                  Documentation, Fade Documentation
 */

/**
 * This file fulfills the requirements stated in Page 2 and 3 of the PSA6 Write
 * Up which is to create a file that will display the Streamline game board as
 * well as handling the key events. Included below are methods that help display
 * the board on a group and methods to handle the movement of the player when a
 * key is pressed.
 */

import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.*;
import javafx.animation.PathTransition.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.*;
import javafx.util.Duration;

/**
 * This class includes methods that help display the board on a group and 
 * methods to handle the movement of the player when a key is pressed. 
 * Important instance variables are the mainScene, the levelGroup that holds 
 * obstacles and trails, the rootGroup that holds everything else, the 
 * playerRect, which is the GUI representation of the player, the goalRect, 
 * which is the GUI representation of the goal, the grid that stores the Shape
 * objects, the Streamline game which is the current level of the game, the 
 * list of next games and the key handler for keyboard input.
 */

public class GuiStreamline extends Application {
    static final double SCENE_WIDTH = 500;
    static final double SCENE_HEIGHT = 600;
    static final String TITLE = "CSE 8b Streamline GUI";
    static final String USAGE = 
        "Usage: \n" + 
        "> java GuiStreamline               - to start a game with default" +
        " size 6*5 and random obstacles\n" + 
        "> java GuiStreamline <filename>    - to start a game by reading g" +
        "ame state from the specified file\n" +
        "> java GuiStreamline <directory>   - to start a game by reading a" +
        "ll game states from files in\n" +
        "                                     the specified directory and " +
        "playing them in order\n";

    static final Color TRAIL_COLOR = Color.BLUEVIOLET;
    static final Color GOAL_COLOR = Color.GREEN;
    static final Color OBSTACLE_COLOR = Color.DIMGRAY;

    // Trail radius will be set to this fraction of the size of a board square.
    static final double TRAIL_RADIUS_FRACTION = 0.1;

    // Squares will be resized to this fraction of the size of a board square.
    static final double SQUARE_FRACTION = 0.8;

    // fading effect
    private static final double FADE_FROM = 1;
    private static final double FADE_TO = 0.1;

    Scene mainScene;
    Group levelGroup;                   // For obstacles and trails
    Group rootGroup;                    // Parent group for everything else
    Player playerRect;                  // GUI representation of the player
    RoundedSquare goalRect;             // GUI representation of the goal

    Shape[][] grid;                     // Same dimensions as the game board

    Streamline game;                    // The current level
    ArrayList<Streamline> nextGames;    // Future levels

    MyKeyHandler myKeyHandler;          // for keyboard input

    /**
     * Returns the width of the board for the current level
     *
     * @param  none
     * @return width of the board
     */

    public int getBoardWidth() {
        return this.game.currentState.board[0].length; 
    }

    /**
     * Returns the height of the board for the current level
     *
     * @param  none
     * @return height of the board
     */

    public int getBoardHeight() {
        return this.game.currentState.board.length;  
    }

    /**
     * Returns a size for a single square of the board that will fit nicely in 
     * the current scene size
     *
     * @param  none
     * @return size of a single square of the board
     */

    public double getSquareSize() {

        // finds the ideal square width and height
        double squareWidth = mainScene.getWidth() / getBoardWidth();
        double squareHeight = mainScene.getHeight() / getBoardHeight();

        // chooses the smallest value
        if(squareWidth > squareHeight) {
            return squareHeight;
        }
        return squareWidth;
    }

    /**
     * Destroys and recreates grid and all trail and obstacle shapes
     *
     * @param  none
     * @return void
     */

    public void resetGrid() {
        // clears the group
        levelGroup.getChildren().clear();

        // creates a new grid
        this.grid = new Shape[getBoardHeight()][getBoardWidth()];

        // loops through every element in the board
        for(int i = 0; i < game.currentState.board.length; i++) {
            for(int j = 0; j < game.currentState.board[0].length; j++) {

                // if there is a trail char, add it to the grid and the group
                if(game.currentState.board[i][j] == GameState.TRAIL_CHAR) {
                    double[] trailPos = boardIdxToScenePos(j,i);
                    Circle trailCircle = new Circle(trailPos[0],trailPos[1],
                            getSquareSize() * TRAIL_RADIUS_FRACTION, 
                            TRAIL_COLOR);
                    grid[i][j] = trailCircle;
                    levelGroup.getChildren().add(trailCircle);
                }

                // if there is an obstacle char, add it to the grid and the 
                // group
                if(game.currentState.board[i][j] == GameState.OBSTACLE_CHAR) {
                    double[] obstaclePos = boardIdxToScenePos(j,i);
                    RoundedSquare obstacleSquare = new RoundedSquare(
                            obstaclePos[0], obstaclePos[1], getSquareSize()
                            * SQUARE_FRACTION);
                    grid[i][j] = obstacleSquare;
                    obstacleSquare.setFill(OBSTACLE_COLOR);
                    levelGroup.getChildren().add(obstacleSquare);
                }

                // if there is an empty space, add it to the grid and the group
                if(game.currentState.board[i][j] == GameState.SPACE_CHAR) {
                    double[] emptyPos = boardIdxToScenePos(j,i);
                    Circle emptyCircle = new Circle(emptyPos[0],emptyPos[1],
                            getSquareSize() * TRAIL_RADIUS_FRACTION, 
                            Color.TRANSPARENT);
                    grid[i][j] = emptyCircle;
                    levelGroup.getChildren().add(emptyCircle);
                }
            }
        }
        updateTrailColors();
    }

    /** 
     * Sets the fill color of all trail Circles making them visible or not
     * depending on if that board position equals TRAIL_CHAR
     *
     * @param  none
     * @return void
     */

    public void updateTrailColors() {
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[0].length; j++) {
                // checks if the object at grid[i][j] is a Circle
                if(grid[i][j] instanceof Circle) {
                    // if there is a trail char at the position of the object
                    if(game.currentState.board[i][j] == GameState.TRAIL_CHAR) {
                        grid[i][j].setFill(Color.BLUEVIOLET);
                    }
                    else {
                        grid[i][j].setFill(Color.TRANSPARENT);
                    }
                }
            }
        }
    }

    /** 
     * Coverts the given board column and row into scene coordinates.
     * Gives the center of the corresponding tile.
     * 
     * @param boardCol a board column to be converted to a scene x
     * @param boardRow a board row to be converted to a scene y
     * @return scene coordinates as length 2 array where index 0 is x
     */

    static final double MIDDLE_OFFSET = 0.5;
    public double[] boardIdxToScenePos (int boardCol, int boardRow) {
        double sceneX = ((boardCol + MIDDLE_OFFSET) * 
                (mainScene.getWidth() - 1)) / getBoardWidth();
        double sceneY = ((boardRow + MIDDLE_OFFSET) * 
                (mainScene.getHeight() - 1)) / getBoardHeight();
        return new double[]{sceneX, sceneY};
    }

    /** 
     * Makes trail markers visible and changes player position
     *
     * @param fromCol old player column
     * @param fromRow old player row
     * @param toCol   new player Column
     * @param toRow   new player row
     * @param isUndo  whether or not it was an undo movement
     * @return        void
     */

    public void onPlayerMoved(int fromCol, int fromRow, int toCol, int toRow, 
            boolean isUndo)
    {
        // If the position is the same, just return
        if (fromCol == toCol && fromRow == toRow) {
            return;
        }

        double squareSize = getSquareSize() * SQUARE_FRACTION;

        // Update the player position
        double[] playerPos = boardIdxToScenePos(toCol, toRow);

        playerRect.setSize(squareSize);
        playerRect.setCenterX(playerPos[0]);
        playerRect.setCenterY(playerPos[1]);

        // updateTrailColors();

        // player animations: for the trails to follow the player
        Path path = new Path();
        path.getElements().add(new MoveTo(fromRow, fromCol));

        PathTransition transition = new PathTransition(
                Duration.millis(FADE_TIME), path, playerRect);
        transition.play();

        updateTrailColors();

        // call onlevelFinished if level is passed
        if(game.currentState.levelPassed == true) {
            onLevelFinished();
        }                     
    }   

    /**
     * Called when a key is pressed
     *
     * @param keyCode the key that is pressed
     * @return        void
     */

    void handleKeyCode(KeyCode keyCode) {
        // gets the old position of the player
        int fromCol = game.currentState.playerCol;
        int fromRow = game.currentState.playerRow;  

        switch (keyCode) {
            // if the up arrow key is pressed
            case UP:             
                game.recordAndMove(Direction.UP);
                onPlayerMoved(fromCol, fromRow, 
                        game.currentState.playerCol,
                        game.currentState.playerRow, false);               
                break;
                // if the left arrow key is pressed
            case LEFT:
                game.recordAndMove(Direction.LEFT);
                onPlayerMoved(fromCol, fromRow, 
                        game.currentState.playerCol,
                        game.currentState.playerRow, false);   
                break;
                // if the down arrow key is pressed
            case DOWN:
                game.recordAndMove(Direction.DOWN);
                onPlayerMoved(fromCol, fromRow, 
                        game.currentState.playerCol,
                        game.currentState.playerRow, false); 
                break;
                // if the right arrow key is pressed
            case RIGHT:
                game.recordAndMove(Direction.RIGHT);
                onPlayerMoved(fromCol, fromRow, 
                        game.currentState.playerCol,
                        game.currentState.playerRow, false); 
                break;
                // if the u key is pressed
            case U:
                game.undo();
                onPlayerMoved(fromCol, fromRow, 
                        game.currentState.playerCol,
                        game.currentState.playerRow, false); 
                break;
                // if the o key is pressed
            case O:
                game.saveToFile();
                break;
                // if the q key is pressed
            case Q:
                System.exit(0);
                break;
                // if any other key is pressed
            default:
                System.out.println("Possible commands:\n w - up\n " + 
                        "a - left\n s - down\n d - right\n u - undo\n " + 
                        "q - quit level");
                break;
        }
    }

    /**
     * This nested class handles keyboard input and calls handleKeyCode() which
     * will perform the action based on the key that is pressed.
     */

    class MyKeyHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent e) {
            KeyCode keyCode = e.getCode();
            handleKeyCode(keyCode);
        }
    }        

    /**
     * Called when the UI needs to be completely redone to reflect a new level
     *
     * @param  none
     * @return void
     */

    public void onLevelLoaded() {
        resetGrid();

        double squareSize = getSquareSize() * SQUARE_FRACTION;

        // Update the player position
        double[] playerPos = boardIdxToScenePos(
                game.currentState.playerCol, game.currentState.playerRow
                );
        playerRect.setSize(squareSize);
        playerRect.setCenterX(playerPos[0]);
        playerRect.setCenterY(playerPos[1]);

        // Update the goal position
        double[] goalPos = boardIdxToScenePos(game.currentState.goalCol,
                game.currentState.goalRow);
        goalRect.setSize(squareSize);
        goalRect.setFill(GOAL_COLOR);
        goalRect.setCenterX(goalPos[0]);
        goalRect.setCenterY(goalPos[1]);
    }

    // Called when the player reaches the goal. Shows the winning animation
    // and loads the next level if there is one.
    /**
     * Called when the player reaches the goal, shows the winning animation
     * and loads the next level if there is one
     * 
     * @param  none
     * @return void
     */

    static final double SCALE_TIME = 175;  // milliseconds for scale animation
    static final double FADE_TIME = 250;   // milliseconds for fade animation
    static final double DOUBLE_MULTIPLIER = 2;
    public void onLevelFinished() {
        // Clone the goal rectangle and scale it up until it covers the screen

        // Clone the goal rectangle
        Rectangle animatedGoal = new Rectangle(
                goalRect.getX(),
                goalRect.getY(),
                goalRect.getWidth(),
                goalRect.getHeight()
                );
        animatedGoal.setFill(goalRect.getFill());

        // Add the clone to the scene
        List<Node> children = rootGroup.getChildren();
        children.add(children.indexOf(goalRect), animatedGoal);

        // Create the scale animation
        ScaleTransition st = new ScaleTransition(
                Duration.millis(SCALE_TIME), animatedGoal
                );
        st.setInterpolator(Interpolator.EASE_IN);

        // Scale enough to eventually cover the entire scene
        st.setByX(DOUBLE_MULTIPLIER * 
                mainScene.getWidth() / animatedGoal.getWidth());
        st.setByY(DOUBLE_MULTIPLIER * 
                mainScene.getHeight() / animatedGoal.getHeight());

        /*
         * This will be called after the scale animation finishes.
         * If there is no next level, quit. Otherwise switch to it and
         * fade out the animated cloned goal to reveal the new level.
         */
        st.setOnFinished(e1 -> {
                
                // checks if there are any more levels
                if(nextGames.size() == 0) {
                System.exit(0);
                }

                // updates the game to the next level
                else {
                game = nextGames.get(0);
                nextGames.remove(0);
                }

                // Update UI to the next level, but it won't be visible yet
                // because it's covered by the animated cloned goal

                onLevelLoaded();

                // creates fade
                FadeTransition fade = new FadeTransition(
                    Duration.millis(FADE_TIME),animatedGoal);
                fade.setFromValue(FADE_FROM);
                fade.setToValue(FADE_TO);
                fade.play();

                // removes fade from group after it is finished
                fade.setOnFinished(e2 -> {
                        rootGroup.getChildren().remove(animatedGoal);
                        });
        });

        // Start the scale animation
        st.play();
    }

    /** 
     * Performs file IO to populate game and nextGames using filenames from
     * command line arguments.
     */

    public void loadLevels() {
        game = null;
        nextGames = new ArrayList<Streamline>();

        List<String> args = getParameters().getRaw();
        if (args.size() == 0) {
            System.out.println("Starting a default-sized random game...");
            game = new Streamline();
            return;
        }

        // at this point args.length == 1

        File file = new File(args.get(0));
        if (!file.exists()) {
            System.out.printf("File %s does not exist. Exiting...", 
                    args.get(0));
            return;
        }

        // if is not a directory, read from the file and start the game
        if (!file.isDirectory()) {
            System.out.printf("Loading single game from file %s...\n", 
                    args.get(0));
            game = new Streamline(args.get(0));
            return;
        }

        // file is a directory, walk the directory and load from all files
        File[] subfiles = file.listFiles();
        Arrays.sort(subfiles);
        for (int i=0; i<subfiles.length; i++) {
            File subfile = subfiles[i];

            // in case there's a directory in there, skip
            if (subfile.isDirectory()) continue;

            // assume all files are properly formatted games, 
            // create a new game for each file, and add it to nextGames
            System.out.printf("Loading game %d/%d from file %s...\n",
                    i+1, subfiles.length, subfile.toString());
            nextGames.add(new Streamline(subfile.toString()));
        }

        // Switch to the first level
        game = nextGames.get(0);
        nextGames.remove(0);
    }

    /**
     * The main entry point for all JavaFX Applications
     * Initializes instance variables, creates the scene, and sets up the UI
     */
    @Override
        public void start(Stage primaryStage) throws Exception {
            // Populate game and nextGames
            loadLevels();

            // Initialize the scene and our groups
            rootGroup = new Group();
            mainScene = new Scene(rootGroup, SCENE_WIDTH, SCENE_HEIGHT, 
                    Color.GAINSBORO);
            levelGroup = new Group();
            rootGroup.getChildren().add(levelGroup);     

            // initializes goalRect and adds it to rootGroup
            this.goalRect = new RoundedSquare();
            rootGroup.getChildren().add(this.goalRect);

            // initializes playerRect and adds it to rootGroup
            this.playerRect = new Player();
            rootGroup.getChildren().add(this.playerRect);

            onLevelLoaded();

            // keyboard input handling
            myKeyHandler = new MyKeyHandler();
            mainScene.setOnKeyPressed(myKeyHandler);

            // Make the scene visible
            primaryStage.setTitle(TITLE);
            primaryStage.setScene(mainScene);
            primaryStage.setResizable(true);
            primaryStage.show();
        }

    /** 
     * Execution begins here, but at this point we don't have a UI yet
     * The only thing to do is call launch() which will eventually result in
     * start() above being called.
     */
    public static void main(String[] args) {
        if (args.length != 0 && args.length != 1) {
            System.out.print(USAGE);
            return;
        }

        launch(args);
    }
}
