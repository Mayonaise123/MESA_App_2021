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
public class updatePassword extends AppCompatActivity {
    // Global Variables
    private int currentApiVersion;
    EditText oldPass;
    EditText newPass;
    EditText confirmPass;
    Person person;

    /*
     * Generates the fields
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        // Gathers the data from previous pages
        Bundle data = getIntent().getExtras();
        person = (Person) data.getParcelable("person");

        Button back = (Button) findViewById(R.id.backBtnUpPass);
        Button savePass = (Button) findViewById(R.id.saveNewPass);

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
                MediaPlayer back = MediaPlayer.create(updatePassword.this, R.raw.back);
                back.start();
                return true;
            }
        });

        // Plays text to speech audio for save new password
        savePass.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer savePass = MediaPlayer.create(updatePassword.this, R.raw.savepassword);
                savePass.start();
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
     * Button saves the New password
     * Button navigates back to the Main Screen after user changes their password
     * Transfers data to the next page
     */
    public void saveNewPass(View view) {
        oldPass = (EditText) findViewById(R.id.oldPassword);
        newPass = (EditText) findViewById(R.id.newPassword);
        confirmPass = (EditText) findViewById(R.id.confirmNewPassword);

        String oldPassStr = oldPass.getText().toString();
        String newPassStr = newPass.getText().toString();
        String confirmPassStr = confirmPass.getText().toString();

        // Confirms the user's old password, and new password has to equal to the confirm new password
        if (!oldPassStr.equals(person.password)) {
            Toast.makeText(getBaseContext(), "Incorrect old password.", Toast.LENGTH_LONG).show();
        } else if (newPassStr.isEmpty() || confirmPassStr.isEmpty()) {
            Toast.makeText(getBaseContext(), "Password cannot be empty.", Toast.LENGTH_LONG).show();
        } else if (newPassStr.matches("[^A-Za-z0-9]")) {
            Toast.makeText(getBaseContext(), "No special characters allowed in Password.", Toast.LENGTH_LONG).show();
        } else if (!newPassStr.equals(confirmPassStr)) {
            Toast.makeText(getBaseContext(), "New passwords do not match.", Toast.LENGTH_LONG).show();
        } else {
            String writeStr = person.writeToTxt("update_password", newPassStr);

            //reads the information and writes in the new data
            //Shows the user their input was successful
            try {
                FileInputStream myObj = openFileInput("userInfo.txt");
                InputStreamReader InputRead = new InputStreamReader(myObj);
                BufferedReader bufferedReader = new BufferedReader(InputRead);
                ArrayList<String> list = new ArrayList<>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Person tempPerson = new Person(line);
                    if (tempPerson.username.equals(person.username)) { // Checks if the username in file matches with user
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
                Toast.makeText(getBaseContext(), "New password saved.", Toast.LENGTH_LONG).show();
                intent.putExtra("person", (Parcelable) person);
                startActivity(intent);
            } catch (IOException | ParseException e) {
            }
        }
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
}