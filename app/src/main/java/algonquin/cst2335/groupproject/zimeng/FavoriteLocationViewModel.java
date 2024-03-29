/**
 * ViewModel for managing UI-related data in the lifecycle of the FavoriteLocation screen.
 *
 * @author Zimeng Wang, 041095956
 * @date Mar 26, 2024
 * @labSection CST2335 - 021
 * @purpose To provide a data holder for favorite locations that the UI can observe, enabling automatic UI updates
 *          in response to data changes.
 */
package algonquin.cst2335.groupproject.zimeng;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class FavoriteLocationViewModel extends ViewModel {
  // MutableLiveData holding a list of FavouriteLocation objects.
  private MutableLiveData<ArrayList<FavouriteLocation>> locations = new MutableLiveData<>();

  /**
   * Gets the LiveData object containing the list of favorite locations.
   *
   * @return A MutableLiveData object containing an ArrayList of FavouriteLocation objects.
   */
  public MutableLiveData<ArrayList<FavouriteLocation>> getLocations() {
    return locations;
  }
}
