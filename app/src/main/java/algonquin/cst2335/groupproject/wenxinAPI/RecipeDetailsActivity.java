package algonquin.cst2335.groupproject.wenxinAPI;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import algonquin.cst2335.groupproject.MainActivity;
import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivityRecipeDetailsBinding;


/**
 * This activity is used to display the details of a recipe. It allows users to view detailed information about a recipe,
 * including its title, summary, source URL, and image. Additionally, users can save the recipe to their local database
 * or remove it if it has already been saved. This activity handles both displaying saved recipes and recipes fetched from
 * an external API.
 *
 * @author Wenxin Li
 * @version 1.6
 * @since 2024-03-30
 */
public class RecipeDetailsActivity extends AppCompatActivity {

    private ActivityRecipeDetailsBinding binding;

    private RecipeDatabase db;
    private RecipeDAO rDAO;

    /**
     * Initializes the activity, sets up the toolbar, database connection, and view bindings.
     * It also determines whether the recipe is already saved and adjusts the UI accordingly.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize database
        db = Room.databaseBuilder(getApplicationContext(), RecipeDatabase.class, "recipe-database").build();
        rDAO = db.cmDAO();

        //toolBar
        setSupportActionBar(binding.recipeToolbar);
        setTitle(R.string.search_recipe_title);



        Intent intent = getIntent();
        Recipe inDetailPageRecipe = intent.getParcelableExtra("RecipeDetails");


        boolean isSaved = intent.getBooleanExtra("IsSaved", false); // default set to false
        showInDetailPage(inDetailPageRecipe);
        if (isSaved) {
            // if is the intent from SavedRecipesActivity then show remove button
            binding.recipeSaveButton.setVisibility(View.GONE);
            binding.recipeRemoveButton.setVisibility(View.VISIBLE);
            //then initialize the remove button
            binding.recipeRemoveButton.setOnClickListener(v -> removeRecipe(inDetailPageRecipe));
        } else {
            // if is the intent from RecipesActivity then show save button
            binding.recipeSaveButton.setVisibility(View.VISIBLE);
            binding.recipeRemoveButton.setVisibility(View.GONE);
            //then initialize the save button
            binding.recipeSaveButton.setOnClickListener(v -> saveRecipe(inDetailPageRecipe));
        }


    }

    /**
     * Handles action bar item clicks, providing navigation back to the main activity,
     * search recipes activity, saved recipes activity, or displaying help information.
     *
     * @param item The menu item that was selected.
     * @return boolean Return true to consume the menu action here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.home) {
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
            return true;
        } else if (item.getItemId() == R.id.search) {
            Intent intent3 = new Intent(this, RecipesActivity.class);
            startActivity(intent3);
            return true;
        } else if (item.getItemId() == R.id.recipe_saved_icon) {
            Intent intent2 = new Intent(this, SavedRecipesActivity.class);
            startActivity(intent2);
            return true;
        } else if (item.getItemId() == R.id.help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RecipeDetailsActivity.this);
            builder.setMessage(getString(R.string.recipe_help_info)).show();
            return true;
        }
        return true;
    }

    /**
     * Inflates the menu; this adds items to the action bar if it is present.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return true;
    }

    /**
     * Displays the detailed information of a recipe in the UI.
     *
     * @param recipe The recipe to display.
     */
    private void showInDetailPage(Recipe recipe) {
        Log.d("RecipeDetailsActivity", "show detail recipe " + recipe.getTitle());
        binding.inDeatilSuammry.setText(recipe.getSummary());

        binding.inDetailSourceURL.setText(recipe.getSourceURL());
        binding.detailID.setText(String.valueOf(recipe.getRecipeID()));
        binding.detailTitle.setText(recipe.getTitle());


        String imageUrl = recipe.getImageURL() != null ? recipe.getImageURL() : "";
        Glide.with(this)
                .load(!imageUrl.isEmpty() ? imageUrl : R.drawable.recipe)
                .into(binding.detailImage);
    }

    /**
     * Saves the currently displayed recipe to the local database.
     *
     * @param recipe The recipe to save.
     */
    public void saveRecipe(Recipe recipe) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Log.d("RecipeDetailsActivity", "click save recipe");
                rDAO.insertRecipe(recipe);
                Snackbar.make(binding.getRoot(), R.string.recipe_saved, Snackbar.LENGTH_LONG).show();
                Log.d("RecipeDetailsActivity", "Recipe saved successfully.");
            } catch (Exception e) {
                Log.e("RecipeDetailsActivity", "Error saving recipe: " + e.getMessage());
            } finally {
                executor.shutdown();
            }
        });
    }

    /**
     * Removes the currently displayed recipe from the local database.
     *
     * @param recipe The recipe to remove.
     */
    public void removeRecipe(Recipe recipe) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            rDAO.deleteRecipe(recipe);
            runOnUiThread(() -> {
                Snackbar.make(binding.getRoot(), R.string.recipe_deleted_prompt, Snackbar.LENGTH_LONG).show();
            });
        });
        executor.shutdown();

    }
}