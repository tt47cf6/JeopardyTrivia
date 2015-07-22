package rogden33.jeopardytrivia;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import rogden33.jeopardytrivia.database.UsersDB;
import rogden33.jeopardytrivia.model.User;


public class LoginActivity extends ActionBarActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Attempts to login a user. The username/password is checked against the SQLite DB
     * and if successful, the user is taken to the next Activity. The login activity is not
     * added to the back stack.
     *
     * @param view the current view object
     */
    public void loginSubmit(View view) {
        UsersDB database = new UsersDB(this);
        EditText usernameEntry = (EditText) findViewById(R.id.login_editText_username);
        EditText pinEntry = (EditText) findViewById(R.id.login_editText_pin);
        String username = usernameEntry.getText().toString();
        String pin = pinEntry.getText().toString();
        User user = database.login(username, pin);
        if (user == null) {
            Toast.makeText(this, "Not authenticated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Welcome, " + username, Toast.LENGTH_LONG).show();
        }
        database.closeDB();
    }

    /**
     * Takes the user to a new activity to create a new user account. After that is done,
     * this activity will be restored so that the user can login with thier new credentials.
     *
     * @param view the current view object
     */
    public void registerNewUser(View view) {
        UsersDB database = new UsersDB(this);
        EditText usernameEntry = (EditText) findViewById(R.id.login_editText_username);
        EditText pinEntry = (EditText) findViewById(R.id.login_editText_pin);
        String username = usernameEntry.getText().toString();
        String pin = pinEntry.getText().toString();
        database.newUser(username, pin);
    }
}
