package algonquin.cst2335.groupproject.mengyingAPI;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class DictionaryAPIViewModel extends ViewModel {
    public MutableLiveData<ArrayList<Vocabulary>> terms = new MutableLiveData<>();
}
