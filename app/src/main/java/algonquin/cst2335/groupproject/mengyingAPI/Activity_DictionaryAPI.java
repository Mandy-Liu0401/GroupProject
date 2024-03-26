package algonquin.cst2335.groupproject.mengyingAPI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import algonquin.cst2335.groupproject.databinding.ActivityDictionaryApiBinding;
import algonquin.cst2335.groupproject.R;



import java.util.ArrayList;

public class Activity_DictionaryAPI extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private static final String URL_TAG = "DICTIONARY_API";
    private static final String URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    private TextView wordTextView;
    private TextView definitionTextView;
    private RequestQueue queue;

    // Define a key for the SharedPreferences
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String USER_INPUT_KEY = "user_input";

    private RecyclerView.Adapter myAdapter;
    private VocabularyDAO vDao;
    ActivityDictionaryApiBinding binding ;
    ArrayList<Vocabulary> terms;
    DictionaryAPIViewModel dictionaryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDictionaryApiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Restore the user input from SharedPreferences
        String savedInput = prefs.getString(USER_INPUT_KEY, "");
        binding.editText.setText(savedInput);

        //initialize two textView and set initial values to null.
        wordTextView = binding.wordTextView;
        definitionTextView = binding.definitionTextView;
        wordTextView.setText("");
        definitionTextView.setText("");

        BottomNavigationView bottomNavigationView = binding.include2.bottomNavigation;
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.home_id);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int item_id = item.getItemId();
            if ( item_id == R.id.home_id ) {
                return true;
            } else if ( item_id == R.id.second_id ) {
                startActivity(new Intent(getApplicationContext(), Activity_Saved_Vocabulary.class));
                return true;
            } else if ( item_id == R.id.third_id ) {
                // Display AlertDialog with instructions
                showInstructionsAlertDialog();
                return false;
            }
            return false;
        });

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        binding.translateButton.setOnClickListener(v -> {
            // Get the text from EditText
            String userInput = binding.editText.getText().toString().trim();

            // Save the user input to SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(USER_INPUT_KEY, userInput);
            editor.apply();

            // Construct the complete URL by appending the user input
            String my_url = URL+userInput;
            fetchDictionary(my_url);
        });

        String savedUserInput = prefs.getString("LoginName", "");
    }

    private void fetchDictionary(String url) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                    // Display "word" and "definitions" of the response string.
            try {
                JSONArray jsonArray = new JSONArray(response);

                // only 1 object
                JSONObject entry = jsonArray.getJSONObject(0);
                String word = entry.getString("word");

                JSONArray meaningsArray = entry.getJSONArray("meanings");
                StringBuilder definitionsBuilder = new StringBuilder();

                int definitionCount = 0;
                int maxDefinitionTolist = 5;

                for (int j = 0; j < meaningsArray.length(); j++) {
                    JSONObject meaning = meaningsArray.getJSONObject(j);
                    String partOfSpeech = meaning.getString("partOfSpeech");
                    JSONArray definitionsArray = meaning.getJSONArray("definitions");

                    for (int k = 0; k < definitionsArray.length(); k++) {
                        JSONObject definitionObject = definitionsArray.getJSONObject(k);
                        String definition = definitionObject.getString("definition");
                        definitionsBuilder.append(partOfSpeech).append(": ").append(definition).append("\n\n");
                        definitionCount++;
                        if (definitionCount >= maxDefinitionTolist){break;}
                    }
                    if (definitionCount >= maxDefinitionTolist){break;}
                }

                runOnUiThread(() -> {
                    wordTextView.setText(word);
                    definitionTextView.setText(definitionsBuilder.toString());
                });
            } catch (JSONException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), R.string.error_message, Snackbar.LENGTH_LONG).show();
                wordTextView.setText("");
                definitionTextView.setText("");
            }
        },
        error -> {Snackbar.make(findViewById(android.R.id.content), R.string.error_message, Snackbar.LENGTH_LONG).show();
            wordTextView.setText("");
            definitionTextView.setText("");}
        );
        // Add TAG to request
        stringRequest.setTag(URL_TAG);

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(URL_TAG);
            //queue.stop();
        }
    }

    // Method to show AlertDialog with instructions
    protected void showInstructionsAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.instruction_title))
                .setMessage(getString(R.string.instruction_body))
                .setNegativeButton(getString(R.string.dialog_button_ok), (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }
}
