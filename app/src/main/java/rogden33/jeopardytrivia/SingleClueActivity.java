package rogden33.jeopardytrivia;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import rogden33.jeopardytrivia.model.Clue;


public class SingleClueActivity extends ActionBarActivity {

    public static final String CLUE_EXTRA_KEY = "rogden33.SingleClueActivity.clueExtra";

    public static final String PRIZE_VALUE_EXTRA_KEY = "rogden33.SingleClueActivity.prizeExtra";

    public static final String USERNAME_EXTRA_KEY = "rogden33.SingleClueActivity.usernameExtra";

    private static final String PRIZE_MULTIPLIER_BUNDLE_KEY = "rogden33.SingleClueActivity.prizeMultipler";

    private Clue myClue;

    private int myPrizeValue;

    private String myUsername;

    private int myPrizeMultiplier;

    private int myScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            myClue = (Clue) getIntent().getSerializableExtra(CLUE_EXTRA_KEY);
            myPrizeValue = getIntent().getIntExtra(PRIZE_VALUE_EXTRA_KEY, 100);
            myUsername = getIntent().getStringExtra(USERNAME_EXTRA_KEY);
            myPrizeMultiplier = 0;
        } else {
            myClue = (Clue) savedInstanceState.getSerializable(CLUE_EXTRA_KEY);
            myPrizeValue = savedInstanceState.getInt(PRIZE_VALUE_EXTRA_KEY);
            myUsername = savedInstanceState.getString(USERNAME_EXTRA_KEY);
            myPrizeMultiplier = savedInstanceState.getInt(PRIZE_MULTIPLIER_BUNDLE_KEY);
        }
        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        getString(R.string.SHARED_PREFS),
                        Context.MODE_PRIVATE);
        myScore = sharedPreferences.getInt(
                getString(R.string.MainMenu_SharedPref_Score_Prefix) + myUsername, 0);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_single_clue);
        } else {
            setContentView(R.layout.activity_single_clue_landscape);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CLUE_EXTRA_KEY, myClue);
        outState.putString(USERNAME_EXTRA_KEY, myUsername);
        outState.putInt(PRIZE_VALUE_EXTRA_KEY, myPrizeValue);
        outState.putInt(PRIZE_MULTIPLIER_BUNDLE_KEY, myPrizeMultiplier);
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        TextView category = (TextView) findViewById(R.id.singleClue_TextView_categoryDisplay);
        TextView clue = (TextView) findViewById(R.id.singleClue_TextView_clueDisplay);
        Button buttonA = (Button) findViewById(R.id.singleClue_Button_choiceA);
        Button buttonB = (Button) findViewById(R.id.singleClue_Button_choiceB);
        Button buttonC = (Button) findViewById(R.id.singleClue_Button_choiceC);
        Button buttonD = (Button) findViewById(R.id.singleClue_Button_choiceD);
        TextView prize = (TextView) findViewById(R.id.singleClue_TextView_prizeDisplay);
        TextView score = (TextView) findViewById(R.id.singleClue_TextView_scoreDisplay);
        category.setText(Html.fromHtml(myClue.getCategory()));
        clue.setText(Html.fromHtml(myClue.getClue()));
        prize.setText(" $" + myPrizeValue);
        score.setText(" $" + myScore);
        Button[] buttons = new Button[] {buttonA, buttonB, buttonC, buttonD};
        String[] possible = myClue.getSelectableAnswers();
        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            button.setText(Html.fromHtml(possible[i]));
            final boolean isCorrect = i == myClue.getCorrectResponseIndex();
            final Activity activity = this;
            button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isCorrect) {
                        myPrizeMultiplier = 1;
                        Toast.makeText(activity.getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
                    } else {
                        myPrizeMultiplier = -1;
                        Toast.makeText(activity.getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                    }
                    activity.finish();
                }
            });
        }

    }


}
