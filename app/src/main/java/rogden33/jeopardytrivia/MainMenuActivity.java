package rogden33.jeopardytrivia;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import rogden33.jeopardytrivia.database.UsersDB;
import rogden33.jeopardytrivia.model.User;


public class MainMenuActivity extends ActionBarActivity {

    public static final String USER_EXTRA_ID = "rogden33.MainMenuActivity.UserExtra";

    public User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        String serializedUser = getIntent().getStringExtra(USER_EXTRA_ID);
        User user = new User(serializedUser);
        showUser(user);
        myUser = user;

    }

    public void showUser(User user) {
        // temporary, display user fields
        TextView username = (TextView) findViewById(R.id.mainMenu_TextView_username);
        TextView highScore = (TextView) findViewById(R.id.mainMenu_TextView_highScore);
        username.setText(user.getUsername());
        highScore.setText("" + user.getHighScore());
    }

//    public void showRandomQuestion() {
//        ConnectivityManager connMgr = (ConnectivityManager)
//                getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected()) {
//            new UserWebTask().execute("http://jservice.io/api/random?count=1");
//        } else {
//            Toast.makeText(this, "No network connection available.",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mainMenu_MenuItem_deleteUser) {
            UsersDB db = new UsersDB(this);
            db.deleteUser(myUser.getUsername());
            db.closeDB();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void randomOnClick(View v) {
        Intent intent = new Intent(this, RandomQuestions.class);
        startActivity(intent);
    }


}
