package rogden33.jeopardytrivia.junit.junit;

import junit.framework.TestCase;

import rogden33.jeopardytrivia.model.GameBoardLocation;

/**
 * Created by Robert on 8/16/2015.
 */
public class GameBoardLocationTest extends TestCase {

    public void testEquals() {
        GameBoardLocation loc1 = new GameBoardLocation(1, 2);
        GameBoardLocation loc2 = new GameBoardLocation(1, 2);
        assertTrue(loc1.equals(loc2));
        assertTrue(loc2.equals(loc1));
    }

    public void testNotEquals() {
        GameBoardLocation loc1 = new GameBoardLocation(2, 2);
        GameBoardLocation loc2 = new GameBoardLocation(1, 2);
        assertFalse(loc1.equals(loc2));
        assertFalse(loc2.equals(loc1));
    }

    public void testEqualsEdge() {
        GameBoardLocation loc1 = new GameBoardLocation(0, -1);
        GameBoardLocation loc2 = new GameBoardLocation(0, -1);
        assertTrue(loc1.equals(loc2));
        assertTrue(loc2.equals(loc1));
    }

    public void testNotEqualsEdge() {
        GameBoardLocation loc1 = new GameBoardLocation(0, 0);
        GameBoardLocation loc2 = new GameBoardLocation(-1, -1);
        assertFalse(loc1.equals(loc2));
        assertFalse(loc2.equals(loc1));
    }

    public void testHashCodeAndEquals() {
        GameBoardLocation loc1 = new GameBoardLocation(1, 2);
        GameBoardLocation loc2 = new GameBoardLocation(1, 2);
        assertTrue(loc1.hashCode() == loc2.hashCode());
        assertTrue(loc1.equals(loc2));
        assertTrue(loc2.equals(loc1));
    }
}
