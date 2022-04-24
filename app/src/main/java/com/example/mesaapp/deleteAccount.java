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
 * User can update new password
 */
public class deleteAccount extends AppCompatActivity {
    // Global Variables
    private int currentApiVersion;
    Person person;
    EditText confirmBox;
    /*
     * Generates the fields
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        // Gathers the data from previous pages
        Bundle data = getIntent().getExtras();
        person = (Person) data.getParcelable("person");
        Button back = (Button) findViewById(R.id.backBtnDelAct);
        confirmBox = (EditText) findViewById(R.id.delAccTextBox);

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
                MediaPlayer back = MediaPlayer.create(deleteAccount.this, R.raw.back);
                back.start();
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
     *Keeps the status bar and navigation bar hidden after the user leaves the page
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

    public void toLoginPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void deleteAccount(View view) {
        if(confirmBox.getText().toString().equals("confirm")) {
            // Sends the user back to the login page before deleting their profile
            toLoginPage(view);
            try {
                FileInputStream myObj = openFileInput("userInfo.txt");
                InputStreamReader InputRead = new InputStreamReader(myObj);
                BufferedReader bufferedReader = new BufferedReader(InputRead);
                ArrayList<String> list = new ArrayList<>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Person tempPerson = new Person(line);
                    if (tempPerson.username.equals(person.username)) { // Checks if the username in file matches with user, if so deletes it
                        list.remove(line);
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
            } catch (IOException | ParseException e) {
            }
        }
    }
}