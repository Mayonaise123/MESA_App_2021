package com.example.mesaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;

/*
 * User inputs their caloric intake
 */
public class caloriesPage extends AppCompatActivity {
    // Global Variables
    private int currentApiVersion;
    EditText sugarText;
    EditText carbText;
    EditText fatText;
    EditText proteinText;
    Person person;

    /*
     * Generates the different macro calorie and sugar fields
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories_page);

        // Gets the data from the main menu
        Bundle data = getIntent().getExtras();
        person = (Person) data.getParcelable("person");

        sugarText = (EditText) findViewById(R.id.sugar);
        carbText = (EditText) findViewById(R.id.carbs);
        fatText = (EditText) findViewById(R.id.fats);
        proteinText = (EditText) findViewById(R.id.proteins);
        Button back = (Button) findViewById(R.id.backBtnCalories);
        Button saveData = (Button) findViewById(R.id.saveCalories);

        // Hides the navigation bar and status bar
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }

        // Plays text to speech audio for back
        back.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer back = MediaPlayer.create(caloriesPage.this, R.raw.back);
                back.start();
                return true;
            }
        });

        // Plays text to speech audio for save data
        saveData.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer saveData = MediaPlayer.create(caloriesPage.this, R.raw.savedata);
                saveData.start();
                return true;
            }
        });
    }

    /*
     * Button navigates back to the Main Screen
     */
    public void toSecondaryPage(View view) {
        Intent intent = new Intent(this, secondaryAct.class);
        intent.putExtra("person", (Parcelable) person);
        startActivity(intent);
    }

    /*
     * Button saves the user's input
     * Button navigates back to the Main Screen after user input their data
     */
    public void saveCalories(View view) {
        //Creates Strings for the fields.
        String sugarTextStr = sugarText.getText().toString();
        String carbTextStr = carbText.getText().toString();
        String fatTextStr = fatText.getText().toString();
        String proteinTextStr = proteinText.getText().toString();

        // Makes sure that all fields are valid
        if (sugarTextStr.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please fill out the sugar.", Toast.LENGTH_LONG).show();
        } else if (carbTextStr.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please fill out the carbohydrate.", Toast.LENGTH_LONG).show();
        } else if (fatTextStr.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please fill out the fat.", Toast.LENGTH_LONG).show();
        } else if (proteinTextStr.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please fill out the protein.", Toast.LENGTH_LONG).show();
        } else {
            // Concatenates the macro-calories into a large string for one entry
            String value = carbTextStr + "," + proteinTextStr + "," + fatTextStr + "," + sugarTextStr;

            // Concatenates the old data with new data
            String writeStr = person.writeToTxt("cal", value);

            // Reads the information and writes in the new data into the file
            try {
                FileInputStream myObj = openFileInput("userInfo.txt");
                InputStreamReader InputRead = new InputStreamReader(myObj);
                BufferedReader bufferedReader = new BufferedReader(InputRead);
                ArrayList<String> list = new ArrayList<>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Person tempPerson = new Person(line);
                    if (tempPerson.username.equals(person.username)) { // If user matches the user with the file
                        list.add(writeStr); // Adds in the new data
                    } else {
                        list.add(line);
                    }
                }
                bufferedReader.close();

                FileOutputStream myWriter = openFileOutput("userInfo.txt", MODE_PRIVATE);
                OutputStreamWriter outputWriter = new OutputStreamWriter(myWriter);
                for (int i = 0; i < list.size(); i = i + 1) {
                    outputWriter.write(list.get(i));
                    outputWriter.write("\n");
                }
                outputWriter.close();

                // Shows the user their input was successful
                // Transfers the user's information to the next page
                Toast.makeText(getBaseContext(), "Input successful!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, secondaryAct.class);
                intent.putExtra("notifCalBool", true);
                intent.putExtra("person", (Parcelable) person);
                startActivity(intent);
            } catch (IOException | ParseException e) {
            }
        }
    }

    /*
     * Keeps the navigation and status bar hidden after the user leaves the page
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }
}