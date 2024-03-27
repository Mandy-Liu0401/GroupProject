/**
 * Author: Zimeng Wang, 041095956
 * Date: Mar 26, 2024
 * Lab Section: CST2335 - 021
 * Purpose:
 */

package algonquin.cst2335.groupproject.zimeng;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourite_locations")
public class FavouriteLocation {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "id")
  public int id;
  @ColumnInfo(name = "latitude")
  public double latitude;
  @ColumnInfo(name = "longitude")
  public double longitude;

  public FavouriteLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public int getId() {
    return id;
  }
  public double getLatitude() {
    return latitude;
  }
  public double getLongitude() {
    return longitude;
  }

  public void setId(int id) {
    this.id = id;
  }
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

}
