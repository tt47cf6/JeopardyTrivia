package rogden33.jeopardytrivia.junit.robotium;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import rogden33.jeopardytrivia.FinalJeopardyActivity;
import rogden33.jeopardytrivia.LoginActivity;

/**
 * Created by Robert on 8/16/2015.
 */
public class FinalJeopardyTest extends ActivityInstrumentationTestCase2<FinalJeopardyActivity> {
    private Solo solo;

    public FinalJeopardyTest() {
        super(FinalJeopardyActivity.class);
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


    public void test07TestFinalJeopardyNoWager() throws InterruptedException {
        solo.clickOnButton(0);
        assertTrue(solo.searchText("Submit"));
    }

    public void test08TestFinalJeopardyTooHighWager() throws InterruptedException {
        solo.enterText(0, "" + Integer.MAX_VALUE);
        solo.clickOnButton(0);
        assertTrue(solo.searchText("Submit"));
    }

    public void test09TestFinalJeopardy() throws InterruptedException {
        solo.enterText(0, "0");
        solo.clickOnButton(0);
        assertTrue(solo.searchText("Score"));
    }
}
