package algonquin.cst2335.groupproject;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import algonquin.cst2335.groupproject.mengyingAPI.Activity_DictionaryAPI;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Activity_Dictionary_API_Test {

    @Rule
    public ActivityScenarioRule<Activity_DictionaryAPI> mActivityScenarioRule =
            new ActivityScenarioRule<>(Activity_DictionaryAPI.class);

    @Test
    public void userInput_translate_Test() {

        ViewInteraction appCompatEditText = onView(withId(R.id.editText));
        appCompatEditText.perform(replaceText("test"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(withId(R.id.translate_button));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.wordTextView));
        textView.check(matches(withText("test")));
    }
//    @Test
//    public void vocabulary_inserted_Test() {
//        ViewInteraction appCompatEditText = onView(withId(R.id.editText));
//        appCompatEditText.perform(replaceText("test"), closeSoftKeyboard());
//
//        ViewInteraction materialButton = onView(withId(R.id.translate_button));
//        materialButton.perform(click());
//
//        userInput_translate_Test();
//
//        ViewInteraction materialButton2 = onView(withId(R.id.save_button));
//        materialButton2.perform(click());
//
//        ViewInteraction actionMenuItemView2 = onView(withId(R.id.second_id));
//        actionMenuItemView2.perform(click());
//
//        ViewInteraction textView = onView(withId(R.id.termText));
//        textView.check(matches(withText("test")));
//    }
//
//    @Test
//    public void vocabulary_deleted_Test() {
//
//        ViewInteraction actionMenuItemView2 = onView(withId(R.id.second_id));
//        actionMenuItemView2.perform(click());
//
//
//        ViewInteraction recyclerView = onView(withId(R.id.saved_recycleView));
//        //click on one itemView
//        recyclerView.perform(actionOnItemAtPosition(0, click()));
//        //click on "Yes" button to implement deletion
//        ViewInteraction materialButton3 = onView(withId(android.R.id.button1));
//        materialButton3.perform(scrollTo(), click());
//
//        ViewInteraction textView = onView(withId(R.id.termText));
//        textView.check(matches(not(withText("test"))));
//    }

}
