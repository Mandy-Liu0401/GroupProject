/**
 * This file will manipulate local database
 *
 * @author Mengying Liu
 * @Student No. 041086143
 * @date Mar 25, 2024
 * @labSecNo. 021
 * @purpose It allows users to add, find a term, delete a term , deleteAllTerms
 *
 */
package algonquin.cst2335.groupproject.mengyingAPI;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VocabularyDAO {
    /**
     * insert a record
     * @param term a term
     */
    @Insert
    void insertTerm(Vocabulary term);

    /**
     * return all records
     * @return a list of all terms
     */
    @Query( "Select * from Vocabulary")
     List<Vocabulary> getAllTerms();

    /**
     * delete a term
     * @param term the term to be deleted
     */
    @Delete
    void deleteTerm(Vocabulary term);

    /**
     * delete all method
     */
    @Query("DELETE FROM Vocabulary")
    void deleteAllTerms();

    /**
     * find a term by the word
     * @param input user input
     * @return teh term object
     */
    @Query("SELECT * FROM Vocabulary WHERE TERM = :input")
    Vocabulary findTermByInput(String input);
}
