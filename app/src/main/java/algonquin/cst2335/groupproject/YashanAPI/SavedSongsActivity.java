package algonquin.cst2335.groupproject.YashanAPI;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

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

public class SavedSongsActivity extends AppCompatActivity {

    private ActivitySavedSongsBinding binding;
    private SongViewModel songViewModel;
    private RecyclerView.Adapter<MyRowHolder> myAdapter;
    private SongDatabase db;
    private SongDAO songDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.savedSongsToolbar);
        setTitle(R.string.saved_songs);

        db = Room.databaseBuilder(getApplicationContext(), SongDatabase.class, "deezer_song_db").build();
        songDAO = db.SongDAO();

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.songs.observe(this, updatedSongs -> {
            if (myAdapter != null) {
                myAdapter.notifyDataSetChanged();
            }
        });

        initializeRecyclerView();
        loadSongsFromDatabase();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.home) {
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
            return true;
        } else if (item.getItemId() == R.id.search) {
            // Handle search action
            return true;

        } else if (item.getItemId() == R.id.help) {
            // Show help dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(SavedSongsActivity.this);
            builder.setMessage(getString(R.string.song_help_info)).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_deezer_song_search, menu);
        return true;
    }

    private void loadSongsFromDatabase() {
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            ArrayList<Song> savedSongs = (ArrayList<Song>) songDAO.getAllSongs();

            runOnUiThread(() -> {
                songViewModel.songs.setValue(savedSongs);
                if (savedSongs.isEmpty()) {
                    Toast.makeText(this, R.string.no_saved_songs_prompt, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

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
                holder.songTitleText.setText(song.getTitle());
                holder.songArtistText.setText(song.getArtist());
                holder.songAlbumText.setText(song.getAlbumName());
            }

            @Override
            public int getItemCount() {
                return songViewModel.songs.getValue() == null ? 0 : songViewModel.songs.getValue().size();
            }
        };
        binding.savedSongsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.savedSongsRecyclerView.setAdapter(myAdapter);
    }

    public void goBack(View view) {
        onBackPressed();
    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView songTitleText;
        TextView songArtistText;
        TextView songAlbumText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            songTitleText = itemView.findViewById(R.id.songTitleText);
            songArtistText = itemView.findViewById(R.id.songArtistText);
            songAlbumText = itemView.findViewById(R.id.songAlbumText);
        }
    }
}
