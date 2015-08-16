package rogden33.jeopardytrivia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import rogden33.jeopardytrivia.model.Clue;
import rogden33.jeopardytrivia.model.Displayable;
import rogden33.jeopardytrivia.model.GameBoard;
import rogden33.jeopardytrivia.model.GameBoardLocation;


public class GameBoardActivity extends ActionBarActivity implements Displayable {

    public static final int NUM_OF_CATEGORIES = 5;

    public static final int NUM_DAILY_DOUBLES = 2;

    public static final int[] PRIZES = {100, 200, 400, 600, 800, 1000};

    private static final String BUNDLE_CATEGORIES_KEY = "rogden33.GameBoardActivity.myCategories";

    private static final String BUNDLE_CLUES_KEY = "rogden33.GameBoardActivity.myClues";

    private static final String BUNDLE_DISPLAY_FLAG_KEY = "rogden33.GameBoardActivity.myDisplayFlag";

    public static final String USERNAME_EXTRA_KEY = "rogden33.GameBoardActivity.usernameExtraKey";

    private static final String LEVEL_BUNDLE_KEY = "rogden33.GameBoardActivity.levelKey";

    private static final String NUM_ANSWERED_KEY = "rogden33.GameBoardActivity.numAnsweredKey";

    private static final String DAILY_DOUBLES_KEY = "rogden33.GameBoardActivity.dailyDoublesKey";

    private String[] myCategories;

    private Clue[][] myClues;

    private boolean myDisplayFlag;

    private String myUsername;

    private int myScore;

    private int myLevel;

    private int myNumberOfCluesAnswered;

    private GameBoardLocation[] myDailyDoubles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        myUsername = getIntent().getStringExtra(USERNAME_EXTRA_KEY);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(getApplicationContext(), getString(R.string.gameBoard_toast_switchToLandscape), Toast.LENGTH_LONG).show();
        }
        if (savedInstanceState == null) {
            new GameBoard(this, PRIZES.length, NUM_OF_CATEGORIES).requestBoard();
            myClues = new Clue[PRIZES.length][NUM_OF_CATEGORIES];
            myDisplayFlag = false;
            myCategories = new String[NUM_OF_CATEGORIES];
            myLevel = 1;
            myNumberOfCluesAnswered = 0;
            Random rand = new Random();
            myDailyDoubles = new GameBoardLocation[NUM_DAILY_DOUBLES];
            Set<GameBoardLocation> doubles = new HashSet<GameBoardLocation>();
            while (doubles.size() < NUM_DAILY_DOUBLES) {
                int x = rand.nextInt(PRIZES.length);
                int y = rand.nextInt(NUM_OF_CATEGORIES);
                doubles.add(new GameBoardLocation(x, y));
            }
            int i = 0;
            for (GameBoardLocation p : doubles) {
                myDailyDoubles[i] = p;
                i++;
            }
        } else {
            myClues = (Clue[][]) savedInstanceState.getSerializable(BUNDLE_CLUES_KEY);
            myCategories = savedInstanceState.getStringArray(BUNDLE_CATEGORIES_KEY);
            myDisplayFlag = savedInstanceState.getBoolean(BUNDLE_DISPLAY_FLAG_KEY);
            myLevel = savedInstanceState.getInt(LEVEL_BUNDLE_KEY);
            myNumberOfCluesAnswered = savedInstanceState.getInt(NUM_ANSWERED_KEY);
            myDailyDoubles = (GameBoardLocation[]) savedInstanceState.getSerializable(DAILY_DOUBLES_KEY);
            TextView title = (TextView) findViewById(R.id.gameBoard_TextView_titleDisplay);
            if (myLevel == 1) {
                title.setText(getString(R.string.gameBoard_singleJeopardyTitle));
            } else if (myLevel == 2) {
                title.setText(getString(R.string.gameBoard_doubleJeopardyTitle));
            }
            populateBoard();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (myDisplayFlag) {
            outState.putStringArray(BUNDLE_CATEGORIES_KEY, myCategories);
            outState.putBoolean(BUNDLE_DISPLAY_FLAG_KEY, myDisplayFlag);
            outState.putSerializable(BUNDLE_CLUES_KEY, myClues);
            outState.putInt(NUM_ANSWERED_KEY, myNumberOfCluesAnswered);
            outState.putInt(LEVEL_BUNDLE_KEY, myLevel);
            outState.putSerializable(DAILY_DOUBLES_KEY, myDailyDoubles);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), MODE_PRIVATE);
        myScore = sharedPreferences.getInt(
                getString(R.string.MainMenu_SharedPref_Score_Prefix) + myUsername, 0);
        TextView scoreView = (TextView) findViewById(R.id.gameBoard_TextView_scoreDisplay);
        scoreView.setText(" $" + myScore);
        if (myNumberOfCluesAnswered == NUM_OF_CATEGORIES * PRIZES.length && myLevel == 1) {
            doDoubleJeopardy();
        } else if(myNumberOfCluesAnswered == NUM_OF_CATEGORIES * PRIZES.length && myLevel == 2) {
            doFinalJeopardy();
        }
    }

    public void display(GameBoard board) {
        myCategories = board.getCategories();
        for(int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            for (int row = 0; row < PRIZES.length; row++) {
                myClues[row][cat] = board.getClue(row, cat);
            }
        }
        myDisplayFlag = true;
        TextView title = (TextView) findViewById(R.id.gameBoard_TextView_titleDisplay);
        if (myLevel == 1) {
            title.setText(getString(R.string.gameBoard_singleJeopardyTitle));
        } else if (myLevel == 2) {
            title.setText(getString(R.string.gameBoard_doubleJeopardyTitle));
        }
        populateBoard();
    }

    private void doDoubleJeopardy() {
        myLevel++;
        myNumberOfCluesAnswered = 0;
        TextView title = (TextView) findViewById(R.id.gameBoard_TextView_titleDisplay);
        title.setText(getResources().getString(R.string.gameBoard_title_loadingText));
        for(int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            int catID = getResources().getIdentifier("gameBoard_TextView_category" + cat, "id", getPackageName());
            TextView categoryDisplay = (TextView) findViewById(catID);
            categoryDisplay.setText("");
        }
        Random rand = new Random();
        Set<GameBoardLocation> doubles = new HashSet<GameBoardLocation>();
        while (doubles.size() < NUM_DAILY_DOUBLES) {
            int x = rand.nextInt(PRIZES.length);
            int y = rand.nextInt(NUM_OF_CATEGORIES);
            doubles.add(new GameBoardLocation(x, y));
        }
        int i = 0;
        for (GameBoardLocation p : doubles) {
            myDailyDoubles[i] = p;
            i++;
        }
        new GameBoard(this, PRIZES.length, NUM_OF_CATEGORIES).requestBoard();
    }

    private void doFinalJeopardy() {
        if (myScore <= 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.gameBoard_toast_noFinalJeopardy), Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, FinalJeopardyActivity.class);
        intent.putExtra(FinalJeopardyActivity.USERNAME_BUNDLE_KEY, myUsername);
        intent.putExtra(FinalJeopardyActivity.SCORE_BUNDLE_KEY, myScore);
        startActivity(intent);
        finish();
    }

    private void populateBoard() {
        for(int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            int catID = getResources().getIdentifier("gameBoard_TextView_category" + cat, "id", getPackageName());
            TextView categoryDisplay = (TextView) findViewById(catID);
            categoryDisplay.setText(myCategories[cat]);
            for (int row = 0; row < PRIZES.length; row++) {
                final int rowF = row;
                final int catF = cat;
                String name = String.format("gameBoard_Button_row%d_col%d", row, cat);
                int id = getResources().getIdentifier(name, "id", getPackageName());
                final Button button = (Button) findViewById(id);
                final Clue clue = myClues[row][cat];
                if (clue == null) {
                    button.setText("");
                    button.setEnabled(false);
                } else {
                    button.setText("$" + (PRIZES[row] * myLevel));
                    button.setEnabled(true);
                    final int prizeValue = PRIZES[row] * myLevel;
                    button.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isDouble = false;
                            for (GameBoardLocation p : myDailyDoubles) {
                                isDouble = isDouble || (p.getX() == rowF && p.getY() == catF);
                                Log.i("DEBUG", "" + (p.getX() == rowF && p.getY() == catF));
                            }
                            if (isDouble) {
                                Intent intent = new Intent(getApplicationContext(), DailyDoubleActivity.class);
                                intent.putExtra(DailyDoubleActivity.CLUE_BUNDLE_KEY, clue);
                                intent.putExtra(DailyDoubleActivity.SCORE_BUNDLE_KEY, myScore);
                                intent.putExtra(DailyDoubleActivity.USERNAME_BUNDLE_KEY, myUsername);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), SingleClueActivity.class);
                                intent.putExtra(SingleClueActivity.CLUE_EXTRA_KEY, clue);
                                intent.putExtra(SingleClueActivity.PRIZE_VALUE_EXTRA_KEY, prizeValue);
                                intent.putExtra(SingleClueActivity.USERNAME_EXTRA_KEY, myUsername);
                                startActivity(intent);
                            }
                            button.setEnabled(false);
                            button.setText("");
                            myClues[rowF][catF] = null;
                            myNumberOfCluesAnswered++;
                        }
                    });
                }
            }
        }
    }

}
