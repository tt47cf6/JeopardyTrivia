package rogden33.jeopardytrivia.junit.robotium;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import java.util.List;
import java.util.Random;

import rogden33.jeopardytrivia.LoginActivity;
import rogden33.jeopardytrivia.database.UsersDB;

/**
 * This Robotium test case checks all the functionality of the LoginActivity and all its fragments.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    /**
     * The Robotium Solo object.
     */
    private Solo solo;

    /**
     * {@inheritDoc}
     */
    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    /**
     * Construct a new Solo object.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Finish all opened activities.
     */
    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * Check that the activity opened and the static text "OR select your username:" is present
     * on the screen.
     */
    public void test00Open() {
        assertTrue(solo.searchText("OR select your username:"));
    }

    /**
     * Test the valid addition of a new user with a random name.
     */
    public void test01NewUserValid() {
        Random rand = new Random();
        // go to register new user fragment
        solo.clickOnButton("Register New User");
        // enter information and continue
        String username = "" + rand.nextInt(10000);
        solo.enterText(0, "testUser" + username);
        solo.enterText(1, "1234");
        solo.enterText(2, "1234");
        solo.clickOnButton("Submit");
        // check we are back on the login activity and the new username is present
        assertTrue(solo.searchText("OR select your username:"));
        assertTrue(solo.searchText(username));
    }

    /**
     * Test that when registering a new user, that if the entered pins do not match, the fragment
     * does not return to the user list fragment because of a successful login. This is checked
     * by looking for the text "Submit" which only appears if the displayed fragment is still the
     * register new user fragment.
     */
    public void test02NewUserPinsNotMatch() {
        Random rand = new Random();
        solo.clickOnButton("Register New User");
        solo.enterText(0, "testUser" + rand.nextInt(10000));
        // non matching pins
        solo.enterText(1, "1234");
        solo.enterText(2, "4321");
        solo.clickOnButton("Submit");
        // check that the fragment did not change
        assertTrue(solo.searchText("Submit"));
    }

    /**
     * Try registering the same user twice and check that the first time is successful when the
     * static text "OR select your username:" is displayed from the user list fragment, but the
     * second time the "Submit" text is displayed because the fragment did not change from the
     * register fragment when the registration failed.
     */
    public void test03NewUserAlreadyExists() {
        Random rand = new Random();
        String username = "testUser" + rand.nextInt(10000);
        // register new valid user
        solo.clickOnButton("Register New User");
        solo.enterText(0, username);
        solo.enterText(1, "1234");
        solo.enterText(2, "1234");
        solo.clickOnButton("Submit");
        // check that valid user registration was successful
        assertTrue(solo.searchText("OR select your username:"));
        // attempt to register the same user again
        solo.clickOnButton("Register New User");
        solo.enterText(0, username);
        solo.enterText(1, "1234");
        solo.enterText(2, "1234");
        solo.clickOnButton("Submit");
        // check that it was not successful the second time
        assertTrue(solo.searchText("Submit"));
    }

    /**
     * Test that trying to register without entering a username does not advance to the user list
     * fragment on success. Instead, the fragment should stay on the register fragment on failure,
     * as indicated by the "submit" button's text.
     */
    public void test04NewUserUsernameNotEntered() {
        solo.clickOnButton("Register New User");
        // do not enter a username
        solo.enterText(1, "1234");
        solo.enterText(2, "1234");
        solo.clickOnButton("Submit");
        // check that registration was not successful
        assertTrue(solo.searchText("Submit"));
    }

    /**
     * Test that trying to register without entering the first pin does not advance to the user list
     * fragment on success. Instead, the fragment should stay on the register fragment on failure,
     * as indicated by the "submit" button's text.
     */
    public void test05NewUserPin1NotEntered() {
        Random rand = new Random();
        solo.clickOnButton("Register New User");
        solo.enterText(0, "testUser" + rand.nextInt(10000));
        // do not enter the first pin
        solo.enterText(2, "1234");
        solo.clickOnButton("Submit");
        // check that registration was not successful
        assertTrue(solo.searchText("Submit"));
    }

    /**
     * Test that trying to register without entering the second pin does not advance to the user list
     * fragment on success. Instead, the fragment should stay on the register fragment on failure,
     * as indicated by the "submit" button's text.
     */
    public void test06NewUserPin2NotEntered() {
        Random rand = new Random();
        solo.clickOnButton("Register New User");
        solo.enterText(0, "testUser" + rand.nextInt(10000));
        solo.enterText(1, "1234");
        solo.clickOnButton("Submit");
        assertTrue(solo.searchText("Submit"));
    }

    /**
     * Test that a valid login takes the user to the MainMenu activity, as indicated by the static
     * "Main Menu" text.
     */
    public void test07ValidLogin() {
        solo.clickOnText("robert");
        solo.enterText(0, "123");
        solo.clickOnButton("Login");
        assertTrue(solo.searchText("Main Menu"));
    }

    /**
     * Test that an invalid login attempt leaves the user on the LoginAttemptFragment and does not
     * advance to the MainMenuActivity, as indicated by the LoginAttemptFragment's static "Login"
     * text.
     */
    public void test08InvalidLogin() {
        solo.clickOnText("robert");
        solo.enterText(0, "999");
        solo.clickOnButton("Login");
        // check we are still on the same fragment and activity
        assertTrue(solo.searchText("Login"));
    }

    /**
     * Check that logging in with no pin leaves the user on the LoginAttemptFragment and does not
     * advance to the MainMenuActivity, as indicated by the LoginAttemptFragment's static "Login"
     * text.
     */
    public void test09NoPinEntered() {
        solo.clickOnText("robert");
        // do not enter a pin
        solo.clickOnButton("Login");
        assertTrue(solo.searchText("Login"));
    }

    /**
     * Test user deletion by logging in under every username that was created during this testing
     * process and deleting them. Since this is the last test to run, this also serves as a nice
     * way to clean up the login DB.
     * Checking that a user was successfully deleted is done by checking that their username no
     * longer appears on the LoginActivity user list fragment that is displayed after the deletion
     * of a user.
     */
    public void test10Delete() {
        UsersDB db = new UsersDB(getActivity());
        List<String> users = db.getAllUsernames();
        db.closeDB();
        for (String user : users) {
            if (user.startsWith("testUser")) {
                // login as test user
                solo.clickOnText(user);
                solo.enterText(0, "1234");
                // delete user
                solo.clickOnButton("Login");
                solo.clickOnButton("Delete User");
                solo.clickOnButton("Yes");
                // check user was deleted
                assertFalse(solo.searchText(user));
            }
        }
    }
}
