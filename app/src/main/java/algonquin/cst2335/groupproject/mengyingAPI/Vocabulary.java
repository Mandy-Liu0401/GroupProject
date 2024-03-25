package algonquin.cst2335.groupproject.mengyingAPI;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Vocabulary {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="ID")
    public int id;
    @ColumnInfo(name="TERM")
    protected String term;
    @ColumnInfo(name="DEFINITION")
    protected List<String> definitions;

    // Empty constructor required by Room
    public Vocabulary() { }

    Vocabulary (String term, List<String> definitions){
        this.term = term;
        this.definitions = definitions;
    }

    public String getTerm() {
        return term;
    }

    public List<String> getDefinitions() {
        return definitions;
    }
}
