package rogden33.jeopardytrivia;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Set;

import rogden33.jeopardytrivia.model.Clue;


public class FinalJeopardyActivity extends ActionBarActivity {

    public static final String USERNAME_BUNDLE_KEY = "rogden33.FinalJeopardyActivity.usernameKey";

    public static final String SCORE_BUNDLE_KEY = "rogden33.FinalJeopardyActivity.scoreKey";

    private static final String READY_FLAG_BUNDLE_KEY = "rogden33.FinalJeopardyActivity.readyFlagKey";

    private static final String CLUE_BUNDLE_KEY = "rogden33.FinalJeopardyActivity.clueKey";

    private String myUsername;

    private int myScore;

    private boolean myReadyFlag;

    private Clue myClue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_jeopardy);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (savedInstanceState == null) {
            myUsername = getIntent().getStringExtra(USERNAME_BUNDLE_KEY);
            myScore = getIntent().getIntExtra(SCORE_BUNDLE_KEY, 100);
            myReadyFlag = false;
            Button submitButton = (Button) findViewById(R.id.finalJeopardy_Button_submitButton);
            submitButton.setEnabled(false);
            new RandomFinalClueGetter().execute();
        } else {
            myUsername = savedInstanceState.getString(USERNAME_BUNDLE_KEY);
            myScore = savedInstanceState.getInt(SCORE_BUNDLE_KEY);
            myReadyFlag = savedInstanceState.getBoolean(READY_FLAG_BUNDLE_KEY);
            if (myReadyFlag) {
                myClue = (Clue) savedInstanceState.getSerializable(CLUE_BUNDLE_KEY);
                clueReady();
            }
        }
        TextView score = (TextView) findViewById(R.id.finalJeopardy_TextView_scoreDisplay);
        score.setText("$" + myScore);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(USERNAME_BUNDLE_KEY, myUsername);
        outState.putInt(SCORE_BUNDLE_KEY, myScore);
        outState.putBoolean(READY_FLAG_BUNDLE_KEY, myReadyFlag);
        if (myReadyFlag) {
            outState.putSerializable(CLUE_BUNDLE_KEY, myClue);
        }
    }

    public void clueReady() {
        TextView category = (TextView) findViewById(R.id.finalJeopardy_TextView_categoryDisplay);
        Button button = (Button) findViewById(R.id.finalJeopardy_Button_submitButton);
        category.setText(Html.fromHtml(myClue.getCategory()));
        button.setEnabled(true);
        myReadyFlag = true;
    }


    public void submit(View view) {
        EditText wagerEntry = (EditText) findViewById(R.id.finalJeopardy_EditText_wagerEntry);
        Editable entry = wagerEntry.getText();
        if (entry == null || entry.toString().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.finalJeopardy_toast_noWagerEntry), Toast.LENGTH_SHORT).show();
            return;
        }
        int wager = Integer.parseInt(entry.toString());
        if (wager > myScore || wager < 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.finalJeopardy_toast_highWagerEntry), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!myReadyFlag) {
            Toast.makeText(getApplicationContext(), getString(R.string.finalJeopardy_toast_clueNotDownloaded), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, SingleClueActivity.class);
        intent.putExtra(SingleClueActivity.PRIZE_VALUE_EXTRA_KEY, wager);
        intent.putExtra(SingleClueActivity.USERNAME_EXTRA_KEY, myUsername);
        intent.putExtra(SingleClueActivity.CLUE_EXTRA_KEY, myClue);
        startActivity(intent);
        finish();
    }

    private class RandomFinalClueGetter extends AsyncTask<Void, Void,
            Void> {

        private List<String> myCategoryIDs;

        private String myCategory;

        private String myStringClue;

        private String myStringResponse;

        private String[] myWrongResponses;

        @Override
        protected Void doInBackground(Void... v) {
            myCategoryIDs = new LinkedList<String>();
            myWrongResponses = new String[3];
            try {
                int index = 0;
                fetchMoreCategories();
                while (index < 1) {
                    if (myCategoryIDs.isEmpty()) {
                        fetchMoreCategories();
                    }
                    index = fillOneCategory(myCategoryIDs.remove(0), index);
                }
                List<String> responses = new ArrayList<String>();
                responses.add(myStringResponse);
                for (String response : myWrongResponses) {
                    responses.add(response);
                }
                Collections.shuffle(responses);
                String[] possibleResponses = new String[4];
                int correctIndex = -1;
                for (int i = 0; i < 4; i++) {
                    possibleResponses[i] = responses.get(i);

                    if (responses.get(i).equals(myStringResponse)) {
                        correctIndex = i;
                    }
                }
                myClue = new Clue(myStringClue, myStringResponse, myCategory,"", 0, possibleResponses, correctIndex);
            } catch (IOException | JSONException e) {

            }
            return null;
        }

        private void fetchMoreCategories() throws IOException, JSONException {
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

        private int fillOneCategory(String category, int index) throws IOException, JSONException {
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
            if (cluesArray.length() < 4) {
                return index;
            }
            for (int i = 0; i < cluesArray.length(); i++) {
                try {
                    JSONObject jsonObject = (JSONObject) cluesArray.get(i);
                    if (!jsonObject.has("answer") || !jsonObject.has("question")) {
                        return index;
                    }
                    String response = jsonObject.getString("answer");
                    String clue = jsonObject.getString("question");
                    if (response == null || clue == null) {
                        return index;
                    }
                    if (i == 0) {
                        myCategory = categroyTitle.toUpperCase();
                        myStringClue = clue;
                        myStringResponse = response;
                    } else if (i - 1 < myWrongResponses.length) {
                        myWrongResponses[i - 1] = response;
                    } else {
                        i = cluesArray.length();
                    }
                } catch (JSONException e) {
                    return index;
                }
            }
            return index + 1;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            clueReady();
        }
    }
}
