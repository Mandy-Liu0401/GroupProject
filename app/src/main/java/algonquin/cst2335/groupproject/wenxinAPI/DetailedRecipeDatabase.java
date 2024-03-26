package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = {DetailedRecipe.class},version = 1)
public abstract class DetailedRecipeDatabase extends RoomDatabase {
    public abstract DetailedRecipeDAO drDAO();

}
