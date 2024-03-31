package algonquin.cst2335.groupproject.YashanAPI;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SongViewModel extends ViewModel {

    /**
     * A LiveData holding a list of {@link Song} objects. This list is observed by the UI
     * (activities or fragments) to react to data changes.
     * <p>
     * The {@link MutableLiveData} allows the data to be changed and observed. Upon initialization,
     * it starts with an empty {@link ArrayList} of {@link Song}. Activities or fragments can observe
     * this data to update the UI whenever the data changes. This separation of concerns ensures
     * that the UI controllers like activities and fragments do not need to worry about the lifecycle
     * and storage of the mutable data.
     */
    public MutableLiveData<ArrayList<Song>> songs = new MutableLiveData<>(new ArrayList<>());
}
