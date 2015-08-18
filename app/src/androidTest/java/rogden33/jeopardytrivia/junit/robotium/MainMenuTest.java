package rogden33.jeopardytrivia.junit.robotium;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import rogden33.jeopardytrivia.MainMenuActivity;

/**
 * A Robotium text case for the MainMenuActivity. Since this activity by itself is very simple,
 * this test case only makes sure that it loads correctly.
 */
public class MainMenuTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
    /**
     * The Robotium Solo object
     */
    private Solo solo;

    /**
     * {@inheritDoc}
     */
    public MainMenuTest() {
        super(MainMenuActivity.class);
    }

    /**
     * Instantiate the Solo object.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Finish all activities started by the test case.
     */
    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * Test that the MainMenuActivity opens, as indicated by the static "Main Menu" text.
     */
    public void test00Open() {
        assertTrue(solo.searchText("Main Menu"));
    }
}
