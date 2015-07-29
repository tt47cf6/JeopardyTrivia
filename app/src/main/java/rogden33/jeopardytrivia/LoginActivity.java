package rogden33.jeopardytrivia;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import rogden33.jeopardytrivia.database.UsersDB;


public class LoginActivity extends ActionBarActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.add(R.id.login_frameLayout_fragmentContainer, new LoginUserListFragment());
        trans.commit();
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
     * Takes the user to a new activity to create a new user account. After that is done,
     * this activity will be restored so that the user can login with thier new credentials.
     *
     */
    public void registerNewUser(View v) {

    }
}
