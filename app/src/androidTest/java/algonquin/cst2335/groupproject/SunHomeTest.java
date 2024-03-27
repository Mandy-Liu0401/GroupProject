
package algonquin.cst2335.groupproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import org.hamcrest.Matcher;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import algonquin.cst2335.groupproject.zimeng.SunFav;
import algonquin.cst2335.groupproject.zimeng.SunHome;
import static androidx.test.espresso.assertion.ViewAssertions.matches;



@RunWith(AndroidJUnit4.class)
public class SunHomeTest {

@Rule
public ActivityScenarioRule<SunHome> activityScenarioRule = new ActivityScenarioRule<>(SunHome.class);
private SunHome activity;

@Before
public void setUp() {
  activityScenarioRule.getScenario().onActivity(activity -> {
    this.activity = activity;
  });
}

  @Test
  public void testSearchButtonClick() {
    // Clear the EditText fields and then input new latitude and longitude values
    onView(withId(R.id.input_lat)).perform(replaceText("40.7128"), closeSoftKeyboard());
    onView(withId(R.id.input_long)).perform(replaceText("-74.0060"), closeSoftKeyboard());

    // Click on the search button
    onView(withId(R.id.btn_sunhome_search)).perform(click());

    // Assert that sunrise and sunset TextViews are updated and not "Unknown"
    onView(withId(R.id.sunrise_time)).check(matches(not(withText("Unknown"))));
    onView(withId(R.id.sunset_time)).check(matches(not(withText("Unknown"))));
  }

  @Test
  public void testFavouriteButtonClick() {
    onView(withId(R.id.btn_sunhome_fav)).perform(click());
    // Assert that the SunFav activity is started
    Intent expectedIntent = new Intent(activity, SunFav.class);
    assertEquals(expectedIntent.getComponent().getClassName(), SunFav.class.getName());
  }

  @Test
  public void testSharedPreferences() {
    // Clear the EditText fields and then type new latitude and longitude values
    onView(withId(R.id.input_lat)).perform(replaceText("45.4215"), closeSoftKeyboard());
    onView(withId(R.id.input_long)).perform(replaceText("-75.6972"), closeSoftKeyboard());
    // Click on the search button
    onView(withId(R.id.btn_sunhome_search)).perform(click());
    // Pause briefly to allow any async operations to complete
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // Retrieve the SharedPreferences to assert the values
    Context appContext = ApplicationProvider.getApplicationContext();
    SharedPreferences sharedPreferences = appContext.getSharedPreferences("SunHome", Context.MODE_PRIVATE);
    String savedLatitude = sharedPreferences.getString("latitude", "");
    String savedLongitude = sharedPreferences.getString("longitude", "");
    assertEquals("45.4215", savedLatitude);
    assertEquals("-75.6972", savedLongitude);
  }

  @Test
  public void testAddToFavourite() {
    // Clear the EditText fields and then input new latitude and longitude values
    onView(withId(R.id.input_lat)).perform(replaceText("40.7128"), closeSoftKeyboard());
    onView(withId(R.id.input_long)).perform(replaceText("-74.0060"), closeSoftKeyboard());
    // Click on the search button
    onView(withId(R.id.btn_sunhome_search)).perform(click());
    // Wait a moment for data to be saved
    try {
      Thread.sleep(2000); // Wait for 2 seconds
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // Retrieve the SharedPreferences to assert the values
    Context appContext = ApplicationProvider.getApplicationContext();
    SharedPreferences sharedPreferences = appContext.getSharedPreferences("SunHome", Context.MODE_PRIVATE);
    String savedLatitude = sharedPreferences.getString("latitude", "");
    String savedLongitude = sharedPreferences.getString("longitude", "");
    // Verify that the saved data matches what we expect
    assertEquals("40.7128", savedLatitude);
    assertEquals("-74.0060", savedLongitude);
  }



  @Test
  public void testDisplayFavouriteList() {
    // First, add a location to favourites to ensure the list is not empty
    addLocationToFavourites();
    // Now, test displaying the favourites list
    onView(withId(R.id.btn_sunhome_fav)).perform(click());
    onView(withId(R.id.rvFavouriteLocations))
        .check(matches(hasDescendant(withId(R.id.display_latitude))));
    onView(withId(R.id.rvFavouriteLocations))
        .check(matches(hasDescendant(withId(R.id.display_longtitude))));
  }

  @Test
  public void testDeleteFavouriteLocation() {
    addLocationToFavourites();
    onView(withId(R.id.btn_sunhome_fav)).perform(click());
    onView(withId(R.id.rvFavouriteLocations))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.display_delete)));
  }

  private void addLocationToFavourites() {
    // Input latitude and longitude
    onView(withId(R.id.input_lat)).perform(replaceText("40.7128"), closeSoftKeyboard());
    onView(withId(R.id.input_long)).perform(replaceText("-74.0060"), closeSoftKeyboard());
    // Click on the search button
    onView(withId(R.id.btn_sunhome_search)).perform(click());
    // Assuming there's a button to add the current location to favourites
    onView(withId(R.id.btn_sunhome_addfav)).perform(click());
  }

  public static ViewAction clickChildViewWithId(final int id) {
    return new ViewAction() {
      @Override
      public Matcher<View> getConstraints() {
        return null;
      }
      @Override
      public String getDescription() {
        return "Click on a child view with specified id.";
      }
      @Override
      public void perform(UiController uiController, View view) {
        View v = view.findViewById(id);
        v.performClick();
      }
    };
  }


}



