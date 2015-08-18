package rogden33.jeopardytrivia.model;

import java.io.Serializable;

/**
 * Used by the GameBoardActivity to easily pick DailyDouble locations randomly without
 * duplicates by using a HashSet. This class is Serializable so that it can be saved in a Bundle.
 *
 */
public class GameBoardLocation implements Serializable {

    /**
     * The category location, or x.
     */
    private final int myX;

    /**
     * The row location, or y.
     */
    private final int myY;

    /**
     * Sole constructor that sets the fields to the given values.
     *
     * @param x the x
     * @param y the y
     */
    public GameBoardLocation(int x, int y) {
        myX = x;
        myY = y;
    }

    /**
     * A getter for x.
     * @return myX
     */
    public int getX() {
        return myX;
    }

    /**
     * A getter for y
     * @return myY
     */
    public int getY() {
        return myY;
    }

    /**
     * Returns a hashcode based on the string, which is dependant on the actual field values.
     *
     * @return a hashcode based on the field values
     */
    @Override
    public int hashCode() {
        return toString().hashCode() + 1;
    }

    /**
     * Compares this to the other object based on the actual values, if the other object is of the
     * same class.
     *
     * @param other the other object to compare to
     * @return true if the other object is equal to this, based on values
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other.getClass() == getClass()) {
            GameBoardLocation loc = (GameBoardLocation) other;
            result = loc.myX == myX && loc.myY == myY;
        }
        return result;
    }

    /**
     * Return a String representation of this object.
     * @return a String representation of this object.
     */
    @Override
    public String toString() {
        return "{" + myX + ", " + myY + "}";
    }
}
