package rogden33.jeopardytrivia.model;

/**
 * Created by Robert on 7/27/2015.
 */
public class Clue {

    public static final int NUMBER_OF_ANSWERS = 4;

    public final String myClue;

    public final String myResponse;

    public final String myCategory;

    public final String myID;

    public final int myDifficulty;

    public final String[] mySelectableAnswers;

    public Clue(String clue, String response, String category, String id, int difficulty) {
        myClue = clue;
        myResponse = response;
        myCategory = category;
        myID = id;
        myDifficulty = difficulty;
        mySelectableAnswers = new String[NUMBER_OF_ANSWERS];
        mySelectableAnswers[0] = myResponse;
        for (int i = 1; i < mySelectableAnswers.length; i++) {

        }
    }

    public String getClue() {
        return myClue.replace("\\'", "'");
    }

    public String getResponse() {
        return myResponse.replace("\\'", "'");
    }

    public String getCategory() {
        return myCategory.replace("\\'", "'");
    }

    public String getID() {
        return myID;
    }

    public String[] getSelectableAnswers() {
        return mySelectableAnswers;
    }

    public int getDifficulty() {
        return myDifficulty;
    }
}
