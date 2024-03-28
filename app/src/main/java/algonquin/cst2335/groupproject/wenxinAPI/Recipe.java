package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;
    @ColumnInfo(name = "RecipeID")
    protected int recipeID;
    @ColumnInfo(name = "Title")
    protected String title;
    @ColumnInfo(name="ImageURL")
    protected String imageURL;
    @ColumnInfo(name="Summary")
    protected String summary;
    @ColumnInfo(name = "SourceURL")
    protected String sourceURL;

    public Recipe(){

    }

    public Recipe( String title, String imageURL, String summary, String sourceURL,int recipeID) {

        this.title=title;
        this.imageURL=imageURL;
        this.summary=summary;
        this.sourceURL=sourceURL;
        this.recipeID=recipeID;
    }
//    public Recipe(int recipeID, String title, String imageURL){
//        this.recipeID=recipeID;
//        this.title=title;
//        this.imageURL=imageURL;
//    }
//    @Ignore
//    public Recipe(int id, int recipeID, String title, String imageURL, String summary, String sourceURL) {
//        this.id = id;
//        this.recipeID = recipeID;
//        this.title = title;
//        this.imageURL = imageURL;
//        this.summary = summary;
//        this.sourceURL = sourceURL;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }
}
