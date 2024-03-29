package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.groupproject.MainActivity;
import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivitySavedRecipesBinding;
import algonquin.cst2335.groupproject.databinding.RecipeSavedItemBinding;

/**
 *
 * Activity for displaying saved recipes.
 * This activity shows a list of recipes that the user has saved to their local database.
 * Users can view detailed information about each recipe and have the option to remove recipes from their saved list.
 *
 * @author  Wenxin Li
 * @version 1.6
 * @since   2024-03-28
 */
public class SavedRecipesActivity extends AppCompatActivity {
    private ActivitySavedRecipesBinding binding;
    private RecipeViewModel recipeViewModel;
    private RecyclerView.Adapter<SavedRecipesActivity.MyRowHolder> myAdapter;
    private RecipeDatabase db;
    private RecipeDAO rDAO;

    /**
     * Sets up the activity's user interface when the activity is created.
     * This includes initializing the toolbar, database, view model, and RecyclerView.
     * It also triggers loading of saved recipes from the database.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes);
        //initialize binding
        binding = ActivitySavedRecipesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //toolBar
        setSupportActionBar(binding.recipeToolbar);
        setTitle(R.string.saved_list);

        //initialize database
        db = Room.databaseBuilder(getApplicationContext(), RecipeDatabase.class, "recipe-database").build();
        rDAO = db.cmDAO();
        //initialize view model
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        recipeViewModel.recipes.observe(this, updatedRecipes -> {
            if (myAdapter != null) {
                myAdapter.notifyDataSetChanged(); // Notify adapter data has changed
            }
        });
        initializeRecyclerView();
        loadRecipesFromDatabase();
    }

    /**
     * Handles action bar item clicks. The action bar will automatically handle clicks on the Home/Up button,
     * so long as a parent activity is specified in AndroidManifest.xml.
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
        }else if(item.getItemId() == R.id.search){
            Intent intent3 =new Intent(this, RecipesActivity.class);
            return true;
        }
        else if (item.getItemId() == R.id.recipe_saved_icon) {
            Intent intent2 = new Intent(this, SavedRecipesActivity.class);
            startActivity(intent2);
            return true;
        } else if (item.getItemId() == R.id.help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SavedRecipesActivity.this);
            builder.setMessage(getString(R.string.recipe_help_info)).show();
            return true;
        }
        return true;
    }

    /**
     * Inflates the menu options from a menu resource (XML file).
     * This adds items to the action bar if it is present.
     *
     * @param menu The options menu in which items are placed.
     * @return boolean Must return true for the menu to be displayed; if false, it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return true;
    }

    /**
     * Loads saved recipes from the local database asynchronously.
     * Once loaded, the recipes are displayed in the RecyclerView through the ViewModel.
     */
    private void loadRecipesFromDatabase() {
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            ArrayList<Recipe> savedRecipes = (ArrayList<Recipe>) rDAO.getAllRecipes();
            runOnUiThread(() -> recipeViewModel.recipes.setValue(savedRecipes));
        });
    }
    /**
     * Initializes the RecyclerView used to display the list of saved recipes.
     * This includes setting up the adapter, layout manager, and item decoration.
     */
    private void initializeRecyclerView() {
        myAdapter = new RecyclerView.Adapter<SavedRecipesActivity.MyRowHolder>() {
            @NonNull
            @Override
            public SavedRecipesActivity.MyRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                RecipeSavedItemBinding savedItemBinding = RecipeSavedItemBinding.inflate(getLayoutInflater(), parent, false);
                return new SavedRecipesActivity.MyRowHolder(savedItemBinding.getRoot(), viewType);
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                Recipe recipe = recipeViewModel.recipes.getValue().get(position);
                holder.recipeIDText.setText(Integer.toString(recipe.recipeID));
                holder.recipeTitleText.setText(recipe.getTitle());
                //image
                String imageUrl = recipe.getImageURL() != null ? recipe.getImageURL() : "";
                Glide.with(holder.itemView.getContext())
                        .load(!imageUrl.isEmpty() ? imageUrl : R.drawable.recipe)
                        .into(holder.recipeImageInput);
            }

            @Override
            public int getItemCount() {
                return recipeViewModel.recipes.getValue() == null ? 0 : recipeViewModel.recipes.getValue().size();
            }
        };
        binding.RecipeRecycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.RecipeRecycleView.setAdapter(myAdapter);
    }
    /**
     * ViewHolder class for displaying each saved recipe in the RecyclerView.
     * This class holds references to the recipe ID, title, and image views, and sets up a click listener for each item.
     */
    class MyRowHolder extends RecyclerView.ViewHolder {
        private final TextView recipeIDText;
        private final TextView recipeTitleText;
        private final ImageView recipeImageInput;

        MyRowHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            recipeIDText = itemView.findViewById(R.id.recipeID);
            recipeTitleText = itemView.findViewById(R.id.recipeTitle);
            recipeImageInput = itemView.findViewById(R.id.recipeImage);

            itemView.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();
                Recipe recipe = recipeViewModel.recipes.getValue().get(position);
                showSavedRecipeDetails(recipe);
            });
        }
    }

    /**
     * Displays detailed information about a saved recipe.
     * This includes showing the recipe's summary, source URL, and image.
     * Users also have the option to remove the recipe from their saved list.
     *
     * @param recipe The recipe to display in detail.
     */
    public void showSavedRecipeDetails(Recipe recipe) {
        Log.d("SavedRecipesActivity", "show detail recipe " + recipe.getTitle());
        binding.inDeatilSuammry.setText(recipe.getSummary());
        binding.inDetailSourceURL.setText(recipe.getSourceURL());

        String imageUrl = recipe.getImageURL() != null ? recipe.getImageURL() : "";
        Glide.with(this).load(!imageUrl.isEmpty() ? imageUrl : R.drawable.recipe).into(binding.detailImage);

        //setOnClickListener to save recipe in recipe details
        binding.recipeRemoveButton.setOnClickListener(v -> removeRecipe(recipe));
    }

    /**
     * Removes a recipe from the saved list and the local database asynchronously.
     * Once removed, the UI is updated to reflect the change.
     *
     * @param recipe The recipe to remove.
     */
    public void removeRecipe(Recipe recipe) {
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            Recipe recipeToDelete = recipe;
            rDAO.deleteRecipe(recipeToDelete);
            ArrayList<Recipe> updatedRecipes = new ArrayList<>(recipeViewModel.recipes.getValue());
            //remove the recipe from list in recycle view
            updatedRecipes.remove(recipeToDelete);
            runOnUiThread(() -> {
                //after deleted update list in recycle view
                recipeViewModel.recipes.setValue(updatedRecipes);
                Snackbar.make(binding.getRoot(), R.string.recipe_deleted_prompt, Snackbar.LENGTH_LONG).show();
            });
        });

    }
}