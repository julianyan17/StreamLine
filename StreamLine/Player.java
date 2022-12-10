/**
 * Author: Julian Wai San Yan
 * CSE8B Login: cs8bwapf
 * Email: jwyan@ucsd.edu
 * Date: 3/6/19
 * File: Player.java
 * Sources of Help: CSE 8B Piazza, PSA6 Write Up, Discussion Slides, Lecture
 *                  Slides, Tutors
 */

/** 
 * This file fulfills the requirements stated in Page 3 of the PSA6 Write Up
 * which is to display the Player on the grid. Included below are methods that
 * will create a Player object and set the size of the Player object.
 */

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

/**
 * This class creates a Player object which is to be added to the grid and also
 * sets the size of the Player object.
 */

public class Player extends RoundedSquare {
    final static double STROKE_FRACTION = 0.1;
    
    /**
     * Constructor that sets the fill color and border color of a player
     *
     * @param  none
     * @return Player object created
     */

    public Player() {
        setFill(Color.BLUEVIOLET);
        setStroke(Color.LAVENDER);
        setStrokeType(StrokeType.CENTERED);
    }
    
    /**
     * Sets the size of the Player object
     *
     * @param size size of the Player object
     * @return     none
     */

    @Override
    public void setSize(double size) {
        
        // the width of the border of the Player
        double strokeWidth = size * STROKE_FRACTION;
        
        // update stroke width
        setStrokeWidth(strokeWidth);

        // sets size without the stroke width
        super.setSize(size - strokeWidth);


    }
}
