package rogden33.jeopardytrivia.model;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This model class fetches and contains a whole game board for use in the GameBoardActivity
 * class. The number of categories and rows can be configured from arguments passed to the
 * constructor. This class does all the handling of exceptions and skipping over bad clues and
 * bad categories from the webservice. Once a full board has been fetched, the display method
 * is called on the class that instantiated this object. The client code can then use
 * getClue() and getCategories() to get the needed information out of the board.
 */
public class GameBoard {

    /**
     * The number of possible responses to have in a clue.
     */
    private static final int SELECTABLE_RESPONSES = 4;

    /**
     * The number of rows to fetch.
     */
    private final int myNumberOfRows;

    /**
     * The number of categories to fetch.
     */
    private final int myNumberOfCategories;

    /**
     * A double array of String clues.
     */
    private final String[][] myClues;

    /**
     * A double array of String the correct responses corresponding to the clues.
     */
    private final String[][] myCorrectResponses;

    /**
     * An array of the category titles.
     */
    private final String[] myCategories;

    /**
     * A reference to a singular random object which is used multiple times in this class.
     */
    private final Random myRandom;

    /**
     * The object to notify when this class is done fetching.
     */
    private final Displayable myActivity;

    /**
     * A reference to this class, needed for passing it in the private class.
     */
    private final GameBoard mySelf = this;

    public GameBoard(Displayable activity, int rows, int cats) {
        // initialize all the fields
        myNumberOfRows = rows;
        myNumberOfCategories = cats;
        myClues = new String[rows][cats];
        myCorrectResponses = new String[rows][cats];
        myRandom = new Random();
        myActivity = activity;
        myCategories = new String[myNumberOfCategories];
    }

    /**
     * Returns a copy of the categories in a String[]
     *
     * @return the categories
     */
    public String[] getCategories() {
        return myCategories.clone();
    }

    /**
     * Creates a Clue object to return based on the information that was fetched for the respective
     * category and row indices.
     *
     * @param row the row index
     * @param cat the category index
     * @return the Clue for the given location
     * @throws IllegalArgumentException if the indices are out of bounds
     */
    public Clue getClue(int row, int cat) throws IllegalArgumentException {
        // check if either index is out of bounds
        if (row >= myNumberOfRows || cat >= myNumberOfCategories) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        // get the basic information
        String clue = myClues[row][cat];
        String correctResponse = myCorrectResponses[row][cat];
        String category = myCategories[cat];
        // get the selectable responses and shuffle them
        List<String> responses = getPossibleAnswers(row, cat);
        Collections.shuffle(responses);
        // put responses in an array and find the correct response
        String[] possible = new String[SELECTABLE_RESPONSES];
        int correctIndex = -1;
        for (int i = 0; i < SELECTABLE_RESPONSES; i++) {
            // check if this is the correct index
            if (responses.get(i).equals(correctResponse)) {
                correctIndex = i;
            }
            possible[i] = responses.get(i);
        }
        // return a new clue, but leave out the original prize value and index, they're not needed
        return new Clue(clue, correctResponse, category, "", 0, possible, correctIndex);
    }

    /**
     * Given the row and category index, generate a list of possible responses, including the
     * correct response. The List is generated from other responses in the same category, to make
     * it a little bit harder, unless the number of rows is less than the number of
     * SELECTABLE_RESPONSES, in which case responses are taken from all over the board.
     *
     * @param row the row index
     * @param cat the category index
     * @return a List of the possible responses
     */
    private List<String> getPossibleAnswers(int row, int cat) {
        // use a Set to ensure no duplicates
        Set<String> possible = new HashSet<String>();
        // add the correct response
        possible.add(myCorrectResponses[row][cat]);
        // take from all over the board if the number of rows is too small
        int catIndex = cat;
        if (myNumberOfRows < SELECTABLE_RESPONSES) {
            catIndex = myRandom.nextInt(myNumberOfCategories);
        }
        // keep adding responses while more are needed
        while (possible.size() < SELECTABLE_RESPONSES) {
            int randRow = myRandom.nextInt(myNumberOfRows);
            possible.add(myCorrectResponses[randRow][catIndex]);
        }
        return new ArrayList<String>(possible);
    }

    /**
     * Called by the client code to start the backgrounded execution to fetch a board. Network
     * connectivity should have already been asserted before calling execute().
     */
    public void requestBoard() {
        new BoardGetter().execute();
    }

    /**
     * The AsyncTask that gets a board with random categories.
     */
    private class BoardGetter extends AsyncTask<Void, Void,
            Void> {

        /**
         * A List of category IDs, fetched at random, that could be used to get question from.
         */
        private List<String> myCategoryIDs;

        /**
         * Starts by fetching random categories, then based on those categories, fetch clues
         * from them. Do this as long as it takes to fill a board without error. Network
         * connectivity should have already been asserted before calling execute().
         *
         * @param v void
         * @return void
         */
        @Override
        protected Void doInBackground(Void... v) {
            myCategoryIDs = new LinkedList<String>();
            try {
                // index tracks the category index that needs to be filled
                int index = 0;
                // get some categories to start off with
                fetchMoreCategories();
                // while there are more categories to fill
                while (index < myNumberOfCategories) {
                    // refill the list of categories if needed
                    while (myCategoryIDs.isEmpty()) {
                        fetchMoreCategories();
                    }
                    // attempt to fill a category
                    index = fillOneCategory(myCategoryIDs.remove(0), index);
                }
            } catch (IOException e) {
                // connectivity testing is assumed to have already happened,
                // so we should never get here
            }
            return null;
        }

        /**
         * Fetches random questions and pulls the category out of every question. Each category is
         * pushed onto the category list so that the whole category can be fetched and filled in
         * later.
         *
         * @throws IOException if there is an IOException
         */
        private void fetchMoreCategories() throws IOException {
            // we'll use a set instead of the list here, so convert it over
            Set<String> categories = new HashSet<String>(myCategoryIDs);
            myCategoryIDs.clear();
            // open the connection
            URL url = new URL("http://jservice.io/api/random?count=50");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            // read in the response
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            // parse JSON object
            try {
                JSONArray jsonarray = new JSONArray(builder.toString());
                for (int i = 0; i < jsonarray.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) jsonarray.get(i);
                        String category = jsonObject.getJSONObject("category").getString("id");
                        categories.add(category);
                    } catch (JSONException e) {
                        // that's ok, just skip
                    }
                }
                // fill the List with the set
                myCategoryIDs.addAll(categories);
            } catch (JSONException e) {
                // that's ok, we'll get to try again
            }
        }

        /**
         * Given a category id, and an index to fill, attempt to fill in that index with the
         * specified category. If successful, return the next index to fill. If not successful,
         * return the current category index to retry filling it with another category.
         *
         * @param categoryID the id of the category to fetch
         * @param catIndex   the category index of the arrays to fill
         * @return the next category index if successful, the current one if not
         * @throws IOException if there is an IO issue
         */
        private int fillOneCategory(String categoryID, int catIndex) throws IOException {
            // open the connection
            URL url = new URL("http://jservice.io/api/category?id=" + categoryID);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            // read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            try {
                // pull out the root information
                JSONObject object = new JSONObject(builder.toString());
                JSONArray cluesArray = (object).getJSONArray("clues");
                String categoryTitle = object.getString("title");
                // if there are not enough responses in the category, just return
                if (cluesArray.length() < myNumberOfRows) {
                    return catIndex;
                }
                for (int row = 0; row < cluesArray.length(); row++) {
                    // get the clue and response
                    JSONObject jsonObject = (JSONObject) cluesArray.get(row);
                    String response = jsonObject.getString("answer");
                    String clue = jsonObject.getString("question");
                    // if there is any null or empty value, skip this category
                    if (response == null || clue == null || response.length() == 0 || clue.length() == 0) {
                        return catIndex;
                    }
                    // if there are more rows that need filling, fill them
                    if (row < myNumberOfRows) {
                        myClues[row][catIndex] = clue;
                        myCorrectResponses[row][catIndex] = response;
                    } else {
                        // otherwise, let's just end the for loop
                        row = cluesArray.length();
                    }
                }
                // set the category titles
                myCategories[catIndex] = categoryTitle.toUpperCase();
                // success
                return catIndex + 1;
            } catch (JSONException e) {
                // JSON parsing error, skip this category
                return catIndex;
            }
        }

        /**
         * Called when the board has been filled and we can let the calling code know that the
         * Clues are ready for display.
         *
         * @param v void
         */
        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            myActivity.display(mySelf);
        }
    }
}
