package rogden33.jeopardytrivia.junit.robotium;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import rogden33.jeopardytrivia.ViewScoresActivity;

/**
 * Created by Robert on 8/16/2015.
 */
public class ViewScoresActivityTest extends ActivityInstrumentationTestCase2<ViewScoresActivity> {
    private Solo solo;

    public ViewScoresActivityTest() {
        super(ViewScoresActivity.class);
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
        assertTrue(solo.searchText("robert"));
    }

    public void test01Share() {
        solo.clickOnButton(0);
        assertTrue(solo.searchText("Share with"));
    }
}
