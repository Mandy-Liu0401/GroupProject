package algonquin.cst2335.groupproject.mengyingAPI;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import algonquin.cst2335.groupproject.R;
import algonquin.cst2335.groupproject.databinding.ActivityHelpBinding;


public class Activity_Help extends AppCompatActivity {
    ActivityHelpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.include1.myToolbar);

        BottomNavigationView bottomNavigationView = binding.include2.bottomNavigation;

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.third_id);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int item_id = item.getItemId();
            if (item_id == R.id.third_id) {
                return true;
            } else if (item_id == R.id.home_id) {
                startActivity(new Intent(getApplicationContext(), Activity_DictionaryAPI.class));
                return true;
            } else if (item_id == R.id.second_id) {
                startActivity(new Intent(getApplicationContext(), Activity_Saved_Vocabulary.class));
                return true;
            }
            return false;
        });


    }
}