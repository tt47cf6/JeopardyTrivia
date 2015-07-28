package rogden33.jeopardytrivia.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import rogden33.jeopardytrivia.RandomQuestionsActivity;

/**
 * Created by Robert on 7/27/2015.
 */
public class QuestionBank {

    private static final int BATCH_SIZE = 50;

    private static final double QUEUE_REFILL_SIZE = 10;

    private final Queue<Clue> myRandomClues;

    private final List<String> myRandomAnswers;

    private final RandomQuestionsActivity myActivity;

    private final Random myRandom;

    private final QuestionBank mySelf = this;

    public QuestionBank(final RandomQuestionsActivity act) {
        myRandomClues = new LinkedList<Clue>();
        myRandomAnswers = new ArrayList<String>();
        myRandom = new Random();
        myActivity = act;
        getNextBatch();
    }

    public boolean ready() {
        return !myRandomClues.isEmpty();
    }

    public Clue getNextRandom() {
        if (myRandomClues.size() <= QUEUE_REFILL_SIZE) {
            getNextBatch();
        }
        return myRandomClues.poll();

    }

    public String getRandomAnswer() {
        return myRandomAnswers.get(myRandom.nextInt(myRandomAnswers.size()));
    }

    private void getNextBatch() {
        ConnectivityManager connMgr = (ConnectivityManager) myActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new QuestionGetter().execute("http://jservice.io/api/random?count=" + BATCH_SIZE);
        } else {
            Toast.makeText(myActivity, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    private class QuestionGetter extends AsyncTask<String, Void,
            String> {


        private static final String TAG = "QuestionGetter";

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to get more questions.";
            }
        }

        private String downloadUrl(String myurl) throws
                IOException {
            InputStream is = null;
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection)
                        url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(TAG, "The response is: " + response);
                is = conn.getInputStream();
                // Convert the InputStream into a string
                String contentAsString = readIt(is);


                Log.d(TAG, "The string is: " + contentAsString);
                return contentAsString;
            } catch (Exception e) {
                Log.d(TAG, "Something happened" +
                        e.getMessage());
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return null;
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream) throws
                IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray jsonarray = new JSONArray(s);
                for (int i = 0; i < jsonarray.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject)
                                jsonarray.get(i);
                        String id = "" + ((Integer) jsonObject.get("id"));
                        String clue = (String) jsonObject.get("question");
                        String response = (String) jsonObject.get("answer");
                        String category = (String) ((JSONObject) jsonObject.get("category")).get("title");
                        int difficulty = jsonObject.getInt("value");
                        Clue newClue = new Clue(mySelf, clue.replace("\\'", "'"), response.replace("\\'", "'"), category.replace("\\'", "'"), id, difficulty);
                        myRandomClues.add(newClue);
                        myRandomAnswers.add(response.replace("\\'", "'"));
                    } catch (JSONException e) {
                        // skip, was just a bad question
                    }
                }
                myActivity.display();
            } catch (JSONException e) {
                Toast.makeText(myActivity, "Could not get more questions", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
