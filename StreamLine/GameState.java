/** 
 * Author: Julian Wai San Yan
 * Date: 2/6/19
 * File: GameState.java
 *
 * Included below are methods that will create a game board, allow a player
 * to navigate on the game board, place obstacles on the game board and also
 * rotate the game board clockwise. There is also a method that prints out a
 * visualization of the board that allows the user to see where their position,
 * the goal and the obstacles are at. 
 */

import java.util.*;

/** 
 * The GameState Class creates a game board that is used to play the Streamline
 * game. Important instance variables are the row and column the player is at,
 * the row and column the goal is at, whether or not the player has reached the
 * goal and a 2D map of the board. 
 */

public class GameState {

    // Used to populate char[][] board below and to display the
    // current state of play.
    final static char TRAIL_CHAR = '.';
    final static char OBSTACLE_CHAR = 'X';
    final static char SPACE_CHAR = ' ';
    final static char CURRENT_CHAR = 'O';
    final static char GOAL_CHAR = '@';
    final static char NEWLINE_CHAR = '\n';

    // This represents a 2D map of the board
    char[][] board;

    // Location of the player
    int playerRow;
    int playerCol;

    // Location of the goal
    int goalRow;
    int goalCol;

    // true means the player completed this level
    boolean levelPassed;

    // borders of game board
    private static final String UPPER_BORDER = "-";
    private static final int UPPER_BORDER_MULTIPLE = 2;
    private static final int UPPER_BORDER_EXTRA = 3;
    private static final String SIDE_BORDER = "|";
    private static final char EDGE_CHAR = '|';

    // empty string
    private static final String EMPTY_STRING = "";

    // 360 rotation
    private static final int MAX_ROTATIONS = 4;

    /** 
     * Initializes a board with the given parameters, fills the board with
     * SPACE_CHAR and initializes all instance variables with the given
     * parameters.
     * 
     * @param int height height of board
     * @param int width width of board
     * @param int playerRow row of the player
     * @param int playerCol column of the player
     * @param int goalRow row of the goal
     * @param int goalCol column of the goal
     */

    public GameState(int height, int width, int playerRow, int playerCol,
                     int goalRow, int goalCol) {
        this.board = new char[height][width];
        // fills the board with SPACE_CHAR
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                this.board[i][j] = SPACE_CHAR;
            }
        }

        // initializes instance variables
        this.playerRow = playerRow;
        this.playerCol = playerCol; 
        this.goalRow = goalRow;
        this.goalCol = goalCol;
        this.levelPassed = false;
    }

    /** 
     * Copies the given GameState and updates it as the current GameState
     *
     * @param GameState other GameState to copy
     */

    public GameState(GameState other) {
        this.board = new char[other.board.length][];
        for(int i = 0; i < other.board.length; i++) {
            // creates deep copy of other board 
            this.board[i] = Arrays.copyOf(other.board[i], 
                                          other.board[i].length);
        }

        // copies the instances variables from other
        this.playerRow = other.playerRow;
        this.playerCol = other.playerCol;
        this.goalRow = other.goalRow;
        this.goalCol = other.goalCol;
        this.levelPassed = other.levelPassed;
    }   

    /** 
     * Add random obstacles into this.board based on the given count. It should
     * be noted that the obstacles will not appear where the player, goal or
     * another obstacle is.
     *
     * @param int count amount of obstacles to be placed on the board
     */

    void addRandomObstacles(int count) {
        if(count < 0) {
            return;
        }
        int emptySpaces = 0;
        // counts the number of empty spaces
        for(int i = 0; i < this.board.length; i++) {
            for(int j = 0; j < this.board[0].length; j++) {
                if(this.board[i][j] == SPACE_CHAR) {
                    emptySpaces += 1;                    
                }
            }
        }
        if (count > emptySpaces) {
            return;
        } 
        Random randomNumber = new Random();
        while(count > 0) {
            // creates random coordinates
            int randomRow = randomNumber.nextInt(this.board.length);
            int randomCol = randomNumber.nextInt(this.board[0].length);
            // checks if random coordinate conflicts with player and goal
            if(randomRow != this.playerRow || randomCol != this.playerCol) { 
                if(randomRow != this.goalRow || randomCol != this.goalCol) {
                    // checks if random coordinate is empty
                    if(this.board[randomRow][randomCol] == SPACE_CHAR) {
                        this.board[randomRow][randomCol] = OBSTACLE_CHAR;
                        count--;
                    }  
                }
            }                          
        }
        return;          
    }

    /** 
     * Rotate clockwise once and updates the current position of the player and
     * goal and creates a new rotated board.
     */

    void rotateClockwise() {
        char[][] rotatedBoard = new char[this.board[0].length]
            [this.board.length];
        // fills the rotated board with the inputs from the original board
        for(int i = 0; i < rotatedBoard.length; i++) {
            for(int j = 0; j < rotatedBoard[0].length; j++) {
                rotatedBoard[i][j] = this.board[board.length - j - 1][i];
            }
        }

        int originalLength = board.length;
        int originalPlayerRow = this.playerRow;
        int originalGoalRow = this.goalRow;      
        this.board = rotatedBoard;

        // updates the new player column
        this.playerRow = playerCol;     
        this.playerCol = (originalLength - 1) - originalPlayerRow;

        // updates the new goal column
        this.goalRow = goalCol;
        this.goalCol = (originalLength - 1) - originalGoalRow;

        return;
    }

    /** 
     * Moves player towards the right while leaving a trail on its path
     * until it is stopped by an obstacle, edge, or goal. When it reaches the
     * goal, level is passed. This method calls on the methods in 
     * Direction.java.
     */

    void moveRight() {
        while(playerCol < this.board[0].length - 1) {
            // if there is a goal
            if((this.playerRow == this.goalRow) && ((this.playerCol + 1) == 
                this.goalCol)) {

                this.levelPassed = true;                    
                this.board[playerRow][playerCol] = TRAIL_CHAR;
                this.playerRow = this.goalRow;
                this.playerCol = this.goalCol;
                return;
            }                         
            // if there is an obstacle
            else if(this.board[playerRow][playerCol + 1] == OBSTACLE_CHAR) {
                return;
            }
            // if there is an edge
            else if(this.board[playerRow][playerCol + 1] == EDGE_CHAR) {
                return;
            }
            // if there is a trail
            else if(this.board[playerRow][playerCol + 1] == TRAIL_CHAR) {
                return;
            }
            // leaves a trail on its path
            else if(this.board[playerRow][playerCol + 1] == SPACE_CHAR) {
                this.board[playerRow][playerCol] = TRAIL_CHAR;
                this.playerCol = playerCol + 1;
            }            
        }
        return;
    }

    /**
     * Move towards the given direction by rotating, moving right and then
     * rotating back again.
     *
     * @param Direction direction the direction to move
     */

    void move(Direction direction) {
        Direction directionToMove = direction;
        int numOfRotations = 0;
        // move right
        if(directionToMove == Direction.RIGHT) {
            moveRight();
        }
        // move up
        else if(directionToMove == Direction.UP) {
            numOfRotations = direction.getRotationCount();
            for(int i = 0; i < numOfRotations; i++) {
                rotateClockwise();
            }
            moveRight();
            for(int i = 0; i < MAX_ROTATIONS - numOfRotations; i++) {
                rotateClockwise();
            }
            return;
        }
        // move left
        else if(directionToMove == Direction.LEFT) {
            numOfRotations = direction.getRotationCount();
            for(int i = 0; i < numOfRotations; i++) {
                rotateClockwise();
            }
            moveRight();
            for(int i = 0; i < MAX_ROTATIONS - numOfRotations; i++) {
                rotateClockwise();
            }
            return;
        }
        // move down
        else if(directionToMove == Direction.DOWN) {
            numOfRotations = direction.getRotationCount();
            for(int i = 0; i < numOfRotations; i++) {
                rotateClockwise();
            }
            moveRight();
            for(int i = 0; i < MAX_ROTATIONS - numOfRotations; i++) {
                rotateClockwise();
            }
            return;
        }
        return;
    }

    /**
     * Overrides equals() method. Compares two GameState objects and returns 
     * true if all fields match.
     *
     * @param Object other Object to compare
     * @return boolean true or false
     */

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        // if other is a GameState object
        if(other instanceof GameState) {
            // if player position is equal
            if(this.playerRow == (((GameState)other).playerRow)) {
                if(this.playerCol == (((GameState)other).playerCol)) {
                    // if goal position is equal
                    if(this.goalRow == (((GameState)other).goalRow)) {
                        if(this.goalCol == (((GameState)other).goalCol)) {
                            // if level passed is equal
                            if(this.levelPassed == (((GameState)other)
                                                      .levelPassed)) {
                                if((((GameState)other).board != null) &&
                                      (this.board != null)) {                                    
                                    // if board length is equal
                                    if(this.board.length == (((GameState)other)
                                                               .board.length)) 
                                                               {
                                        // if inputs in board is equal
                                        for(int i = 0; i < this.board.length; 
                                            i++) {
                                            if(Arrays.deepEquals(this.board,
                                               (((GameState)other).board))) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /** 
     * Overrides toString() method. Prints out the board from GameState as a
     * String and places the player's positon, goal position and obstacles on
     * the board. Also attaches a border around the board.
     *
     * @param  none 
     * @return String board as String
     */
    
    @Override
    public String toString() {
        String stringOfBoard = EMPTY_STRING;
        // creates a deep copy of this board
        char[][] copyOfBoard = new char[this.board.length][];
        for(int i = 0; i < this.board.length; i++) {
            copyOfBoard[i] = Arrays.copyOf(this.board[i], 
                                           this.board[i].length);
        }
        copyOfBoard[playerRow][playerCol] = CURRENT_CHAR;
        copyOfBoard[goalRow][goalCol] = GOAL_CHAR;
        // creates an upper border
        for(int i = 0; i < UPPER_BORDER_MULTIPLE * this.board[0].length 
                + UPPER_BORDER_EXTRA; i++) {
            stringOfBoard += UPPER_BORDER;
        }
        stringOfBoard += NEWLINE_CHAR;
        // adds each input in the board to the String
        for(int i = 0; i < this.board.length; i++) {
            stringOfBoard += SIDE_BORDER;
            stringOfBoard += SPACE_CHAR;
            for(int j = 0; j < this.board[0].length; j++) {
                stringOfBoard += copyOfBoard[i][j];
                stringOfBoard += SPACE_CHAR;
            }
            stringOfBoard += SIDE_BORDER;        
            stringOfBoard += NEWLINE_CHAR;
        }
        // creates a lower border
        for(int i = 0; i < UPPER_BORDER_MULTIPLE * this.board[0].length 
                + UPPER_BORDER_EXTRA; i++) {
            stringOfBoard += UPPER_BORDER;
        }
        stringOfBoard += NEWLINE_CHAR;
        return stringOfBoard;       
    }
}
