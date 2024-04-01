package algonquin.cst2335.groupproject.YashanAPI;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Database class for managing the SQLite database that stores Song entities.
 */
@Database(entities = {Song.class}, version = 1)
public abstract class SongDatabase extends RoomDatabase {

    /**
     * Get the Data Access Object (DAO) for Song entities.
     * @return The DAO for interacting with Song entities in the database.
     */
    public abstract SongDAO SongDAO();

}
