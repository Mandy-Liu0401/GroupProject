/**
 * database interface gives a dao access to vocabularyDAO
 *
 * @author Mengying Liu
 * @Student No. 041086143
 * @date Mar 25, 2024
 * @labSecNo. 021
 * @purpose It allows users to search terms and save terms
 *
 */
package algonquin.cst2335.groupproject.mengyingAPI;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Vocabulary.class}, version=1)
public abstract class VocabularyDatabase  extends RoomDatabase {
    /**
     * abstract method to gain access to dao object
     * @return dao object
     */
    public abstract VocabularyDAO vDAO();
}
