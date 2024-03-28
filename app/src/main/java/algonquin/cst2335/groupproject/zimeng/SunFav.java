/**
 * Manages the display and interaction with the user's favourite locations.
 * This activity allows users to view, delete, and interact with saved favourite locations.
 *
 * @author Zimeng Wang
 * @date Mar 26, 2024
 * @labSection CST2335 - 021
 * @purpose To provide an interface for users to manage their saved favourite locations. The activity
 *          supports viewing all saved locations, deleting selected locations, and navigating back to
 *          the main functionality of the application.
 */

package algonquin.cst2335.groupproject.zimeng;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import algonquin.cst2335.groupproject.R;

public class SunFav extends AppCompatActivity {
  private SunMenuHandler menuHandler;
  private RecyclerView recyclerView;
  private FavouriteLocationAdapter adapter;
  private FavoriteLocationViewModel viewModel;
  private AppDatabase db;

  /**
   * Initializes the activity, sets up the RecyclerView for listing favourite locations,
   * and prepares the ViewModel for observing data changes.
   *
   * @param savedInstanceState A Bundle containing previously saved instance state.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Set the layout for the current activity.
    setContentView(R.layout.sun_fav);

    // Initialize the menu handler for this activity.
    menuHandler = new SunMenuHandler(this);

    // Set up the toolbar.
    Toolbar toolbar = findViewById(R.id.myToolbar);
    setSupportActionBar(toolbar);

    // Initialize the RecyclerView for displaying favourite locations.
    recyclerView = findViewById(R.id.rvFavouriteLocations);
    adapter = new FavouriteLocationAdapter();
    // Set the adapter and layout manager for the RecyclerView.
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    // Set up the ViewModel and observe changes to locations data.
    viewModel = new ViewModelProvider(this).get(FavoriteLocationViewModel.class);
    viewModel.getLocations().observe(this, locations -> adapter.setLocations(locations));

    // Initialize the database and load favourite locations.
    db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "favourite-locations-db").allowMainThreadQueries().build();
    loadFavouriteLocations();

    // Set up the back button and its click listener to navigate to the SunHome activity.
    Button btnBackToSunHome = findViewById(R.id.btn_backtosunhome);
    btnBackToSunHome.setOnClickListener(v -> {
      Intent intent = new Intent(SunFav.this, SunHome.class);
      // Ensure no new instance of SunHome activity is created if it is already in the back stack.
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      startActivity(intent);
    });
  }

  /**
   * Handles options menu creation.
   *
   * @param menu The options menu in which items are placed.
   * @return boolean Return true for the menu to be displayed, false otherwise.
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
   * Loads favourite locations from the database and updates the view model.
   */
  private void loadFavouriteLocations() {
    // Fetch all favourite location entries from the database.
    List<FavouriteLocation> locations = db.favouriteLocationDao().getAll();
    // Update the LiveData within the ViewModel with the new list of locations.
    viewModel.getLocations().setValue(new ArrayList<>(locations));
  }

  /**
   * Displays a confirmation dialog before deleting a favourite location. If the user confirms,
   * the location is deleted from the database and the list of locations is refreshed. Additionally,
   * an undo option is provided through a Snackbar, allowing the user to revert the deletion.
   *
   * @param location The FavouriteLocation object to be deleted.
   */
  public void deleteFavouriteLocation(FavouriteLocation location) {
    // Show a confirmation dialog before deletion.
    new AlertDialog.Builder(this)
        .setTitle(R.string.sunfav_delete)
        .setMessage(R.string.sunfav_deleteconfirmation)
        .setPositiveButton(R.string.sunfav_deleteyes, (dialog, which) -> {
          // Perform the deletion from the database.
          db.favouriteLocationDao().delete(location);
          // Reload the favourite locations to update the UI.
          loadFavouriteLocations();
          // Show a confirmation Snackbar with an option to undo the deletion.
          Snackbar.make(recyclerView, R.string.sunfav_deleted, Snackbar.LENGTH_LONG)
              .setAction(R.string.sunfav_deleteundo, v -> {
                // If undo is clicked, re-insert the location into the database.
                db.favouriteLocationDao().insert(location);
                loadFavouriteLocations();
              })
              .show();
        })
        .setNegativeButton(R.string.sunfav_deleteno, null)
        .show();
  }

  /**
   * Adapter class for the RecyclerView in SunFav activity.
   */
  private class FavouriteLocationAdapter extends RecyclerView.Adapter<FavouriteLocationAdapter.ViewHolder> {

    private ArrayList<FavouriteLocation> locations = new ArrayList<>();

    /**
     * Inflates the layout for individual list items.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View for each list item.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      // Inflate the layout for each list item.
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sun_location, parent, false);
      // Return a new ViewHolder instance.
      return new ViewHolder(view);
    }

    /**
     * Binds the data to the view holder for each list item.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      // Retrieve the location object based on position.
      FavouriteLocation location = locations.get(position);

      // Set the text views for latitude and longitude.
      holder.latitudeTextView.setText(String.format(getString(R.string.sunfav_latformat), location.getLatitude()));
      holder.longitudeTextView.setText(String.format(getString(R.string.sunfav_lngformat), location.getLongitude()));

      // Set the delete button's onclick listener to remove the location from the database.
      holder.deleteButton.setOnClickListener(v -> deleteFavouriteLocation(location));

      // Set the search button's onclick listener to navigate to the SunHome with the current location's details.
      holder.searchButton.setOnClickListener(v -> {
        Intent intent = new Intent(SunFav.this, SunHome.class);
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude", location.getLongitude());

        // Save the selected latitude and longitude to SharedPreferences for later retrieval.
        SharedPreferences sharedPreferences = getSharedPreferences("SunHome", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("searchLatitude", Double.toString(location.getLatitude()));
        editor.putString("searchLongitude", Double.toString(location.getLongitude()));
        editor.apply();

        // Start the SunHome activity with the selected location's data.
        startActivity(intent);
      });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
      // Return the size of the locations list.
      return locations.size();
    }

    /**
     * Updates the data set of the adapter and notifies any registered observers that the data set has changed.
     *
     * @param locations The new set of FavouriteLocation objects that will be displayed.
     */
    public void setLocations(ArrayList<FavouriteLocation> locations) {
      // Update the list of locations and notify the adapter to refresh the list.
      this.locations = locations;
      notifyDataSetChanged();
    }

    /**
     * Provides a reference to the views for each data item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
      TextView latitudeTextView;
      TextView longitudeTextView;
      ImageView deleteButton;
      ImageView searchButton;

      public ViewHolder(View itemView) {
        super(itemView);
        // Initialize the views from the layout.
        latitudeTextView = itemView.findViewById(R.id.display_latitude);
        longitudeTextView = itemView.findViewById(R.id.display_longtitude);
        deleteButton = itemView.findViewById(R.id.display_delete);
        searchButton = itemView.findViewById(R.id.display_search);
      }
    }
  }


}