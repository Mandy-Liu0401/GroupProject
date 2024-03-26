package algonquin.cst2335.groupproject.zimeng;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FavouriteLocation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
  public abstract FavouriteLocationDao favouriteLocationDao();
}