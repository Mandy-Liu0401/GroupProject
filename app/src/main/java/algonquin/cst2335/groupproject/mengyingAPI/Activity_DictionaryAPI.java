package algonquin.cst2335.groupproject.mengyingAPI;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivityDictionaryApiBinding;
import algonquin.cst2335.groupproject.databinding.SearchResultBinding;


import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Activity_DictionaryAPI extends AppCompatActivity {
    private RecyclerView.Adapter myAdapter;
    private VocabularyDAO vDao;
    ActivityDictionaryApiBinding binding ;
    ArrayList<Vocabulary> terms;
    DictionaryAPIViewModel dictionaryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDictionaryApiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.include1.myToolbar);

        BottomNavigationView bottomNavigationView = binding.include2.bottomNavigation;
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.home_id);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int item_id = item.getItemId();
            if ( item_id == R.id.home_id ) {
                return true;
            } else if ( item_id == R.id.second_id ) {
                startActivity(new Intent(getApplicationContext(), Activity_Saved_Vocabulary.class));
                return true;
            } else if ( item_id == R.id.third_id ) {
                startActivity(new Intent(getApplicationContext(), Activity_Help.class));
                return true;
            }
            return false;
        });

        VocabularyDatabase db = Room.databaseBuilder(getApplicationContext(), VocabularyDatabase.class,
                "database-name").build();
        vDao = db.vDAO();

        //the ViewModel will keep a version of your ArrayList of data
        // so that it survives orientation changes of your app
        dictionaryModel = new ViewModelProvider(this).get(DictionaryAPIViewModel.class);
        terms = dictionaryModel.terms.getValue();

        if(terms == null) {
            dictionaryModel.terms.setValue( terms = new ArrayList<>());
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                terms.addAll( vDao.getAllTerms() ); //Once you get the data from database

                runOnUiThread( () ->  binding.currentRecycleView.setAdapter( myAdapter )); //You can then load the RecyclerView
            });
        }

        //initialize RecyclerView after rotating
        binding.currentRecycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.currentRecycleView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == 0){
                    // For returned search result, inflate the search_result layout
                    SearchResultBinding binding = SearchResultBinding.inflate(getLayoutInflater(),parent,false);


                else {
                    // For not found search result, raise a snackbar warning
                    //NotFoundBinding binding = NotFoundBinding.inflate(getLayoutInflater(),parent,false);

                }return new MyRowHolder(binding.getRoot());}
            }

            @Override
            //where you set the objects in your layout for the row.
            //set the data for your ViewHolder object that will go at row position in the list
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                Vocabulary obj = terms.get(position);
                holder.messageText.setText(obj.getMessage());
                holder.timeText.setText(obj.getTimeSent());
            }

            @Override
            public int getItemCount() {
                return terms.size();
            }

            @Override
            public int getItemViewType(int position){
                Vocabulary term = terms.get(position);
                return term.isFound() ? 0 : 1;
            }

        });

        binding.translateButton.setOnClickListener(click -> sendMessage(true));
        binding.receiveButton.setOnClickListener(click -> sendMessage(false));
    }
        }
    }



}
