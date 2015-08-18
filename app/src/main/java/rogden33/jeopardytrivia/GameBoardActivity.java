package rogden33.jeopardytrivia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import rogden33.jeopardytrivia.model.Clue;
import rogden33.jeopardytrivia.model.Displayable;
import rogden33.jeopardytrivia.model.GameBoard;
import rogden33.jeopardytrivia.model.GameBoardLocation;

/**
 * The GameBoard activity. This class takes care of single Jeopardy, picking locations for Daily
 * Doubles, resetting the board for DoubleJeopardy, and controlling if a player makes it forward to
 * final Jeopardy.
 */
public class GameBoardActivity extends ActionBarActivity implements Displayable {

    /**
     * The number of categories. Must match the XML Layout
     */
    public static final int NUM_OF_CATEGORIES = 5;

    /**
     * The number of daily doubles to have per board.
     */
    public static final int NUM_DAILY_DOUBLES = 2;

    /**
     * The prize values to assign to each row. Values are doubled in Double Jeopardy.
     */
    public static final int[] PRIZES = {100, 200, 400, 600, 800, 1000};

    /**
     * The Bundle key for the category string array.
     */
    private static final String BUNDLE_CATEGORIES_KEY = "rogden33.GameBoardActivity.myCategories";

    /**
     * The Bundle for the Clues array
     */
    private static final String BUNDLE_CLUES_KEY = "rogden33.GameBoardActivity.myClues";

    /**
     * The Bundle key for the username.
     */
    public static final String USERNAME_EXTRA_KEY = "rogden33.GameBoardActivity.usernameExtraKey";

    /**
     * The Bundle key for the level.
     */
    private static final String LEVEL_BUNDLE_KEY = "rogden33.GameBoardActivity.levelKey";

    /**
     * The Bundle key for the number of clues answered.
     */
    private static final String NUM_ANSWERED_KEY = "rogden33.GameBoardActivity.numAnsweredKey";

    /**
     * The Bundle key for the daily double locations array.
     */
    private static final String DAILY_DOUBLES_KEY = "rogden33.GameBoardActivity.dailyDoublesKey";

    /**
     * An array of the category titles.
     */
    private String[] myCategories;

    /**
     * The array of clues that make up the board.
     */
    private Clue[][] myClues;

    /**
     * The player's username.
     */
    private String myUsername;

    /**
     * The player's score.
     */
    private int myScore;

    /**
     * The current level. Single Jeopardy = 1, Double Jeopardy = 2
     */
    private int myLevel;

    /**
     * The number of clues that have been answered in the current round. This feild is watched to
     * tell when a round is over.
     */
    private int myNumberOfCluesAnswered;

    /**
     * An array of locations for the daily doubles.
     */
    private GameBoardLocation[] myDailyDoubles;

    /**
     * {@inheritDoc}
     * If there is a savedInstanceState, restore the state from that. If not, set fields to their
     * default values.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);
        // hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // get the username
        myUsername = getIntent().getStringExtra(USERNAME_EXTRA_KEY);
        // check for network connectivity, finish if none
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            // no connection
            Toast.makeText(getApplicationContext(), getString(R.string.gameBoard_toast_noConnection), Toast.LENGTH_SHORT).show();
            finish();
        }
        // invite the user to switch to landscape orientation if they are using portrait
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(getApplicationContext(), getString(R.string.gameBoard_toast_switchToLandscape), Toast.LENGTH_LONG).show();
        }
        if (savedInstanceState == null) {
            // get a new game board
            new GameBoard(this, PRIZES.length, NUM_OF_CATEGORIES).requestBoard();
            // set fields to default values
            myClues = new Clue[PRIZES.length][NUM_OF_CATEGORIES];
            myCategories = new String[NUM_OF_CATEGORIES];
            myLevel = 1;
            myNumberOfCluesAnswered = 0;
            Random rand = new Random();
            // come up with random daily double locations
            myDailyDoubles = new GameBoardLocation[NUM_DAILY_DOUBLES];
            // use a set to keep duplicates away
            Set<GameBoardLocation> doubles = new HashSet<GameBoardLocation>();
            while (doubles.size() < NUM_DAILY_DOUBLES) {
                int x = rand.nextInt(PRIZES.length);
                int y = rand.nextInt(NUM_OF_CATEGORIES);
                doubles.add(new GameBoardLocation(x, y));
            }
            // put locations into an array
            int i = 0;
            for (GameBoardLocation p : doubles) {
                myDailyDoubles[i] = p;
                i++;
            }
        } else {
            // restore the previous state
            myClues = (Clue[][]) savedInstanceState.getSerializable(BUNDLE_CLUES_KEY);
            myCategories = savedInstanceState.getStringArray(BUNDLE_CATEGORIES_KEY);
            myLevel = savedInstanceState.getInt(LEVEL_BUNDLE_KEY);
            myNumberOfCluesAnswered = savedInstanceState.getInt(NUM_ANSWERED_KEY);
            myDailyDoubles = (GameBoardLocation[]) savedInstanceState.getSerializable(DAILY_DOUBLES_KEY);
            // set the title
            TextView title = (TextView) findViewById(R.id.gameBoard_TextView_titleDisplay);
            if (myLevel == 1) {
                title.setText(getString(R.string.gameBoard_singleJeopardyTitle));
            } else if (myLevel == 2) {
                title.setText(getString(R.string.gameBoard_doubleJeopardyTitle));
            }
            // repopulate the board
            populateBoard();
        }
    }

    /**
     * Save the previous state.
     *
     * @param outState the Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArray(BUNDLE_CATEGORIES_KEY, myCategories);
        outState.putSerializable(BUNDLE_CLUES_KEY, myClues);
        outState.putInt(NUM_ANSWERED_KEY, myNumberOfCluesAnswered);
        outState.putInt(LEVEL_BUNDLE_KEY, myLevel);
        outState.putSerializable(DAILY_DOUBLES_KEY, myDailyDoubles);
    }

    /**
     * On every resume, set the updated score and check if all the question have been answered
     * and we should go to the next round.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // update the score
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), MODE_PRIVATE);
        myScore = sharedPreferences.getInt(
                getString(R.string.MainMenu_SharedPref_Score_Prefix) + myUsername, 0);
        TextView scoreView = (TextView) findViewById(R.id.gameBoard_TextView_scoreDisplay);
        scoreView.setText(" $" + myScore);
        // check if we are ready for double or final jeopardy
        if (myNumberOfCluesAnswered == NUM_OF_CATEGORIES * PRIZES.length && myLevel == 1) {
            doDoubleJeopardy();
        } else if (myNumberOfCluesAnswered == NUM_OF_CATEGORIES * PRIZES.length && myLevel == 2) {
            doFinalJeopardy();
        }
    }

    /**
     * Called when a full gameBoard has been fetched from the web service. Update the myClues[][].
     * and set the title accordingly.
     *
     * @param board a reference to the model GameBoard a reference to the board model object
     */
    public void display(GameBoard board) {
        // get the categories
        myCategories = board.getCategories();
        // get the Clues
        for (int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            for (int row = 0; row < PRIZES.length; row++) {
                myClues[row][cat] = board.getClue(row, cat);
            }
        }
        // set the title
        TextView title = (TextView) findViewById(R.id.gameBoard_TextView_titleDisplay);
        if (myLevel == 1) {
            title.setText(getString(R.string.gameBoard_singleJeopardyTitle));
        } else if (myLevel == 2) {
            title.setText(getString(R.string.gameBoard_doubleJeopardyTitle));
        }
        // populate the board
        populateBoard();
    }

    /**
     * Called when the first round is finished and double Jeopardy can be played. While waiting for
     * more clues to be fetched, the title is set to Loading, and new Daily double locations are
     * chosen.
     */
    private void doDoubleJeopardy() {
        // increase level and reset number of clues answered
        myLevel++;
        myNumberOfCluesAnswered = 0;
        // set the title to loading
        TextView title = (TextView) findViewById(R.id.gameBoard_TextView_titleDisplay);
        title.setText(getResources().getString(R.string.gameBoard_title_loadingText));
        // clear the category titles
        for (int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            int catID = getResources().getIdentifier("gameBoard_TextView_category" + cat, "id", getPackageName());
            TextView categoryDisplay = (TextView) findViewById(catID);
            categoryDisplay.setText("");
        }
        // choose new daily double locations
        Random rand = new Random();
        Set<GameBoardLocation> doubles = new HashSet<GameBoardLocation>();
        while (doubles.size() < NUM_DAILY_DOUBLES) {
            int x = rand.nextInt(PRIZES.length);
            int y = rand.nextInt(NUM_OF_CATEGORIES);
            doubles.add(new GameBoardLocation(x, y));
        }
        // save new locations to their array
        int i = 0;
        for (GameBoardLocation p : doubles) {
            myDailyDoubles[i] = p;
            i++;
        }
        // request a new game board
        new GameBoard(this, PRIZES.length, NUM_OF_CATEGORIES).requestBoard();
    }

    /**
     * Called when the last clue has been answered in Double Jeopardy. If the player's score is more
     * than zero, they can play, per Jeopardy rules. If so, pass off to FinalJeopardyActivity.
     */
    private void doFinalJeopardy() {
        if (myScore <= 0) {
            // score is zero or less, no final jeopardy
            Toast.makeText(getApplicationContext(), getString(R.string.gameBoard_toast_noFinalJeopardy), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // start final jeopardy
        Intent intent = new Intent(this, FinalJeopardyActivity.class);
        intent.putExtra(FinalJeopardyActivity.USERNAME_BUNDLE_KEY, myUsername);
        intent.putExtra(FinalJeopardyActivity.SCORE_BUNDLE_KEY, myScore);
        startActivity(intent);
        finish();
    }

    /**
     * Populates the board once all the clues are available. The category titles are set,
     * the button values are set, and buttons get a new onClick listener to display their question.
     * Daily Double checking also occurs here.
     */
    private void populateBoard() {
        for (int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            // update the category title
            int catID = getResources().getIdentifier("gameBoard_TextView_category" + cat, "id", getPackageName());
            TextView categoryDisplay = (TextView) findViewById(catID);
            categoryDisplay.setText(myCategories[cat]);
            for (int row = 0; row < PRIZES.length; row++) {
                // get a reference to the button based on the layout formatting
                String name = String.format("gameBoard_Button_row%d_col%d", row, cat);
                int id = getResources().getIdentifier(name, "id", getPackageName());
                final Button button = (Button) findViewById(id);
                // get the clue
                final Clue clue = myClues[row][cat];
                if (clue == null) {
                    // the clue has already been answered
                    button.setText("");
                    button.setEnabled(false);
                } else {
                    // the clue has not been answered
                    // set the button text
                    button.setText("$" + (PRIZES[row] * myLevel));
                    button.setEnabled(true);
                    // make some final variable copies for use in the listener
                    final int prizeValue = PRIZES[row] * myLevel;
                    final int rowF = row;
                    final int catF = cat;
                    button.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // check if this question is a daily double
                            boolean isDouble = false;
                            for (GameBoardLocation p : myDailyDoubles) {
                                isDouble = isDouble || (p.getX() == rowF && p.getY() == catF);
                            }
                            if (isDouble) {
                                // go to the daily double activity
                                Intent intent = new Intent(getApplicationContext(), DailyDoubleActivity.class);
                                intent.putExtra(DailyDoubleActivity.CLUE_BUNDLE_KEY, clue);
                                intent.putExtra(DailyDoubleActivity.SCORE_BUNDLE_KEY, myScore);
                                intent.putExtra(DailyDoubleActivity.USERNAME_BUNDLE_KEY, myUsername);
                                startActivity(intent);
                            } else {
                                // go to the single clue activity
                                Intent intent = new Intent(getApplicationContext(), SingleClueActivity.class);
                                intent.putExtra(SingleClueActivity.CLUE_EXTRA_KEY, clue);
                                intent.putExtra(SingleClueActivity.PRIZE_VALUE_EXTRA_KEY, prizeValue);
                                intent.putExtra(SingleClueActivity.USERNAME_EXTRA_KEY, myUsername);
                                startActivity(intent);
                            }
                            // disable this button and clear its text
                            button.setEnabled(false);
                            button.setText("");
                            // set the clue to null to indicate it has been answered
                            myClues[rowF][catF] = null;
                            // count that another clue was answered
                            myNumberOfCluesAnswered++;
                        }
                    });
                }
            }
        }
    }

}
