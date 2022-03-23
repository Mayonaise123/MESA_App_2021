package com.example.mesaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
 * User can update their height values
 */
public class updateHeight extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // Global Variables
    private int currentApiVersion;
    Person person;
    Spinner newHeightFeet;
    Spinner newHeightInch;

    /*
     * Generates the spinners for new height
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_height);

        // Gets the data from previous pages
        Bundle data = getIntent().getExtras();
        person = (Person) data.getParcelable("person");

        Button back = (Button) findViewById(R.id.backBtnUpHeight);
        Button saveData = (Button) findViewById(R.id.saveNewHeight);

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

        newHeightFeet = (Spinner) findViewById(R.id.newHeightFeetValue);
        ArrayAdapter<CharSequence> adapterFeet = ArrayAdapter.createFromResource(this, R.array.heightFeet, android.R.layout.simple_spinner_item);
        adapterFeet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newHeightFeet.setAdapter(adapterFeet);
        newHeightFeet.setOnItemSelectedListener(this);

        newHeightInch = (Spinner) findViewById(R.id.newHeightInchValue);
        ArrayAdapter<CharSequence> adapterInch = ArrayAdapter.createFromResource(this, R.array.heightInch, android.R.layout.simple_spinner_item);
        adapterInch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newHeightInch.setAdapter(adapterInch);
        newHeightInch.setOnItemSelectedListener(this);

        // Plays text to speech audio for back
        back.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer back = MediaPlayer.create(updateHeight.this, R.raw.back);
                back.start();
                return true;
            }
        });

        // Plays text to speech audio for save new height
        saveData.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer saveData = MediaPlayer.create(updateHeight.this, R.raw.saveheight);
                saveData.start();
                return true;
            }
        });
    }

    /*
     * Button navigates back to the Settings Page
     * Transfers data to the next page
     */
    public void toSettingsPage(View view) {
        Intent intent = new Intent(this, settingsPage.class);
        intent.putExtra("person", (Parcelable) person);
        startActivity(intent);
    }

    /*
     * Button saves the user's new height value
     * Button navigates back to the Main Screen after user input their data
     * Transfers data to the next page
     */
    public void saveNewHeight(View view) {
        String feetStr = newHeightFeet.getSelectedItem().toString();
        String inchStr = newHeightInch.getSelectedItem().toString();
        // Calculates the height in inches
        String newHeight = Integer.toString(Integer.parseInt(feetStr) * 12 + Integer.parseInt(inchStr));

        // Checks whether the fields are empty
        if (feetStr.isEmpty() || inchStr.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please fill with valid height.", Toast.LENGTH_LONG).show();
        } else {
            // Saves the new height into the string
            String writeStr = person.writeToTxt("update_height", newHeight);

            // Reads the information and writes in the new data
            // Shows the user their input was successful
            try {
                FileInputStream myObj = openFileInput("userInfo.txt");
                InputStreamReader InputRead = new InputStreamReader(myObj);
                BufferedReader bufferedReader = new BufferedReader(InputRead);
                ArrayList<String> list = new ArrayList<>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Person tempPerson = new Person(line);
                    if (tempPerson.username.equals(person.username)) { // Checks if username in file matches with the user
                        list.add(writeStr);
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
                Intent intent = new Intent(this, secondaryAct.class);
                Toast.makeText(getBaseContext(), "Changes were saved!", Toast.LENGTH_LONG).show();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}