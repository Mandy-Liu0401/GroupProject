package algonquin.cst2335.groupproject.mengyingAPI;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class DictionaryAPIViewModel extends ViewModel {
    public MutableLiveData<ArrayList<Vocabulary>> terms;

    public DictionaryAPIViewModel() {
        terms = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<Vocabulary>> getTerms() {
        return terms;
    }
}
