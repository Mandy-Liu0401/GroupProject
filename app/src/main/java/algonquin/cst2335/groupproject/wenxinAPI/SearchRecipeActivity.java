package algonquin.cst2335.groupproject.wenxinAPI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivityDetailRecipeBinding;

public class SearchRecipeActivity extends AppCompatActivity {

    private ActivityDetailRecipeBinding binding;
    private RecipeViewModel recipeViewModel;
    private RecyclerView.Adapter myAdapter;

    private DetailedRecipeDatabase db;
    private DetailedRecipeDAO drDAO;

    protected RequestQueue queue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipe);

        binding = ActivityDetailRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);


    }

    private void initializeRecyclerView() {
        myAdapter = new RecyclerView.Adapter<MyRowHolder>() {

            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        };
    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        private final TextView recipeTitle;
        private final ImageView Image;
        private final TextView recipeID;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            recipeTitle = itemView.findViewById(R.id.recipeTitle);
            Image = itemView.findViewById(R.id.recipeImage);
            recipeID = itemView.findViewById(R.id.recipeID);

            //select one from the list, will go to detail activity
            itemView.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();

                //not sure itemview id?
                int selectedRecipeID = itemView.getId();

                showDetailedRecipe(selectedRecipeID);


            });

        }

        private void showDetailedRecipe(int recipeID) {
            Intent newIntent = new Intent(SearchRecipeActivity.this, DetailedRecipeActivity.class);
            newIntent.putExtra("see_recipe", recipeID);
            startActivity(newIntent);
        }


    }
}
