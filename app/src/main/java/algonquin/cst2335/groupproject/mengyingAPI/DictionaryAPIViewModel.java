/**
 * This page manages live data terms
 *
 * @author Mengying Liu
 * @Student No. 041086143
 * @date Mar 25, 2024
 * @labSecNo. 021
 * @purpose It allows users to search terms and save terms
 *
 */
package algonquin.cst2335.groupproject.mengyingAPI;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class DictionaryAPIViewModel extends ViewModel {
    /**
     * MutableLiveData holding a list of terms
     */
    public MutableLiveData<ArrayList<Vocabulary>> terms;

    /**
     * constructor
     */
    public DictionaryAPIViewModel() {
        terms = new MutableLiveData<>();
    }

    /**
     * Gets the LiveData object containing the list of terms
     * @return live data terms
     */
    public MutableLiveData<ArrayList<Vocabulary>> getTerms() {
        return terms;
    }
}
