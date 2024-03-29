package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import algonquin.cst2335.groupproject.MainActivity;
import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivityRecipesBinding;
import algonquin.cst2335.groupproject.databinding.ActivitySavedRecipesBinding;
import algonquin.cst2335.groupproject.databinding.RecipeSavedItemBinding;
import algonquin.cst2335.groupproject.databinding.RecipeSearchBinding;

public class SavedRecipesActivity extends AppCompatActivity {
    private ActivitySavedRecipesBinding binding;
    private RecipeViewModel recipeViewModel;
    private RecyclerView.Adapter<SavedRecipesActivity.MyRowHolder> myAdapter;
    private RecipeDatabase db;
    private RecipeDAO rDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes);
        //initialize binding
        binding = ActivitySavedRecipesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolBar
        setSupportActionBar(binding.recipeToolbar);


        db = Room.databaseBuilder(getApplicationContext(), RecipeDatabase.class, "recipe-database").build();
        rDAO = db.cmDAO();

        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        recipeViewModel.recipes.observe(this, updatedRecipes -> {
            if (myAdapter != null) {
                myAdapter.notifyDataSetChanged(); // Notify adapter data has changed
            }
        });

        initializeRecyclerView();
        loadMessagesFromDatabase();


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.recipe_saved_icon) {
//            Intent intent2 = new Intent(this, RecipesActivity.class);
//            startActivity(intent2);
//            return true;
        } else if (item.getItemId() == R.id.home) {
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
            return true;
        } else {
            if (item.getItemId() == R.id.help) {
                Toast.makeText(this, "This is Version 1.0, Author: Wenxin Li", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return true;
    }


    private void loadMessagesFromDatabase() {
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            ArrayList<Recipe> savedRecipes = (ArrayList<Recipe>) rDAO.getAllRecipes();
            runOnUiThread(() -> recipeViewModel.recipes.setValue(savedRecipes));
        });
    }

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

//                ChatMessage message = chatModel.messages.getValue().get(position);
//                holder.messageText.setText(message.getMessage());y
//                holder.timeText.setText(message.getTimeSent());
            }

            @Override
            public int getItemCount() {
                return recipeViewModel.recipes.getValue() == null ? 0 : recipeViewModel.recipes.getValue().size();
            }
        };
        binding.RecipeRecycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.RecipeRecycleView.setAdapter(myAdapter);
    }

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
//                int clickedID = recipe.recipeID;

                showSavedRecipeDetails(recipe);


//                AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
//                builder.setMessage("Do you want to delete this message?")
//                        .setNegativeButton("NO", null)
//                        .setPositiveButton("YES", (dialog, cl) -> deleteMessage(position))
//                        .show();
            });

//            itemView.setOnClickListener(clk -> {
//                int position = getAbsoluteAdapterPosition();
//                Recipe recipe = recipeViewModel.recipes.getValue().get(position);
//                int clickedID = recipe.recipeID;
//
//                fetchSecondQuery(recipe);


//                AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
//                builder.setMessage("Do you want to delete this message?")
//                        .setNegativeButton("NO", null)
//                        .setPositiveButton("YES", (dialog, cl) -> deleteMessage(position))
//                        .show();
//            });
        }


    }

    public void showSavedRecipeDetails(Recipe recipe) {
        Log.d("SavedRecipesActivity", "show detail recipe " + recipe.getTitle());
        binding.inDeatilSuammry.setText(recipe.getSummary());
        binding.inDetailSourceURL.setText(recipe.getSourceURL());

        String imageUrl = recipe.getImageURL() != null ? recipe.getImageURL() : "";
        Glide.with(this).load(!imageUrl.isEmpty() ? imageUrl : R.drawable.recipe).into(binding.detailImage);


        binding.recipeRemoveButton.setOnClickListener(v -> removeRecipe(recipe));
    }


    public void removeRecipe(Recipe recipe) {
        Executor thread = Executors.newSingleThreadExecutor();

        thread.execute(() -> {

            Recipe recipeToDelete = recipe;
            rDAO.deleteMessage(recipeToDelete);
            ArrayList<Recipe> updatedRecipes = new ArrayList<>(recipeViewModel.recipes.getValue());
            updatedRecipes.remove(recipeToDelete);
            runOnUiThread(() -> {
                recipeViewModel.recipes.setValue(updatedRecipes);
                Snackbar.make(binding.getRoot(), "Recipe deleted", Snackbar.LENGTH_LONG).show();
            });
        });

    }
}