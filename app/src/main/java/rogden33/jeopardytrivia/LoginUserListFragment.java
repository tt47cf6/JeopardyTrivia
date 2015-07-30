package rogden33.jeopardytrivia;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import rogden33.jeopardytrivia.database.UsersDB;


/**
 * This fragment displays a list of users in a ListView for the user to select. When a username is
 * selected, the fragment is replaced with the LoginAttemptFragment. When that happens, this
 * fragment is added to the back stack to allow the user to return to this list.
 * At the top of this fragment is also a button to register a new user which replaces this fragment
 * with the register user fragment. Once that fragment is complete, the user is returned to this
 * fragment to login with their new credentials.
 */
public class LoginUserListFragment extends Fragment {

    /**
     * {@inheritDoc}
     */
    public LoginUserListFragment() {
        // required empty constructor
    }

    /**
     * {@inheritDoc}
     * Sets up the ListView and ListAdapter. Sets up the item onClick listener to replace
     * this fragment with the LoginAttemptFragment. Also sets up the register button listener.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // get a list of all usernames
        UsersDB db = new UsersDB(getActivity());
        final List<String> users = db.getAllUsernames();
        db.closeDB();
        // initialize the View
        View v = inflater.inflate(R.layout.fragment_login_user_list, container,
                false);
        // set up the ListView adapter
        ListView userList = (ListView)
                v.findViewById(R.id.login_listView_usersFragment);
        ArrayAdapter<String> adapter = new
                ArrayAdapter<String>(v.getContext(),
                android.R.layout.simple_list_item_1
                , android.R.id.text1, users);
        userList.setAdapter(adapter);
        // set up the on item click listener
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * {@inheritDoc}
             * Goes to the LoginUserAttempt fragment, giving the selected username as an argument.
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int i, long l) {
                // set up fragment
                LoginAttemptFragment frag = new LoginAttemptFragment();
                // give username as argument
                Bundle args = new Bundle();
                args.putString(LoginAttemptFragment.USERNAME_ARG, users.get(i));
                frag.setArguments(args);
                // replace with login attempt fragment
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                trans.replace(R.id.login_frameLayout_fragmentContainer, frag);
                trans.addToBackStack(null);
                trans.commit();
            }
        });
        // sets up the register button listener
        Button registerButton = (Button) v.findViewById(R.id.login_Button_newUserButton);
        registerButton.setOnClickListener(new Button.OnClickListener() {
            /**
             * Replaces the current fragment with the RegisterFragment.
             *
             * @param v the current view
             */
            @Override
            public void onClick(View v) {
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                trans.replace(R.id.login_frameLayout_fragmentContainer, new RegisterFragment());
                trans.addToBackStack(null);
                trans.commit();
            }
        });
        return v;
    }

}
