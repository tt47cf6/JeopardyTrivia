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


public class ViewScoresActivity extends ActionBarActivity {

    public static final int NUMBER_OF_SCORES = 25;
    public static final String USERNAME_EXTRA_KEY = "rogden33.viewScores.usernameExtraKey";

    private List<UserScorePair> myList;

    private String myUsername;

    private int myScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_view_scores);

        myList = getUsers();
        ListView scoreList = (ListView)
                findViewById(R.id.viewScores_ListView_scoreListDisplay);
        UserScorePairAdapter adapter = new UserScorePairAdapter(this, myList);
        scoreList.setAdapter(adapter);

        myUsername = getIntent().getStringExtra(USERNAME_EXTRA_KEY);
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), MODE_PRIVATE);
        myScore = sharedPreferences.getInt(
                    getString(R.string.MainMenu_SharedPref_Score_Prefix) + myUsername, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_scores, menu);
        return true;
    }

    public void share(View v) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        if (myList.get(0).myScore == this.myScore) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.viewScore_staticHighScoreShareText) + myScore);
        } else {
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.viewScore_staticShareText) + myScore);
        }
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.viewScores_shareWith)));
    }

    private List<UserScorePair> getUsers() {
        UsersDB db = new UsersDB(this);
        List<String> allUsernames = db.getAllUsernames();
        db.closeDB();
        List<UserScorePair> result = new LinkedList<UserScorePair>();
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), MODE_PRIVATE);
        for (String username : allUsernames) {
            int score = sharedPreferences.getInt(
                    getString(R.string.MainMenu_SharedPref_Score_Prefix) + username, 0);
            result.add(new UserScorePair(username, score));
        }
        Collections.sort(result);
        while (result.size() > NUMBER_OF_SCORES) {
            result.remove(NUMBER_OF_SCORES);
        }
        return result;
    }

    private class UserScorePair implements Comparable<UserScorePair> {

        private final String myUser;

        private final int myScore;

        public UserScorePair(String user, int score) {
            myUser = user;
            myScore = score;
        }

        @Override
        public int compareTo(UserScorePair other) {
            return other.myScore - myScore;
        }

    }

    private class UserScorePairAdapter extends ArrayAdapter<UserScorePair> {
        public UserScorePairAdapter(Context context, List<UserScorePair> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View result, ViewGroup parent) {
            UserScorePair pair = getItem(position);
            if (result == null) {
                result = LayoutInflater.from(getContext()).inflate(R.layout.item_userscorepair, parent, false);
            }
            TextView name = (TextView) result.findViewById(R.id.viewScore_TextView_itemLayout_username);
            TextView score = (TextView) result.findViewById(R.id.viewScore_TextView_itemLayout_score);
            name.setText(pair.myUser);
            score.setText("$" + pair.myScore);
            return result;
        }
    }
}
