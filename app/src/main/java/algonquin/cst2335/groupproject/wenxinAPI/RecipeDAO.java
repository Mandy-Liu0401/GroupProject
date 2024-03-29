package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
/**
 *
 * Data Access Object (DAO) for the {@link Recipe} entity.
 * This interface defines the database interactions related to {@link Recipe} objects,
 * allowing for operations such as insertion, retrieval, and deletion of recipes from the database.
 * Room uses this interface to create a concrete implementation that allows for direct interaction
 * with the database, encapsulating SQL queries and providing a more robust and simplified data management system.
 *
 * @author  Wenxin Li
 * @version 1.6
 * @since   2024-03-27
 */
@Dao
public interface RecipeDAO {
    /**
     * Inserts a new {@link Recipe} into the database.
     *
     * @param r The {@link Recipe} object to be inserted. It must not be null.
     * @return The row ID of the inserted recipe. This can be used to identify the inserted record.
     */
    @Insert
    public long insertRecipe(Recipe r);
    /**
     * Retrieves all {@link Recipe} entries from the database.
     *
     * @return A {@link List} of {@link Recipe} objects representing all recipes stored in the database.
     *         The list is empty if no recipes are found.
     */
    @Query("Select * from Recipe")
    public List<Recipe> getAllRecipes();
    /**
     * Deletes a specific {@link Recipe} from the database.
     *
     * @param r The {@link Recipe} object to be deleted from the database. It must not be null.
     */
    @Delete
    public void deleteRecipe(Recipe r);


}
