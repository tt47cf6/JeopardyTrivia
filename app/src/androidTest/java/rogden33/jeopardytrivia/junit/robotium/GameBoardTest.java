package rogden33.jeopardytrivia.junit.robotium;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;

import rogden33.jeopardytrivia.LoginActivity;
import rogden33.jeopardytrivia.RandomQuestionsActivity;

/**
 * Created by Robert on 8/16/2015.
 */
public class GameBoardTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private Solo solo;

    public GameBoardTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
        solo.clickOnText("robert");
        solo.enterText(0, "123");
        solo.clickOnButton("Login");
        solo.clickOnButton(2);
        solo.waitForText("Single");
    }

    @Override
    public void tearDown() throws Exception {
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    public void test00Open() {
        assertTrue(solo.searchText("Jeopardy!"));
    }

    public void test01TestAllCluesOpen() {
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            assertTrue(solo.searchText("CATEGORY"));
            solo.goBack();
        }
    }

    public void test02TestFor2DailyDoubles() {
        int count = 0;
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            if (solo.searchText("Daily Double")) {
                count++;
            }
            solo.goBack();
        }
        assertTrue(count == 2);
    }

    public void test03DailyDoubleNoWager() {
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            if (solo.searchText("Daily Double")) {
                solo.clickOnButton(0);
                assertTrue(solo.searchText("Submit"));
                return;
            }
            solo.goBack();
        }
    }

    public void test04DailyDoubleTooHighWager() {
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            if (solo.searchText("Daily Double")) {
                solo.enterText(0, "" + Integer.MAX_VALUE);
                solo.clickOnButton(0);
                assertTrue(solo.searchText("Submit"));
                return;
            }
            solo.goBack();
        }
    }

    public void test05TestGoToDoubleJeopardy() {
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            solo.goBack();
        }
        solo.waitForText("Double");
        assertTrue(solo.searchText("Double Jeopardy!"));
    }

    public void test06TestButtonScores() {
        int[] prizes = new int[] {100, 200, 400, 600, 800, 1000};
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            assertTrue(solo.searchText("" + (prizes[i / 5])) || solo.searchText("Daily Double"));
            solo.goBack();
        }
        solo.waitForText("Double");
        for (int i = 0; i < 30; i++) {
            solo.clickOnButton(i);
            assertTrue(solo.searchText("" + (2 * prizes[i / 5])) || solo.searchText("Daily Double"));
            solo.goBack();
        }
    }


}
