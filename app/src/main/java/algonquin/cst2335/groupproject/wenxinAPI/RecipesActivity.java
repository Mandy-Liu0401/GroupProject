package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import algonquin.cst2335.groupproject.MainActivity;
import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivityRecipesBinding;
import algonquin.cst2335.groupproject.databinding.RecipeSearchBinding;
/**
 *
 * Activity responsible for displaying and searching recipes.
 * It allows users to search for recipes via an external API, view the results in a list, and select a recipe to see detailed information.
 * Users can also save their favorite recipes to a local database for later viewing.mply displays "Hello World!" to the standard output.
 *
 * @author  Wenxin Li
 * @version 1.6
 * @since   2024-03-27
 */
public class RecipesActivity extends AppCompatActivity {

    private ActivityRecipesBinding binding;
    private RecipeViewModel recipeViewModel;
    private RecyclerView.Adapter<MyRowHolder> myAdapter;
    private RecipeDatabase db;
    private RecipeDAO rDAO;
    private RequestQueue requestQueue;


    /**
     * Initializes the activity, sets up the UI components and data bindings.
     * This method is called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize binding
        binding = ActivityRecipesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolBar
        setSupportActionBar(binding.recipeToolbar);
        setTitle(R.string.search_recipe_title);

        //initialize database
        db = Room.databaseBuilder(getApplicationContext(), RecipeDatabase.class, "recipe-database").build();
        rDAO = db.cmDAO();

        //initialize VewModel
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        recipeViewModel.recipes.observe(this, updatedRecipes -> {
            if (myAdapter != null) {
                myAdapter.notifyDataSetChanged(); // Notify adapter data has changed
            }
        });

        initializeRecyclerView();
        initializeSearchButtons();

        // set last search term into editText
        EditText searchField = findViewById(R.id.enterRecipe);
        searchField.setText(getLastSearchTerm());
    }
    /**
     * Handles action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button,
     * so long as you specify a parent activity in AndroidManifest.xml.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.home) {
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
            return true;
        }else if(item.getItemId() == R.id.search){
            Intent intent3 =new Intent(this, RecipesActivity.class);
            startActivity(intent3);
            return true;
        }
        else if (item.getItemId() == R.id.recipe_saved_icon) {
            Intent intent2 = new Intent(this, SavedRecipesActivity.class);
            startActivity(intent2);
            return true;
        } else if (item.getItemId() == R.id.help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RecipesActivity.this);
                builder.setMessage(getString(R.string.recipe_help_info)).show();
                return true;
            }
        return true;
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
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
     * Initializes the search button and its click listener.
     * Sets up the functionality to initiate a recipe search based on the user's query.
     */
    private void initializeSearchButtons() {
        binding.searchRecipeButton.setOnClickListener(v -> searchRecipe());
    }

    /**
     * Initiates a search for recipes based on the text input by the user.
     * It makes an API call to fetch recipes matching the search term and updates the UI with the results.
     */

    private void searchRecipe() {
        //clean view for next search
        recipeViewModel.recipes.setValue(new ArrayList<>());

        requestQueue = Volley.newRequestQueue(this);
        String query = binding.enterRecipe.getText().toString().trim();
        //saved to file
        EditText searchField = findViewById(R.id.enterRecipe);
        String searchTerm = searchField.getText().toString();
        saveSearchTerm(searchTerm);

        String url = "https://api.spoonacular.com/recipes/complexSearch?query=" + query + "&apiKey=" + "abb4102db3714c6eb68729fbb6ad77fd";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // if success then parse
                    parseRecipesResponse(response);
                },
                error -> {
                    // failed toast
                    Toast.makeText(RecipesActivity.this, R.string.error + error.getMessage(), Toast.LENGTH_LONG).show();
                });
        // add to queue
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Parses the JSON response from the recipe search API call.
     * It extracts the relevant recipe information and updates the ViewModel with the new data.
     *
     * @param response The JSONObject response received from the API.
     */
    private void parseRecipesResponse(JSONObject response) {
        //List<ChatMessage> recipesResult = new ArrayList<>();
        try {
            JSONArray recipesArray = response.getJSONArray("results");
            for (int i = 0; i < recipesArray.length(); i++) {
                JSONObject recipeObject = recipesArray.getJSONObject(i);

                int recipeID = recipeObject.getInt("id");
                String title = recipeObject.getString("title");
                String imageURL = recipeObject.optString("image", "");

                Recipe recipe = new Recipe(title, imageURL, null, null, recipeID);

                //set to viewModel
                ArrayList<Recipe> recipeList = new ArrayList<>(recipeViewModel.recipes.getValue());
                recipeList.add(recipe);
                recipeViewModel.recipes.setValue(recipeList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RecipesActivity.this, R.string.error_parsing + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Saves the most recent search term entered by the user.
     * This allows the application to remember the user's last search when they return to the app.
     *
     * @param searchTerm The search term to save.
     */
    public void saveSearchTerm(String searchTerm) {
        // initialize SharedPreferences and editor, save searchTerm to "LastSearchTerm"
        SharedPreferences sharedPreferences = getSharedPreferences("RecipeSearchTerm", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LastSearchTerm", searchTerm);
        editor.apply();
    }

    /**
     * Retrieves the last search term entered by the user from shared preferences.
     *
     * @return The last search term entered by the user, or an empty string if none is found.
     */
    public String getLastSearchTerm() {
        // get SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("RecipeSearchTerm", MODE_PRIVATE);
        // 读取键名为"LastSearchTerm"的值，如果不存在则返回空字符串""
        return sharedPreferences.getString("LastSearchTerm", "");
    }
    /**
     * Initializes the RecyclerView used to display the list of recipes.
     * Sets up the adapter, layout manager, and any necessary decoration or item click listeners.
     */
    private void initializeRecyclerView() {
        myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                RecipeSearchBinding searchBinding = RecipeSearchBinding.inflate(getLayoutInflater(), parent, false);
                return new MyRowHolder(searchBinding.getRoot(), viewType);
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
     * ViewHolder class for recipe list items.
     * Holds views for the recipe ID, title, and image, and sets up the click listener for each item.
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

                fetchSecondQuery(recipe);
            });
        }
    }
    /**
     * Fetches additional details for a selected recipe by making a second API call using the recipe's ID.
     *
     * @param recipe The recipe for which to fetch additional details.
     */
    public void fetchSecondQuery(Recipe recipe) {
        requestQueue = Volley.newRequestQueue(this);
        int queryID = recipe.getRecipeID();

        String url = "https://api.spoonacular.com/recipes/" + queryID + "/information?apiKey=" + "abb4102db3714c6eb68729fbb6ad77fd";

        Log.d("RecipesActivity", "Fetching details for recipe ID: " + queryID + " with URL: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // The request is successful and the response data is parsed
                    Log.d("RecipesActivity", ": request is successful  " + response.toString());
                    parseSecondRecipesResponse(response, recipe);
                },
                error -> {
                    // Request failed, handling error
                    Log.e("RecipesActivity", "Request failed, handling error: " + error.toString());
                    Toast.makeText(RecipesActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });
        requestQueue.add(jsonObjectRequest);
    }
    /**
     * Parses the JSON response from the second API call to fetch additional recipe details.
     * Updates the UI with the detailed information for the selected recipe.
     *
     * @param response The JSONObject response received from the API.
     * @param recipe   The recipe object to update with additional details.
     */
    private void parseSecondRecipesResponse(JSONObject response, Recipe recipe) {

        try {
            Log.d("RecipesActivity", "Start parsing response data");

            //parsing request to get sourceURL and summary from api
            String sourceURL = response.getString("sourceUrl");
            String summary = response.getString("summary");


            int recipeID = recipe.getRecipeID();
            String imageURL = recipe.getImageURL();
            String title = recipe.getTitle();

            Log.d("RecipesActivity", "The analysis is completed and stored in the recipe.");
            //recipe object is for showing details and for user who wanted to save recipe
            Recipe detailRecipe = new Recipe(title, imageURL, summary, sourceURL, recipeID);

            showDetails(detailRecipe);
            Log.d("RecipesActivity", "parsing done  " + title);
        } catch (
                JSONException e) {
            e.printStackTrace();
            Toast.makeText(RecipesActivity.this, R.string.error_parsing+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Displays detailed information about a selected recipe.
     * Updates the UI with the recipe's details, including the summary and source URL.
     *
     * @param recipe The recipe to display.
     */
    public void showDetails(Recipe recipe) {
        Log.d("RecipesActivity", "show detail recipe " + recipe.getTitle());
        binding.inDeatilSuammry.setText(recipe.getSummary());
        binding.inDetailSourceURL.setText(recipe.getSourceURL());

        //if imageUrl is null, set a default image
        String imageUrl = recipe.getImageURL() != null ? recipe.getImageURL() : "";
        Glide.with(this)
                .load(!imageUrl.isEmpty() ? imageUrl : R.drawable.recipe)
                .into(binding.detailImage);
        //user can click save if they like this recipe
        binding.recipeSaveButton.setOnClickListener(v -> saveRecipe(recipe));
    }
    /**
     * Saves a recipe to the local database for later viewing.
     * This method runs asynchronously to avoid blocking the UI thread.
     *
     * @param recipe The recipe to save.
     */
    public void saveRecipe(Recipe recipe) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Log.d("RecipesActivity", "click save recipe");
                rDAO.insertRecipe(recipe);
                Snackbar.make(binding.getRoot(), R.string.recipe_saved, Snackbar.LENGTH_LONG).show();
                Log.d("RecipesActivity", "Recipe saved successfully.");
            } catch (Exception e) {
                Log.e("RecipesActivity", "Error saving recipe: " + e.getMessage());
            } finally {
                executor.shutdown();
            }
        });
    }
}