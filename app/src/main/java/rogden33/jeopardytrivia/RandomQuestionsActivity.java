package rogden33.jeopardytrivia;

import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import rogden33.jeopardytrivia.model.Clue;
import rogden33.jeopardytrivia.model.QuestionBank;


public class RandomQuestionsActivity extends ActionBarActivity {

    public static final String LOADING_TEXT = "Loading...";
    private QuestionBank myQuestionBank;

    private Button[] myAnswerButtons;

    private boolean myFirstDisplayFlag = true;

    private Clue myClue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_random_questions);
        } else {
            setContentView(R.layout.activity_random_questions_landscape);
        }
        myQuestionBank = new QuestionBank(this);
        Button nextButton = (Button) findViewById(R.id.randomQuestions_Button_next);
        TextView clue = (TextView) findViewById(R.id.randomQuestions_TextView_clueDisplay);
        TextView cate = (TextView) findViewById(R.id.randomQuestions_TextView_categoryDisplay);
        nextButton.setEnabled(false);
        clue.setText(LOADING_TEXT);
        cate.setText(LOADING_TEXT);
        Button choiceA = (Button) findViewById(R.id.randomQuestions_Button_choiceA);
        Button choiceB = (Button) findViewById(R.id.randomQuestions_Button_choiceB);
        Button choiceC = (Button) findViewById(R.id.randomQuestions_Button_choiceC);
        Button choiceD = (Button) findViewById(R.id.randomQuestions_Button_choiceD);
        myAnswerButtons = new Button[] {choiceA, choiceB, choiceC, choiceD};
        final RandomQuestionsActivity parent = this;
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
