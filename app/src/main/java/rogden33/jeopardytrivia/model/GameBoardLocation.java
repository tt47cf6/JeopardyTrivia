package rogden33.jeopardytrivia.model;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by Robert on 8/15/2015.
 */
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
        Log.i("DEBUG", "" + toString().hashCode());
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        Log.i("DEBUG", "Me: " + toString() + " other: " + other.toString());
        if (other.getClass() == getClass()) {
            GameBoardLocation loc = (GameBoardLocation) other;
            result = loc.myX == myX && loc.myY == myY;
        }
        Log.i("DEBUG", "result: " + result);
        return result;
//        Log.i("DEBUG", "result: " + toString().equals(other.toString()));
//        return toString().equals(other.toString());
    }

    @Override
    public String toString() {
        return "{" + myX + ", " + myY + "}";
    }
}
