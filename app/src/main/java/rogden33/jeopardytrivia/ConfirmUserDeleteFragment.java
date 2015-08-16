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

/**
 * A confirmation-type Dialog Fragment for checking if the user actually intends to delete the
 * current user. If so, the user is removed from the database and SharedPref, if not, nothing
 * happens.
 */
public class ConfirmUserDeleteFragment extends DialogFragment {

    /**
     * {@inheritDoc}
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.confirmDelete_confirmationPrompt))
                // yes, delete user
                .setPositiveButton(getString(R.string.confirmDelete_positiveText), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // delete user from SQLite
                        String username = ((MainMenuActivity) getActivity()).getUsername();
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
                // no, do not delete user
                .setNegativeButton(getString(R.string.confirmDelete_negativeText), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        return builder.create();

    }

    /**
     * {@inheritDoc}
     * Sets the color of the buttons.
     */
    @Override
    public void onStart() {
        super.onStart();
        Button positive = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
        Button negative = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_NEGATIVE);
        positive.setTextColor(Color.BLACK);
        negative.setTextColor(Color.BLACK);
    }
}
