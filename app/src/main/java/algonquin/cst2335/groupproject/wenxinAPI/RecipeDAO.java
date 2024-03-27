package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeDAO {

    @Insert
    public long insertRecipe(Recipe r);

    @Query("Select * from Recipe")
    public List<Recipe> getAllRecipes();

    @Delete
    public void deleteMessage(Recipe r);


}
