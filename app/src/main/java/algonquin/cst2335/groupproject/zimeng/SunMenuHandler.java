package algonquin.cst2335.groupproject.zimeng;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.app.AlertDialog;
import algonquin.cst2335.groupproject.MainActivity;
import algonquin.cst2335.groupproject.R;

public class SunMenuHandler {
    private Context context;

    public SunMenuHandler(Context context) {
        this.context = context;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = ((Activity) context).getMenuInflater();
        inflater.inflate(R.menu.sun_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_home) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }  else if (id == R.id.menu_about) {
            showAboutDialog();
            return true;
        }
        return false;
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("About");
        builder.setMessage("Instructions for using the interface:\n\n" +
            "1. Enter the latitude and longitude of the desired location.\n" +
            "2. Click the Search button to retrieve the sunrise and sunset times.\n" +
            "3. Click the Add button to save the location to your favourites.\n" +
            "4. Use the menu options to navigate between screens.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}