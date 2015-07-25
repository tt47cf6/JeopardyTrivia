package rogden33.jeopardytrivia;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import rogden33.jeopardytrivia.database.UsersDB;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        Button submitButton = (Button) v.findViewById(R.id.login_Button_register_submitButton);
        final Fragment fragment = this;
        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("HERE I AM", "HELLO");
                UsersDB db = new UsersDB(getActivity());
                Editable usernameText = ((EditText) getActivity().findViewById(R.id.login_EditText_register_usernameEntry)).getText();
                Editable pinText = ((EditText) getActivity().findViewById(R.id.login_EditText_register_pinEntry)).getText();
                Editable pinText2 = ((EditText) getActivity().findViewById(R.id.login_EditText_register_pinEntry2)).getText();
                // check for no entry
                if (usernameText == null || usernameText.toString().length() == 0) {
                    Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pinText == null || pinText.toString().length() == 0) {
                    Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pinText2 == null || pinText2.toString().length() == 0) {
                    Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                String username = usernameText.toString();
                String pin = pinText.toString();
                String pin2 = pinText2.toString();
                // make sure username is unique
                List<String> existingUsers = db.getAllUsernames();
                boolean contains = false;
                for (String user : existingUsers) {
                    contains |= user.equals(username);
                }
                if (contains) {
                    Toast.makeText(getActivity(), "This username already exists!", Toast.LENGTH_LONG).show();
                    return;
                }
                // check pins match
                if (!pin.equals(pin2)) {
                    Toast.makeText(getActivity(), "Pins do not match!", Toast.LENGTH_LONG).show();
                    return;
                }
                // submit to DB
                db.newUser(username, pin);
                Toast.makeText(getActivity(), "User created!", Toast.LENGTH_LONG).show();
                // finish
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_frameLayout_fragmentContainer, new LoginUserListFragment()).commit();
            }
        });

        return v;
    }


}
