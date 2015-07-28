package rogden33.jeopardytrivia.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Robert on 7/27/2015.
 */
public class Clue {

    public static final int NUMBER_OF_ANSWERS = 4;

    private final QuestionBank myBank;

    private final String myClue;

    private final String myResponse;

    private final String myCategory;

    private final String myID;

    private final int myDifficulty;

    private final String[] mySelectableAnswers;

    private int myCorrectResponseIndex;

    public Clue(QuestionBank qb, String clue, String response, String category, String id, int difficulty) {
        myBank = qb;
        myClue = clue;
        myResponse = response;
        myCategory = category;
        myID = id;
        myDifficulty = difficulty;
        mySelectableAnswers = new String[NUMBER_OF_ANSWERS];
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

    public String[] getSelectableAnswers() {
        Set<String> possibleSet = new HashSet<String>();
        possibleSet.add(myResponse);
        while (possibleSet.size() < NUMBER_OF_ANSWERS) {
            possibleSet.add(myBank.getRandomAnswer());
        }
        List<String> shuffleMe = new ArrayList<String>(possibleSet);
        Collections.shuffle(shuffleMe);
        for (int i = 0; i < NUMBER_OF_ANSWERS; i++) {
            if (shuffleMe.get(i).equals(myResponse)) {
                myCorrectResponseIndex = i;
            }
            mySelectableAnswers[i] = shuffleMe.get(i);
        }
        return mySelectableAnswers.clone();
    }

    public int getDifficulty() {
        return myDifficulty;
    }

    public int getCorrectResponseIndex() { return myCorrectResponseIndex; }

}
