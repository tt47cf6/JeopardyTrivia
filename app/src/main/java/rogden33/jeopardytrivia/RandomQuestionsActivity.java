package rogden33.jeopardytrivia;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import rogden33.jeopardytrivia.model.Clue;
import rogden33.jeopardytrivia.model.RandomQuestionBank;

/**
 * An activity to play with random clues and responses. Responses are selected from an array of
 * other random responses, only one of which is correct. For each correctly chosen response, 5
 * points are added to the user's score. For each incorrectly chosen response, 1 point is taken
 * from the score. Clues may be skipped without penalty.
 * This activity is designed to work in either portrait or landscape orientations. When the device's
 * orientation changed, this activity is restarted with all state information going into the
 * savedInstanceState Bundle. This allows for resetting of the content view to another layout
 * designed for landscpae orientation.
 * Upon first loading, this activity shows a static Loading... text until the RandomQuestionBank
 * uses the display() callback method to inform this activity that there are now clues to display.
 */
public class RandomQuestionsActivity extends ActionBarActivity {

    /**
     * The key for the username in the saved instance state bundle.
     */
    public static final String USERNAME_EXTRA_KEY = "rogden33.RandomQuestions.usernameExtra";

    /**
     * The static loading text when the activity first starts, before the RandomQuestionBank has loaded.
     */
    public static final String LOADING_TEXT = "Loading...";

    /**
     * The key for the Clue in the saved instance state bundle.
     */
    public static final String MYCLUE_BUNDLE_KEY = "rogden33.RandomQuestions.myClue";

    /**
     * The key for the RandomQuestionBank in the saved instance state bundle.
     */
    public static final String QUESTIONBANK_BUNDLE_KEY = "rogden33.RandomQuestions.questionBank";

    /**
     * The key for the first response selection button in the saved instance state bundle.
     */
    public static final String BUTTONA_BUNDLE_KEY = "rogden33.RandomQuestions.buttonA";

    /**
     * The key for the second response selection button in the saved instance state bundle.
     */
    public static final String BUTTONB_BUNDLE_KEY = "rogden33.RandomQuestions.buttonB";

    /**
     * The key for the third response selection button in the saved instance state bundle.
     */
    public static final String BUTTONC_BUNDLE_KEY = "rogden33.RandomQuestions.buttonC";

    /**
     * The key for the fourth response selection button in the saved instance state bundle.
     */
    public static final String BUTTOND_BUNDLE_KEY = "rogden33.RandomQuestions.buttonD";

    public static final String STREAK_BUNDLE_KEY = "rogden33.RandomQuestions.streak";
    public static final int CORRECT_POINTS = 200;
    public static final int INCORRECT_POINTS = 150;
    public static final int STREAK_RESET = 0;

    /**
     * A reference to the RandomQuestionBank to get the next random clue from.
     */
    private RandomQuestionBank myRandomQuestionBank;

    /**
     * An array of references to the selection buttons. This array allows for prettier code.
     */
    private Button[] myAnswerButtons;

    /**
     * True while the loading text is to be dispalyed. Set to false once the first batch is loaded
     * in the RandomQuestionBank.
     */
    private boolean myFirstDisplayFlag = true;

    /**
     * The current Clue being displayed.
     */
    private Clue myClue;

    /**
     * The username of the current logged in user.
     */
    private String myUsername;

    /**
     * The current score of the user. This is saved to SharedPrefs on pause
     */
    private int myScore;

    private int myStreak;

    /**
     * Sets up the activity. If the savedInstanceState is not null, the previous state is
     * restored. Otherwise, the TextViews are loaded with the static loading text. Either way,
     * each button is also given its on click listener in order to add or subtract points and
     * display a message to the user.
     *
     * @param savedInstanceState the saved instance state. may not be null on orientation change
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        // get username extra
        myUsername = getIntent().getStringExtra(USERNAME_EXTRA_KEY);
        // set content view based on current orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_random_questions);
        } else {
            setContentView(R.layout.activity_random_questions_landscape);
        }
        // get references to all view objects
        Button nextButton = (Button) findViewById(R.id.randomQuestions_Button_next);
        TextView clue = (TextView) findViewById(R.id.randomQuestions_TextView_clueDisplay);
        TextView cate = (TextView) findViewById(R.id.randomQuestions_TextView_categoryDisplay);
        TextView streak = (TextView) findViewById(R.id.randomQuestions_TextView_streakDisplay);
        Button choiceA = (Button) findViewById(R.id.randomQuestions_Button_choiceA);
        Button choiceB = (Button) findViewById(R.id.randomQuestions_Button_choiceB);
        Button choiceC = (Button) findViewById(R.id.randomQuestions_Button_choiceC);
        Button choiceD = (Button) findViewById(R.id.randomQuestions_Button_choiceD);
        myAnswerButtons = new Button[] {choiceA, choiceB, choiceC, choiceD};
        // keep a reference to this activity for use in the Button onclick listeners
        final RandomQuestionsActivity parent = this;
        if (savedInstanceState == null) {
            // no saved state, use loading text
            myRandomQuestionBank = new RandomQuestionBank(this);
            // disable next button until there are questions available
            nextButton.setEnabled(false);
            clue.setText(LOADING_TEXT);
            cate.setText(LOADING_TEXT);
            streak.setText("" + STREAK_RESET);
        } else {
            // restore previous state
            myClue = (Clue) savedInstanceState.getSerializable(MYCLUE_BUNDLE_KEY);
            myRandomQuestionBank = (RandomQuestionBank) savedInstanceState.getSerializable(QUESTIONBANK_BUNDLE_KEY);
            clue.setText(Html.fromHtml(myClue.getClue()));
            cate.setText(Html.fromHtml(myClue.getCategory()));
            streak.setText(savedInstanceState.getString(STREAK_BUNDLE_KEY));
            choiceA.setText(savedInstanceState.getString(BUTTONA_BUNDLE_KEY));
            choiceB.setText(savedInstanceState.getString(BUTTONB_BUNDLE_KEY));
            choiceC.setText(savedInstanceState.getString(BUTTONC_BUNDLE_KEY));
            choiceD.setText(savedInstanceState.getString(BUTTOND_BUNDLE_KEY));
        }
        // create on click listeners for the buttons
        for (int i = 0; i < myAnswerButtons.length; i++) {
            final int index = i;
            Button button = myAnswerButtons[i];
            button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myClue.getCorrectResponseIndex() == index) {
                        // correct selection
                        Toast.makeText(parent.getApplicationContext(), "Correct! +" + CORRECT_POINTS + " points", Toast.LENGTH_SHORT).show();
                        myScore += CORRECT_POINTS;
                        myStreak++;
                        nextClue(null);
                    } else {
                        // incorrect selection
                        Toast.makeText(parent.getApplicationContext(), "Incorrect. -" + INCORRECT_POINTS + " points", Toast.LENGTH_SHORT).show();
                        myScore -= INCORRECT_POINTS;
                        myStreak = STREAK_RESET;
                        ((TextView) findViewById(R.id.randomQuestions_TextView_streakDisplay)).setText("0");
                        ((TextView) findViewById(R.id.randomQuestions_TextView_scoreDisplay)).setText("" + myScore);
                    }
                }
            });
        }
    }

    /**
     * {@inheritDoc
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_random_questions, menu);
        return true;
    }

    /**
     * On resume, load in the current score from SharedPreferences. The key uses a prefix along with
     * the user name for effective use of the SharedPreferences.
     */
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), MODE_PRIVATE);
        myScore = sharedPreferences.getInt(
                getString(R.string.MainMenu_SharedPref_Score_Prefix) + myUsername, 0);
        TextView score = (TextView) findViewById(R.id.randomQuestions_TextView_scoreDisplay);
        score.setText("" + myScore);
    }

    /**
     * On pause, save the current score. The key uses a prefix along with the user name for
     * effective use of the SharedPreferences.
     */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        getString(R.string.SHARED_PREFS),
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.MainMenu_SharedPref_Score_Prefix) + myUsername, myScore);
        editor.apply();
    }

    /**
     * When the activity is forced to close, remember all of the state information using the keys
     * above.
     *
     * @param outState the saved state information
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // get refernece to the buttons
        TextView streak = (TextView) findViewById(R.id.randomQuestions_TextView_streakDisplay);
        Button choiceA = (Button) findViewById(R.id.randomQuestions_Button_choiceA);
        Button choiceB = (Button) findViewById(R.id.randomQuestions_Button_choiceB);
        Button choiceC = (Button) findViewById(R.id.randomQuestions_Button_choiceC);
        Button choiceD = (Button) findViewById(R.id.randomQuestions_Button_choiceD);
        // save state
        outState.putSerializable(MYCLUE_BUNDLE_KEY, myClue);
        outState.putSerializable(QUESTIONBANK_BUNDLE_KEY, myRandomQuestionBank);
        outState.putString(BUTTONA_BUNDLE_KEY, choiceA.getText().toString());
        outState.putString(BUTTONB_BUNDLE_KEY, choiceB.getText().toString());
        outState.putString(BUTTONC_BUNDLE_KEY, choiceC.getText().toString());
        outState.putString(BUTTOND_BUNDLE_KEY, choiceD.getText().toString());
        outState.putString(STREAK_BUNDLE_KEY, streak.getText().toString());
    }

    /**
     * This callback-like method is called each time the RandomQuestionBank fetches another batch of
     * Clues. The first time this is called, load the first random question. Every other time, do
     * nothing.
     */
    public void display() {
        if (myFirstDisplayFlag) {
            // enable next button
            Button nextButton = (Button) findViewById(R.id.randomQuestions_Button_next);
            nextButton.setEnabled(true);
            myFirstDisplayFlag = false;
            // get and display clue
            nextClue(null);
        }
    }

    /**
     * Called whenever the next question should be shown, whether as an onClick method for the
     * Next button, or programmatically in this class. This method loads in the next random question
     * from the RandomQuestionBank and sets the TextViews and Button texts appropriately.
     *
     * @param v the current View. not used, so OK to be null
     */
    public void nextClue(View v) {
        // get and store next clue
        Clue next = myRandomQuestionBank.getNextRandom();
        myClue = next;
        // set TextViews
        TextView clue = (TextView) findViewById(R.id.randomQuestions_TextView_clueDisplay);
        TextView cate = (TextView) findViewById(R.id.randomQuestions_TextView_categoryDisplay);
        TextView score = (TextView) findViewById(R.id.randomQuestions_TextView_scoreDisplay);
        TextView streak = (TextView) findViewById(R.id.randomQuestions_TextView_streakDisplay);
        clue.setText(Html.fromHtml(next.getClue()));
        cate.setText(Html.fromHtml(next.getCategory()));
        score.setText("" + myScore);
        streak.setText("" + myStreak);
        // set Buttons
        String[] possibleAnswers = next.getSelectableAnswers();
        for (int i = 0; i < myAnswerButtons.length; i++) {
            myAnswerButtons[i].setText(Html.fromHtml(possibleAnswers[i]));
        }

    }

    public void quit(View v) {
        finish();
    }


}
