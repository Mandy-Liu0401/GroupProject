package algonquin.cst2335.groupproject.YashanAPI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import algonquin.cst2335.groupproject.MainActivity;
import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivityDeezerSongSearchBinding;
import algonquin.cst2335.groupproject.databinding.SongItemBinding;

public class DeezerSongSearchActivity extends AppCompatActivity {

    private ActivityDeezerSongSearchBinding binding;
    private RecyclerView.Adapter<MyRowHolder> myAdapter;
    private SongViewModel songViewModel;
    private SongDatabase database;
    private SongDAO songDAO;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeezerSongSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        setTitle(R.string.search_songs);

        database = Room.databaseBuilder(getApplicationContext(), SongDatabase.class, "deezer_song_db").build();
        songDAO = database.SongDAO();

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.songs.observe(this, updatedSongs -> {
            if (myAdapter != null) {
                myAdapter.notifyDataSetChanged();
            }
        });

        initializeRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_deezer_song_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_saved_songs) {
            startActivity(new Intent(DeezerSongSearchActivity.this, SavedSongsActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initializeRecyclerView() {
        myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                SongItemBinding songItemBinding = SongItemBinding.inflate(getLayoutInflater(), parent, false);
                return new MyRowHolder(songItemBinding.getRoot());
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
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(myAdapter);
    }

    public void searchSongs(View view) {
        String query = binding.searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            requestQueue = Volley.newRequestQueue(this);
            String url = "https://api.deezer.com/search/track/?q=" + query;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            ArrayList<Song> songs = new ArrayList<>();
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject songObject = data.getJSONObject(i);
                                int songId = songObject.getInt("id");
                                String title = songObject.getString("title");
                                String artist = songObject.getJSONObject("artist").getString("name");
                                int duration = songObject.getInt("duration");
                                String albumName = songObject.getJSONObject("album").getString("title");
                                String albumCoverUrl = songObject.getJSONObject("album").getString("cover_small");

                                Song song = new Song(songId, title, artist, duration, albumName, albumCoverUrl);
                                songs.add(song);
                            }
                            songViewModel.songs.setValue(songs);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DeezerSongSearchActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(DeezerSongSearchActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSearchTerm(String searchTerm) {
        SharedPreferences sharedPreferences = getSharedPreferences("DeezerSearchTerm", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LastSearchTerm", searchTerm);
        editor.apply();
    }

    private String getLastSearchTerm() {
        SharedPreferences sharedPreferences = getSharedPreferences("DeezerSearchTerm", MODE_PRIVATE);
        return sharedPreferences.getString("LastSearchTerm", "");
    }

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
