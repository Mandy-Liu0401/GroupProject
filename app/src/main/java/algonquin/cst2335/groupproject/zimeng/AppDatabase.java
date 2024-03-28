/**
 * Serves as the primary access point to the application's persisted, favorite location data.
 *
 * @author Zimeng Wang
 * @date Mar 26, 2024
 * @labSection CST2335 - 021
 * @purpose To create a centralized database access point for the application, specifically managing the storage
 *          and retrieval of favourite location data.
 */
package algonquin.cst2335.groupproject.zimeng;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FavouriteLocation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

  /**
   * Provides access to the FavouriteLocationDao interface for managing favorite locations in the database.
   *
   * @return FavouriteLocationDao, an interface for database operations related to favorite locations.
   */
  public abstract FavouriteLocationDao favouriteLocationDao();
}
