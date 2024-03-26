package algonquin.cst2335.groupproject.mengyingAPI;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.room.Room;

import com.android.volley.RequestQueue;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivitySavedVocabularyBinding;
import algonquin.cst2335.groupproject.databinding.VocabularyBinding;


public class Activity_Saved_Vocabulary extends AppCompatActivity {
    ActivitySavedVocabularyBinding binding;
    private RequestQueue queue;

    private RecyclerView.Adapter myAdapter;
    private VocabularyDAO vDao;
    DictionaryAPIViewModel dictionaryModel;
    ArrayList<Vocabulary> terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedVocabularyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigationView = binding.include2.bottomNavigation;
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.second_id);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int item_id = item.getItemId();
            if (item_id == R.id.second_id) {
                return true;
            } else if (item_id == R.id.home_id) {
                startActivity(new Intent(getApplicationContext(), Activity_DictionaryAPI.class));
                return true;
            } else if (item_id == R.id.third_id) {
                // Display AlertDialog with instructions
                showInstructionsAlertDialog();
                return false;
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

        if (terms == null) {
            dictionaryModel.terms.setValue(terms = new ArrayList<>());
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                terms.addAll(vDao.getAllTerms()); //Once you get the data from database

                runOnUiThread(() -> binding.savedRecycleView.setAdapter(myAdapter)); //You can then load the RecyclerView
            });
        }

        //initialize RecyclerView after rotating
        binding.savedRecycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.savedRecycleView.setAdapter(myAdapter =new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
                if (viewType ==1) {
                    // For returned search result, inflate the search_result layout
                    VocabularyBinding binding = VocabularyBinding.inflate(getLayoutInflater(), parent, false);
                    return new MyRowHolder(binding.getRoot());
                } return new MyRowHolder(binding.getRoot());
            }
            @Override
            //where you set the objects in your layout for the row.
            //set the data for your ViewHolder object that will go at row position in the list
            public void onBindViewHolder (@NonNull MyRowHolder holder,int position){
            Vocabulary obj = terms.get(position);
            holder.termText.setText(obj.getTerm());
            holder.definitionText.setText(obj.getDefinitions());
            }

            @Override
            public int getItemCount () {
            return terms.size();
            }

            @Override
            public int getItemViewType(int position){
                return 1;
            }
        });
    }
    protected void showInstructionsAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.instruction_title))
                .setMessage(getString(R.string.instruction_body))
                .setNegativeButton(getString(R.string.dialog_button_ok), (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView termText;
        TextView definitionText;
        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            termText = itemView.findViewById(R.id.termText);
            definitionText = itemView.findViewById(R.id.definitionText);

            itemView.setOnClickListener(clk ->{
                int position = getAbsoluteAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder( Activity_Saved_Vocabulary.this );
                builder.setMessage((getString(R.string.question_body) + termText.getText()))
                    .setTitle(getString(R.string.question_title))
                    .setNegativeButton(getString(R.string.dialog_button_no), (dialog, cl) -> {})
                    .setPositiveButton(getString(R.string.dialog_button_yes), (dialog, cl) -> {

                        Vocabulary removedTerm = terms.get(position);
                        terms.remove(position); // Remove the term from the ArrayList
                        myAdapter.notifyItemRemoved(position);// Notify the adapter of the data change

                        // Remove the term from the database
                        Executor thread = Executors.newSingleThreadExecutor();
                        thread.execute(() -> {
                            vDao.deleteTerm(removedTerm);
                        });

                        Snackbar.make(termText, getString(R.string.snackbar_message) + termText,
                                        Snackbar.LENGTH_LONG).setAction(getString(R.string.undo_button), click ->{
                                    terms.add(position, removedTerm);
                                    myAdapter.notifyItemInserted(position);
                                })
                                .show();
                    })
                    .create().show();
            });

        }
    }
}