/** 
 * Author: Julian Wai San Yan
 * Date: 2/6/19
 *
 * Included below are methods that will create a Streamline game, allow the
 * user to control the player in the terminal and also save the current
 * progress of the board. There is also a method that allows the player to undo
 * their moves. 
 */

import java.util.*;
import java.io.*;

/**
 * The Streamline class creates a Streamline game and allows a user to control
 * the movement of their character through the terminal. Important instance
 * variables are the current state of the game board and a list that contains
 * all of the movements that have been made. 
 */

public class Streamline {

    final static int DEFAULT_HEIGHT = 6;
    final static int DEFAULT_WIDTH = 5;

    final static String OUTFILE_NAME = "saved_streamline_game";

    GameState currentState;
    List<GameState> previousStates;

    // default characteristics of a game board
    private static final int DEFAULT_PLAYERROW = 5;
    private static final int DEFAULT_PLAYERCOL = 0;
    private static final int DEFAULT_GOALROW = 0;
    private static final int DEFAULT_GOALCOL = 4;
    private static final int DEFAULT_OBSTACLES = 3;

    // ask for an input
    private static final String INPUT = "> ";

    // movement keys
    private static final String UP = "w";
    private static final String LEFT = "a";
    private static final String DOWN = "s";
    private static final String RIGHT = "d";
    private static final String UNDO = "u";
    private static final String SAVE_TO_FILE = "o";
    private static final String QUIT = "q";

    // level passed statement
    private static final String LEVEL_PASSED = "Level Passed!";

    // space and new line for saveToFile
    private static final String SPACE_STRING = " ";
    private static final String LINE_STRING = "\n";

    // saved message
    private static final String SAVED_SUCCESS = "Saved current state to: saved_streamline_game";

    /**
     * Begins a game with the default 6 by 5 board with the starting player
     * position at the left corner of the board and the goal position at the
     * top right corner of the board. It also creates a new ArrayList in which
     * all moves will be stored in. 
     */

    public Streamline() {
        this.currentState = new GameState(DEFAULT_HEIGHT,
                DEFAULT_WIDTH,
                DEFAULT_PLAYERROW,DEFAULT_PLAYERCOL,
                DEFAULT_GOALROW,DEFAULT_GOALCOL);
        currentState.addRandomObstacles(DEFAULT_OBSTACLES);
        this.previousStates = new ArrayList<GameState>();
    }

    public Streamline(String filename) {
        try {
            loadFromFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 
     * Loads a file and reads the information in the file. Based on the 
     * information in the file, it initializes all instance variables to the
     * information given in the file. It also creates a game board with the
     * obstacles at the given positions.
     * 
     * @param String filename name of file to read 
     */

    protected void loadFromFile(String filename) throws IOException {
        Scanner file = new Scanner(new File(filename));
        this.previousStates = new ArrayList<GameState>();

        // gets the height and width
        int newHeight = file.nextInt();
        int newWidth = file.nextInt();

        // gets the player row and player column
        int newPlayerRow = file.nextInt();
        int newPlayerCol = file.nextInt();

        // gets the goal row and goal column
        int newGoalRow = file.nextInt();
        int newGoalCol = file.nextInt();

        // creates a new gameState with the initialized instance variables
        GameState newGameState = new GameState(newHeight, newWidth, 
                newPlayerRow, newPlayerCol,
                newGoalRow, newGoalCol);

        file.nextLine();
        // gets the obstacles, empty spaces, and trails     
        for(int i = 0; i < newGameState.board.length; i++) {
            String line = file.nextLine();
            for(int j = 0; j < newGameState.board[0].length; j++) {
                newGameState.board[i][j] = line.charAt(j);
            }
        }
        this.currentState = newGameState;
    }

    /**
     * Saves a copy of the current game and adds it into the previousStates
     * ArrayList. It then makes a move based on the given direction and updates
     * the current state to one with the new move.
     *
     * @param Direction direction direction to move
     * @return          none
     */

    void recordAndMove(Direction direction) {
        if(direction == null) {
            return;
        }
        GameState copyOfGameState = new GameState(currentState);
        this.currentState.move(direction);
        if(previousStates != null) {
            // if last element in previousStates is not equal to currentState
            if (!copyOfGameState.equals(currentState)) {
                previousStates.add(copyOfGameState);
            }
        }
        return;
    }


    /** 
     * Reverts back to the previous move and removes the previous move from the
     * previousStates ArrayList.
     */

    void undo() {
        if(previousStates.isEmpty()) {
            return;
        }
        // gets the most recent state and sets it to currentState
        this.currentState = previousStates.get(previousStates.size() - 1);
        // remove the previous state from the list
        previousStates.remove(previousStates.size() - 1);
        return;
    }

    /** 
     * Runs game indefinitely until the player has reached the goal by 
     * printing the currentState. It takes the inputs "w", "a", "s", "d", "u",
     * "o", and "q" and performs actions based on the command that the letters
     * correspond to.
     */

    void play() {
        Scanner scanner = new Scanner(System.in); 
        while(!currentState.levelPassed) {
            System.out.println(this.currentState.toString());
            System.out.print(INPUT);
            String input = scanner.nextLine();
            if(input.equals(UP)) {
                recordAndMove(Direction.UP);
            }
            else if(input.equals(LEFT)) {
                recordAndMove(Direction.LEFT);
            }
            else if(input.equals(DOWN)) {
                recordAndMove(Direction.DOWN);
            }
            else if(input.equals(RIGHT)) {
                recordAndMove(Direction.RIGHT);
            }
            else if(input.equals(UNDO)) {
                undo();
            }
            else if(input.equals(SAVE_TO_FILE)) {
                saveToFile();
            }
            else if(input.equals(QUIT)) {            
                return;
            }
        }
        if(currentState.levelPassed == true) { 
            System.out.println(this.currentState.toString());
            System.out.println(LEVEL_PASSED);
        }
        return;
    }

    /**
     * Saves the current state of the board including the board height, board
     * width, player row, player column, goal row, goal column and positions
     * of the obstacles, trails and empty spaces.
     */

    void saveToFile() {
        String filename = OUTFILE_NAME;
        try {
            PrintWriter writer = new PrintWriter(filename);
            // prints first line with board height and board width
            writer.print(String.valueOf(currentState.board.length));
            writer.print(SPACE_STRING);
            writer.print(String.valueOf(currentState.board[0].length));
            // prints second line with player row and player column
            writer.print(LINE_STRING);
            writer.print(String.valueOf(currentState.playerRow));
            writer.print(SPACE_STRING);
            writer.print(String.valueOf(currentState.playerCol));
            // prints third line with goal row and goal column
            writer.print(LINE_STRING);
            writer.print(String.valueOf(currentState.goalRow));
            writer.print(SPACE_STRING);
            writer.print(String.valueOf(currentState.goalCol));
            // prints the board
            writer.print(LINE_STRING);
            for(int i = 0; i < DEFAULT_HEIGHT; i++) {
                for(int j = 0; j < DEFAULT_WIDTH; j++) {
                    writer.print(currentState.board[i][j]);
                }
                writer.print(LINE_STRING);
            }
            writer.close();
            System.out.println(SAVED_SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
