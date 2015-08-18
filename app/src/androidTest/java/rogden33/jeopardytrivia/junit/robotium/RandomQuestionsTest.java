package rogden33.jeopardytrivia.junit.robotium;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;

import com.robotium.solo.Solo;

import rogden33.jeopardytrivia.RandomQuestionsActivity;

/**
 * A Robotium test case to test the RandomQuestionActivity and the RandomQuestionsBank.
 */
public class RandomQuestionsTest extends ActivityInstrumentationTestCase2<RandomQuestionsActivity> {
    /**
     * The Robotium Solo object.
     */
    private Solo solo;

    /**
     * {@inheritDoc}
     */
    public RandomQuestionsTest() {
        super(RandomQuestionsActivity.class);
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
     * Finish all activities opened while testing.
     */
    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * Test that the activity opens by looking for the static "CATEGORY" text in the activity.
     */
    public void test00Open() {
        assertTrue(solo.searchText("CATEGORY"));
    }

    /**
     * Test that one of the possible buttons to select leads to the next question. This is indicated
     * by the streak going from 0 to 1 and is tested by looking for the "1" Streak value.
     */
    public void test01OneRightAnswer() {
        boolean gotItRight = false;
        // check all buttons
        for (int i = 0; i < 4; i++) {
            solo.clickOnButton(i);
            // if the last button was correct, the streak goes to 1
            if (solo.getText(4).getText().toString().equals("1")) {
                gotItRight = true;
                // stop clicking buttons
                i = 4;
            }
        }
        // make sure one of the buttons was correct
        assertTrue(gotItRight);
    }

    /**
     * Test that there are no blank buttons or TextViews in the activity through at least the first
     * batch. This ensures that the Clues are loaded successfully.
     */
    public void test02NoNullText() {
        for (int i = 0; i < 51; i++) {
            TextView category = solo.getText(4);
            TextView clue = solo.getText(7);
            Button choiceA = solo.getButton(0);
            Button choiceB = solo.getButton(1);
            Button choiceC = solo.getButton(2);
            Button choiceD = solo.getButton(3);
            // check category
            assertTrue(category.getText().toString().length() > 0);
            // check clue
            assertTrue(clue.getText().toString().length() > 0);
            // check buttons
            assertTrue(choiceA.getText().length() > 0);
            assertTrue(choiceB.getText().length() > 0);
            assertTrue(choiceC.getText().length() > 0);
            assertTrue(choiceD.getText().length() > 0);
            // advance
            solo.clickOnButton("Next");
        }
    }

    /**
     * Test the RandomQuestionBank by checking that it is able to refill and continue serving
     * Clues after the first batch of clues is exhausted. Since the first batch is no more than
     * 50 clues, check that the first 51 clues all have text on the first button, which implies
     * that the Clue loaded successfully.
     */
    public void test03QuestionBankRefill() {
        for (int i = 0; i < 51; i++) {
            // advance through the clues
            solo.clickOnButton("Next");
            Button choiceA = solo.getButton(0);
            // check the button text
            assertTrue(choiceA.getText().length() > 0);
        }
    }
}
