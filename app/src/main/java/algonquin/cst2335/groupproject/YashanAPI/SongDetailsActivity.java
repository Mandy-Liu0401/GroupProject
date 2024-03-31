package algonquin.cst2335.groupproject.YashanAPI;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import algonquin.cst2335.groupproject.R;

public class SongDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_details);

        // Retrieve song details from intent
        // Retrieve song details from intent with the correct key
        Song song = getIntent().getParcelableExtra("song");


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
        ImageView albumCoverImageView = findViewById(R.id.albumCoverImageView);

        // Set song details to views
        titleTextView.setText(song.getTitle());
        artistTextView.setText(song.getArtist());
        albumTextView.setText(song.getAlbumName());

        // Load album cover image using Glide library
        Glide.with(this)
                .load(song.getAlbumCoverUrl())
                .placeholder(R.drawable.dictionary)
                .error(R.drawable.dictionary)
                .into(albumCoverImageView);
    }
}
