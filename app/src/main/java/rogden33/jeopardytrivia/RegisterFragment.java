package rogden33.jeopardytrivia;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import rogden33.jeopardytrivia.database.UsersDB;

/**
 * A fragment to add a new user to the SQLite database. This fragment ensures that the username
 * selected is unique from usernames already in existence, and that the pins, entered twice, both
 * match. If user creation is successful, this fragment is replaced back with the LoginUserList
 * fragment. If not, the view is unchanged.
 */
public class RegisterFragment extends Fragment {

    /**
     * {@inheritDoc}
     */
    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     * This method creates an onClick listener for the submit button in this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        Button submitButton = (Button) v.findViewById(R.id.login_Button_register_submitButton);
        // keep a reference to this parent class for use in the listener
        final Fragment fragment = this;
        submitButton.setOnClickListener(new Button.OnClickListener() {
            /**
             * This method first checks that all the fields are filled out. If not, the method
             * returns without doing anything and a Toast is shown. If all the fields are filled in,
             * we then check the the two pins match. If they don't, a Toast is shown and the
             * method returns. If the pins do match, we then check that the entered username does
             * not already exist. If this all works, a request is made of the SQLite DB to add the
             * user and the fragment is replaced.
             *
             * @param v the view
             */
            @Override
            public void onClick(View v) {
                UsersDB db = new UsersDB(getActivity());
                // get references to EditTexts
                Editable usernameText = ((EditText) getActivity().findViewById(R.id.login_EditText_register_usernameEntry)).getText();
                Editable pinText = ((EditText) getActivity().findViewById(R.id.login_EditText_register_pinEntry)).getText();
                Editable pinText2 = ((EditText) getActivity().findViewById(R.id.login_EditText_register_pinEntry2)).getText();
                // check for no entry in any EditText
                if (usernameText == null || usernameText.toString().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.login_register_toast_fillInAllFields), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pinText == null || pinText.toString().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.login_register_toast_fillInAllFields), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pinText2 == null || pinText2.toString().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.login_register_toast_fillInAllFields), Toast.LENGTH_SHORT).show();
                    return;
                }
                // get strings from EditTexts
                String username = usernameText.toString();
                String pin = pinText.toString();
                String pin2 = pinText2.toString();
                // make sure username is unique
                List<String> existingUsers = db.getAllUsernames();
                boolean contains = false;
                for (String user : existingUsers) {
                    contains |= user.equalsIgnoreCase(username);
                }
                if (contains) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.login_register_toast_usernameExists), Toast.LENGTH_LONG).show();
                    return;
                }
                // check pins match
                if (!pin.equals(pin2)) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.login_register_toast_pinsDontMatch), Toast.LENGTH_LONG).show();
                    return;
                }
                // submit to DB
                db.newUser(username, pin);
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.login_register_toast_userCreated), Toast.LENGTH_LONG).show();
                // finish
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_frameLayout_fragmentContainer, new LoginUserListFragment()).commit();
            }
        });

        return v;
    }


}
