package rogden33.jeopardytrivia;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


public class GameBoardActivity extends ActionBarActivity {

    public static final int NUM_OF_CATEGORIES = 5;

    public static final int[] PRIZES = {100, 200, 400, 600, 800, 1000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        // populate categories
        String[] cats = new String[] {"cat 1", "cat 2", "cat 3", "cat 4", "cat 5"};
        for(int cat = 0; cat < NUM_OF_CATEGORIES; cat++) {
            int catID = getResources().getIdentifier("gameBoard_TextView_category" + cat, "id", getPackageName());
            TextView categoryDisplay = (TextView) findViewById(catID);
            categoryDisplay.setText(cats[cat]);
            for (int row = 0; row < PRIZES.length; row++) {
                String name = String.format("gameBoard_Button_row%d_col%d", row, cat);
                Log.i("NAME", name);
                int id = getResources().getIdentifier(name, "id", getPackageName());
                Button button = (Button) findViewById(id);
                button.setText("$" + (PRIZES[row]));
            }
        }
        // populate prize values



    }


}
