package rogden33.jeopardytrivia;

import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import rogden33.jeopardytrivia.model.Clue;
import rogden33.jeopardytrivia.model.GameBoard;


public class GameBoardActivity extends ActionBarActivity {

    public static final int NUM_OF_CATEGORIES = 5;

    public static final int[] PRIZES = {100, 200, 400, 600, 800, 1000};

    private String[] myCategories;

    private Clue[][] myClues;

    private boolean myDisplayFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        new GameBoard(this, PRIZES.length, NUM_OF_CATEGORIES).requestBoard();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(getApplicationContext(), "You are encouraged to switch to landscape orientation", Toast.LENGTH_LONG).show();
        }
        myClues = new Clue[PRIZES.length][NUM_OF_CATEGORIES];
    }

    public void display(GameBoard board) {
        myCategories = board.getCategories();
        for(int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            for (int row = 0; row < PRIZES.length; row++) {
                myClues[row][cat] = board.getClue(row, cat);
            }
        }
        myDisplayFlag = true;
        populateBoard();
    }

    public void populateBoard() {
        for(int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            int catID = getResources().getIdentifier("gameBoard_TextView_category" + cat, "id", getPackageName());
            TextView categoryDisplay = (TextView) findViewById(catID);
            categoryDisplay.setText(myCategories[cat]);
            for (int row = 0; row < PRIZES.length; row++) {
                String name = String.format("gameBoard_Button_row%d_col%d", row, cat);
                int id = getResources().getIdentifier(name, "id", getPackageName());
                Button button = (Button) findViewById(id);
                button.setText("$" + (PRIZES[row]));
            }
        }
    }


}
