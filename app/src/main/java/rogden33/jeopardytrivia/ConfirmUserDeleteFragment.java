package rogden33.jeopardytrivia;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import rogden33.jeopardytrivia.database.UsersDB;

public class ConfirmUserDeleteFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // delete user from SQLite
                        String username = ((MainMenuActivity)getActivity()).getUsername();
                        UsersDB db = new UsersDB(getActivity());
                        db.deleteUser(username);
                        db.closeDB();
                        // remove SharedPref entry
                        SharedPreferences sharedPreferences =
                                getActivity().getSharedPreferences(
                                        getString(R.string.SHARED_PREFS),
                                        Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(getString(R.string.MainMenu_SharedPref_Score_Prefix) + username);
                        editor.apply();
                        // return to login activity
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        // finish the current activity
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();

    }

    @Override
    public void onStart() {
        super.onStart();
        Button positive = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
        Button negative = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_NEGATIVE);
        positive.setTextColor(Color.BLACK);
        negative.setTextColor(Color.BLACK);
    }
}
