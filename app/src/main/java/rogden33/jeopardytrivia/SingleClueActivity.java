package rogden33.jeopardytrivia;

import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class SingleClueActivity extends ActionBarActivity {

    public static final String CLUE_EXTRA_KEY = "rogden33.SingleClueActivity.clueExtra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_single_clue);
        } else {
            setContentView(R.layout.activity_single_clue_landscape);
        }
    }


}
