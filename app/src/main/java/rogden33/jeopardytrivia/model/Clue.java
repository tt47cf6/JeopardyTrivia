package rogden33.jeopardytrivia.model;

/**
 * Created by Robert on 7/27/2015.
 */
public class Clue {

    public final String myClue;

    public final String myResponse;

    public final String myCategory;

    public final String myID;

    public final int myDifficulty;

    public Clue(String clue, String response, String category, String id, int difficulty) {
        myClue = clue;
        myResponse = response;
        myCategory = category;
        myID = id;
        myDifficulty = difficulty;
    }

    public String getClue() {
        return myClue;
    }

    public String getResponse() {
        return myResponse;
    }

    public String getCategory() {
        return myCategory;
    }

    public String getID() {
        return myID;
    }

    public int getDifficulty() {
        return myDifficulty;
    }
}
