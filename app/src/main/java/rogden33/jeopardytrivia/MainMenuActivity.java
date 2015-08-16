package rogden33.jeopardytrivia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import rogden33.jeopardytrivia.database.UsersDB;

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
    public String myUsername;

    /**
     * The current score of the current user.
     */
    public int myScore;

    /**
     * Sets the content view and grabs the username String extra.
     *
     * @param savedInstanceState unused saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
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
        usernameDisplay.setText("Username: " + myUsername);
        highscoreDisplay.setText("Score: " + myScore);
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
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    /**
     * When a menu item is clicked, check if it is the delete user menu item. If so, delete
     * this user from the SQLiteDB and finish this activity. The user's score is also deleted.
     *
     * @param item the menu item that was clicked
     * @return {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mainMenu_MenuItem_deleteUser) {
            // delete user from SQLite
            UsersDB db = new UsersDB(this);
            db.deleteUser(myUsername);
            db.closeDB();
            // remove SharedPref entry
            SharedPreferences sharedPreferences =
                    getSharedPreferences(
                            getString(R.string.SHARED_PREFS),
                            Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(getString(R.string.MainMenu_SharedPref_Score_Prefix) + myUsername);
            editor.apply();
            // return to login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            // finish the current activity
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void viewScoreOnClick(View v) {
        Intent intent = new Intent(this, ViewScores.class);
        intent.putExtra(ViewScores.USERNAME_EXTRA_KEY, myUsername);
        startActivity(intent);
    }

    public void gameBoardOnClick(View view) {
        Intent intent = new Intent(this, GameBoardActivity.class);
        intent.putExtra(GameBoardActivity.USERNAME_EXTRA_KEY, myUsername);
        startActivity(intent);
    }


    public void finalOnClick(View v) {
        Intent intent = new Intent(this, FinalJeopardyActivity.class);
        intent.putExtra(FinalJeopardyActivity.USERNAME_BUNDLE_KEY, myUsername);
        intent.putExtra(FinalJeopardyActivity.SCORE_BUNDLE_KEY, myScore);
        startActivity(intent);
    }
}
