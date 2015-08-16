package rogden33.jeopardytrivia.junit.junit;

import junit.framework.TestCase;

import rogden33.jeopardytrivia.model.Clue;
import rogden33.jeopardytrivia.model.Displayable;
import rogden33.jeopardytrivia.model.GameBoard;

/**
 * Created by Robert on 8/16/2015.
 */
public class GameBoardTest extends TestCase implements Displayable{

    private static final int NUM_OF_CATEGORIES = 5;

    private static final int NUM_OF_ROWS = 5;

    private GameBoard myBoard;

    private boolean myReadyFlag;

    public void setUp() {
        myReadyFlag = false;
        GameBoard board = new GameBoard(this, NUM_OF_ROWS, NUM_OF_CATEGORIES);
        board.requestBoard();
        while (!myReadyFlag) {}
    }

    public void testCreation() {
        assertNotNull(myBoard.getCategories());
    }

    public void testCategories() {
        for (String cat : myBoard.getCategories()) {
            assertNotNull(cat);
            assertTrue(cat.length() > 0);
        }
    }

    public void testNoEscapes() {
        for (int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            for (int row = 0; row < NUM_OF_ROWS; row++) {
                Clue clue = myBoard.getClue(row, cat);
                assertFalse(clue.getCategory().contains("\\"));
                assertFalse(clue.getClue().contains("\\"));
                assertFalse(clue.getResponse().contains("\\"));
                for (String resp : clue.getSelectableAnswers()) {
                    assertFalse(resp.contains("\\"));
                }
            }
        }
    }

    public void testCategoriesMatch() {
        for (int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            String expected = myBoard.getCategories()[cat];
            for (int row = 0; row < NUM_OF_ROWS; row++) {
                Clue clue = myBoard.getClue(row, cat);
                assertEquals(true, expected.equals(clue.getCategory()));
            }
        }
    }

    public void testForNonNull() {
        for (int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            for (int row = 0; row < NUM_OF_ROWS; row++) {
                Clue clue = myBoard.getClue(row, cat);
                assertNotNull(clue);
                assertNotNull(clue.getCategory());
                assertNotNull(clue.getClue());
                assertNotNull(clue.getResponse());
                assertTrue(clue.getCorrectResponseIndex() >= 0);
                assertTrue(clue.getCategory().length() > 0);
                assertTrue(clue.getResponse().length() > 0);
                for (String resp : clue.getSelectableAnswers()) {
                    assertNotNull(resp);
                    assertTrue(resp.length() > 0);
                }
            }
        }
    }

    public void display(GameBoard board) {
        myBoard = board;
        myReadyFlag = true;
    }
}
