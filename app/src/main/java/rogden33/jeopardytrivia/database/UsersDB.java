package rogden33.jeopardytrivia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("pin", pin);
        long rowId = mySQLDB.insert("Users", null,
                contentValues);
        return rowId != -1;
    }

    // TODO User model
//    public User login(String username, String pin) {
//        // hash and check
//        // load previous scores
//    }

    public void closeDB() {
        mySQLDB.close();
    }

}

class UserInfoDBHelper extends SQLiteOpenHelper {
    private static final String CREATE_USER_SQL =
            "CREATE TABLE IF NOT EXISTS Users (username TEXT PRIMARY KEY, pin TEXT)";
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
