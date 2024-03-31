package algonquin.cst2335.groupproject;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;

import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;


import static algonquin.cst2335.groupproject.SunHomeTest.clickChildViewWithId;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toolbar;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import algonquin.cst2335.groupproject.databinding.RecipeSearchBinding;
import algonquin.cst2335.groupproject.mengyingAPI.Activity_DictionaryAPI;
import algonquin.cst2335.groupproject.wenxinAPI.Recipe;
import algonquin.cst2335.groupproject.wenxinAPI.RecipeDetailsActivity;
import algonquin.cst2335.groupproject.wenxinAPI.RecipesActivity;
import algonquin.cst2335.groupproject.wenxinAPI.SavedRecipesActivity;
import algonquin.cst2335.groupproject.zimeng.SunFav;

/**
 * This class performs integration tests on the RecipeActivity within the group project application.
 * It tests various functionalities such as saving a recipe, displaying a help dialog, navigating to saved recipes,
 * populating search results, displaying recipe details, and navigating back within the app.
 * It ensures that the app's key features related to recipe management work as expected.
 *
 * @author Wenxin Li
 * @version 1.6
 * @since 2024-03-27
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {
    @Rule
    public ActivityScenarioRule<RecipesActivity> activityRule = new ActivityScenarioRule<>(RecipesActivity.class);

    /**
     * Sets up the test environment before each test.
     * Specifically, it navigates to the RecipeActivity and ensures the search input field is cleared.
     */
    @Before
    public void clearRecipeInput() {

        onView(withId(R.id.enterRecipe)).perform(clearText());
    }

    /**
     * Resets the app UI after each test.
     * Attempts to navigate back unconditionally to avoid state leakage between tests.
     */
    @After
    public void resetAppUI() {
        try {
            Espresso.pressBackUnconditionally();
        } catch (Exception e) {
        }

    }

    /**
     * Tests the functionality of saving a recipe from the search results.
     * This test simulates a user searching for "chicken", selecting the first result,
     * saving it, and then verifying that the recipe appears in the saved recipes list.
     *
     * @throws InterruptedException if the thread sleeping for UI response is interrupted.
     */
    @Test
    public void testSaveRecipe() throws InterruptedException {
        // Ensure the app starts with RecipesActivity
        // Perform a search operation
        onView(ViewMatchers.withId(R.id.enterRecipe)).perform(ViewActions.typeText("chicken"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.searchRecipeButton)).perform(click());
        // Adding a delay to wait for network response
        Thread.sleep(5000);
        // Select the first recipe from the search results
        onView(withId(R.id.RecipeRecycleView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // Click on the save button in RecipeDetailsActivity
        onView(withId(R.id.recipeSaveButton)).perform(click());
        // Go back to RecipesActivity
        Espresso.pressBack();
        // Open SavedRecipesActivity to verify the recipe was saved
        onView(withId(R.id.recipe_saved_icon)).perform(click());
        onView(withId(R.id.RecipeRecycleView))
                .check(matches(hasMinimumChildCount(1))); // Assuming at least one item is there
    }

    /**
     * Tests the functionality of displaying a help dialog within the app.
     * This test opens the help option from the menu and verifies that the help dialog is displayed with the correct information.
     */
    @Test
    public void testDisplayHelpDialog() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.recipe_help)).perform(click());

        // check AlertDialog content
        onView(withId(android.R.id.message))
                .check(matches(withText(R.string.recipe_help_info)))
                .check(matches(isDisplayed()));

    }

    /**
     * Tests the navigation to the Saved Recipes screen from the RecipesActivity.
     * This test clicks on the saved recipes icon and verifies that the Saved Recipes screen is displayed.
     *
     * @throws InterruptedException if the thread sleeping for UI response is interrupted.
     */
    @Test
    public void testNavigationToSavedRecipes() throws InterruptedException {
        onView(withId(R.id.recipe_saved_icon)).perform(click());
        onView(withId(R.id.RecipeRecycleView)).check(matches(isDisplayed()));
    }

    /**
     * Tests that searching for recipes populates the search results.
     * This test simulates a user searching for "pasta" and verifies that at least one result is returned and displayed.
     *
     * @throws InterruptedException if the thread sleeping for UI response is interrupted.
     */
    @Test
    public void testRecipeSearchPopulatesResults() throws InterruptedException {
        onView(withId(R.id.enterRecipe)).perform(typeText("pasta"), closeSoftKeyboard());
        onView(withId(R.id.searchRecipeButton)).perform(click());
        Thread.sleep(5000); // Wait for network response, consider using IdlingResource
        onView(withId(R.id.RecipeRecycleView)).check(matches(hasMinimumChildCount(1)));
    }

    /**
     * Tests the display of recipe details when a recipe from the search results is selected.
     * This test simulates a user searching for "salad", selecting the first result, and verifying that the details screen is displayed with content.
     *
     * @throws InterruptedException if the thread sleeping for UI response is interrupted.
     */
    @Test
    public void testRecipeDetailsDisplay() throws InterruptedException {

        onView(withId(R.id.enterRecipe)).perform(typeText("salad"), closeSoftKeyboard());
        onView(withId(R.id.searchRecipeButton)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.RecipeRecycleView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.inDeatilSuammry)).check(matches(not(withText("")))); // Assuming your summary TextView has ID recipeSummary
    }

    /**
     * Tests the back functionality within the RecipesActivity.
     * This test simulates a user performing a search, selecting a recipe, and then using the back functionality to return to the search results.
     *
     * @throws InterruptedException if the thread sleeping for UI response is interrupted.
     */
    @Test
    public void testBackFunctionality() throws InterruptedException {

        onView(withId(R.id.enterRecipe)).perform(typeText("soup"), closeSoftKeyboard());
        onView(withId(R.id.searchRecipeButton)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.RecipeRecycleView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Espresso.pressBack();
        onView(withId(R.id.searchRecipeButton)).check(matches(isDisplayed())); // check if is in RecipesActivity
    }

    /**
     * Tests the functionality that allows the user to return to the MainActivity from the RecipeActivity.
     * It simulates the user action of pressing a button that should navigate back to the MainActivity,
     * and then checks if the MainActivity is displayed by verifying the presence of a specific view.
     *
     * @throws InterruptedException if the thread sleeping for UI response is interrupted.
     */
    @Test
    public void testReturnToMainActivityFromRecipeActivity() throws InterruptedException {
        onView(withId(R.id.searchRecipeButton)).perform(click());
        Thread.sleep(1000);
        onView(withContentDescription(R.string.home)).perform(click());
        onView(withId(R.id.textView)).check(matches(isDisplayed()));
    }

}
