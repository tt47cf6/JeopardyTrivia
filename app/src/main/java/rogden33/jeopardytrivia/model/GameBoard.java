package rogden33.jeopardytrivia.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameBoard {

    private static final int SELECTABLE_RESPONSES = 4;

    private final int myNumberOfRows;

    private final int myNumberOfCategories;

    private String[][] myClues;

    private String[][] myCorrectResponses;

    private String[] myCategories;

    private Random myRandom;

    public GameBoard(int rows, int cats) {
        myNumberOfRows = rows;
        myNumberOfCategories = cats;
        myClues = new String[rows][cats];
        myCorrectResponses = new String[rows][cats];
        myRandom = new Random();
    }

    public void setCategories(String[] cats) throws IllegalArgumentException {
        if (cats.length != myNumberOfCategories) {
            throw new IllegalArgumentException("Number of categories does not match!");
        }
        myCategories = cats.clone();
    }

    public void setClueResponse(int row, int cat, String clue, String response) throws IllegalArgumentException {
        if (row >= myNumberOfRows || cat >= myNumberOfCategories) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        myClues[row][cat] = clue;
        myCorrectResponses[row][cat] = response;
    }

    public String[] getCategories() {
        return myCategories.clone();
    }

    public String getClue(int row, int cat) throws IllegalArgumentException {
        if (row >= myNumberOfRows || cat >= myNumberOfCategories) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        return myClues[row][cat];
    }

    public List<String> getPossibleAnswers(int row, int cat) throws IllegalArgumentException {
        if (row >= myNumberOfRows || cat >= myNumberOfCategories) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        Set<String> possible = new HashSet<String>();
        while (possible.size() < SELECTABLE_RESPONSES - 1) {
            int randRow = myRandom.nextInt(myNumberOfRows);
            if (randRow != row) {
                possible.add(myCorrectResponses[randRow][cat]);
            }
        }
        List<String> result = new ArrayList<String>(possible);
        result.add(0, myCorrectResponses[row][cat]);
        return result;
    }
}
