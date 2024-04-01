package algonquin.cst2335.groupproject.YashanAPI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.groupproject.MainActivity;
import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.YashanAPI.Song;
import algonquin.cst2335.groupproject.YashanAPI.SongDAO;
import algonquin.cst2335.groupproject.YashanAPI.SongDatabase;
import algonquin.cst2335.groupproject.YashanAPI.SongViewModel;
import algonquin.cst2335.groupproject.databinding.ActivitySavedSongsBinding;
import algonquin.cst2335.groupproject.databinding.SavedSongItemBinding;

/**
 * Activity for displaying saved songs.
 */
public class SavedSongsActivity extends AppCompatActivity {

    private ActivitySavedSongsBinding binding;
    private SongViewModel songViewModel;
    private RecyclerView.Adapter<MyRowHolder> myAdapter;
    private SongDatabase db;
    private SongDAO songDAO;

    ArrayList<Song> savedSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.savedSongsToolbar);
        setTitle(R.string.saved_songs);

        // Initialize the database and DAO
        db = Room.databaseBuilder(getApplicationContext(), SongDatabase.class, getString(R.string.deezer_song_db)).build();
        songDAO = db.SongDAO();

        // Initialize the ViewModel
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.songs.observe(this, updatedSongs -> {
            if (myAdapter != null) {
                myAdapter.notifyDataSetChanged();
            }
        });

        // Load saved songs from the database
        loadSongsFromDatabase();

        // Initialize the RecyclerView
        initializeRecyclerView();
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

    /**
     * Display the help dialog with instructions on how to use the interface.
     */
    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Help));
        builder.setMessage(getString(R.string.help_dialog_message));
        builder.setPositiveButton(getString(R.string.OK), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Load saved songs from the database.
     */
    private void loadSongsFromDatabase() {
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            savedSongs = (ArrayList<Song>) songDAO.getAllSongs();
            runOnUiThread(() -> {
                songViewModel.songs.setValue(savedSongs);
                if (savedSongs.isEmpty()) {
                    Toast.makeText(this, R.string.no_saved_songs_prompt, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Initialize the RecyclerView to display saved songs.
     */
    private void initializeRecyclerView() {
        myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                SavedSongItemBinding savedItemBinding = SavedSongItemBinding.inflate(getLayoutInflater(), parent, false);
                return new MyRowHolder(savedItemBinding.getRoot());
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                Song song = songViewModel.songs.getValue().get(position);
                holder.songTitle.setText(song.getTitle());
                holder.songArtist.setText(song.getArtist());
                holder.songAlbum.setText(song.getAlbumName());
                Glide.with(holder.itemView.getContext()).load(song.getAlbumCoverUrl()).into(holder.albumCover);
            }

            @Override
            public int getItemCount() {
                return songViewModel.songs.getValue() == null ? 0 : songViewModel.songs.getValue().size();
            }
        };
        binding.savedSongsRecyclerView.setAdapter(myAdapter);
    }

    /**
     * ViewHolder class for holding views of each row in the RecyclerView.
     */
    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        TextView songArtist;
        TextView songAlbum;
        ImageView albumCover;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.songTitleText);
            songArtist = itemView.findViewById(R.id.songArtistText);
            songAlbum = itemView.findViewById(R.id.songAlbumText);
            albumCover = itemView.findViewById(R.id.albumCoverImageView);
        }
    }
}
