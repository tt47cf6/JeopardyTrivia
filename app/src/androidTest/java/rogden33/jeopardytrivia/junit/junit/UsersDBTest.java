package rogden33.jeopardytrivia.junit.junit;

import android.test.AndroidTestCase;

import rogden33.jeopardytrivia.database.UsersDB;

/**
 * This Unit test cases checks that the SQLiteDB is properly storing information and behaving
 * correctly with a new user, valid login, and invalid login.
 */
public class UsersDBTest extends AndroidTestCase {

    /**
     * A reference to the database to use.
     */
    private UsersDB myDB;

    /**
     * Construct a new database instance for every test.
     */
    public void setUp() {
        myDB = new UsersDB(getContext());
        // remove any previous users with the test name
        myDB.deleteUser("testUser");
    }

    /**
     * Test that adding a new user works. This is tested by checking that the new username is
     * found in getAllUsernames() call.
     */
    public void testNewUser() {
        // add new user
        myDB.newUser("testUser", "1234");
        // check new username exists
        boolean contains = false;
        for (String user : myDB.getAllUsernames()) {
            contains |= user.equals("testUser");
        }
        assertTrue(contains);
    }

    /**
     * Attempt to login with a new user account. If the user is valid, the username will be
     * returned, otherwise null will be returned. First, make sure the returned username is not null,
     * and then that it matches the username.
     */
    public void testLogin() {
        myDB.newUser("testUser", "1234");
        String verified = myDB.login("testUser", "1234");
        assertNotNull(verified);
        assertTrue(verified.equals("testUser"));
    }

    /**
     * Check that entering an invalid pin returns null.
     */
    public void testFailedLogin() {
        myDB.newUser("testUser", "1234");
        assertNull(myDB.login("testUser", "4321"));
    }

    /**
     * Always remove the testUser if added after each test, and close the DB.
     */
    public void tearDown() {
        myDB.deleteUser("testUser");
        myDB.closeDB();
    }
}
