package com.example.mesaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * User inputs the hours of sleep
 */
public class sleepPage extends AppCompatActivity implements TextToSpeech.OnInitListener{
    // Global Variables
    private int currentApiVersion;
    EditText sleepText;
    Person person;
    TextToSpeech textToSpeech;
    TextView sleepInstructions;

    /*
     * Displays an edit text for the user to input their data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_page);

        // Gets the data from previous pages
        Bundle data = getIntent().getExtras();
        person = (Person) data.getParcelable("person");

        sleepText = (EditText) findViewById(R.id.sleep);
        Button back = (Button) findViewById(R.id.backBtnSleep);
        Button saveData = (Button) findViewById(R.id.saveSleep);
        sleepInstructions = (TextView) findViewById(R.id.instructionsSleep);
        textToSpeech = new TextToSpeech(this, this);

        String sleepHint = sleepText.getHint().toString();
        String sleepInstructionsText = sleepInstructions.getText().toString();

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
                MediaPlayer back = MediaPlayer.create(sleepPage.this, R.raw.back);
                back.start();
                return true;
            }
        });

        // Plays text to speech audio for save data
        saveData.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer saveData = MediaPlayer.create(sleepPage.this, R.raw.savedata);
                saveData.start();
                return true;
            }
        });
        sleepText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int speech = textToSpeech.speak(sleepHint, TextToSpeech.QUEUE_FLUSH,null);
                return true;
            }
        });
        sleepInstructions.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int speech = textToSpeech.speak(sleepInstructionsText, TextToSpeech.QUEUE_FLUSH,null);
                return true;
            }
        });
    }

    /*
     * Button navigates back to the Main Screen
     * Transfers the data to the next page
     */
    public void toSecondaryPage(View view) {
        Intent intent = new Intent(this, secondaryAct.class);
        intent.putExtra("person", (Parcelable) person);
        startActivity(intent);
    }

    /*
     * Saves the user's input
     * Navigates the User back to the Main Screen after they save their data
     * Transfers the data to the next page
     */
    public void saveSleep(View view) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String saveDataDate = sdf.format(new Date());

        // Checks whether the fields are valid
        // Users can only input once per day
        String sleepTextStr = sleepText.getText().toString();
        if (sleepTextStr.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please fill out the sleep.", Toast.LENGTH_LONG).show();
        } else if (person.splitLongStr[6].contains(saveDataDate)) {
            Toast.makeText(getBaseContext(), "Only one input per day.", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(sleepTextStr) > 24) {
            Toast.makeText(getBaseContext(), "24 is the maximum hours of sleep", Toast.LENGTH_LONG).show();
        } else {
            // Adds the new data into the old data string
            String writeStr = person.writeToTxt("sleep", sleepTextStr);

            // Reads the information and writes in the new data
            // Shows the user their input was successful
            try {
                //Reads the file and adds the new data into the string
                FileInputStream myObj = openFileInput("userInfo.txt");
                InputStreamReader InputRead = new InputStreamReader(myObj);
                BufferedReader bufferedReader = new BufferedReader(InputRead);
                ArrayList<String> list = new ArrayList<>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Person tempPerson = new Person(line);
                    if (tempPerson.username.equals(person.username)) { // Checks which line in the file is the user's
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
                Toast.makeText(getBaseContext(), "Input successful!", Toast.LENGTH_LONG).show();
                intent.putExtra("notifSleepBool", true);
                intent.putExtra("person", (Parcelable) person);
                startActivity(intent);
            } catch (IOException | ParseException e) {
            }
        }
    }

    /*
     * Keeps the status bar and navigation bar hidden after the user leaves the page
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
    public void onInit(int i) {

    }
}