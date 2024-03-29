package algonquin.cst2335.groupproject;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;

import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
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
    @Test
    public void vocabulary_deleted_Test() {

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

        ViewInteraction recyclerView = onView(withId(R.id.saved_recycleView));
        //click on one itemView
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        //click on "Yes" button to implement deletion
        ViewInteraction materialButton3 = onView(allOf(withId(android.R.id.button1), withText("Yes"),
                childAtPosition(
                        childAtPosition(withClassName(is("android.widget.ScrollView")), 0),
                        3)));
        materialButton3.perform(click());

        ViewInteraction textView = onView(withId(R.id.termText));
        textView.check(matches(withText("two")));
    }
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
