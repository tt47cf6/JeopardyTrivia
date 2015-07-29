package rogden33.jeopardytrivia;

import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import rogden33.jeopardytrivia.model.Clue;
import rogden33.jeopardytrivia.model.QuestionBank;


public class RandomQuestionsActivity extends ActionBarActivity {

    public static final String LOADING_TEXT = "Loading...";

    public static final String MYCLUE_BUNDLE_KEY = "rogden33.RandomQuestions.myClue";

    public static final String QUESTIONBANK_BUNDLE_KEY = "rogden33.RandomQuestions.questionBank";

    public static final String BUTTONA_BUNDLE_KEY = "rogden33.RandomQuestions.buttonA";

    public static final String BUTTONB_BUNDLE_KEY = "rogden33.RandomQuestions.buttonB";

    public static final String BUTTONC_BUNDLE_KEY = "rogden33.RandomQuestions.buttonC";

    public static final String BUTTOND_BUNDLE_KEY = "rogden33.RandomQuestions.buttonD";

    private QuestionBank myQuestionBank;

    private Button[] myAnswerButtons;

    private boolean myFirstDisplayFlag = true;

    private Clue myClue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Random", savedInstanceState == null ? "null" : savedInstanceState.toString());
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_random_questions);
        } else {
            setContentView(R.layout.activity_random_questions_landscape);
        }
        Button nextButton = (Button) findViewById(R.id.randomQuestions_Button_next);
        TextView clue = (TextView) findViewById(R.id.randomQuestions_TextView_clueDisplay);
        TextView cate = (TextView) findViewById(R.id.randomQuestions_TextView_categoryDisplay);
        Button choiceA = (Button) findViewById(R.id.randomQuestions_Button_choiceA);
        Button choiceB = (Button) findViewById(R.id.randomQuestions_Button_choiceB);
        Button choiceC = (Button) findViewById(R.id.randomQuestions_Button_choiceC);
        Button choiceD = (Button) findViewById(R.id.randomQuestions_Button_choiceD);
        myAnswerButtons = new Button[] {choiceA, choiceB, choiceC, choiceD};
        final RandomQuestionsActivity parent = this;
        if (savedInstanceState == null) {
            myQuestionBank = new QuestionBank(this);
            nextButton.setEnabled(false);
            clue.setText(LOADING_TEXT);
            cate.setText(LOADING_TEXT);
        } else {
            myClue = (Clue) savedInstanceState.getSerializable(MYCLUE_BUNDLE_KEY);
            myQuestionBank = (QuestionBank) savedInstanceState.getSerializable(QUESTIONBANK_BUNDLE_KEY);
            clue.setText(Html.fromHtml(myClue.getClue()));
            cate.setText(Html.fromHtml(myClue.getCategory()));
            choiceA.setText(savedInstanceState.getString(BUTTONA_BUNDLE_KEY));
            choiceB.setText(savedInstanceState.getString(BUTTONB_BUNDLE_KEY));
            choiceC.setText(savedInstanceState.getString(BUTTONC_BUNDLE_KEY));
            choiceD.setText(savedInstanceState.getString(BUTTOND_BUNDLE_KEY));
        }
        for (int i = 0; i < myAnswerButtons.length; i++) {
            final int index = i;
            Button button = myAnswerButtons[i];
            button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myClue.getCorrectResponseIndex() == index) {
                        Toast.makeText(parent, "Correct!", Toast.LENGTH_SHORT).show();
                        nextClue(null);
                    } else {
                        Toast.makeText(parent, "No, that is not correct", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_random_questions, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Button choiceA = (Button) findViewById(R.id.randomQuestions_Button_choiceA);
        Button choiceB = (Button) findViewById(R.id.randomQuestions_Button_choiceB);
        Button choiceC = (Button) findViewById(R.id.randomQuestions_Button_choiceC);
        Button choiceD = (Button) findViewById(R.id.randomQuestions_Button_choiceD);
        outState.putSerializable(MYCLUE_BUNDLE_KEY, myClue);
        outState.putSerializable(QUESTIONBANK_BUNDLE_KEY, myQuestionBank);
        outState.putString(BUTTONA_BUNDLE_KEY, choiceA.getText().toString());
        outState.putString(BUTTONB_BUNDLE_KEY, choiceB.getText().toString());
        outState.putString(BUTTONC_BUNDLE_KEY, choiceC.getText().toString());
        outState.putString(BUTTOND_BUNDLE_KEY, choiceD.getText().toString());
    }

    public void display() {
        if (myFirstDisplayFlag) {
            Clue next = myQuestionBank.getNextRandom();
            Button nextButton = (Button) findViewById(R.id.randomQuestions_Button_next);
            nextButton.setEnabled(true);
            myFirstDisplayFlag = false;
            nextClue(null);
        }
    }

    public void nextClue(View v) {
        Clue next = myQuestionBank.getNextRandom();
        myClue = next;
        TextView clue = (TextView) findViewById(R.id.randomQuestions_TextView_clueDisplay);
        TextView cate = (TextView) findViewById(R.id.randomQuestions_TextView_categoryDisplay);
        clue.setText(Html.fromHtml(next.getClue()));
        cate.setText(Html.fromHtml(next.getCategory()));
        String[] possibleAnswers = next.getSelectableAnswers();
        for (int i = 0; i < myAnswerButtons.length; i++) {
            myAnswerButtons[i].setText(Html.fromHtml(possibleAnswers[i]));
        }

    }


}
