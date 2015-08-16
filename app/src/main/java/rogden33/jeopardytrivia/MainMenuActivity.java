package rogden33.jeopardytrivia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

/**
 * The Main Menu activity. Right now, this Activity only shows the current user and their current
 * score, as well as a button to go to the RandomQuestions activity. To support smooth orientation
 * and view change, the current score TextView is updated each time the activity is resumed with the
 * most recent entry in the SharedPreferences for the given username.
 */
public class MainMenuActivity extends ActionBarActivity {

    /**
     * A key entry for the username extra passed in from the LoginAttemptFragment.
     */
    public static final String USER_EXTRA_ID = "rogden33.MainMenuActivity.UserExtra";

    /**
     * The username of the current user.
     */
    private String myUsername;

    /**
     * The current score of the current user.
     */
    private int myScore;

    /**
     * Sets the content view and grabs the username String extra.
     *
     * @param savedInstanceState unused saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the layout based on the orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main_menu);
        } else {
            setContentView(R.layout.activity_main_menu_landscape);
        }
        // hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // get uername extra
        String username = getIntent().getStringExtra(USER_EXTRA_ID);
        myUsername = username;
    }

    /**
     * Updates the activity's TextViews with the most up to date username and score.
     */
    public void showUser() {
        TextView usernameDisplay = (TextView) findViewById(R.id.mainMenu_TextView_username);
        TextView highscoreDisplay = (TextView) findViewById(R.id.mainMenu_TextView_score);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // use new lines since we have the room in portrait
            usernameDisplay.setText("USERNAME\n" + myUsername);
            highscoreDisplay.setText("SCORE\n$" + myScore);
        } else {
            // probably not enough room, keep info on the same line
            usernameDisplay.setText("USERNAME: " + myUsername);
            highscoreDisplay.setText("SCORE: $" + myScore);
        }

    }

    /**
     * Returns the current user's username. Used by the delete DialogFragment.
     *
     * @return the player's username
     */
    public String getUsername() {
        return myUsername;
    }

    /**
     * On resume, get the user's score from SharedPreferences. The score defaults to 0 if there was
     * no valid entry found. In order to effectiently store all users' score, a static prefix is
     * used with the SharedPreferences key, concatenated before the username.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // get score
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), MODE_PRIVATE);
        myScore = sharedPreferences.getInt(
                getString(R.string.MainMenu_SharedPref_Score_Prefix) + myUsername, 0);
        // update view
        showUser();
    }

    /**
     * The onClick listener for the delete user button. This button shows the confirmation dialog
     * which does the rest of the work.
     *
     * @param v the current view
     */
    public void deleteUser(View v) {
        ConfirmUserDeleteFragment dialog = new ConfirmUserDeleteFragment();
        dialog.show(getFragmentManager(), "confirmDelete" );
    }

    /**
     * Statically defined onclick listener for the RandomQuestions button in this activity. Starts
     * the RandomQuestions activity.
     *
     * @param v the current view
     */
    public void randomOnClick(View v) {
        Intent intent = new Intent(this, RandomQuestionsActivity.class);
        intent.putExtra(RandomQuestionsActivity.USERNAME_EXTRA_KEY, myUsername);
        startActivity(intent);
    }

    /**
     * The onClick listener for the view score activity button.
     *
     * @param v the current view
     */
    public void viewScoreOnClick(View v) {
        Intent intent = new Intent(this, ViewScoresActivity.class);
        intent.putExtra(ViewScoresActivity.USERNAME_EXTRA_KEY, myUsername);
        startActivity(intent);
    }

    /**
     * The onClick listener for the gameBoard play activity button.
     *
     * @param view the current view
     */
    public void gameBoardOnClick(View view) {
        Intent intent = new Intent(this, GameBoardActivity.class);
        intent.putExtra(GameBoardActivity.USERNAME_EXTRA_KEY, myUsername);
        startActivity(intent);
    }



}
