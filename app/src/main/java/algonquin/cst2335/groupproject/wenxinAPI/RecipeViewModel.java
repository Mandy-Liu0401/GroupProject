package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class RecipeViewModel extends ViewModel {
    public MutableLiveData<ArrayList<Recipe>> recipes = new MutableLiveData<>(new ArrayList<>());
}
