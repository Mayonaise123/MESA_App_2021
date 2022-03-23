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
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

/*
 * User is directed to this page first
 * User has to sign in in order to input new data
 */
public class MainActivity extends AppCompatActivity {
    // Global Variables
    private int currentApiVersion;
    EditText name;
    EditText password;
    Person person;

    // Generates the name and password field
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.nameLogin);
        password = findViewById(R.id.passwordlogin);
        Button signInBtn = findViewById(R.id.signInBtn);
        Button createAccount = findViewById(R.id.createAccount);

        // Hides the status and notification bar
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

        // Plays text to speech audio for sign in
        signInBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer signIn = MediaPlayer.create(MainActivity.this, R.raw.signinsound);
                signIn.start();
                return true;
            }
        });

        // Plays text to speech audio for sign up
        createAccount.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer createAccount = MediaPlayer.create(MainActivity.this, R.raw.createaccount);
                createAccount.start();
                return true;
            }
        });
    }

    /*
     * Navigates to the sign up page
     */
    public void toSignUpPage(View view) {
        Intent intent = new Intent(this, signUpPage.class);
        startActivity(intent);
    }

    /*
     * Navigates to the main menu
     */
    public void toSecondaryPage(View view) {
        String nameString = name.getText().toString();
        String passwordString = password.getText().toString();

        // Checks whether the fields are empty and correct
        if (!nameString.equals("") && !passwordString.equals("")) {
            if (isMatched(nameString, passwordString)) {
                Intent intent = new Intent(this, secondaryAct.class);
                intent.putExtra("person", (Parcelable) person);
                startActivity(intent);
            } else {
                Toast.makeText(getBaseContext(), "Invalid Username or Password", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getBaseContext(), "Please enter Username and password.", Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Checks whether the name and password are correct
     */
    public boolean isMatched(String nameString, String passwordString) {
        // Reads the file to check whether the fields are right
        try {
            FileInputStream myObj = openFileInput("userInfo.txt");
            InputStreamReader InputRead = new InputStreamReader(myObj);
            BufferedReader bufferedReader = new BufferedReader(InputRead);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Person tempPerson = new Person(line);
                if (tempPerson.password.equals(passwordString) && tempPerson.username.equals(nameString)) {
                    person = tempPerson;
                    return true;
                }
            }
            bufferedReader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return false;
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
}