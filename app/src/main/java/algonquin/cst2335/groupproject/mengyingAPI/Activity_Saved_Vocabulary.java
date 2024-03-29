/**
 * This page will display all terms saved in local database,
 *
 * @author Mengying Liu
 * @Student No. 041086143
 * @date Mar 25, 2024
 * @labSecNo. 021
 * @purpose It allows users to view each term, delete each term, and empty all terms with the icon in the right top corner.
 *
 */
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.groupproject.MainActivity;
import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivitySavedVocabularyBinding;

public class Activity_Saved_Vocabulary extends AppCompatActivity {

    /**
     * binding variable linked with corresponding file
     */
    ActivitySavedVocabularyBinding binding;
    /**
     * recycle view object
     */
    private RecyclerView recycleView;
    /**
     * adapter object
     */
    private SavedAdapter myAdapter;
    /**
     * Dao object for manipulate local database
     */
    private VocabularyDAO vDao;
    /**
     * viewModel object for live data array terms
     */
    private DictionaryAPIViewModel dictionaryModel;
    /**
     * database variable represent vocabulary db
     */
    private VocabularyDatabase db;
    /**
     * array list terms used to hold each vocabulary object
     */
    private ArrayList<Vocabulary> terms;

    /**
     * app start point method, it initiates recycleView and viewModel
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     */
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


        // Check if ViewModel already has data
        if (dictionaryModel.getTerms().getValue() == null || dictionaryModel.getTerms().getValue().isEmpty()) {
            // Load data from database
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

    }

    /**
     * this method prompts an instruction when clicking help tab
     */
    protected void showInstructionsAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.instruction_title))
                .setMessage(getString(R.string.instruction_body))
                .setNegativeButton(getString(R.string.dialog_button_ok), (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }

    /**
     * this method is responsible for deleting a term from vocabulary
     * @param obj the Vocabulary object retrieved by position
     * @param position position of the term in array terms
     */
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

    /**
     * this method allows user to view each term's definitions.
     * @param obj
     */
    public void view_term(Vocabulary obj) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Saved_Vocabulary.this);
        builder.setMessage(obj.getDefinitions())
                .setTitle(obj.getTerm())
                .setNegativeButton(getString(R.string.dialog_button_ok), (dialog, cl) -> {
                })
                .create().show();
    }

    /**
     * this method flush all record in Vocabulary database
     */
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

    /**
     * this method exits this app and return to the main entrance
     */
    protected void exit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Saved_Vocabulary.this);
        builder.setMessage((getString(R.string.exit_message) ))
                .setTitle(getString(R.string.question_title))
                .setNegativeButton(getString(R.string.dialog_button_no), (dialog, cl) -> {
                })
                .setPositiveButton(getString(R.string.dialog_button_yes), (dialog, cl) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                })
                .create().show();
    }
        private class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.ViewHolder> {
            private ArrayList<Vocabulary> terms = new ArrayList<>();

            /**
             * This is responsible for creating a layout for a row, and setting the TextViews in code.
             * @param parent   The ViewGroup into which the new View will be added after it is bound to
             *                 an adapter position.
             * @param viewType The view type of the new View.
             * @return ViewHolder
             */
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // For each record, inflate vocabulary layout
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vocabulary, parent, false);
                return new ViewHolder(itemView);
            }

            /**
             * where you set the objects in your layout for the row.
             * @param holder   The ViewHolder which should be updated to represent the contents of the
             *                 item at the given position in the data set.
             * @param position The position of the item within the adapter's data set.
             */
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                Vocabulary obj = terms.get(position);

                holder.termText.setText(obj.getTerm());
                holder.definitionText.setText(obj.getDefinitions());

                holder.view_icon.setOnClickListener(click -> view_term(obj));
            }

            /**
             * count the number of element in the array terms
             * @return int
             */
            @Override
            public int getItemCount() {
                return terms.size();
            }

            /**
             * notifies the adapter  whenever there is an update
             * @param terms
             */
            public void setTerms(ArrayList<Vocabulary> terms) {
                this.terms = terms;
                notifyDataSetChanged();
            }
            private class ViewHolder extends RecyclerView.ViewHolder {
                TextView termText;
                TextView definitionText;
                ImageView view_icon;

                /**
                 * constructor for creating a viewHolder
                 * @param itemView
                 */
                public ViewHolder(@NonNull View itemView) {
                    super(itemView);

                    termText = itemView.findViewById(R.id.termText);
                    definitionText = itemView.findViewById(R.id.definitionText);
                    view_icon = itemView.findViewById(R.id.view_icon);

                    itemView.setOnClickListener(click-> {
                     int position = getAbsoluteAdapterPosition();
                     delete_term(terms.get(position),position);
                    });
                }
            }
        }

    /**
     * responsible for loading teh specific menu
     * @param menu The options menu in which you place your items.
     *
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.dictionary_menu, menu);
        return true;
    }

    /**
     * implementation for each menu item
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.exit_icon) {
            exit();
        }
        else if (id == R.id.empty_icon){
            emptyAll();
        }
        return super.onOptionsItemSelected(item);
    }
}