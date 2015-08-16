package rogden33.jeopardytrivia;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

/**
 * This is the starting Activity where a user logs in. All of th functionality of this class is
 * done in fragments. The first fragment to display and select usernames is added in the onCreate
 * method.
 */
public class LoginActivity extends ActionBarActivity {

    /**
     * {@inheritDoc}
     * Loads in the LoginUserList fragment to display a list of all users.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_login);
        } else {
            setContentView(R.layout.activity_login_landscape);
        }
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.add(R.id.login_frameLayout_fragmentContainer, new LoginUserListFragment());
        trans.commit();
    }

}
