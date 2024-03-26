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

    // Empty constructor required by Room
    public Vocabulary() { }

    Vocabulary (String term, String definitions){
        this.term = term;
        this.definitions = definitions;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
    public String getDefinitions() {
        return definitions;
    }

    public void setDefinitions(String definitions) {
        this.definitions = definitions;
    }
}
