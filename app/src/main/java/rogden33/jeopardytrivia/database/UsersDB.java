package rogden33.jeopardytrivia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper class on Android's SQLite database. This class is specifically designed to handle user
 * login and registration events, including deleting of a user. This database only contains
 * information about user names and their pins, no other information is stored in this class. Under
 * the hood, a user's pin is stored using a SHA-256 secure hash (no salt). When hashed, the pin and
 * username are concatenated together to make it more secure against user-unspecific dictionary
 * attacks. This hashing is done transparently to client classes.
 */
public class UsersDB {

    /**
     * The current version of the database table schema.
     */
    public static final int DB_VERSION = 1;

    /**
     * The filename for the DB file.
     */
    public static final String DB_NAME = "Users.db";

    /**
     * A reference to the SQLite helper class.
     */
    private UserInfoDBHelper myDBHelper;

    /**
     * A reference to the opened SQLite DB object.
     */
    private SQLiteDatabase mySQLDB;

    /**
     * This constructor opens a connection to the database.
     *
     * @param context the context of the Activity or Fragment using this DB
     */
    public UsersDB(Context context) {
        myDBHelper = new UserInfoDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mySQLDB =
                myDBHelper.getWritableDatabase();
    }

    /**
     * Creates a new user in the database. True is returned if the operation succeeds. Should the
     * operation fail, it is because the username already exists in the DB.
     *
     * @param username the username for the new user
     * @param pin the pin in plaintext for the new user
     * @return true if the operation succeeded, false otherwise
     */
    public boolean newUser(String username, String pin) {
        try {
            // hash pin number
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            String hash = new String(hasher.digest((username + pin).getBytes()));
            // insert into DB
            ContentValues contentValues = new ContentValues();
            contentValues.put("username", username);
            contentValues.put("pin", hash);
            long rowId = mySQLDB.insert("Users", null,
                    contentValues);
            return rowId != -1;
        } catch (NoSuchAlgorithmException e) {
            // won't be a problem
            return false;
        }

    }

    /**
     * Returns a list of all existing usernames in the DB, for display in the Login Activity.
     *
     * @return a list of all existing usernames
     */
    public List<String> getAllUsernames() {
        List<String> result = new ArrayList<String>();
        // do query
        Cursor c = mySQLDB.query(
                "Users",
                new String[]{"username"},
                null,
                null,
                null,
                null,
                null
        );
        // generate list
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            result.add(c.getString(0));
            c.moveToNext();
        }
        return result;
    }

    /**
     * Attempts to login a user with the given username and pin pair. If the user is valid and the
     * correct pin was entered, the username is returned. If the user is not valid, or the incorrect
     * pin was entered, null is returned. To check whether the user is authenticated, the pin is
     * hashed with the same algorithm it was stored with (SHA-256). If the hashes match, the user
     * is authenticated.
     *
     * @param username the username that was entered
     * @param pin the pin that was entered, in plaintext
     * @return the username if authenticated, null otherwise
     */
    public String login(String username, String pin) {
        // get entry from DB
        String[] columns = new String[]{"username", "pin"};
        Cursor c = mySQLDB.query(
                "Users",
                columns,
                "username = ?",
                new String[]{username},
                null,
                null,
                null
        );
        // since username is a primary key, there should be exactly one result
        if (c.getCount() != 1) {
            // no entries, bad username, return null
            return null;
        }
        // get the hash of the single result
        c.moveToFirst();
        String pinHash = c.getString(1);
        // hash and check
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            String hash = new String(hasher.digest((username + pin).getBytes()));
            if (hash.equals(pinHash)) {
                return username;
            }
        } catch (NoSuchAlgorithmException e) {
            // do nothing
        }
        // hashes did not match
        return null;
    }

    /**
     * Deletes a given user by username from the DB. If the username does not exist, nothing
     * changes in the DB.
     *
     * @param username the username to delete
     */
    public void deleteUser(String username) {
        mySQLDB.delete("Users", "username=?", new String[]
                {username});
    }

    /**
     * Closes the DB. This should always be done after finishing operations with the DB.
     */
    public void closeDB() {
        mySQLDB.close();
    }

}

/**
 * A simple helper class to help create and upgrade the SQLite table as needed.
 */
class UserInfoDBHelper extends SQLiteOpenHelper {
    /**
     * A static command to create the table. Only two columns are created.
     */
    private static final String CREATE_USER_SQL =
            "CREATE TABLE IF NOT EXISTS Users (username TEXT PRIMARY KEY, pin TEXT)";
    /**
     * Used on table upgrade to drop the table.
     */
    private static final String DROP_USER_SQL =
            "DROP TABLE IF EXISTS Users";

    /**
     * Constructor that simply calls the super constructor.
     *
     * @param context the current context
     * @param name the DB name
     * @param factory the cursor factory
     * @param version the DB version
     */
    public UserInfoDBHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER_SQL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i,
                          int i1) {
        sqLiteDatabase.execSQL(DROP_USER_SQL);
        onCreate(sqLiteDatabase);
    }
}
