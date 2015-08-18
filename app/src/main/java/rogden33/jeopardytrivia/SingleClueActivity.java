package rogden33.jeopardytrivia;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import rogden33.jeopardytrivia.model.Clue;

/**
 * This class does the game play for a single clue, which is used in the full game board play,
 * including Final Jeopardy. The Clue, clue prize value, and username are all given as extras to
 * this class.
 */
public class SingleClueActivity extends ActionBarActivity {

    /**
     * The Bundle key for the Clue.
     */
    public static final String CLUE_EXTRA_KEY = "rogden33.SingleClueActivity.clueExtra";

    /**
     * The Bundle key for the prize value.
     */
    public static final String PRIZE_VALUE_EXTRA_KEY = "rogden33.SingleClueActivity.prizeExtra";

    /**
     * The Bundle key for the player's username.
     */
    public static final String USERNAME_EXTRA_KEY = "rogden33.SingleClueActivity.usernameExtra";

    /**
     * The Bundle key for the prize multipler.
     */
    private static final String PRIZE_MULTIPLIER_BUNDLE_KEY = "rogden33.SingleClueActivity.prizeMultipler";

    /**
     * The Clue that is being answered.
     */
    private Clue myClue;

    /**
     * The prize value of the clue.
     */
    private int myPrizeValue;

    /**
     * The player's username.
     */
    private String myUsername;

    /**
     * The prize multiplier which is used to track if a user answered correctly or incorrectly.
     * This value is multiplied with the prize value and that is then added to the user's score.
     * If a player answers correctly, this is 1; incorrectly, -1.
     */
    private int myPrizeMultiplier;

    /**
     * The player's current score.
     */
    private int myScore;

    /**
     * {@inheritDoc}
     * Sets the field values based on the Intent or savedInstanceState if it is not null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // set fields to values from the intent
            myClue = (Clue) getIntent().getSerializableExtra(CLUE_EXTRA_KEY);
            myPrizeValue = getIntent().getIntExtra(PRIZE_VALUE_EXTRA_KEY, 100);
            myUsername = getIntent().getStringExtra(USERNAME_EXTRA_KEY);
            myPrizeMultiplier = 0;
        } else {
            // set fields to calues from the savedInstanceState
            myClue = (Clue) savedInstanceState.getSerializable(CLUE_EXTRA_KEY);
            myPrizeValue = savedInstanceState.getInt(PRIZE_VALUE_EXTRA_KEY);
            myUsername = savedInstanceState.getString(USERNAME_EXTRA_KEY);
            myPrizeMultiplier = savedInstanceState.getInt(PRIZE_MULTIPLIER_BUNDLE_KEY);
        }
        // get the currect score from sharedPref
        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        getString(R.string.SHARED_PREFS),
                        Context.MODE_PRIVATE);
        myScore = sharedPreferences.getInt(
                getString(R.string.MainMenu_SharedPref_Score_Prefix) + myUsername, 0);
        // hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // set the layout based on the current orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_single_clue);
        } else {
            setContentView(R.layout.activity_single_clue_landscape);
        }
    }

    /**
     * Save the current state of the class.
     *
     * @param outState the Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CLUE_EXTRA_KEY, myClue);
        outState.putString(USERNAME_EXTRA_KEY, myUsername);
        outState.putInt(PRIZE_VALUE_EXTRA_KEY, myPrizeValue);
        outState.putInt(PRIZE_MULTIPLIER_BUNDLE_KEY, myPrizeMultiplier);
    }

    /**
     * Save the score to sharedPref on pause, with consideration to the prizeMultipler.
     */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        getString(R.string.SHARED_PREFS),
                        Context.MODE_PRIVATE);
        myScore += myPrizeMultiplier * myPrizeValue;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.MainMenu_SharedPref_Score_Prefix) + myUsername, myScore);
        editor.apply();
    }

    /**
     * Populate the layout on resume.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // get reference to the needed objects
        TextView category = (TextView) findViewById(R.id.singleClue_TextView_categoryDisplay);
        TextView clue = (TextView) findViewById(R.id.singleClue_TextView_clueDisplay);
        Button buttonA = (Button) findViewById(R.id.singleClue_Button_choiceA);
        Button buttonB = (Button) findViewById(R.id.singleClue_Button_choiceB);
        Button buttonC = (Button) findViewById(R.id.singleClue_Button_choiceC);
        Button buttonD = (Button) findViewById(R.id.singleClue_Button_choiceD);
        TextView prize = (TextView) findViewById(R.id.singleClue_TextView_prizeDisplay);
        TextView score = (TextView) findViewById(R.id.singleClue_TextView_scoreDisplay);
        // set the category and clue displays
        category.setText(Html.fromHtml(myClue.getCategory()));
        clue.setText(Html.fromHtml(myClue.getClue()));
        // set the prize value and score displays
        prize.setText(" $" + myPrizeValue);
        score.setText(" $" + myScore);
        // set the button texts
        Button[] buttons = new Button[] {buttonA, buttonB, buttonC, buttonD};
        String[] possible = myClue.getSelectableAnswers();
        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            button.setText(Html.fromHtml(possible[i]));
            // set the onClick listeners to set the prize multipler accordingly
            final boolean isCorrect = i == myClue.getCorrectResponseIndex();
            button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isCorrect) {
                        // correct response
                        myPrizeMultiplier = 1;
                        Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
                    } else {
                        // incorrect response
                        myPrizeMultiplier = -1;
                        Toast.makeText(getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                    }
                    // finish the activity
                    finish();
                }
            });
        }

    }


}
