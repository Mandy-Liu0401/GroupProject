/**
 * DAO interface for managing CRUD operations on the FavouriteLocation entities within the
 * application's database.
 *
 * @author Zimeng Wang
 * @date Mar 26, 2024
 * @labSection CST2335 - 021
 * @purpose To facilitate access and manipulation of favourite locations data stored in the app's database,
 *          providing an abstraction layer over complex SQL operations.
 */
package algonquin.cst2335.groupproject.zimeng;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FavouriteLocationDao {

  /**
   * Retrieves all favourite location entries from the database.
   *
   * @return A list of FavouriteLocation entities.
   */
  @Query("SELECT * FROM favourite_locations")
  List<FavouriteLocation> getAll();

  /**
   * Inserts a new favourite location into the database.
   *
   * @param location The FavouriteLocation entity to insert into the database.
   */
  @Insert
  void insert(FavouriteLocation location);

  /**
   * Deletes a specific favourite location from the database.
   *
   * @param location The FavouriteLocation entity to delete from the database.
   */
  @Delete
  void delete(FavouriteLocation location);
}
