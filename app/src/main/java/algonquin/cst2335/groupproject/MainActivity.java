package algonquin.cst2335.groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import algonquin.cst2335.groupproject.databinding.ActivityMainBinding;
import algonquin.cst2335.groupproject.mengyingAPI.DictionaryAPI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
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