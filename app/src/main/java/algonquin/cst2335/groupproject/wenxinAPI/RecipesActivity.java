package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.annotation.NonNull;
import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivityRecipesBinding;
import algonquin.cst2335.groupproject.databinding.RecipeSearchBinding;

public class RecipesActivity extends AppCompatActivity {

    private ActivityRecipesBinding binding;
    private RecipeViewModel recipeViewModel;
    private RecyclerView.Adapter<MyRowHolder> myAdapter;
    private RecipeDatabase db;
    private RecipeDAO rDAO;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize binding
        binding = ActivityRecipesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


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
        initializeSendReceiveButtons();


    }

    private void loadMessagesFromDatabase() {
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            ArrayList<Recipe> loadedMessages = (ArrayList<Recipe>) rDAO.getAllRecipes();
            runOnUiThread(() -> recipeViewModel.recipes.setValue(loadedMessages));
        });
    }

    private void initializeSendReceiveButtons() {
        binding.searchRecipeButton.setOnClickListener(v -> searchRecipe(true));
        //binding.buttonReceive.setOnClickListener(v -> sendMessage(false));
    }

    private void searchRecipe(boolean isSearch){
        requestQueue = Volley.newRequestQueue(this);
        String query = binding.enterRecipe.getText().toString().trim();

        String url = "https://api.spoonacular.com/recipes/complexSearch?query=" + query + "&apiKey=" + "c4b0e4aef58e44d6b73030321fae8094";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // if success then parse
                    parseRecipesResponse(response);
                },
                error -> {
                    // failed toast
                    Toast.makeText(RecipesActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });

        // add to queue
        requestQueue.add(jsonObjectRequest);

    }

    private void parseRecipesResponse(JSONObject response) {
        //List<ChatMessage> recipesResult = new ArrayList<>();
        try {
            JSONArray recipesArray = response.getJSONArray("results");
            for (int i = 0; i < recipesArray.length(); i++) {
                JSONObject recipeObject = recipesArray.getJSONObject(i);

                int recipeID =recipeObject.getInt("id");
                String title = recipeObject.getString("title");
                String imageURL = recipeObject.optString("image", "");

                Recipe recipe = new Recipe(recipeID,title,imageURL);

                //set to viewModel
                ArrayList<Recipe> recipeList = new ArrayList<>(recipeViewModel.recipes.getValue());
                recipeList.add(recipe);
                recipeViewModel.recipes.setValue(recipeList);

                //ChatMessage recipe = new ChatMessage(title, summary,y);

//                ArrayList<ChatMessage> update = new ArrayList<>(chatModel.messages.getValue());
//                update.add(recipe);
//                chatModel.messages.setValue(update);

                // recipesResult.add(recipe);



            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RecipesActivity.this, "Error parsing recipes: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void initializeRecyclerView() {
        myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder( ViewGroup parent, int viewType) {

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

//                ChatMessage message = chatModel.messages.getValue().get(position);
//                holder.messageText.setText(message.getMessage());
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

//            itemView.setOnClickListener(clk -> {
//                int position = getAbsoluteAdapterPosition();
//                AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
//                builder.setMessage("Do you want to delete this message?")
//                        .setNegativeButton("NO", null)
//                        .setPositiveButton("YES", (dialog, cl) -> deleteMessage(position))
//                        .show();
//            });
        }

    }
}