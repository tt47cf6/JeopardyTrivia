package rogden33.jeopardytrivia.model;

import android.util.Log;

import java.io.Serializable;

public class GameBoardLocation implements Serializable {
    private int myX;
    private int myY;

    public GameBoardLocation(int x, int y) {
        myX = x;
        myY = y;
    }

    public int getX() {
        return myX;
    }

    public int getY() {
        return myY;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other.getClass() == getClass()) {
            GameBoardLocation loc = (GameBoardLocation) other;
            result = loc.myX == myX && loc.myY == myY;
        }
        return result;
    }

    @Override
    public String toString() {
        return "{" + myX + ", " + myY + "}";
    }
}
