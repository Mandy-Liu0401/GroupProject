/**
 * This file simulate a record in database
 *
 * @author Mengying Liu
 * @Student No. 041086143
 * @date Mar 25, 2024
 * @labSecNo. 021
 *
 */
package algonquin.cst2335.groupproject.mengyingAPI;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Vocabulary {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="ID")
    protected int id;
    @ColumnInfo(name="TERM")
    protected String term;
    @ColumnInfo(name="DEFINITION")
    protected String definitions;

    /**
     * Empty constructor required by Room
     */
    public Vocabulary() { }

    /**
     * overloaded constructor with term and definition passed
     * @param term the term string
     * @param definitions the definitions string
     */
    Vocabulary (String term, String definitions){
        this.term = term;
        this.definitions = definitions;
    }
    /**
     * getter for term
     * @return a term
     */
    public String getTerm() {
        return term;
    }

    /**
     * setter for a term
     * @param term each term passed
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * getter for definitions
     * @return the definitions
     */
    public String getDefinitions() {
        return definitions;
    }

    /**
     * setter for definitions
     * @param definitions a string passed as definitions
     */
    public void setDefinitions(String definitions) {
        this.definitions = definitions;
    }
}
