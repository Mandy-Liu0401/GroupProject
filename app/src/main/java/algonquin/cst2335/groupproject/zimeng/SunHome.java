/**
 * Author: Zimeng Wang, 041095956
 * Date: Mar 26, 2024
 * Lab Section: CST2335 - 021
 * Purpose:
 */

package algonquin.cst2335.groupproject.zimeng;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    binding = SunHomeBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    menuHandler = new SunMenuHandler(this);

    // Set the toolbar
    androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Set today's date
    SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.sunhome_date_format), Locale.getDefault());
    String currentDate = dateFormat.format(new Date());
    binding.sunhomeDate.setText(currentDate);

    // Initialize SharedPreferences
    sharedPreferences = getSharedPreferences("SunHome", Context.MODE_PRIVATE);
    // Retrieve saved latitude, longitude, sunrise, and sunset times
    String savedLatitude = sharedPreferences.getString("latitude", "");
    String savedLongitude = sharedPreferences.getString("longitude", "");
    String savedSunrise = sharedPreferences.getString("sunrise", "");
    String savedSunset = sharedPreferences.getString("sunset", "");
    // Retrieve saved EditText content
    String savedSearchLatitude = sharedPreferences.getString("searchLatitude", "");
    String savedSearchLongitude = sharedPreferences.getString("searchLongitude", "");

    // Set the retrieved values to the TextView fields
    binding.latitude.setText(savedLatitude);
    binding.longtitude.setText(savedLongitude);
    binding.sunriseTime.setText(savedSunrise);
    binding.sunsetTime.setText(savedSunset);
    binding.inputLat.setText(savedSearchLatitude);
    binding.inputLong.setText(savedSearchLongitude);

    // If saved latitude and longitude are not empty, do the sunrise/sunset query
    if (!savedLatitude.isEmpty() && !savedLongitude.isEmpty()) {
      double latitude = Double.parseDouble(savedLatitude);
      double longitude = Double.parseDouble(savedLongitude);
      doSunriseSunsetQuery(latitude, longitude);
    }


    // Initialize the database
    db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "favourite-locations-db").allowMainThreadQueries().build();

    binding.btnSunhomeSearch.setOnClickListener(v -> {
      String latitude = binding.inputLat.getText().toString();
      String longitude = binding.inputLong.getText().toString();

      if (!latitude.isEmpty() && !longitude.isEmpty()) {
        lookupSunriseSunset(latitude, longitude);
      } else {
        Toast.makeText(SunHome.this, R.string.sunhome_toast_enter_lat_long, Toast.LENGTH_SHORT).show();
      }
    });

    binding.btnSunhomeAddfav.setOnClickListener(v -> {
      String latitude = binding.latitude.getText().toString();
      String longitude = binding.longtitude.getText().toString();

      if (!latitude.isEmpty() && !longitude.isEmpty()) {
        try {
          FavouriteLocation location = new FavouriteLocation(Double.parseDouble(latitude), Double.parseDouble(longitude));
          location.setId(0);
          db.favouriteLocationDao().insert(location);
          Toast.makeText(SunHome.this, R.string.sunhome_toast_location_added, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
          Log.e("SunHome", getString(R.string.sunhome_toast_error_adding_location), e);
          Toast.makeText(SunHome.this, R.string.sunhome_toast_error_adding_location + e.getMessage(), Toast.LENGTH_LONG).show();
        }
      } else {
        Toast.makeText(SunHome.this, R.string.sunhome_toast_lookup_location_first, Toast.LENGTH_SHORT).show();
      }
    });

    binding.btnSunhomeFav.setOnClickListener(v -> {
      Intent intent = new Intent(SunHome.this, SunFav.class);
      startActivity(intent);
    });

    Intent intent = getIntent();
    if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
      double latitude = intent.getDoubleExtra("latitude", 0);
      double longitude = intent.getDoubleExtra("longitude", 0);
      binding.inputLat.setText(String.valueOf(latitude));
      binding.inputLong.setText(String.valueOf(longitude));
      lookupSunriseSunset(String.valueOf(latitude), String.valueOf(longitude));
    }

  }

  @Override
  protected void onPause() {
    super.onPause();
    // Save the current latitude, longitude, sunrise, and sunset times
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString("latitude", binding.latitude.getText().toString());
    editor.putString("longitude", binding.longtitude.getText().toString());
    editor.putString("sunrise", binding.sunriseTime.getText().toString());
    editor.putString("sunset", binding.sunsetTime.getText().toString());
    editor.putString("searchLatitude", binding.inputLat.getText().toString());
    editor.putString("searchLongitude", binding.inputLong.getText().toString());
    editor.apply();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return menuHandler.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return menuHandler.onOptionsItemSelected(item);
  }

  private void lookupSunriseSunset(String latitude, String longitude) {
    double lat = Double.parseDouble(latitude);
    double lng = Double.parseDouble(longitude);
    String url = "https://api.sunrise-sunset.org/json?lat=" + lat + "&lng=" + lng + "&date=today";

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
        response -> {
          try {
            JSONObject results = response.getJSONObject("results");
            String sunriseUtc = results.getString("sunrise");
            String sunsetUtc = results.getString("sunset");

            SimpleDateFormat inputFormat = new SimpleDateFormat(getString(R.string.sunhome_input_time_format), Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat(getString(R.string.sunhome_output_time_format), Locale.US);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date sunriseDate = inputFormat.parse(sunriseUtc);
            Date sunsetDate = inputFormat.parse(sunsetUtc);

            outputFormat.setTimeZone(TimeZone.getDefault());
            String sunrise = outputFormat.format(sunriseDate);
            String sunset = outputFormat.format(sunsetDate);

            // Save the retrieved sunrise and sunset times
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("latitude", latitude);
            editor.putString("longitude", longitude);
            editor.putString("sunrise", sunrise);
            editor.putString("sunset", sunset);
            editor.apply();

            runOnUiThread(() -> {
              binding.latitude.setText(latitude);
              binding.longtitude.setText(longitude);
              binding.sunriseTime.setText(sunrise);
              binding.sunsetTime.setText(sunset);
            });
          } catch (JSONException | ParseException e) {
            e.printStackTrace();
          }
        },
        error -> {
          Toast.makeText(SunHome.this, R.string.sunhome_toast_error_occured + error.getMessage(), Toast.LENGTH_SHORT).show();
        });

    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(jsonObjectRequest);
  }

  private void doSunriseSunsetQuery(double latitude, double longitude) {
    String url = "https://api.sunrise-sunset.org/json?lat=" + latitude + "&lng=" + longitude + "&date=today";

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
        (Request.Method.GET, url, null, response -> {
          try {
            JSONObject results = response.getJSONObject("results");
            String sunriseUtc = results.getString("sunrise");
            String sunsetUtc = results.getString("sunset");

            SimpleDateFormat inputFormat = new SimpleDateFormat(getString(R.string.sunhome_input_time_format), Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat(getString(R.string.sunhome_output_time_format), Locale.US);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date sunriseDate = inputFormat.parse(sunriseUtc);
            Date sunsetDate = inputFormat.parse(sunsetUtc);

            outputFormat.setTimeZone(TimeZone.getDefault());
            String sunrise = outputFormat.format(sunriseDate);
            String sunset = outputFormat.format(sunsetDate);

            binding.sunriseTime.setText(sunrise);
            binding.sunsetTime.setText(sunset);

          } catch (JSONException | ParseException e) {
            e.printStackTrace();
          }
        }, error -> {
          Toast.makeText(SunHome.this, R.string.sunhome_toast_error_fetching_data, Toast.LENGTH_SHORT).show();
        });

    Volley.newRequestQueue(this).add(jsonObjectRequest);
  }

}