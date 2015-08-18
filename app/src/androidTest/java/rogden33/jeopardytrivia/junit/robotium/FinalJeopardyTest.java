package rogden33.jeopardytrivia.junit.robotium;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import rogden33.jeopardytrivia.FinalJeopardyActivity;
import rogden33.jeopardytrivia.LoginActivity;

/**
 * This Robotium test case checks that the Final Jeopardy activity is working as it should.
 * Specifically, that the activity does not change when no wager is given, or if the given wager
 * is higher than the user's score. Lastly, check that a valid wager entry works.
 */
public class FinalJeopardyTest extends ActivityInstrumentationTestCase2<FinalJeopardyActivity> {
    /**
     * The Robotium Solo object.
     */
    private Solo solo;

    /**
     * {@inheritDoc}
     */
    public FinalJeopardyTest() {
        super(FinalJeopardyActivity.class);
    }

    /**
     * Sets up the Solo object.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Finish all activities opened during the test run.
     */
    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }


    /**
     * Test that after not entering a wager, we remain on the Final Jeopardy activity. This is
     * asserted by looking for the "Submit" button text that is in the Final Jeopardy activity.
     * If the test fails, the current activity would move to the SingleClueActivity which does not
     * have the Submit button.
     */
    public void test01TestFinalJeopardyNoWager() throws InterruptedException {
        solo.clickOnButton(0);
        // check we are still in the FinalJeopardyActivity
        assertTrue(solo.searchText("Submit"));
    }

    /**
     * Test that entering a wager that is too high, such as the maximum Integer value, also causes
     * the current activity to not change. This is asserted by looking for the "Submit" button text
     * that is in the Final Jeopardy activity. If the test fails, the current activity would move
     * to the SingleClueActivity which does not have the Submit button.
     */
    public void test02TestFinalJeopardyTooHighWager() throws InterruptedException {
        solo.enterText(0, "" + Integer.MAX_VALUE);
        solo.clickOnButton(0);
        assertTrue(solo.searchText("Submit"));
    }

    /**
     * Test that entering a valid wager that is not too high switches the current activity to the
     * SingleClueActivity. This is asserted by looking for the text "Score" out of the "Your Score"
     * TestView object in that activity.
     */
    public void test03TestFinalJeopardy() throws InterruptedException {
        solo.enterText(0, "0");
        solo.clickOnButton(0);
        assertTrue(solo.searchText("Score"));
    }
}
