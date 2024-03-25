package algonquin.cst2335.groupproject.mengyingAPI;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import java.util.ArrayList;

@Database(entities = {Vocabulary.class}, version=1)
public abstract class VocabularyDatabase  extends RoomDatabase {
    public abstract VocabularyDAO vDAO();
}
