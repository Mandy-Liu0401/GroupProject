/**
 * Handles menu actions within the application's sun-themed activities.
 *
 * @author Zimeng Wang
 * @date Mar 26, 2024
 * @labSection CST2335 - 021
 * @purpose Provide a centralized mechanism for handling menu-related actions across sun-themed screens of the application.
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

    /**
     * Context used for accessing resources, inflating menus, and starting activities.
     */
    private Context context;

    /**
     * Constructor that initializes the SunMenuHandler with the given context.
     *
     * @param context The context used for menu actions.
     */
    public SunMenuHandler(Context context) {
        this.context = context;
    }

    /**
     * Inflates the menu with sun-themed options.
     *
     * @param menu The menu in which items are placed.
     * @return true to display the menu; false to not display the menu.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = ((Activity) context).getMenuInflater();
        inflater.inflate(R.menu.sun_menu, menu);
        return true;
    }

    /**
     * Handles the selection of menu items, performing actions like navigation or displaying dialogs.
     *
     * @param item The menu item that was selected.
     * @return true if the event was handled; false otherwise.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        } else if (id == R.id.menu_about) {
            showAboutDialog();
            return true;
        }
        return false;
    }

    /**
     * Displays an "About" dialog with information about the sun-themed features of the application.
     */
    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.sunmenu_about);
        builder.setMessage(R.string.sunmenu_about_message);
        builder.setPositiveButton(R.string.sunmenu_message_ok_button, (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
