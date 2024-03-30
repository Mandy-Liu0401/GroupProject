/**
 * This page takes user input, allows to save a term to vocabulary
 *
 * @author Mengying Liu
 * @Student No. 041086143
 * @date Mar 25, 2024
 * @labSecNo. 021
 * @purpose It allows users to search terms and save terms
 *
 */
package algonquin.cst2335.groupproject.mengyingAPI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import kotlinx.coroutines.*;
import java.lang.Exception;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import algonquin.cst2335.groupproject.databinding.ActivityDictionaryApiBinding;
import algonquin.cst2335.groupproject.R;



import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Activity_DictionaryAPI extends AppCompatActivity {

    /**
     *  This tag is used to identify and cancel specific requests later
     */
    private static final String URL_TAG = "DICTIONARY_API";
    /**
     * this string variable stores teh first part of url
     */
    private static final String URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    /**
     * this variable stores terms
     */
    private TextView wordTextView;
    /**
     * this variable stores definitions
     */
    private TextView definitionTextView;

    /**
     * this variable will store concatenated definition string
     */
    private String definitionsResult;
    /**
     * stores request queue when using volley
     */
    private RequestQueue queue;

    /**
     * name of SharedPreferences file
     */
    private static final String PREFS_NAME = "Dictionary";
    /**
     * key name for userinput
     */
    private static final String USER_INPUT_KEY = "user_input";

    /**
     * key name for term saved in prefs file
     */
    private static final String WORD_KEY = "saved_term";

    /**
     * key name for definitions saved in prefs file
     */
    private static final String DEFINITION_KEY = "saved_definition";
    /**
     * binding used to connect java code with corresponding layout widget
     */
    ActivityDictionaryApiBinding binding ;

    /**
     * vocabulary object
     */
    Vocabulary vocabulary;

    /**
     * page start point
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDictionaryApiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Restore the user input from SharedPreferences
        String savedInput = prefs.getString(USER_INPUT_KEY, "");
        String savedDefinition = prefs.getString(DEFINITION_KEY, "");
        String savedWord = prefs.getString(WORD_KEY,"");

        binding.editText.setText(savedInput);
        binding.wordTextView.setText(savedWord);
        binding.definitionTextView.setText(savedDefinition);


        //initialize two textView and set initial values to null.
        wordTextView = binding.wordTextView;
        definitionTextView = binding.definitionTextView;

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

        //set up translate button listener
        binding.translateButton.setOnClickListener(v -> {

            // Get the text from EditText
            String userInput = binding.editText.getText().toString().trim();

            // Construct the complete URL by appending the user input
            String my_url = URL+userInput;
            fetchDictionary(my_url);
        });

        VocabularyDatabase db = Room.databaseBuilder(getApplicationContext(), VocabularyDatabase.class,
                "vocabulary_db").allowMainThreadQueries().build();

        // Set up save_button click listener
        binding.saveButton.setOnClickListener(click-> {
            String userInput = binding.editText.getText().toString().trim();
            String definitions = definitionsResult;

            vocabulary = new Vocabulary(userInput,definitions);

            // Check if the entry already exists in the database
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                Vocabulary existingVocabulary = db.vDAO().findTermByInput(userInput);
                if (existingVocabulary == null && definitions != null) {
                    // Entry does not exist, so insert the new vocabulary
                    db.vDAO().insertTerm(vocabulary);
                    // display a message indicating successful insertion
                    runOnUiThread(() -> {
                        Toast.makeText(Activity_DictionaryAPI.this, getString(R.string.save_successful), Toast.LENGTH_SHORT).show();
                    });
                }
                if (definitions == null){
                    // display a message indicating not found result
                    runOnUiThread(() -> {
                        Toast.makeText(Activity_DictionaryAPI.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    });

                } if (existingVocabulary != null ){
                    // Entry already exists, so display a message indicating duplication
                    runOnUiThread(() -> {
                        Toast.makeText(Activity_DictionaryAPI.this, getString(R.string.save_unsuccessful), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    /**
     * method to get search result from internet based on url
     * @param url url for each term
     */
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
                    definitionsResult = definitionsBuilder.toString();
                    definitionTextView.setText(definitionsResult);

                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    // Save the user input to SharedPreferences
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(USER_INPUT_KEY, word);
                    editor.putString(WORD_KEY,word);
                    editor.putString(DEFINITION_KEY,definitionsResult);
                    editor.apply();
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
        error -> {
            //show a toast warning if no search result
            Toast.makeText(Activity_DictionaryAPI.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
            wordTextView.setText("");
            definitionTextView.setText("");}
        );
        // Add TAG to request
        stringRequest.setTag(URL_TAG);

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * stop method when the app stops
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(URL_TAG);

        }
    }

    /**
     * Method to show AlertDialog with instructions
     */
    protected void showInstructionsAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.instruction_title))
            .setMessage(getString(R.string.instruction_body))
            .setNegativeButton(getString(R.string.dialog_button_ok), (dialog, which) -> {
                dialog.dismiss();
            }).show();
    }
}
