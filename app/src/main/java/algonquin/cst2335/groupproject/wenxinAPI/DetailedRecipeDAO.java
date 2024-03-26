package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DetailedRecipeDAO {
    @Insert
    public long insertRecipe(DetailedRecipe recipe);

    @Query("SELECT * FROM DetailedRecipe")
    public List<DetailedRecipe> getAllRecipes();

    @Delete
    public void deleteRecipe(DetailedRecipe recipe);

    @Query("DELETE FROM DetailedRecipe")
    public void deleteAllRecipes();
}
