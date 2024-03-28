/**
 * Entity representing a favorite location in the 'favourite_locations' database table.
 *
 * @author Zimeng Wang, 041095956
 * @date Mar 26, 2024
 * @labSection CST2335 - 021
 * @purpose To define the schema for storing favorite location data.
 */
package algonquin.cst2335.groupproject.zimeng;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourite_locations")
public class FavouriteLocation {

  /**
   * Unique identifier for the location entry.
   */
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "id")
  public int id;

  /**
   * Latitude component of the location.
   */
  @ColumnInfo(name = "latitude")
  public double latitude;

  /**
   * Longitude component of the location.
   */
  @ColumnInfo(name = "longitude")
  public double longitude;

  /**
   * Constructs a FavouriteLocation object with specified latitude and longitude.
   *
   * @param latitude The latitude of the location.
   * @param longitude The longitude of the location.
   */
  public FavouriteLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Returns the ID of the location.
   *
   * @return The unique ID of the location.
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the latitude of the location.
   *
   * @return The latitude value.
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * Returns the longitude of the location.
   *
   * @return The longitude value.
   */
  public double getLongitude() {
    return longitude;
  }

  /**
   * Sets the ID of the location.
   *
   * @param id The unique identifier to set.
   */
  public void setId(int id) {
    this.id = id;
  }


}
