package rogden33.jeopardytrivia;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import rogden33.jeopardytrivia.model.User;


public class MainMenuActivity extends ActionBarActivity {

    public static final String USER_EXTRA_ID = "rogden33.MainMenuActivity.UserExtra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        String serializedUser = getIntent().getStringExtra(USER_EXTRA_ID);
        User user = new User(serializedUser);
        showUser(user);
        // get a random question
        showRandomQuestion();

    }

    public void showUser(User user) {
        // temporary, display user fields
        TextView username = (TextView) findViewById(R.id.mainMenu_TextView_username);
        TextView highScore = (TextView) findViewById(R.id.mainMenu_TextView_highScore);
        username.setText(user.getUsername());
        highScore.setText("" + user.getHighScore());
    }

    public void showRandomQuestion() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new UserWebTask().execute("http://jservice.io/api/random?count=1");
        } else {
            Toast.makeText(this, "No network connection available.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class UserWebTask extends AsyncTask<String, Void,
            String> {
        private static final String TAG = "UserWebTask";

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl) throws
                IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection)
                        url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(TAG, "The response is: " + response);
                is = conn.getInputStream();
                // Convert the InputStream into a string
                String contentAsString = readIt(is);
                Log.d(TAG, "The string is: " + contentAsString);
                return contentAsString;
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch (Exception e) {
                Log.d(TAG, "Something happened" +
                        e.getMessage());
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return null;
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream) throws
                IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[stream.available()];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Parse JSON
            try {
                JSONArray jsonarray = new JSONArray(s);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonObject = (JSONObject)
                            jsonarray.get(i);
                    String clue = (String) jsonObject.get("question");
                    String response = (String) jsonObject.get("answer");
                    TextView clueDisplay = (TextView) findViewById(R.id.mainMenu_TextView_clue);
                    TextView respDisplay = (TextView) findViewById(R.id.mainMenu_TextView_response);
                    clueDisplay.setText(clue);
                    respDisplay.setText(response);
                }

            } catch (JSONException e) {
                Log.d(TAG, "Parsing JSON Exception " +
                        e.getMessage());
            }
        }
    }
}
