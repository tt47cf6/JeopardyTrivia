package rogden33.jeopardytrivia;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import rogden33.jeopardytrivia.model.Clue;

/**
 * A simple activity that is called whenever the user hits a daily double. Before displaying the
 * SingleClueActivity, a wager is gotten from the user while they view their current score and
 * the category of the question. Wagers cannot be greater than a user's current score. If a user's
 * score is zero or less, the player can wager up to $1000.
 */
public class DailyDoubleActivity extends ActionBarActivity {

    /**
     * The Bundle key for the username.
     */
    public static final String USERNAME_BUNDLE_KEY = "rogden33.DailyDoubleActivity.usernameKey";

    /**
     * The Bundle key for the current user's score.
     */
    public static final String SCORE_BUNDLE_KEY = "rogden33.DailyDoubleActivity.scoreKey";

    /**
     * The Bundle key for the Clue, serialized.
     */
    public static final String CLUE_BUNDLE_KEY = "rogden33.DailyDoubleActivity.clueKey";

    /**
     * When the user's score is zero or less, allow the player to wager up to this amount.
     * Jeopardy rules set this at $1000.
     */
    private static final int NEGATIVE_SCORE_WAGER_TO = 1000;

    /**
     * The player's username.
     */
    private String myUsername;

    /**
     * The player's current score.
     */
    private int myScore;

    /**
     * The clue that is being played against.
     */
    private Clue myClue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_double);
        // hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // if there is not a saved instance stat, get fields from the intent
        if (savedInstanceState == null) {
            myUsername = getIntent().getStringExtra(USERNAME_BUNDLE_KEY);
            myScore = getIntent().getIntExtra(SCORE_BUNDLE_KEY, 100);
            myClue = (Clue) getIntent().getSerializableExtra(CLUE_BUNDLE_KEY);
        } else {
            // otherwise, get fields from the savedInstanceState
            myUsername = savedInstanceState.getString(USERNAME_BUNDLE_KEY);
            myScore = savedInstanceState.getInt(SCORE_BUNDLE_KEY);
            myClue = (Clue) savedInstanceState.getSerializable(CLUE_BUNDLE_KEY);
        }
        // set the score display
        TextView score = (TextView) findViewById(R.id.dailyDouble_TextView_scoreDisplay);
        score.setText("$" + myScore);
        // set the category display
        TextView category = (TextView) findViewById(R.id.dailyDouble_TextView_categoryDisplay);
        category.setText(Html.fromHtml(myClue.getCategory()));
        // notify the user of max wager when their score is zero or less
        if (myScore <= 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.dailyDouble_toast_negativeZeroScore) + NEGATIVE_SCORE_WAGER_TO, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Save the username, score, and clue when the Activity is closing.
     *
     * @param outState the Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(USERNAME_BUNDLE_KEY, myUsername);
        outState.putInt(SCORE_BUNDLE_KEY, myScore);
        outState.putSerializable(CLUE_BUNDLE_KEY, myClue);
    }

    /**
     * The onClick method for the Submit button. First check that a wager has been entered, and
     * is within the allowed range. Then go to the SingleClueActivity with the wager as the clue
     * value.
     *
     * @param view the current view
     */
    public void submit(View view) {
        EditText wagerEntry = (EditText) findViewById(R.id.dailyDouble_EditText_wagerEntry);
        Editable entry = wagerEntry.getText();
        // check that a wager was given
        if (entry == null || entry.toString().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.dailyDouble_toast_noWagerEntered), Toast.LENGTH_SHORT).show();
            return;
        }
        // check the wager bounds
        int maxWager = myScore <= 0 ? NEGATIVE_SCORE_WAGER_TO : myScore;
        int wager = Integer.parseInt(entry.toString());
        if (wager > maxWager) {
            Toast.makeText(getApplicationContext(), getString(R.string.dailyDouble_toast_highWagerEntered), Toast.LENGTH_SHORT).show();
            return;
        }
        // start the SingleClueActivity
        Intent intent = new Intent(this, SingleClueActivity.class);
        intent.putExtra(SingleClueActivity.PRIZE_VALUE_EXTRA_KEY, wager);
        intent.putExtra(SingleClueActivity.USERNAME_EXTRA_KEY, myUsername);
        intent.putExtra(SingleClueActivity.CLUE_EXTRA_KEY, myClue);
        startActivity(intent);
        // close this activity
        finish();
    }

}
