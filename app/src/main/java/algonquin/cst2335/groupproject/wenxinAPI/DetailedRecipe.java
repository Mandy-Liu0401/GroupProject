package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DetailedRecipe {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "recipeID")
    private int recipeID;
    @ColumnInfo(name ="Image")
    private String image;
    @ColumnInfo(name="Summary")
    private String summary;
    @ColumnInfo(name ="SourceURl")
    private String sourceUrl;

    public DetailedRecipe() {
    }

    public DetailedRecipe(int recipeID, String image, String summary, String sourceUrl) {
        this.recipeID = recipeID;
        this.image = image;
        this.summary = summary;
        this.sourceUrl = sourceUrl;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
