/**
 * Author: Zimeng Wang, 041095956
 * Date: Mar 26, 2024
 * Lab Section: CST2335 - 021
 * Purpose:
 */

package algonquin.cst2335.groupproject.zimeng;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FavouriteLocation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
  public abstract FavouriteLocationDao favouriteLocationDao();
}