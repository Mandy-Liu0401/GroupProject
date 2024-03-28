package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.annotation.NonNull;

import android.app.Activity;

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
        //loadMessagesFromDatabase();
        initializeSendReceiveButtons();


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.recipe_saved_icon){
//            Intent intent2 = new Intent(this, RecipesActivity.class);
//            startActivity(intent2);
//            return true;



        }else{
            if (item.getItemId() == R.id.item_2) {

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

//    private void loadMessagesFromDatabase() {
//        Executor thread = Executors.newSingleThreadExecutor();
//        thread.execute(() -> {
//            ArrayList<Recipe> loadedMessages = (ArrayList<Recipe>) rDAO.getAllRecipes();
//            runOnUiThread(() -> recipeViewModel.recipes.setValue(loadedMessages));
//        });
//    }

    private void initializeSendReceiveButtons() {
        binding.searchRecipeButton.setOnClickListener(v -> searchRecipe(true));

    }


    private void searchRecipe(boolean isSearch) {
        //clean view for next search
        recipeViewModel.recipes.setValue(new ArrayList<>());
        requestQueue = Volley.newRequestQueue(this);
        String query = binding.enterRecipe.getText().toString().trim();

        String url = "https://api.spoonacular.com/recipes/complexSearch?query=" + query + "&apiKey=" + "abb4102db3714c6eb68729fbb6ad77fd";

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

                int recipeID = recipeObject.getInt("id");
                String title = recipeObject.getString("title");
                String imageURL = recipeObject.optString("image", "");

                Recipe recipe = new Recipe(title, imageURL, null, null, recipeID);

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
                int clickedID = recipe.recipeID;

                fetchSecondQuery(clickedID);


//                AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
//                builder.setMessage("Do you want to delete this message?")
//                        .setNegativeButton("NO", null)
//                        .setPositiveButton("YES", (dialog, cl) -> deleteMessage(position))
//                        .show();
            });
        }

    }

    public void fetchSecondQuery(int recipeID) {
        requestQueue = Volley.newRequestQueue(this);
        int queryID = recipeID;

        String url = "https://api.spoonacular.com/recipes/" + queryID + "/information?apiKey=" + "abb4102db3714c6eb68729fbb6ad77fd";

        Log.d("RecipesActivity", "Fetching details for recipe ID: " + queryID + " with URL: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // 请求成功，解析响应数据
                    parseSecondRecipesResponse(response);
                },
                error -> {
                    // 请求失败，处理错误
                    Toast.makeText(RecipesActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });

        // 将请求添加到请求队列
        requestQueue.add(jsonObjectRequest);


    }

    private void parseSecondRecipesResponse(JSONObject response) {

        try {
            JSONArray recipesArray = response.getJSONArray("results");
            for (int i = 0; i < recipesArray.length(); i++) {
                JSONObject recipeObject = recipesArray.getJSONObject(i);

                //we get all information this time
                int recipeID = recipeObject.getInt("id");
                String imageURL = recipeObject.getString("image");
                String sourceURL = recipeObject.getString("sourceURL");
                String title = recipeObject.getString("title");
                String summary = recipeObject.optString("image", "");

                Recipe recipe = new Recipe(title, imageURL, summary, sourceURL, recipeID);

                showDetail(recipe);


            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RecipesActivity.this, "Error parsing recipes: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void showDetail(Recipe recipe) {
        binding.inDeatilSuammry.setText(recipe.getSummary());
        binding.inDetailSourceURL.setText(recipe.getSourceURL());

        String imageUrl = recipe.getImageURL() != null ? recipe.getImageURL() : "";
        Glide.with(this)
                .load(!imageUrl.isEmpty() ? imageUrl : R.drawable.recipe)
                .into(binding.detailImage);

        binding.recipeSaveButton.setOnClickListener(v -> saveRecipe(recipe));


    }

    public void saveRecipe(Recipe recipe) {
        rDAO.insertRecipe(recipe);


    }


}