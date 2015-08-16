package rogden33.jeopardytrivia.junit.junit;

import junit.framework.TestCase;

import rogden33.jeopardytrivia.model.GameBoardLocation;

/**
 * Tests the GameBoardLocation object to ensure the equals and hashcode methods are behaving
 * properly.
 */
public class GameBoardLocationTest extends TestCase {

    /**
     * Test two objects which should equal each other, do.
     */
    public void testEquals() {
        GameBoardLocation loc1 = new GameBoardLocation(1, 2);
        GameBoardLocation loc2 = new GameBoardLocation(1, 2);
        assertTrue("Objects should have been equal to each other", loc1.equals(loc2));
        assertTrue("Objects should have been equal to each other", loc2.equals(loc1));
    }

    /**
     * Test two object which should not equal each other, don't.
     */
    public void testNotEquals() {
        GameBoardLocation loc1 = new GameBoardLocation(2, 2);
        GameBoardLocation loc2 = new GameBoardLocation(1, 2);
        assertFalse("Objects should not have been equal to each other", loc1.equals(loc2));
        assertFalse("Objects should not have been equal to each other", loc2.equals(loc1));
    }

    /**
     * Test two objects which should equal each other, do.
     */
    public void testEqualsEdge() {
        GameBoardLocation loc1 = new GameBoardLocation(0, -1);
        GameBoardLocation loc2 = new GameBoardLocation(0, -1);
        assertTrue("Objects should have been equal to each other", loc1.equals(loc2));
        assertTrue("Objects should have been equal to each other", loc2.equals(loc1));
    }

    /**
     * Test two object which should not equal each other, don't.
     */
    public void testNotEqualsEdge() {
        GameBoardLocation loc1 = new GameBoardLocation(0, 0);
        GameBoardLocation loc2 = new GameBoardLocation(-1, -1);
        assertFalse("Objects should not have been equal to each other", loc1.equals(loc2));
        assertFalse("Objects should not have been equal to each other", loc2.equals(loc1));
    }

    /**
     * Test that the hashCode and equals methods both behave in the same way.
     */
    public void testHashCodeAndEquals() {
        GameBoardLocation loc1 = new GameBoardLocation(1, 2);
        GameBoardLocation loc2 = new GameBoardLocation(1, 2);
        assertTrue("Hashcodes did not match and should have", loc1.hashCode() == loc2.hashCode());
        assertTrue("Objects should have been equal to each other", loc1.equals(loc2));
        assertTrue("Objects should have been equal to each other", loc2.equals(loc1));
    }
}
