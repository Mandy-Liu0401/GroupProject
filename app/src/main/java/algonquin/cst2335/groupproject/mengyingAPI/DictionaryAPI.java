package algonquin.cst2335.groupproject.mengyingAPI;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import algonquin.cst2335.groupproject.databinding.ActivityDictionaryApiBinding;
import algonquin.cst2335.groupproject.databinding.NotFoundBinding;
import algonquin.cst2335.groupproject.databinding.SearchResultBinding;


import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DictionaryAPI extends AppCompatActivity {
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
        setSupportActionBar(binding.myToolbar);

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

                runOnUiThread( () ->  binding.recycleView.setAdapter( myAdapter )); //You can then load the RecyclerView
            });
        }

        //initialize RecyclerView after rotating
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.recycleView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == 0){
                    // For returned search result, inflate the search_result layout
                    SearchResultBinding binding = SearchResultBinding.inflate(getLayoutInflater(),parent,false);
                    return new MyRowHolder(binding.getRoot());}

                else {
                    // For not found search result, inflate the snot found layout
                    NotFoundBinding binding = NotFoundBinding.inflate(getLayoutInflater(),parent,false);
                    return new MyRowHolder(binding.getRoot());}
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
