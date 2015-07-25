package rogden33.jeopardytrivia;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import rogden33.jeopardytrivia.database.UsersDB;
import rogden33.jeopardytrivia.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginAttemptFragment extends Fragment {

    public static final String USERNAME_ARG = "rogden33.LoginAttemptFragment.USERNAME_ARG";


    public LoginAttemptFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_attempt, container, false);
        // get username and show in fragment
        Bundle args = getArguments();
        final String username = args.getString(USERNAME_ARG);
        Button loginButton = (Button) v.findViewById(R.id.login_Button_loginAttempt_submit);
        TextView usernameView = (TextView) v.findViewById(R.id.login_TextView_loginAttempt_usernameDisplay);
        usernameView.setText("Username: " + username);
        // configure button
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
                User user = db.login(username, pin);
                db.closeDB();
                if (user == null) {
                    Toast.makeText(getActivity(), "Could not authenticate", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getActivity(), MainMenuActivity.class);
                    intent.putExtra(MainMenuActivity.USER_EXTRA_ID, user.serialize());
                    startActivity(intent);
                    Toast.makeText(getActivity(), "Welcome, " + username, Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }

            }
        });
        return v;
    }


}
