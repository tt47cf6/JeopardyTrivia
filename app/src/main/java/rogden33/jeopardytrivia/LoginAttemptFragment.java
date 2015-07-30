package rogden33.jeopardytrivia;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import rogden33.jeopardytrivia.database.UsersDB;


/**
 * A simple fragment to attempt to authenticate the user that was selected in the previous fragment,
 * LoginUserListFragment. The username is passed in as an argument to this class. Only if the user
 * is authenticated, the Activity changes to the MainMenu activity. If the user is not authenticated
 * a Toast is shown.
 */
public class LoginAttemptFragment extends Fragment {

    /**
     * The key for the username argument in the Bundle.
     */
    public static final String USERNAME_ARG = "rogden33.LoginAttemptFragment.USERNAME_ARG";

    /**
     * {@inheritDoc}
     */
    public LoginAttemptFragment() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     * When the view is first created, the username that is attempting to be logged in under is
     * displayed. The login button is then given a listener that attempts to authenticate the user
     * in the SQLite DB. If successful, an Intent is made to go to the MainMenu activity and this
     * activity is finished since the user does not need to return here.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_attempt, container, false);
        // get username
        Bundle args = getArguments();
        final String username = args.getString(USERNAME_ARG);
        // display username
        TextView usernameView = (TextView) v.findViewById(R.id.login_TextView_loginAttempt_usernameDisplay);
        usernameView.setText("Username: " + username);
        // configure button
        Button loginButton = (Button) v.findViewById(R.id.login_Button_loginAttempt_submit);
        loginButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersDB db = new UsersDB(getActivity());
                // get pin
                EditText pinEntry = (EditText) getActivity().findViewById(R.id.login_EditText_loginAttempt_pinEntry);
                if (pinEntry.getText() == null || pinEntry.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "Enter your pin", Toast.LENGTH_LONG).show();
                    return;
                }
                String pin = pinEntry.getText().toString();
                // attempt to login
                String authenticatedUser = db.login(username, pin);
                db.closeDB();
                if (authenticatedUser == null) {
                    // not authenticated
                    Toast.makeText(getActivity(), "Could not authenticate", Toast.LENGTH_LONG).show();
                } else {
                    // authentication successful, intent to MainMenu
                    Intent intent = new Intent(getActivity(), MainMenuActivity.class);
                    intent.putExtra(MainMenuActivity.USER_EXTRA_ID, authenticatedUser);
                    startActivity(intent);
                    Toast.makeText(getActivity(), "Welcome, " + username, Toast.LENGTH_SHORT).show();
                    // finish this activity
                    getActivity().finish();
                }
            }
        });
        return v;
    }


}
