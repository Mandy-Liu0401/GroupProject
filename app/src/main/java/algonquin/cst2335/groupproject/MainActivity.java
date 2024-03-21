package algonquin.cst2335.groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import algonquin.cst2335.groupproject.databinding.ActivityMainBinding;
import algonquin.cst2335.groupproject.mengyingAPI.DictionaryAPI;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());//load layout xml on main screen

        setSupportActionBar(binding.myToolbar);//tool bar for 4 entrances
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_1) {

        }

        else if (id == R.id.item_2) {

        }

        else if (id == R.id.item_3) {

            Intent intent = new Intent(this, DictionaryAPI.class);
            startActivity(intent);
            return true;
        }

        else if (id == R.id.item_4) {

        }
        return super.onOptionsItemSelected(item);
    }
}