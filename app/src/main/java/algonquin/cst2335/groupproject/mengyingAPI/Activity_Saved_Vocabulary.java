package algonquin.cst2335.groupproject.mengyingAPI;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivitySavedVocabularyBinding;

public class Activity_Saved_Vocabulary extends AppCompatActivity {
    ActivitySavedVocabularyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedVocabularyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.include1.myToolbar);

        BottomNavigationView bottomNavigationView = binding.include2.bottomNavigation;
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.second_id);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int item_id = item.getItemId();
            if ( item_id == R.id.second_id) {
                return true;
            } else if ( item_id == R.id.home_id ) {
                startActivity(new Intent(getApplicationContext(), Activity_DictionaryAPI.class));
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
}
