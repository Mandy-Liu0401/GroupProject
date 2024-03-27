package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.RoomDatabase;


@Database(entities = {Recipe.class}, version=1)
public abstract class RecipeDatabase  extends RoomDatabase {
    public abstract RecipeDAO cmDAO();
}
