package rogden33.jeopardytrivia.model;

/**
 * This interface is used for the testing so that both the JUnit test case for the GameBoard and
 * the GameBoardActivity can use the GameBoard model class. This interface has a single method,
 * display(), which is called as a callback method to notify that a full GameBoard has been fetched
 * from the webservice.
 */
public interface Displayable {

    /**
     * Called when a full GameBoard has been fetched.
     *
     * @param board a reference to the model GameBoard
     */
    void display(GameBoard board);
}
