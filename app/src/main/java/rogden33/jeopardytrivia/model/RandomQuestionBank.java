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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import rogden33.jeopardytrivia.RandomQuestionsActivity;

/**
 * This class fetches batches clue and response pairs from the online webservice, JService. Right
 * now, only random clues are fetched but in the future there will be support for fetching of
 * entire Jeopardy game boards as well. Random questions are maintained in a queue. When the queue
 * starts getting low on entries, more clues are fetched. Clues are generated in this class,
 * complete with their array of other possible responses for multiple choice features. During the
 * first batch of questions that is fetched, the first several responses are skipped in order to
 * create a reasonable resource of random responses to pull from.
 */
public class RandomQuestionBank implements Serializable {

    /**
     * The size of the batch to fetch.
     */
    private static final int BATCH_SIZE = 50;

    /**
     * The size at which the queue should be refilled with another batch of questions.
     */
    private static final double QUEUE_REFILL_SIZE = 10;

    /**
     * The number of selectable responses to give to a Clue.
     */
    private static final int SELECTABLE_RESPONSES = 4;

    /**
     * Upon first loading, the point at which to start adding Clues to the queue and not just
     * the myRandomAnswers list.
     */
    private static final int GENERATE_RANDOM_AT = 10;

    /**
     * The queue of random clues.
     */
    private final Queue<Clue> myRandomClues;

    /**
     * A list of random responses taken from each clue in each batch. Random selections for each
     * Clue object are pulled from this list.
     */
    private final List<String> myRandomAnswers;

    /**
     * A reference to the parent RandomQuestionsActivity. This is used to check the network
     * connection status, as well as to inform the activity that the very first batch of questions
     * is ready to be utilized.
     */
    private final RandomQuestionsActivity myActivity;

    /**
     * A Random object which is used multiple times in a method call, so kept as a field for
     * efficiency.
     */
    private final Random myRandom;

    /**
     * A constructor to initialize all the final fields and get the first batch in an Async manner.
     *
     * @param act the parent activity
     */
    public RandomQuestionBank(final RandomQuestionsActivity act) {
        myRandomClues = new ConcurrentLinkedQueue<Clue>();
        myRandomAnswers = Collections.synchronizedList(new ArrayList<String>());
        myRandom = new Random();
        myActivity = act;
        getNextBatch();
    }

    /**
     * Polls the next random Clue from the queue.
     *
     * @return the next Random clue
     */
    public Clue getNextRandom() {
        // get another batch if the queue size is getting low
        if (myRandomClues.size() <= QUEUE_REFILL_SIZE) {
            getNextBatch();
        }
        return myRandomClues.poll();

    }

    /**
     * Generate a List of random responses for a Clue. This method should only be called
     * when there are a sufficient number of responses in the myRandomResponses list to prevent
     * live locking. The correct response is passed in as a parameter so that it can be included in
     * the output with no duplicates.
     *
     * @param correct the correct response
     * @return a List of selectable responses, including the correct response
     * @throws IllegalStateException if there are too few responses in the myRandomAnswers list
     */
    private List<String> getRandomSelections(String correct) throws IllegalStateException {
        // check the myRandomAnswers list is sufficiently sized  to prevent live lock
        if (myRandomAnswers.size() < GENERATE_RANDOM_AT) {
            throw new IllegalStateException("Too few responses to get from!");
        }
        // use a Set to avoid duplications
        Set<String> possible = new HashSet<String>();
        // add in the correct response
        possible.add(correct);
        // put in more responses randomly while more are needed
        while (possible.size() < SELECTABLE_RESPONSES) {
            possible.add(myRandomAnswers.get(myRandom.nextInt(myRandomAnswers.size())));
        }
        return new ArrayList<String>(possible);
    }

    /**
     * Uses the RandomQuestionGetter AsyncTask to get another batch of questions if there is an
     * available network connection.
     */
    private void getNextBatch() {
        ConnectivityManager connMgr = (ConnectivityManager) myActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // ok connection
            new RandomQuestionGetter().execute("http://jservice.io/api/random?count=" + BATCH_SIZE);
        } else {
            // no connection
            Toast.makeText(myActivity, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A helper class to get another batch of random questions. The JService API response is in
     * JSON format, which is parsed in the postExecute method. The handling of the getting
     * selectable responses for a Clue is also done there.
     */
    private class RandomQuestionGetter extends AsyncTask<String, Void,
            String> {

        private static final String TAG = "RandomQuestionGetter";

        /**
         * Fetch a batch of questions.
         *
         * @param urls the url to fetch from
         * @return a string response
         */
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to get more questions.";
            }
        }

        /**
         * Attempts to fetch the web response from the given URL. Returns the response as a String.
         *
         * @param fetchFrom the URL to fetch from
         * @return the response
         * @throws IOException if the connection fails
         */
        private String downloadUrl(String fetchFrom) throws
                IOException {
            InputStream inputStream = null;
            try {
                // setup the connection parameters
                URL url = new URL(fetchFrom);
                HttpURLConnection conn = (HttpURLConnection)
                        url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                inputStream = conn.getInputStream();
                // Convert the InputStream into a string
                String contentAsString = bufferedReadIn(inputStream);
                return contentAsString;
            } catch (Exception e) {
                Log.d(TAG, "Something happened" +
                        e.getMessage());
            } finally {
                // close inputStream if possible
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return null;
        }

        /**
         * Takes in an InputStream and returns a String from all the available data in the stream.
         * This is done with the use of a BufferedReader and a StringBuilder to allow for
         * arbitrarily sized responses.
         *
         * @param stream the input stream
         * @return all available data in the stream
         * @throws IOException if there is a stream problem
         */
        public String bufferedReadIn(InputStream stream) throws
                IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }

        /**
         * Given the http response from the download, parse it as a JSON string with the known
         * schema of the JService web service. Once parsed, construct Clues based on the response
         * and generate random selections for each Clue. Enqueue the Clue and continue for the rest
         * of the batch.
         * There are also two bug fixes here, first the JService web service escapes single
         * quotation marks which are not escaped in the Java string. This is handled manually.
         * Also, sometimes the value field in the JSON is null because the question was used in
         * Final Jeopardy. In this case, the question is skipped when it raises an exception. If the
         * whole httpResponse could not be parsed as a JSON object, a Toast is shown.
         *
         * @param httpResponse
         */
        @Override
        protected void onPostExecute(String httpResponse) {
            super.onPostExecute(httpResponse);
            try {
                // parse JSON object
                JSONArray jsonarray = new JSONArray(httpResponse);
                for (int i = 0; i < jsonarray.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject)
                                jsonarray.get(i);
                        // get fields
                        String id = "" + ((Integer) jsonObject.get("id"));
                        String clue = ((String) jsonObject.get("question")).replace("\\'", "'");
                        String response = ((String) jsonObject.get("answer")).replace("\\'", "'");
                        String category = ((String) ((JSONObject) jsonObject.get("category")).get("title")).replace("\\'", "'");
                        int difficulty = jsonObject.getInt("value");
                        // add to the myRandomAnswers resource
                        myRandomAnswers.add(response);
                        // if the myRandomAnswers list is large enough, start using it to generate
                        // random selection for new Clues.
                        if (myRandomAnswers.size() > GENERATE_RANDOM_AT && !clue.contains("seen here")) {
                            // get random selections and shuffle it
                            List<String> possible = getRandomSelections(response);
                            Collections.shuffle(possible);
                            // while converting to an array, look for the correct response
                            String[] selections = new String[possible.size()];
                            int correctIndex = 0;
                            for (int j = 0; j < selections.length; j++) {
                                selections[j] = possible.get(j);
                                if (selections[j].equals(response)) {
                                    correctIndex = j;
                                }
                            }
                            // create ans enqueue the new Clue
                            Clue newClue = new Clue(clue, response, category, id, difficulty, selections, correctIndex);
                            myRandomClues.add(newClue);
                        }

                    } catch (JSONException e) {
                        // skip, was just a bad question
                    }
                }
                // let the parent Activity know in the event of the first batch that there are now
                // Clues ready to be displayed. This functions like a callback method.
                myActivity.display();
            } catch (JSONException e) {
                // could not parse the JSON object string
                Toast.makeText(myActivity, "Could not get more questions", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
