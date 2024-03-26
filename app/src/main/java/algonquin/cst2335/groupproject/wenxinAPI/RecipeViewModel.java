package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class RecipeViewModel {
    public MutableLiveData<ArrayList<DetailedRecipe>> messages = new MutableLiveData<>(new ArrayList<>());
}
