package rogden33.jeopardytrivia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import rogden33.jeopardytrivia.database.UsersDB;


public class MainMenuActivity extends ActionBarActivity {

    public static final String USER_EXTRA_ID = "rogden33.MainMenuActivity.UserExtra";

    public String myUsername;

    public int myScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        String username = getIntent().getStringExtra(USER_EXTRA_ID);
        myUsername = username;
    }

    public void showUser(String username) {
        // temporary, display user fields
        TextView usernameDisplay = (TextView) findViewById(R.id.mainMenu_TextView_username);
        TextView highscoreDisplay = (TextView) findViewById(R.id.mainMenu_TextView_score);
        usernameDisplay.setText("Username: " + username);
        highscoreDisplay.setText("Score: " + myScore);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.SHARED_PREFS), MODE_PRIVATE);
        myScore = sharedPreferences.getInt(
                getString(R.string.MainMenu_SharedPref_Score_Prefix) + myUsername, 0);
        showUser(myUsername);
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mainMenu_MenuItem_deleteUser) {
            UsersDB db = new UsersDB(this);
            db.deleteUser(myUsername);
            db.closeDB();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void randomOnClick(View v) {
        Intent intent = new Intent(this, RandomQuestionsActivity.class);
        intent.putExtra(RandomQuestionsActivity.USERNAME_EXTRA_KEY, myUsername);
        startActivity(intent);
    }

    public void incHighScore(View v) {
        myScore++;
        TextView highscoreDisplay = (TextView) findViewById(R.id.mainMenu_TextView_score);
        highscoreDisplay.setText("Score: " + myScore);
    }


}
