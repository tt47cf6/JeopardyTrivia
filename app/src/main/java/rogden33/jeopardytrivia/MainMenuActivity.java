package rogden33.jeopardytrivia;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import rogden33.jeopardytrivia.model.User;


public class MainMenuActivity extends ActionBarActivity {

    public static final String USER_EXTRA_ID = "rogden33.MainMenuActivity.UserExtra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        String serializedUser = getIntent().getStringExtra(USER_EXTRA_ID);
        User user = new User(serializedUser);
        TextView username = (TextView) findViewById(R.id.mainMenu_TextView_username);
        TextView highScore = (TextView) findViewById(R.id.mainMenu_TextView_highScore);
        username.setText(user.getUsername());
        highScore.setText("" + user.getHighScore());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
