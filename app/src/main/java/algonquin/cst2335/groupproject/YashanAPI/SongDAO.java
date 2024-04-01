package algonquin.cst2335.groupproject.YashanAPI;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

/**
 * Data Access Object (DAO) for interacting with the Song entity in the database.
 */
@Dao
public interface SongDAO {

    /**
     * Insert a new song into the database.
     * @param song The song to insert.
     * @return The row ID of the newly inserted song.
     */
    @Insert
    long insertSong(Song song);

    /**
     * Retrieve all songs from the database.
     * @return A list of all songs stored in the database.
     */
    @Query("SELECT * FROM Song")
    List<Song> getAllSongs();

    /**
     * Delete a song from the database.
     * @param song The song to delete.
     */
    @Delete
    void deleteSong(Song song);
}
