package rogden33.jeopardytrivia.junit.robotium;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import java.util.List;
import java.util.Random;

import rogden33.jeopardytrivia.LoginActivity;
import rogden33.jeopardytrivia.database.UsersDB;

/**
 * Created by Robert on 8/16/2015.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private Solo solo;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    public void test00Open() {
        assertTrue(solo.searchText("OR select your username:"));
    }

    public void test01NewUserValid() {
        Random rand = new Random();
        solo.clickOnButton("Register New User");
        solo.enterText(0, "testUser" + rand.nextInt(10000));
        solo.enterText(1, "1234");
        solo.enterText(2, "1234");
        solo.clickOnButton("Submit");
        assertTrue(solo.searchText("OR select your username:"));
    }

    public void test02NewUserPinsNotMatch() {
        Random rand = new Random();
        solo.clickOnButton("Register New User");
        solo.enterText(0, "testUser" + rand.nextInt(10000));
        solo.enterText(1, "1234");
        solo.enterText(2, "4321");
        solo.clickOnButton("Submit");
        assertTrue(solo.searchText("Submit"));
    }

    public void test03NewUserAlreadyExists() {
        Random rand = new Random();
        String username = "testUser" + rand.nextInt(10000);
        solo.clickOnButton("Register New User");
        solo.enterText(0, username);
        solo.enterText(1, "1234");
        solo.enterText(2, "1234");
        solo.clickOnButton("Submit");
        assertTrue(solo.searchText("OR select your username:"));
        solo.clickOnButton("Register New User");
        solo.enterText(0, username);
        solo.enterText(1, "1234");
        solo.enterText(2, "1234");
        solo.clickOnButton("Submit");
        assertTrue(solo.searchText("Submit"));
    }

    public void test04NewUserUsernameNotEntered() {
        Random rand = new Random();
        solo.clickOnButton("Register New User");
        solo.enterText(1, "1234");
        solo.enterText(2, "1234");
        solo.clickOnButton("Submit");
        assertTrue(solo.searchText("Submit"));
    }

    public void test05NewUserPin1NotEntered() {
        Random rand = new Random();
        solo.clickOnButton("Register New User");
        solo.enterText(0, "testUser" + rand.nextInt(10000));
        solo.enterText(2, "1234");
        solo.clickOnButton("Submit");
        assertTrue(solo.searchText("Submit"));
    }

    public void test06NewUserPin2NotEntered() {
        Random rand = new Random();
        solo.clickOnButton("Register New User");
        solo.enterText(0, "testUser" + rand.nextInt(10000));
        solo.enterText(1, "1234");
        solo.clickOnButton("Submit");
        assertTrue(solo.searchText("Submit"));
    }

    public void test07ValidLogin() {
        solo.clickOnText("robert");
        solo.enterText(0, "123");
        solo.clickOnButton("Login");
        assertTrue(solo.searchText("Main Menu"));
    }

    public void test08InvalidLogin() {
        solo.clickOnText("robert");
        solo.enterText(0, "999");
        solo.clickOnButton("Login");
        assertTrue(solo.searchText("Login"));
    }

    public void test09NoPinEntered() {
        solo.clickOnText("robert");
        solo.clickOnButton("Login");
        assertTrue(solo.searchText("Login"));
    }

    public void test10Delete() {
        UsersDB db = new UsersDB(getActivity());
        List<String> users = db.getAllUsernames();
        db.closeDB();
        for (String user : users) {
            if (user.startsWith("testUser")) {
                solo.clickOnText(user);
                solo.enterText(0, "1234");
                solo.clickOnButton("Login");
                solo.clickOnButton("Delete User");
                solo.clickOnButton("Yes");
                assertFalse(solo.searchText(user));
            }
        }
    }

}
