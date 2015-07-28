package rogden33.jeopardytrivia;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import rogden33.jeopardytrivia.model.Clue;
import rogden33.jeopardytrivia.model.QuestionBank;


public class RandomQuestionsActivity extends ActionBarActivity {

    public static final String LOADING_TEXT = "Loading...";
    private QuestionBank myQuestionBank;

    private boolean myFirstDisplayFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_questions);
        myQuestionBank = new QuestionBank(this);
        Button nextButton = (Button) findViewById(R.id.randomQuestions_Button_next);
        TextView clue = (TextView) findViewById(R.id.randomQuestions_TextView_clueDisplay);
        TextView resp = (TextView) findViewById(R.id.randomQuestions_TextView_responseDisplay);
        TextView cate = (TextView) findViewById(R.id.randomQuestions_TextView_categoryDisplay);
        nextButton.setEnabled(false);
        clue.setText(LOADING_TEXT);
        resp.setText(LOADING_TEXT);
        cate.setText(LOADING_TEXT);
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
        TextView clue = (TextView) findViewById(R.id.randomQuestions_TextView_clueDisplay);
        TextView resp = (TextView) findViewById(R.id.randomQuestions_TextView_responseDisplay);
        TextView cate = (TextView) findViewById(R.id.randomQuestions_TextView_categoryDisplay);
        clue.setText(Html.fromHtml(next.getClue()));
        resp.setText(Html.fromHtml(next.getResponse()));
        cate.setText(Html.fromHtml(next.getCategory()));

    }


}
