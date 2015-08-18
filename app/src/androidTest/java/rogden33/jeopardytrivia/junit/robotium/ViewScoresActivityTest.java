package rogden33.jeopardytrivia.junit.robotium;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import rogden33.jeopardytrivia.ViewScoresActivity;

/**
 * A Robotium test case for the ViewScoresActivity. This is a simple activity so it only checks
 * that the activity is opened and displays the 'robert' user, and that the Share button opens
 * a Share dialog.
 */
public class ViewScoresActivityTest extends ActivityInstrumentationTestCase2<ViewScoresActivity> {
    /**
     * The Robotium Solo object.
     */
    private Solo solo;

    /**
     * {@inheritDoc}
     */
    public ViewScoresActivityTest() {
        super(ViewScoresActivity.class);
    }

    /**
     * Instantiates the Solo object.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Finishes all activities opened by the test cases.
     */
    @Override
    public void tearDown() throws Exception {
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    /**
     * Tests that the activity opens and shows the robert user.
     */
    public void test00Open() {
        assertTrue(solo.searchText("robert"));
    }

    /**
     * Tests the share button works and displays the Share with dialog.
     */
    public void test01Share() {
        solo.clickOnButton(0);
        assertTrue(solo.searchText("Share with"));
    }
}
