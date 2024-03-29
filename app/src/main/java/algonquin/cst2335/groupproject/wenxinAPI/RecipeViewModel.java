package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
/**
 * ViewModel for managing UI-related data in a lifecycle-conscious way for the recipe application.
 * This class is designed to store and manage UI-related data so that the data survives configuration changes
 * such as screen rotations, and is available to the appropriate UI controllers (activities, fragments).
 * It holds a list of {@link Recipe} objects that can be observed for changes.
 *  @author  Wenxin Li
 *  @version 1.6
 *  @since   2024-03-27
 */
public class RecipeViewModel extends ViewModel {
    /**
     * A LiveData holding a list of {@link Recipe} objects. This list is observed by the UI
     * (activities or fragments) to react to data changes.
     * <p>
     * The {@link MutableLiveData} allows the data to be changed and observed. Upon initialization,
     * it starts with an empty {@link ArrayList} of {@link Recipe}. Activities or fragments can observe
     * this data to update the UI whenever the data changes. This separation of concerns ensures
     * that the UI controllers like activities and fragments do not need to worry about the lifecycle
     * and storage of the mutable data.
     */
    public MutableLiveData<ArrayList<Recipe>> recipes = new MutableLiveData<>(new ArrayList<>());
}
