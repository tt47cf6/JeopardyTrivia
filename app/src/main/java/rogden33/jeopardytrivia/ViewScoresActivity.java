package rogden33.jeopardytrivia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import rogden33.jeopardytrivia.database.UsersDB;

/**
 * This activity shows all users' scores and displays them on a list with a Share button at the
 * bottom. The list is sorted based on the user's score, highest at the top. This class uses a
 * custom adapter to display the users and scores.
 */
public class ViewScoresActivity extends ActionBarActivity {

    /**
     * The top most number of users to dispaly in the lsit.
     */
    public static final int NUMBER_OF_SCORES = 25;

    /**
     * The Bundle key for the username.
     */
    public static final String USERNAME_EXTRA_KEY = "rogden33.viewScores.usernameExtraKey";

    /**
     * The list of userScorePairs to display.
     */
    private List<UserScorePair> myList;

    /**
     * The current player's username. Used for checking if they have the highest score.
     */
    private String myUsername;

    /**
     * The current player's score.
     */
    private int myScore;

    /**
     * {@inheritDoc}
     * Initializes the fields and sets the list adapter.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // set the layout
        setContentView(R.layout.activity_view_scores);
        // set the list adapter
        myList = getUsers();
        ListView scoreList = (ListView)
                findViewById(R.id.viewScores_ListView_scoreListDisplay);
        UserScorePairAdapter adapter = new UserScorePairAdapter(this, myList);
        scoreList.setAdapter(adapter);
        // set the username and score fields
        myUsername = getIntent().getStringExtra(USERNAME_EXTRA_KEY);
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), MODE_PRIVATE);
        myScore = sharedPreferences.getInt(
                    getString(R.string.MainMenu_SharedPref_Score_Prefix) + myUsername, 0);
    }

    /**
     * The Share button's onClick listener that shares the player's score with other apps that
     * accept plain text sharing. If the user's score is the highest, a different text is passed to
     * share saying that the player has the highest score. Otherwise, just a normal 'check out my
     * score' message is passed.
     *
     * @param v the current view
     */
    public void share(View v) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        if (myList.get(0).myScore == this.myScore) {
            // player has the highest score
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.viewScore_staticHighScoreShareText) + myScore);
        } else {
            // player does not have the highest score
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.viewScore_staticShareText) + myScore);
        }
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.viewScores_shareWith)));
    }

    /**
     * Gets a list of all user score pairs from the database and sharedPref. This list is sorted
     * and then clipped down to 25 responses or less.
     *
     * @return a top 25 list of user score pairs sorted highest to lowes
     */
    private List<UserScorePair> getUsers() {
        // get all usernames
        UsersDB db = new UsersDB(this);
        List<String> allUsernames = db.getAllUsernames();
        db.closeDB();
        List<UserScorePair> result = new LinkedList<UserScorePair>();
        // get corresponding scores
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), MODE_PRIVATE);
        for (String username : allUsernames) {
            int score = sharedPreferences.getInt(
                    getString(R.string.MainMenu_SharedPref_Score_Prefix) + username, 0);
            result.add(new UserScorePair(username, score));
        }
        // sort
        Collections.sort(result);
        // shave off lower scores if there are more than 25
        while (result.size() > NUMBER_OF_SCORES) {
            result.remove(NUMBER_OF_SCORES);
        }
        return result;
    }

    /**
     * A simple, comparable class that holds a username and score pair.
     */
    private class UserScorePair implements Comparable<UserScorePair> {

        /**
         * The username.
         */
        private final String myUser;

        /**
         * The score.
         */
        private final int myScore;

        /**
         * Constructor that initalizes the fields with the passed values.
         *
         * @param user the username
         * @param score the score
         */
        public UserScorePair(String user, int score) {
            myUser = user;
            myScore = score;
        }

        /**
         * Compares based on score in reverse order, highest to lowest.
         *
         * @param other the other object to compare to
         * @return how this object should be sorted
         */
        @Override
        public int compareTo(UserScorePair other) {
            return other.myScore - myScore;
        }

    }

    /**
     * A custom adapter to display both username and score in a single line based on the
     * corresponding custom layout.
     */
    private class UserScorePairAdapter extends ArrayAdapter<UserScorePair> {

        /**
         * {@inheritDoc}
         */
        public UserScorePairAdapter(Context context, List<UserScorePair> list) {
            super(context, 0, list);
        }

        /**
         * {@inheritDoc}
         * Sets the view with the given fields based on the position.
         */
        @Override
        public View getView(int position, View result, ViewGroup parent) {
            // get the object at that position
            UserScorePair pair = getItem(position);
            if (result == null) {
                result = LayoutInflater.from(getContext()).inflate(R.layout.item_userscorepair, parent, false);
            }
            // set the TextViews
            TextView name = (TextView) result.findViewById(R.id.viewScore_TextView_itemLayout_username);
            TextView score = (TextView) result.findViewById(R.id.viewScore_TextView_itemLayout_score);
            name.setText(pair.myUser);
            score.setText("$" + pair.myScore);
            return result;
        }
    }
}
