package rogden33.jeopardytrivia.junit.junit;

import junit.framework.TestCase;

import rogden33.jeopardytrivia.model.Clue;
import rogden33.jeopardytrivia.model.Displayable;
import rogden33.jeopardytrivia.model.GameBoard;

/**
 * Tests the GameBoard class.
 */
public class GameBoardTest extends TestCase implements Displayable {

    /**
     * The number of categories to test with.
     */
    private static final int NUM_OF_CATEGORIES = 5;

    /**
     * The number of rows to test with.
     */
    private static final int NUM_OF_ROWS = 5;

    /**
     * A reference to the board.
     */
    private GameBoard myBoard;

    /**
     * A flag to indicate when the board is loaded and ready.
     */
    private boolean myReadyFlag;

    /**
     * Constructs a new board and blocks while it is loading.
     */
    public void setUp() {
        myReadyFlag = false;
        // request new board
        GameBoard board = new GameBoard(this, NUM_OF_ROWS, NUM_OF_CATEGORIES);
        board.requestBoard();
        // spin until it is ready
        while (!myReadyFlag) {}
    }

    /**
     * Make sure the categories get assigned and the board can be successfully created.
     */
    public void testCreation() {
        assertNotNull(myBoard.getCategories());
    }

    /**
     * Test that all categories are not null and are not empty.
     */
    public void testCategories() {
        for (String cat : myBoard.getCategories()) {
            assertNotNull("Category should not be null", cat);
            assertTrue("Category should not be an empty string", cat.length() > 0);
        }
    }

    /**
     * Test that there are no escaped characters in the final string outputs.
     */
    public void testNoEscapes() {
        for (int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            for (int row = 0; row < NUM_OF_ROWS; row++) {
                Clue clue = myBoard.getClue(row, cat);
                assertFalse("An escaped character was found in the category", clue.getCategory().contains("\\"));
                assertFalse("An escaped character was found in the clue", clue.getClue().contains("\\"));
                assertFalse("An escaped character was found in the response", clue.getResponse().contains("\\"));
                for (String resp : clue.getSelectableAnswers()) {
                    assertFalse("An escaped character was found in a selectable response", resp.contains("\\"));
                }
            }
        }
    }

    /**
     * Test that all the categories of all clues in a column match the category title.
     */
    public void testCategoriesMatch() {
        for (int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            String expected = myBoard.getCategories()[cat];
            for (int row = 0; row < NUM_OF_ROWS; row++) {
                Clue clue = myBoard.getClue(row, cat);
                assertEquals("Clue's category does not match the category title", true, expected.equals(clue.getCategory()));
            }
        }
    }

    /**
     * Ensure all fields of a clue are not null or empty strings.
     */
    public void testForNonNull() {
        for (int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            for (int row = 0; row < NUM_OF_ROWS; row++) {
                Clue clue = myBoard.getClue(row, cat);
                assertNotNull("Clue is null", clue);
                assertNotNull("Category is null", clue.getCategory());
                assertNotNull("Clue's clue is null", clue.getClue());
                assertNotNull("Response is null", clue.getResponse());
                assertTrue("Correct index is invalid", clue.getCorrectResponseIndex() >= 0);
                assertTrue("Category is an empty string", clue.getCategory().length() > 0);
                assertTrue("Response is an empty string", clue.getResponse().length() > 0);
                assertTrue("Clue's clue is an empty string", clue.getClue().length() > 0);
                for (String resp : clue.getSelectableAnswers()) {
                    assertNotNull("Selectable response is null", resp);
                    assertTrue("Selectable response is an empty string", resp.length() > 0);
                }
            }
        }
    }

    /**
     * Called when the board is ready to be used
     *
     * @param board a reference to the model GameBoard
     */
    public void display(GameBoard board) {
        myBoard = board;
        myReadyFlag = true;
    }
}
