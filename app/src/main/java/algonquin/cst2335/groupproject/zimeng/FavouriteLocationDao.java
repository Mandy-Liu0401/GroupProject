/**
 * Author: Zimeng Wang, 041095956
 * Date: Mar 26, 2024
 * Lab Section: CST2335 - 021
 * Purpose:
 */

package algonquin.cst2335.groupproject.zimeng;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FavouriteLocationDao {

  @Query("SELECT * FROM favourite_locations")
  List<FavouriteLocation> getAll();

  @Insert
  void insert(FavouriteLocation location);

  @Delete
  void delete(FavouriteLocation location);

}