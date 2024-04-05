package algonquin.cst2335.groupproject;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import algonquin.cst2335.groupproject.YashanAPI.DeezerSongSearchActivity;

@RunWith(AndroidJUnit4.class)
public class DeezerSongSearchActivityTest {

    @Rule
    public ActivityScenarioRule<DeezerSongSearchActivity> activityScenarioRule =
            new ActivityScenarioRule<>(DeezerSongSearchActivity.class);

    /**
     * Test case to check if the search field is displayed.
     */
    @Test
    public void testSearchFieldVisibility() {
        onView(withId(R.id.searchEditText)).check(matches(isDisplayed()));
    }

    /**
     * Test case to check if the search button is displayed.
     */
    @Test
    public void testSearchButtonVisibility() {
        onView(withId(R.id.searchButton)).check(matches(isDisplayed()));
    }

    /**
     * Test case to check if the RecyclerView is displayed.
     */
    @Test
    public void testRecyclerViewVisibility() {
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    /**
     * Test case to check if the help menu item is displayed.
     */
    @Test
    public void testHelpMenuItemVisibility() {
        onView(withId(R.id.help)).check(matches(isDisplayed()));
    }

    /**
     * Test case to check if clicking the search button with an empty query displays an error message.
     */
    @Test
    public void testEmptyQueryErrorMessage() {
        // Perform a click on the search button without entering any query
        onView(withId(R.id.searchButton)).perform(ViewActions.click());

        // Check if the error message dialog is displayed
        onView(withId(android.R.id.message)).check(matches(withText("Please enter a search query")));
    }
}