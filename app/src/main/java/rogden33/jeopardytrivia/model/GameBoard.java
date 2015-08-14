package rogden33.jeopardytrivia.model;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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

import rogden33.jeopardytrivia.GameBoardActivity;

public class GameBoard {

    private static final int SELECTABLE_RESPONSES = 4;

    private final int myNumberOfRows;

    private final int myNumberOfCategories;

    private String[][] myClues;

    private String[][] myCorrectResponses;

    private String[] myCategories;

    private Random myRandom;

    private GameBoardActivity myActivity;

    private GameBoard mySelf = this;

    public GameBoard(GameBoardActivity activity, int rows, int cats) {
        myNumberOfRows = rows;
        myNumberOfCategories = cats;
        myClues = new String[rows][cats];
        myCorrectResponses = new String[rows][cats];
        myRandom = new Random();
        myActivity = activity;
        myCategories = new String[myNumberOfCategories];
    }

    private void setClueResponse(int row, int cat, String clue, String response) throws IllegalArgumentException {
        if (row >= myNumberOfRows || cat >= myNumberOfCategories) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        myClues[row][cat] = clue;
        myCorrectResponses[row][cat] = response;
    }

    public String[] getCategories() {
        return myCategories.clone();
    }

    public Clue getClue(int row, int cat) throws IllegalArgumentException {
        if (row >= myNumberOfRows || cat >= myNumberOfCategories) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        String clue = myClues[row][cat];
        String[] possible = new String[SELECTABLE_RESPONSES];
        int correctIndex = -1;
        String category = myCategories[cat];
        List<String> responses = getPossibleAnswers(row, cat);
        String correct = responses.get(0);
        Collections.shuffle(responses);
        for (int i = 0; i < SELECTABLE_RESPONSES; i++) {
            String resp = responses.get(i);
            if (resp.equals(correct)) {
                correctIndex = i;
            }
            possible[i] = responses.get(i);
        }
        return new Clue(clue, correct, category, "", 0, possible, correctIndex);
    }

    private List<String> getPossibleAnswers(int row, int cat) {
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

    public void requestBoard() {
        new RandomCategoryGetter().execute();
    }

    private class RandomCategoryGetter extends AsyncTask<Void, Void,
            Void> {

        private List<String> myCategoryIDs;

        @Override
        protected Void doInBackground(Void... v) {
            myCategoryIDs = new LinkedList<String>();
            try {
                int index = 0;
                fetchMoreCategories();
                while (index < myNumberOfCategories) {
                    if (myCategoryIDs.isEmpty()) {
                        fetchMoreCategories();
                    }
                    index = fillOneCategory(myCategoryIDs.remove(0), index);
                }
            } catch (Exception e) {

            }
            return null;
        }

        private void fetchMoreCategories() throws Exception {
            Set<String> categories = new HashSet<String>(myCategoryIDs);
            myCategoryIDs.clear();
            URL url = new URL("http://jservice.io/api/random?count=50");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            // parse JSON object
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
            myCategoryIDs.addAll(categories);
        }

        private int fillOneCategory(String category, int index) throws Exception {
            URL url = new URL("http://jservice.io/api/category?id=" + category);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            JSONObject object = new JSONObject(builder.toString());
            JSONArray cluesArray = (object).getJSONArray("clues");
            String categroyTitle = object.getString("title");
            if (cluesArray.length() < myNumberOfRows) {
                return index;
            }
            for (int i = 0; i < cluesArray.length(); i++) {
                try {
                    JSONObject jsonObject = (JSONObject) cluesArray.get(i);
                    String response = jsonObject.getString("answer");
                    String clue = jsonObject.getString("question");
                    if (i < myNumberOfRows) {
                        setClueResponse(i, index, clue, response);
                    } else {
                        i = cluesArray.length();
                    }
                } catch (JSONException e) {
                    return index;
                }
            }
            myCategories[index] = categroyTitle;
            return index + 1;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            myActivity.display(mySelf);
        }
    }
}
