package algonquin.cst2335.groupproject.YashanAPI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.bumptech.glide.Glide;

import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.YashanAPI.Song;
import algonquin.cst2335.groupproject.YashanAPI.SongDAO;
import algonquin.cst2335.groupproject.YashanAPI.SongDatabase;
import algonquin.cst2335.groupproject.databinding.ActivitySavedSongsBinding;
import algonquin.cst2335.groupproject.databinding.ActivitySongDetailsBinding;
import algonquin.cst2335.groupproject.wenxinAPI.RecipeDatabase;

public class SongDetailsActivity extends AppCompatActivity {

    private ActivitySongDetailsBinding binding;
    private Song song;
    private SongDatabase db;
    private SongDAO songDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongDetailsBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_song_details);


        setSupportActionBar(binding.toolbar);
        setTitle(R.string.saved_songs);

        // Initialize SongDAO
        songDAO = Room.databaseBuilder(getApplicationContext(), SongDatabase.class, "song-database")
                .allowMainThreadQueries() // For simplicity, allow queries on the main thread (not recommended for production)
                .build()
                .SongDAO();

        // Retrieve song details from intent
        song = getIntent().getParcelableExtra(getString(R.string.song));

        // Check if song object is null
        if (song == null) {
            Toast.makeText(this, "Error: Song details not found", Toast.LENGTH_SHORT).show();
            finish(); // Finish activity if song details are not found
            return;
        }

        // Initialize views
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView artistTextView = findViewById(R.id.artistTextView);
        TextView albumTextView = findViewById(R.id.albumTextView);
        TextView durationTextView = findViewById(R.id.durationTextView);
        ImageView albumCoverImageView = findViewById(R.id.albumCoverImageView);
        Button saveButton = findViewById(R.id.saveButton);

        // Set song details to views
        titleTextView.setText(song.getTitle());
        artistTextView.setText(song.getArtist());
        albumTextView.setText(song.getAlbumName());
        durationTextView.setText(String.valueOf(song.getDuration())); // Convert duration to String
        Glide.with(this)
                .load(song.getAlbumCoverUrl())
                .placeholder(R.drawable.songs)
                .error(R.drawable.songs)
                .into(albumCoverImageView);

        // Set onClickListener for the save button
        saveButton.setOnClickListener(v -> saveSongToDatabase());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_deezer_song_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home_Button) {
            // Navigate to DeezerSongSearchActivity
            Intent homeIntent = new Intent(this, DeezerSongSearchActivity.class);
            startActivity(homeIntent);
            return true;
        } else if (id == R.id.menu_saved_songs) {
            // Navigate to SavedSongsActivity
            Intent savedIntent = new Intent(this, SavedSongsActivity.class);
            startActivity(savedIntent);
            return true;
        } else if (id == R.id.help) {
            // Show help dialog
            showHelpDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Help));
        builder.setMessage (getString(R.string.Instructions_for_how_to_use_the_interface));
        builder.setPositiveButton(getString(R.string.OK), (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    // Method to save the song to the database
    private void saveSongToDatabase() {
        long result = songDAO.insertSong(song);
        if (result != -1) {
            Toast.makeText(this, getString(R.string.song_saved_succesfully), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.Failed_to_save_song), Toast.LENGTH_SHORT).show();

        }
    }
}
