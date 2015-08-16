package rogden33.jeopardytrivia.junit.robotium;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.robotium.solo.Solo;

import rogden33.jeopardytrivia.MainMenuActivity;
import rogden33.jeopardytrivia.RandomQuestionsActivity;

/**
 * Created by Robert on 8/16/2015.
 */
public class RandomQuestionsTest extends ActivityInstrumentationTestCase2<RandomQuestionsActivity> {
    private Solo solo;

    public RandomQuestionsTest() {
        super(RandomQuestionsActivity.class);
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
        assertTrue(solo.searchText("CATEGORY"));
    }

    public void test01OneRightAnswer() {
        boolean gotItRight = false;
        for (int i = 0; i < 4; i++) {
            solo.clickOnButton(i);
            if (solo.searchText("1")) {
                gotItRight = true;
            }
        }
        assertTrue(gotItRight);
    }

    public void test02QuestionBankRefill() {
        for (int i = 0; i < 51; i++) {
            solo.clickOnButton("Next");
            assertTrue(solo.searchText("CATEGORY"));
        }
    }

    public void test03NoNullText() {
        for (int i = 0; i < 50; i++) {
            Button choiceA = solo.getButton(0);
            Button choiceB = solo.getButton(1);
            Button choiceC = solo.getButton(2);
            Button choiceD = solo.getButton(3);
            assertTrue(choiceA.getText().length() > 0);
            assertTrue(choiceB.getText().length() > 0);
            assertTrue(choiceC.getText().length() > 0);
            assertTrue(choiceD.getText().length() > 0);
            solo.clickOnButton("Next");
        }
    }
}
