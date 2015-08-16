package rogden33.jeopardytrivia.model;

import java.io.Serializable;

/**
 * An immutable class that holds all the fields needed in a clue/response pair, as well as
 * other random answers selected from the RandomQuestionBank. This is used for multiple choice selection
 * when that is chosen in an Activity or Fragment.
 */
public class Clue implements Serializable {

    /**
     * The clue.
     */
    private final String myClue;

    /**
     * The response.
     */
    private final String myResponse;

    /**
     * The category.
     */
    private final String myCategory;

    /**
     * The question ID from the JService webserivce.
     */
    private final String myID;

    /**
     * The monetary value, mapped to difficulty of a question.
     */
    private final int myDifficulty;

    /**
     * An array of possible answers, including the correct response.
     */
    private final String[] mySelectableAnswers;

    /**
     * The index in the mySelectableAnswers array of the correct response.
     */
    private final int myCorrectResponseIndex;

    /**
     * A constructor that sets all the final fields.
     *
     * @param clue the clue
     * @param response the response
     * @param category the category
     * @param id the JService ID
     * @param difficulty the difficulty
     * @param possible the other possible selections
     * @param correctIndex the index of the correct response in the possible selections
     */
    public Clue(String clue, String response, String category, String id, int difficulty, String[] possible, int correctIndex) {
        // ' are escaped in the web service, so take out the escapes here
        myClue = clue.replace("\\", "");
        myResponse = response.replace("\\", "");
        myCategory = category.replace("\\", "");
        myID = id;
        myDifficulty = difficulty;
        mySelectableAnswers = possible.clone();
        myCorrectResponseIndex = correctIndex;
        for (int i = 0; i < mySelectableAnswers.length; i++) {
            mySelectableAnswers[i] = mySelectableAnswers[i].replace("\\", "");
        }
    }

    /**
     * A getter for the clue.
     *
     * @return the clue
     */
    public String getClue() {
        return myClue;
    }

    /**
     * A getter for the response.
     *
     * @return the response
     */
    public String getResponse() {
        return myResponse;
    }

    /**
     * A getter for the category.
     *
     * @return the category
     */
    public String getCategory() {
        return myCategory;
    }

    /**
     * A getter for the id.
     *
     * @return the ID
     */
    public String getID() {
        return myID;
    }

    /**
     * A getter for the selectable answers.
     *
     * @return the selectable answers
     */
    public String[] getSelectableAnswers() {
        return mySelectableAnswers.clone();
    }

    /**
     * A getter for the difficulty.
     *
     * @return the diffucilty
     */
    public int getDifficulty() {
        return myDifficulty;
    }

    /**
     * A getter for the correct response index.
     *
     * @return the correct response index
     */
    public int getCorrectResponseIndex() {
        return myCorrectResponseIndex;
    }

}
