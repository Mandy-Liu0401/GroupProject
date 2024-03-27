/**
 * Author: Zimeng Wang, 041095956
 * Date: Mar 26, 2024
 * Lab Section: CST2335 - 021
 * Purpose:
 */

package algonquin.cst2335.groupproject.zimeng;

import android.app.Activity;
import android.content.Context;
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
        builder.setTitle(R.string.sunmenu_about);
        builder.setMessage(R.string.sunmenu_about_message);
        builder.setPositiveButton(R.string.sunmenu_message_ok_button, (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}