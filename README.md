Author: Julian Yan  
Date: 2/6/2019  
Title: Stream Line  
Summary: This program utilizes JavaFX in order to visualize a Streamline game where users must use the keyboard to manuever their square piece through obstacles to reach the final goal. As the player progresses, the levels will increase in difficulty with increased number of obstacles. 

Contents: 
* Direction.java 
  * An enumerator defining the 4 possible move directions in a Streamline game
    * Direction(int rotationCount)
    * int getRotationCount()
  
* GameState.java
  * This class includes methods that will create a game board, allow a player to navigate on the game board, place obstacles on the game board and also rotate the game board clockwise. There is also a method that prints out a visualization of the board that allows the user to see where their position, the goal and the obstacles are at. 
    * public GameState(int height, int width, int playerRow, int playerCol, int goalRow, int goalCol)
    * GameState(GameState other)
    * void addRandomObstacles(int count)
    *  void rotateClockwise()
    * void moveRight()
    * void move(Direction direction)
    * boolean equals(Object other)
    * String toString()
  
* Streamline.java
  * This class includes methods that will create a Streamline game, allow the user to control the player in the terminal and also save the current progress of the board. There is also a method that allows the player to undo their moves. 
    * Streamline()
    * Streamline(String filename)
      * void loadFromFile(String filename)
    * recordAndMove(Direction direction)
    * void undo()
    * void play()
    * void saveToFile() {

* GuiStreamline.java
  * This class creates a file that will display the Streamline game board as well as handling the key events. It includes methods that help display the board and methods to handle the movement of the player when a key is pressed.
    * int getBoardWidth()
    * int getBoardHeight()
    * double getSquareSize()
    * void resetGrid()
    * void updateTrailColors()
    * double[] boardIdxToScenePos (int boardCol, int boardRow)
    * void onPlayerMoved(int fromCol, int fromRow, int toCol, int toRow, boolean isUndo)
    * void handleKeyCode(KeyCode keyCode)
    * void onLevelLoaded()
    * void onLevelFinished()
    * void loadLevels()
    * void start(Stage primaryStage)

* Player.java
  * This class displays the Player on the grid. It includes methods that will create a Player object and set the size of the Player object.
    * Player()
    * void setSize(double size)

* RoundedSquare.java
  * This class defines RoundedSquare objects used for the tiles and provide methods to access coordinates of tiles.
    * RoundedSquare()
    * RoundedSquare(double size)
    * RoundedSquare (double centerX, double centerY, double size)
    * double getCenterX()
    * void setCenterX(double centerX)
    * double getCenterY()
    * void setCenterY(double centerY)
    * double getSize()
    * void setSize(double size)
    * double getArcFraction()
    * void setArcFraction(double arcFraction)
