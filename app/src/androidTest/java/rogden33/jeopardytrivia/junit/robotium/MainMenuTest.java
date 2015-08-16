package rogden33.jeopardytrivia.junit.robotium;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import rogden33.jeopardytrivia.MainMenuActivity;

/**
 * Created by Robert on 8/16/2015.
 */
public class MainMenuTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
    private Solo solo;

    public MainMenuTest() {
        super(MainMenuActivity.class);
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
        assertTrue(solo.searchText("Main Menu"));
    }
}
