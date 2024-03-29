/**
 * This class is responsible for displaying the main screen of the sun-themed feature, including
 * the UI for searching sunrise and sunset times based on geographic coordinates.
 *
 * @author Zimeng Wang
 * @date Mar 26, 2024
 * @labSection CST2335 - 021
 * @purpose To provide functionality for users to search for and display sunrise and sunset times, manage favourite locations.
 */

package algonquin.cst2335.groupproject.zimeng;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.SunHomeBinding;

public class SunHome extends AppCompatActivity {
  private SunMenuHandler menuHandler;
  private SunHomeBinding binding;
  private SharedPreferences sharedPreferences;
  private AppDatabase db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Inflate the layout for this activity using data binding
    binding = SunHomeBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    // Initialize the custom menu handler for this activity
    menuHandler = new SunMenuHandler(this);

    // Setup the toolbar as the app bar for the activity
    androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Display the current date on the UI
    SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.sunhome_date_format), Locale.getDefault());
    String currentDate = dateFormat.format(new Date());
    binding.sunhomeDate.setText(currentDate);

    // Initialize SharedPreferences to store and retrieve persistent key-value pairs
    sharedPreferences = getSharedPreferences("SunHome", Context.MODE_PRIVATE);

    // Retrieve previously saved location and time data from SharedPreferences
    String savedLatitude = sharedPreferences.getString("latitude", "");
    String savedLongitude = sharedPreferences.getString("longitude", "");
    String savedSunrise = sharedPreferences.getString("sunrise", "");
    String savedSunset = sharedPreferences.getString("sunset", "");

    // Retrieve the last searched latitude and longitude values
    String savedSearchLatitude = sharedPreferences.getString("searchLatitude", "");
    String savedSearchLongitude = sharedPreferences.getString("searchLongitude", "");

    // Update the UI with the retrieved values
    binding.latitude.setText(savedLatitude);
    binding.longtitude.setText(savedLongitude);
    binding.sunriseTime.setText(savedSunrise);
    binding.sunsetTime.setText(savedSunset);
    binding.inputLat.setText(savedSearchLatitude);
    binding.inputLong.setText(savedSearchLongitude);

    // If there are saved latitude and longitude, automatically perform a sunrise/sunset query
    if (!savedLatitude.isEmpty() && !savedLongitude.isEmpty()) {
      double latitude = Double.parseDouble(savedLatitude);
      double longitude = Double.parseDouble(savedLongitude);
      doSunriseSunsetQuery(latitude, longitude);
    }


    // If there are saved latitude and longitude, automatically perform a sunrise/sunset query
    db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "favourite-locations-db").allowMainThreadQueries().build();

    // Setup listener for the search button to perform sunrise and sunset time query
    binding.btnSunhomeSearch.setOnClickListener(v -> {
      String latitude = binding.inputLat.getText().toString();
      String longitude = binding.inputLong.getText().toString();

      // Validate input before performing the query
      if (!latitude.isEmpty() && !longitude.isEmpty()) {
        lookupSunriseSunset(latitude, longitude);
      } else {
        Toast.makeText(SunHome.this, R.string.sunhome_toast_enter_lat_long, Toast.LENGTH_SHORT).show();
      }
    });

    // Setting up listeners
    binding.btnSunhomeSearch.setOnClickListener(v -> handleSearchButtonClick());
    binding.btnSunhomeAddfav.setOnClickListener(v -> handleAddFavButtonClick());
    binding.btnSunhomeFav.setOnClickListener(v -> handleFavButtonClick());

    // If the activity was started with an intent containing latitude and longitude, use those values
    Intent intent = getIntent();
    if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
      double latitude = intent.getDoubleExtra("latitude", 0);
      double longitude = intent.getDoubleExtra("longitude", 0);
      binding.inputLat.setText(String.valueOf(latitude));
      binding.inputLong.setText(String.valueOf(longitude));
      lookupSunriseSunset(String.valueOf(latitude), String.valueOf(longitude));
    }

  }

  /**
   * Saves the current state of UI elements and user inputs to SharedPreferences when the activity enters the paused state.
   */
  @Override
  protected void onPause() {
    super.onPause();
    // Initialize the SharedPreferences.Editor to make changes
    SharedPreferences.Editor editor = sharedPreferences.edit();

    // Save the current values from the UI to SharedPreferences
    editor.putString("latitude", binding.latitude.getText().toString());
    editor.putString("longitude", binding.longtitude.getText().toString());
    editor.putString("sunrise", binding.sunriseTime.getText().toString());
    editor.putString("sunset", binding.sunsetTime.getText().toString());
    editor.putString("searchLatitude", binding.inputLat.getText().toString());
    editor.putString("searchLongitude", binding.inputLong.getText().toString());

    // Apply the changes to SharedPreferences
    editor.apply();
  }

  /**
   * Inflates the menu for the activity using the SunMenuHandler.
   *
   * @param menu The options menu in which items are placed.
   * @return true for the menu to be displayed; false otherwise.
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return menuHandler.onCreateOptionsMenu(menu);
  }

  /**
   * Handles action bar item clicks.
   *
   * @param item The menu item that was selected.
   * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return menuHandler.onOptionsItemSelected(item);
  }

  /**
   * Performs a lookup for sunrise and sunset times based on the provided latitude and longitude.
   *
   * @param latitude  The latitude of the location to fetch sunrise and sunset times for.
   * @param longitude The longitude of the location to fetch sunrise and sunset times for.
   */
  private void lookupSunriseSunset(String latitude, String longitude) {
    double lat = Double.parseDouble(latitude);
    double lng = Double.parseDouble(longitude);
    // Construct the request URL with the provided latitude and longitude
    String url = "https://api.sunrise-sunset.org/json?lat=" + lat + "&lng=" + lng + "&date=today";

    // Create a JsonObjectRequest to retrieve the sunrise and sunset times
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
        response -> {
          try {
            // Parse the response to extract sunrise and sunset times in UTC
            JSONObject results = response.getJSONObject("results");
            String sunriseUtc = results.getString("sunrise");
            String sunsetUtc = results.getString("sunset");

            // Convert UTC times to local times
            SimpleDateFormat inputFormat = new SimpleDateFormat(getString(R.string.sunhome_input_time_format), Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat(getString(R.string.sunhome_output_time_format), Locale.US);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date sunriseDate = inputFormat.parse(sunriseUtc);
            Date sunsetDate = inputFormat.parse(sunsetUtc);

            outputFormat.setTimeZone(TimeZone.getDefault());
            String sunrise = outputFormat.format(sunriseDate);
            String sunset = outputFormat.format(sunsetDate);

            // Update the UI with the new times and save them to SharedPreferences
            runOnUiThread(() -> {
              binding.latitude.setText(latitude);
              binding.longtitude.setText(longitude);
              binding.sunriseTime.setText(sunrise);
              binding.sunsetTime.setText(sunset);
            });

            // Save the retrieved sunrise and sunset times
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("latitude", latitude);
            editor.putString("longitude", longitude);
            editor.putString("sunrise", sunrise);
            editor.putString("sunset", sunset);
            editor.apply();

          } catch (JSONException | ParseException e) {
            e.printStackTrace();
          }
        },
        error -> {
          // Handle any errors from the request
          Toast.makeText(SunHome.this, R.string.sunhome_toast_error_occured + error.getMessage(), Toast.LENGTH_SHORT).show();
        });

    // Add the request to the Volley RequestQueue for execution
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(jsonObjectRequest);
  }

  /**
   * Triggers a sunrise and sunset time query for a predefined location.
   *
   * @param latitude  The latitude of the location.
   * @param longitude The longitude of the location.
   */
  private void doSunriseSunsetQuery(double latitude, double longitude) {

    // Construct the URL for the sunrise-sunset API with query parameters for latitude, longitude, and date
    String url = "https://api.sunrise-sunset.org/json?lat=" + latitude + "&lng=" + longitude + "&date=today";

    // Create a JSON object request with the GET method for the constructed URL
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
        (Request.Method.GET, url, null, response -> {
          try {
            // Parse the 'results' JSON object from the response
            JSONObject results = response.getJSONObject("results");
            String sunriseUtc = results.getString("sunrise");
            String sunsetUtc = results.getString("sunset");

            // Define the format of the input time (UTC) and output time (local time zone)
            SimpleDateFormat inputFormat = new SimpleDateFormat(getString(R.string.sunhome_input_time_format), Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat(getString(R.string.sunhome_output_time_format), Locale.US);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Parse the sunrise and sunset times from UTC to Date objects
            Date sunriseDate = inputFormat.parse(sunriseUtc);
            Date sunsetDate = inputFormat.parse(sunsetUtc);

            // Format the Date objects to strings in the local time zone
            outputFormat.setTimeZone(TimeZone.getDefault());
            String sunrise = outputFormat.format(sunriseDate);
            String sunset = outputFormat.format(sunsetDate);

            // Set the formatted sunrise and sunset times to the TextViews
            binding.sunriseTime.setText(sunrise);
            binding.sunsetTime.setText(sunset);

          } catch (JSONException | ParseException e) {
            // Handle JSON parsing and date formatting exceptions
            e.printStackTrace();
          }
        }, error -> {
          // Handle errors in network request and show a Toast with the error message
          Toast.makeText(SunHome.this, R.string.sunhome_toast_error_fetching_data, Toast.LENGTH_SHORT).show();
        });

    // Add the request to the Volley request queue for execution
    Volley.newRequestQueue(this).add(jsonObjectRequest);
  }

  /**
   * Handles the click event of the search button.
   */
  private void handleSearchButtonClick() {
    // Retrieve latitude and longitude input from the user.
    String latitude = binding.inputLat.getText().toString();
    String longitude = binding.inputLong.getText().toString();

    // Check if both latitude and longitude inputs are filled.
    if (!latitude.isEmpty() && !longitude.isEmpty()) {
      // Call method to perform the sunrise and sunset times lookup.
      lookupSunriseSunset(latitude, longitude);
    } else {
      // Prompt user to enter both latitude and longitude if one (or both) are missing.
      Toast.makeText(SunHome.this, R.string.sunhome_toast_enter_lat_long, Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Handles the click event of the add to favorites button.
   */
  private void handleAddFavButtonClick() {
    // Retrieve the displayed latitude and longitude from the TextViews.
    String latitude = binding.latitude.getText().toString();
    String longitude = binding.longtitude.getText().toString();

    // Check if the latitude and longitude have been set.
    if (!latitude.isEmpty() && !longitude.isEmpty()) {
      try {
        // Create a new FavouriteLocation object and insert it into the database.
        FavouriteLocation location = new FavouriteLocation(Double.parseDouble(latitude), Double.parseDouble(longitude));
        location.setId(0);
        db.favouriteLocationDao().insert(location);
        // Confirm to the user that the location was added.
        Toast.makeText(SunHome.this, R.string.sunhome_toast_location_added, Toast.LENGTH_SHORT).show();
      } catch (Exception e) {
        // Log and show an error if there was a problem adding the location.
        Log.e("SunHome", getString(R.string.sunhome_toast_error_adding_location), e);
        Toast.makeText(SunHome.this, getString(R.string.sunhome_toast_error_adding_location) + e.getMessage(), Toast.LENGTH_LONG).show();
      }
    } else {
      // Prompt the user to perform a location lookup before trying to add it to favorites.
      Toast.makeText(SunHome.this, R.string.sunhome_toast_lookup_location_first, Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Handles the click event of the view favorites button.
   */
  private void handleFavButtonClick() {
    // Create an intent to navigate to the SunFav activity.
    Intent intent = new Intent(SunHome.this, SunFav.class);
    // Start the SunFav activity.
    startActivity(intent);
  }


}