package rogden33.jeopardytrivia.junit.junit;

import android.test.AndroidTestCase;

import junit.framework.TestCase;

import rogden33.jeopardytrivia.database.UsersDB;

/**
 * Created by Robert on 8/16/2015.
 */
public class UsersDBTest extends AndroidTestCase {

    private UsersDB myDB;

    public void setUp() {
        myDB = new UsersDB(getContext());
    }

    public void testNewUser() {
        myDB.newUser("testUser", "1234");
        boolean contains = false;
        for (String user : myDB.getAllUsernames()) {
            contains |= user.equals("testUser");
        }
        assertTrue(contains);
    }

    public void testLogin() {
        myDB.newUser("testUser", "1234");
        assertNotNull(myDB.login("testUser", "1234"));
    }

    public void testFailedLogin() {
        assertNull(myDB.login("testUser", "4321"));
    }

    public void tearDown() {
        myDB.deleteUser("testUser");
        myDB.closeDB();
    }
}
