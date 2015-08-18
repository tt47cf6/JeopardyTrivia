package rogden33.jeopardytrivia.junit.robotium;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import rogden33.jeopardytrivia.LoginActivity;

/**
 * This Robotium test case tests all the behavior of the GameBoardActivity. To test all the
 * functionality, I used my user account on the device that has a nonnull score with it to test
 * the full scope of the activity. As such, the setup work is to login and navigate to the
 * GameBoardActivity.
 */
public class GameBoardTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    /**
     * The Robotium Solo object.
     */
    private Solo solo;

    /**
     * {@inheritDoc}
     */
    public GameBoardTest() {
        super(LoginActivity.class);
    }

    /**
     * Logs in on the 'robert' account and navigates to the GameBoardActivity.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
        // login
        solo.clickOnText("robert");
        solo.enterText(0, "123");
        solo.clickOnButton("Login");
        // goto game board
        solo.clickOnButton(2);
        // wait for it to load
        solo.waitForText("Single");
    }

    /**
     * Finish all activities that were opened while testing.
     */
    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * Simply test that the activity opened successfully.
     */
    public void test00Open() {
        assertTrue(solo.searchText("Jeopardy!"));
    }

    /**
     * Test that each button has a clue associated with it and leads to either the DailyDouble
     * activity, or the SingleClue activity, both of which have the static 'CATEGORY' text.
     */
    public void test01TestAllCluesOpen() {
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            assertTrue(solo.searchText("CATEGORY"));
            solo.goBack();
        }
    }

    /**
     * Open every clue on the first board and check that only two DailyDouble activities were ever
     * started.
     */
    public void test02TestFor2DailyDoubles() {
        int count = 0;
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            // see if the current activity is a daily double
            if (solo.searchText("Daily Double")) {
                count++;
            }
            solo.goBack();
        }
        // make sure there were only 2
        assertTrue(count == 2);
    }

    /**
     * Find the first daily double, and test that giving no wager causes the activity to not advance
     * to the SingleClueActivity. This is done by checking for the text 'Submit' which only appears
     * in the daily double activity and not the SingleClue activity.
     */
    public void test03DailyDoubleNoWager() {
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            // look for daily double
            if (solo.searchText("Daily Double")) {
                // do not enter a wager
                solo.clickOnButton(0);
                // check activity has not changed
                assertTrue(solo.searchText("Submit"));
                return;
            }
            solo.goBack();
        }
    }

    /**
     * Find the first daily double and test that entering a wager that is too high causes the
     * activity to not advance to the SingleClueActivity. This is done by checking for the text
     * 'Submit' which only appears in the daily double activity and not the SingleClue activity.
     */
    public void test04DailyDoubleTooHighWager() {
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            // look for a daily double
            if (solo.searchText("Daily Double")) {
                // enter a wager that is too high
                solo.enterText(0, "" + Integer.MAX_VALUE);
                solo.clickOnButton(0);
                // check that the activity has not changed
                assertTrue(solo.searchText("Submit"));
                return;
            }
            solo.goBack();
        }
    }

    /**
     * Test that after all buttons in Single Jeopardy have been clicked, that the activity
     * changes to Double Jeopardy, once it loads
     */
    public void test05TestGoToDoubleJeopardy() {
        // go through single Jeopardy
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            solo.goBack();
        }
        // wait for double jeopardy to load
        solo.waitForText("Double");
        // check we are in the double jeopardy activity
        assertTrue(solo.searchText("Double Jeopardy!"));
    }

    /**
     * Test that every button's score matches the score of the clue in both single and double
     * jeopardy. This is done by looking for the text of the prize value which is displayed in the
     * SingleClueActivity. Since daily doubles might complicate that, the text "Daily Double" is
     * also ok, if the button clicked on led to a daily double.
     */
    public void test06TestButtonScores() {
        int[] prizes = new int[] {100, 200, 400, 600, 800, 1000};
        // check all of single jeopardy
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            assertTrue(solo.searchText("" + (prizes[i / 5])) || solo.searchText("Daily Double"));
            solo.goBack();
        }
        // wait for double jeopardy to load
        solo.waitForText("Double");
        // check all of double jeopardy
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            assertTrue(solo.searchText("" + (2 * prizes[i / 5])) || solo.searchText("Daily Double"));
            solo.goBack();
        }
    }


}
