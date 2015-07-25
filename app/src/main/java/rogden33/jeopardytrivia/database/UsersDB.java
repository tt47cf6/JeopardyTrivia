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

import rogden33.jeopardytrivia.model.User;

public class UsersDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Users.db";
    private UserInfoDBHelper myDBHelper;
    private SQLiteDatabase mySQLDB;

    public UsersDB(Context context) {
        myDBHelper = new UserInfoDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mySQLDB =
                myDBHelper.getWritableDatabase();
    }

    public boolean newUser(String username, String pin) {
        try {
            // hash pin number
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            String hash = new String(hasher.digest((username + pin).getBytes()));
            // insert into DB
            ContentValues contentValues = new ContentValues();
            contentValues.put("username", username);
            contentValues.put("pin", hash);
            contentValues.put("highScore", "0");
            long rowId = mySQLDB.insert("Users", null,
                    contentValues);
            return rowId != -1;
        } catch (NoSuchAlgorithmException e) {
            // won't be a problem
            return false;
        }

    }

    public List<String> getAllUsernames() {
        List<String> result = new ArrayList<String>();
        Cursor c = mySQLDB.query(
                "Users",
                new String[] {"username"},
                null,
                null,
                null,
                null,
                null
        );
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            result.add(c.getString(0));
            c.moveToNext();
        }
        return result;
    }

    public User login(String username, String pin) {
        // get entry from DB
        String[] columns = new String[]{"username", "pin", "highScore"};
        Cursor c = mySQLDB.query(
                "Users",
                columns,
                "username = ?",
                new String[]{username},
                null,
                null,
                null
        );
        if (c.getCount() != 1) {
            return null;
        }
        c.moveToFirst();
        String pinHash = c.getString(1);
        String highScore = c.getString(2);
        // hash and check
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            String hash = new String(hasher.digest((username + pin).getBytes()));
            if (hash.equals(pinHash)) {
                return new User(username, Integer.parseInt(highScore));
            }
        } catch (NoSuchAlgorithmException e) {
            // do nothing
        }
        return null;
    }

    public void closeDB() {
        mySQLDB.close();
    }

}

class UserInfoDBHelper extends SQLiteOpenHelper {
    private static final String CREATE_USER_SQL =
            "CREATE TABLE IF NOT EXISTS Users (username TEXT PRIMARY KEY, pin TEXT, highScore TEXT)";
    private static final String DROP_USER_SQL =
            "DROP TABLE IF EXISTS Users";

    public UserInfoDBHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i,
                          int i1) {
        sqLiteDatabase.execSQL(DROP_USER_SQL);
        onCreate(sqLiteDatabase);
    }
}
