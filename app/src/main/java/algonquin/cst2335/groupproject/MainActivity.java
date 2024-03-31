package algonquin.cst2335.groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import algonquin.cst2335.groupproject.YashanAPI.DeezerSongSearchActivity;
import algonquin.cst2335.groupproject.YashanAPI.DeezerSongSearchActivity;
import algonquin.cst2335.groupproject.wenxinAPI.RecipesActivity;
import algonquin.cst2335.groupproject.zimeng.SunHome;
import algonquin.cst2335.groupproject.mengyingAPI.Activity_DictionaryAPI;


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
            Intent intent = new Intent(MainActivity.this, SunHome.class);
            startActivity(intent);
        }

        else if (id == R.id.item_2) {
            Intent intentRecipe =new Intent(this, RecipesActivity.class);
            startActivity(intentRecipe);

        }

        else if (id == R.id.item_3) {
            Intent intent = new Intent(this, Activity_DictionaryAPI.class);
            startActivity(intent);

        }

        else if (id == R.id.item_4) {
            Intent intent = new Intent(this, DeezerSongSearchActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}