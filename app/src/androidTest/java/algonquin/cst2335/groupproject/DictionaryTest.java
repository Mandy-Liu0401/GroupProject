
/**
 * This page contains 5 Junit tests
 *
 * @author Mengying Liu
 * @Student No. 041086143
 * @date Mar 25, 2024
 * @labSecNo. 021
 * @purpose % Junit tests include button, menu, logic , and database functionalities.
 *
 */
package algonquin.cst2335.groupproject;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;


import androidx.test.espresso.ViewInteraction;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import algonquin.cst2335.groupproject.mengyingAPI.Activity_DictionaryAPI;
import algonquin.cst2335.groupproject.mengyingAPI.Activity_Saved_Vocabulary;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class DictionaryTest {

    @Rule
    public ActivityScenarioRule<Activity_DictionaryAPI> mActivityScenarioRule =
            new ActivityScenarioRule<>(Activity_DictionaryAPI.class);

    private Activity_Saved_Vocabulary mockVocabulary;
    /**
     * this test method will check if userInput can be translated or not.
     */
    @Test
    public void userInput_translate_Test() {

        ViewInteraction appCompatEditText = onView(withId(R.id.editText));
        appCompatEditText.perform(replaceText("one"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(withId(R.id.translate_button));
        materialButton.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(withId(R.id.wordTextView));
        textView.check(matches(withText("one")));
    }

    /**
     * this method asserts when go to vocabulary, all saved term will be displayed.
     */
    @Test
    public void vocabulary_show_Test() {

        userInput_translate_Test();

        ViewInteraction materialButton2 = onView(withId(R.id.save_button));
        materialButton2.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction actionMenuItemView2 = onView(withId(R.id.second_id));
        actionMenuItemView2.perform(click());

        ViewInteraction textView = onView(withId(R.id.termText));
        textView.check(matches(withText("one")));
    }

    /**
     * this test asserts when clicking on help button, a manual instruction will be prompted.
     */
    @Test
    public void help_test() {

        ViewInteraction bottomNavigationItemView = onView(withId(R.id.third_id));
        bottomNavigationItemView.perform(click());

        ViewInteraction textView = onView(
                allOf(IsInstanceOf.<View>instanceOf(android.widget.TextView.class), withText("Instruction for Dictionary API"),
                        withParent(allOf(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        textView.check(matches(withText("Instruction for Dictionary API")));

    }

    /**
     * this test asserts after exit_icon pressed, user will redirected to main page with hello message
     */
    @Test
    public void exit_test() {
        ViewInteraction vocabulary_page = onView(withId(R.id.second_id));
        vocabulary_page.perform(click());

        ViewInteraction exit_icon= onView(withId(R.id.exit_icon));
        exit_icon.perform(click());

        ViewInteraction yesButton = onView(withId(android.R.id.button1));
        yesButton.perform(scrollTo(), click());

        ViewInteraction textView = onView(withId(R.id.textView));
        textView.check(matches(withText("Welcome to the final project")));
    }


    /**
     * this test saves 2 words to local vocabulary, and after deleting the first one,
     * it left with the later term.
     */
    @Test
    public void vocabulary_deleted_Test() {

        //input, translate and save the first term
        ViewInteraction inputBox = onView(withId(R.id.editText));
        inputBox.perform(replaceText("one"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(withId(R.id.translate_button));
        materialButton.perform(click());

        ViewInteraction materialButton2 = onView(withId(R.id.save_button));
        materialButton2.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //input, translate and save the second term
        inputBox.perform(replaceText("two"), closeSoftKeyboard());
        materialButton.perform(click());
        materialButton2.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction actionMenuItemView2 = onView(withId(R.id.second_id));
        actionMenuItemView2.perform(click());

        //delete the first term
        ViewInteraction recyclerView = onView(withId(R.id.saved_recycleView));

        recyclerView.perform(actionOnItemAtPosition(0, click()));
        ViewInteraction materialButton3 = onView(allOf(withId(android.R.id.button1), withText("Yes"),
                childAtPosition(
                        childAtPosition(withClassName(is("android.widget.ScrollView")), 0),
                        3)));
        materialButton3.perform(click());

        //the left term is comparing with the second term indeed
        ViewInteraction textView = onView(withId(R.id.termText));
        textView.check(matches(withText("two")));
    }

    /**
     * This method creates a matcher for selecting a child view at a specific position
     * within a parent RecyclerView.
     * @param parentMatcher A matcher for selecting the parent RecyclerView.
     * @param position The position of the child view
     * @return A matcher for selecting the child view at the specified position
     */
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
