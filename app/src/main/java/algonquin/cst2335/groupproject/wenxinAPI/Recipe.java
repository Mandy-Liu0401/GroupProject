package algonquin.cst2335.groupproject.wenxinAPI;
public class Recipe {
    private String title;
    private String image;
    private int recipeID;

    public Recipe() {
    }

    public Recipe(String title, String image, int recipeID) {
        this.title = title;
        this.image = image;
        this.recipeID = recipeID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }
}
