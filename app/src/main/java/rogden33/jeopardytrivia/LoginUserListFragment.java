package rogden33.jeopardytrivia;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
 * A fragment representing a list of Items.
 * <p>
 */
public class LoginUserListFragment extends Fragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LoginUserListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        UsersDB db = new UsersDB(getActivity());
        final List<String> users = db.getAllUsernames();
        Log.i("UserFrag", users.toString());
        db.closeDB();
        View v = inflater.inflate(R.layout.fragment_login_user_list, container,
                false);
        ListView userList = (ListView)
                v.findViewById(R.id.login_listView_usersFragment);
        ArrayAdapter<String> adapter = new
                ArrayAdapter<String>(v.getContext(),
                android.R.layout.simple_list_item_1
                , android.R.id.text1, users);
        userList.setAdapter(adapter);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int i, long l) {
                Log.i("UserFrag", "You clicked user " + users.get(i));
                // set up fragment
                LoginAttemptFragment frag = new LoginAttemptFragment();
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
        Button registerButton = (Button) v.findViewById(R.id.login_Button_newUserButton);
        registerButton.setOnClickListener(new Button.OnClickListener() {

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
