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


public class DailyDoubleActivity extends ActionBarActivity {

    public static final String USERNAME_BUNDLE_KEY = "rogden33.DailyDoubleActivity.usernameKey";

    public static final String SCORE_BUNDLE_KEY = "rogden33.DailyDoubleActivity.scoreKey";

    public static final String CLUE_BUNDLE_KEY = "rogden33.DailyDoubleActivity.clueKey";

    private static final int NEGATIVE_SCORE_WAGER_TO = 1000;

    private String myUsername;

    private int myScore;

    private Clue myClue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_double);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (savedInstanceState == null) {
            myUsername = getIntent().getStringExtra(USERNAME_BUNDLE_KEY);
            myScore = getIntent().getIntExtra(SCORE_BUNDLE_KEY, 100);
            myClue = (Clue) getIntent().getSerializableExtra(CLUE_BUNDLE_KEY);
        } else {
            myUsername = savedInstanceState.getString(USERNAME_BUNDLE_KEY);
            myScore = savedInstanceState.getInt(SCORE_BUNDLE_KEY);
            myClue = (Clue) savedInstanceState.getSerializable(CLUE_BUNDLE_KEY);
        }
        TextView score = (TextView) findViewById(R.id.dailyDouble_TextView_scoreDisplay);
        score.setText("$" + myScore);
        TextView category = (TextView) findViewById(R.id.dailyDouble_TextView_categoryDisplay);
        category.setText(Html.fromHtml(myClue.getCategory()));
        if (myScore <= 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.dailyDouble_toast_negativeZeroScore) + NEGATIVE_SCORE_WAGER_TO, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(USERNAME_BUNDLE_KEY, myUsername);
        outState.putInt(SCORE_BUNDLE_KEY, myScore);
        outState.putSerializable(CLUE_BUNDLE_KEY, myClue);
    }


    public void submit(View view) {
        EditText wagerEntry = (EditText) findViewById(R.id.dailyDouble_EditText_wagerEntry);
        Editable entry = wagerEntry.getText();
        if (entry == null || entry.toString().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.dailyDouble_toast_noWagerEntered), Toast.LENGTH_SHORT).show();
            return;
        }
        int maxWager = myScore <= 0 ? NEGATIVE_SCORE_WAGER_TO : myScore;
        int wager = Integer.parseInt(entry.toString());
        if (wager > maxWager) {
            Toast.makeText(getApplicationContext(), getString(R.string.dailyDouble_toast_highWagerEntered), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, SingleClueActivity.class);
        intent.putExtra(SingleClueActivity.PRIZE_VALUE_EXTRA_KEY, wager);
        intent.putExtra(SingleClueActivity.USERNAME_EXTRA_KEY, myUsername);
        intent.putExtra(SingleClueActivity.CLUE_EXTRA_KEY, myClue);
        startActivity(intent);
        finish();
    }

}
