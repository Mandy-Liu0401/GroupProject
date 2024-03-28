package algonquin.cst2335.groupproject.mengyingAPI;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.room.Room;

import com.android.volley.RequestQueue;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivitySavedVocabularyBinding;



public class Activity_Saved_Vocabulary extends AppCompatActivity {
    ActivitySavedVocabularyBinding binding;
    private RecyclerView recycleView;
    private SavedAdapter myAdapter;
    private VocabularyDAO vDao;
    private DictionaryAPIViewModel dictionaryModel;
    private VocabularyDatabase db;

    private ArrayList<Vocabulary> terms;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedVocabularyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.myToolbar2);

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


        // Initialize ViewModel
        dictionaryModel = new ViewModelProvider(this).get(DictionaryAPIViewModel.class);



        if (terms == null) {
            terms = new ArrayList<>();

            // Initialize database and DAO
            db = Room.databaseBuilder(getApplicationContext(), VocabularyDatabase.class,
                    "vocabulary_db").allowMainThreadQueries().build();
            vDao = db.vDAO();

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                terms.addAll(vDao.getAllTerms()); // Fetch data from the database
                // Update LiveData with the fetched data
                dictionaryModel.getTerms().postValue(terms);
            });
        }

        // Observe LiveData to update the adapter's data source when data changes
        dictionaryModel.getTerms().observe(this, terms -> {
            // Update the data source of the adapter
            myAdapter.setTerms(terms);
        });

        //initialize RecyclerView after rotating
        recycleView = findViewById(R.id.saved_recycleView);

        myAdapter = new SavedAdapter();
        recycleView.setAdapter(myAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(this));

    }

    protected void showInstructionsAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.instruction_title))
                .setMessage(getString(R.string.instruction_body))
                .setNegativeButton(getString(R.string.dialog_button_ok), (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }
    public void delete_term(Vocabulary obj, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Saved_Vocabulary.this);
        builder.setMessage((getString(R.string.question_body) + obj.getTerm()))
                .setTitle(getString(R.string.question_title))
                .setNegativeButton(getString(R.string.dialog_button_no), (dialog, cl) -> {
                })
                .setPositiveButton(getString(R.string.dialog_button_yes), (dialog, cl) -> {

                    // Remove the term from the database
                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(() -> {
                        vDao.deleteTerm(obj);
                    });

                    terms.remove(position);
                    myAdapter.notifyItemRemoved(position);

                    Snackbar.make(recycleView, getString(R.string.snackbar_message) + obj.getTerm(),
                            Snackbar.LENGTH_LONG).setAction(getString(R.string.undo_button), click -> {
                        terms.add(position, obj);
                        vDao.insertTerm(obj);
                        myAdapter.notifyItemInserted(position);
                    }).show();
                })
                .create().show();
    }

    public void view_term(Vocabulary obj) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Saved_Vocabulary.this);
        builder.setMessage(obj.getDefinitions())
                .setTitle(obj.getTerm())
                .setNegativeButton(getString(R.string.dialog_button_ok), (dialog, cl) -> {
                })
                .create().show();
    }

    protected void emptyAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Saved_Vocabulary.this);
        builder.setMessage(getString(R.string.delete_all_body))
                .setTitle(getString(R.string.delete_all_title))
                .setNegativeButton(getString(R.string.dialog_button_no), (dialog, which) -> {
                })
                .setPositiveButton(getString(R.string.dialog_button_yes), (dialog, which) -> {
                    // Clear the ArrayList and notify the adapter
                    terms.clear();
                    myAdapter.notifyDataSetChanged();

                    // Delete all terms from the database
                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(() -> {
                        vDao.deleteAllTerms();
                    });
                    Toast.makeText(this, getString(R.string.delete_all_after), Toast.LENGTH_LONG).show();
                })
                .create().show();

    }
        private class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.ViewHolder> {

            private ArrayList<Vocabulary> terms = new ArrayList<>();

            @NonNull
            @Override
            //This is responsible for creating a layout for a row, and setting the TextViews in code.
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // For each record, inflate vocabulary layout
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vocabulary, parent, false);
                return new ViewHolder(itemView);
            }

            @Override
            //where you set the objects in your layout for the row.
            //set the data for your ViewHolder object that will go at row position in the list
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


                Vocabulary obj = terms.get(position);

                holder.termText.setText(obj.getTerm());
                holder.definitionText.setText(obj.getDefinitions());

//                //holder.itemView.setOnClickListener(clic
//                holder.delete_icon.setOnClickListener(click -> delete_term(obj, position));
//                holder.review_icon.setOnClickListener(click -> view_term(obj));
//                holder.empty_icon.setOnClickListener(click -> emptyAll());


            }

            @Override
            public int getItemCount() {
                return terms.size();
            }

            public void setTerms(ArrayList<Vocabulary> terms) {
                this.terms = terms;
                notifyDataSetChanged();
            }
            private class ViewHolder extends RecyclerView.ViewHolder {
                TextView termText;
                TextView definitionText;
                ImageView delete_icon;
                ImageView review_icon;
                ImageView empty_icon;

                public ViewHolder(@NonNull View itemView) {
                    super(itemView);

                    termText = itemView.findViewById(R.id.termText);
                    definitionText = itemView.findViewById(R.id.definitionText);

                    itemView.setOnClickListener(click-> {
                     int position = getAbsoluteAdapterPosition();
                     view_term(terms.get(position));
                    });
                }
            }
        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.dictionary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.view) {

        }

        else if (id == R.id.delete) {

        }
        else if (id == R.id.empty){
            emptyAll();
        }
        return super.onOptionsItemSelected(item);
    }
}