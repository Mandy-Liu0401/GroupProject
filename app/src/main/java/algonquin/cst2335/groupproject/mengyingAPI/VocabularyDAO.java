package algonquin.cst2335.groupproject.mengyingAPI;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VocabularyDAO {
    @Insert
    void insertTerm(Vocabulary term);

    @Query( "Select * from Vocabulary")
    public List<Vocabulary> getAllTerms();

    @Delete
    void deleteTerm(Vocabulary term);

    @Query("DELETE FROM Vocabulary")
    void deleteAllTerms();
}
