/**
 * Author: Zimeng Wang, 041095956
 * Date: Mar 26, 2024
 * Lab Section: CST2335 - 021
 * Purpose:
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sun_fav);

    menuHandler = new SunMenuHandler(this);

    Toolbar toolbar = findViewById(R.id.myToolbar);
    setSupportActionBar(toolbar);

    recyclerView = findViewById(R.id.rvFavouriteLocations);
    adapter = new FavouriteLocationAdapter();
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    viewModel = new ViewModelProvider(this).get(FavoriteLocationViewModel.class);
    viewModel.getLocations().observe(this, locations -> adapter.setLocations(locations));

    db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "favourite-locations-db").allowMainThreadQueries().build();

    loadFavouriteLocations();

    Button btnBackToSunHome = findViewById(R.id.btn_backtosunhome);
    btnBackToSunHome.setOnClickListener(v -> {
      Intent intent = new Intent(SunFav.this, SunHome.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      startActivity(intent);
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return menuHandler.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return menuHandler.onOptionsItemSelected(item);
  }

  private void loadFavouriteLocations() {
    List<FavouriteLocation> locations = db.favouriteLocationDao().getAll();
    viewModel.getLocations().setValue(new ArrayList<>(locations));
  }

  public void deleteFavouriteLocation(FavouriteLocation location) {
    new AlertDialog.Builder(this)
        .setTitle(R.string.sunfav_delete)
        .setMessage(R.string.sunfav_deleteconfirmation)
        .setPositiveButton(R.string.sunfav_deleteyes, (dialog, which) -> {
          db.favouriteLocationDao().delete(location);
          loadFavouriteLocations();
          Snackbar.make(recyclerView, R.string.sunfav_deleted, Snackbar.LENGTH_LONG)
              .setAction(R.string.sunfav_deleteundo, v -> {
                db.favouriteLocationDao().insert(location);
                loadFavouriteLocations();
              })
              .show();
        })
        .setNegativeButton(R.string.sunfav_deleteno, null)
        .show();
  }

  private class FavouriteLocationAdapter extends RecyclerView.Adapter<FavouriteLocationAdapter.ViewHolder> {

    private ArrayList<FavouriteLocation> locations = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sun_location, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      FavouriteLocation location = locations.get(position);
      holder.latitudeTextView.setText(String.format(getString(R.string.sunfav_latformat), location.getLatitude()));
      holder.longitudeTextView.setText(String.format(getString(R.string.sunfav_lngformat), location.getLongitude()));
      holder.deleteButton.setOnClickListener(v -> deleteFavouriteLocation(location));
      holder.searchButton.setOnClickListener(v -> {
        Intent intent = new Intent(SunFav.this, SunHome.class);
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude", location.getLongitude());

        // Save the selected latitude and longitude to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("SunHome", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("searchLatitude", Double.toString(location.getLatitude()));
        editor.putString("searchLongitude", Double.toString(location.getLongitude()));
        editor.apply();

        startActivity(intent);
      });
    }

    @Override
    public int getItemCount() {
      return locations.size();
    }

    public void setLocations(ArrayList<FavouriteLocation> locations) {
      this.locations = locations;
      notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
      TextView latitudeTextView;
      TextView longitudeTextView;
      ImageView deleteButton;
      ImageView searchButton;

      public ViewHolder(View itemView) {
        super(itemView);
        latitudeTextView = itemView.findViewById(R.id.display_latitude);
        longitudeTextView = itemView.findViewById(R.id.display_longtitude);
        deleteButton = itemView.findViewById(R.id.display_delete);
        searchButton = itemView.findViewById(R.id.display_search);
      }
    }
  }


}