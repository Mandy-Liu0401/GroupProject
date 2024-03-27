/**
 * Author: Zimeng Wang, 041095956
 * Date: Mar 26, 2024
 * Lab Section: CST2335 - 021
 * Purpose:
 */

package algonquin.cst2335.groupproject.zimeng;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class FavoriteLocationViewModel extends ViewModel {
  private MutableLiveData<ArrayList<FavouriteLocation>> locations =new MutableLiveData<>();
  public MutableLiveData<ArrayList<FavouriteLocation>> getLocations(){return locations;}
}
