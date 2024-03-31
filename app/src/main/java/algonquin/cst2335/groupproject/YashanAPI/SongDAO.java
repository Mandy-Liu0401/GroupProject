package algonquin.cst2335.groupproject.YashanAPI;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
import algonquin.cst2335.groupproject.YashanAPI.Song;
@Dao
public interface SongDAO {

    @Insert
    long insertSong(Song song);

    @Query("Select * from Song")
    List<Song> getAllSongs();

    @Delete
    void deleteSong(Song song);
}

