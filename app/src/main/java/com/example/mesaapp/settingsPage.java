package com.example.mesaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

/*
 * Allows user to change their password and height value
 */
public class settingsPage extends AppCompatActivity {
    private int currentApiVersion;
    Person person;

    /*
     * Generates the fields
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        // Gathers the data from the previous pages
        Bundle data = getIntent().getExtras();
        person = (Person) data.getParcelable("person");

        Button back = (Button) findViewById(R.id.backBtnSettings);
        Button updatePass = (Button) findViewById(R.id.newPassword);
        Button updateHeight = (Button) findViewById(R.id.newHeight);

        // Hides the status and navigation bar
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
                MediaPlayer back = MediaPlayer.create(settingsPage.this, R.raw.back);
                back.start();
                return true;
            }
        });

        // Plays text to speech audio for update password
        updatePass.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer saveData = MediaPlayer.create(settingsPage.this, R.raw.updatepass);
                saveData.start();
                return true;
            }
        });

        // Plays text to speech audio for update height
        updateHeight.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer saveData = MediaPlayer.create(settingsPage.this, R.raw.updateheight);
                saveData.start();
                return true;
            }
        });

        // Plays text to speech audio for delete account
        /* TODO: Implement audio for button
        deleteAccount.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer saveData = MediaPlayer.create(settingsPage.this, R.raw.deleteaccount);
                saveData.start();
                return true;
            }
        }); */
    }

    /*
     * Navigates back to the Main Menu
     * Transfers the data to the next page
     */
    public void toSecondaryPage(View view) {
        Intent intent = new Intent(this, secondaryAct.class);
        intent.putExtra("person", (Parcelable) person);
        startActivity(intent);
    }

    /*
     * Navigates to the Update Password Page
     * Transfers the data to the next page
     */
    public void toUpdatePassPage(View view) {
        Intent intent = new Intent(this, updatePassword.class);
        intent.putExtra("person", (Parcelable) person);
        startActivity(intent);
    }

    /*
     * Navigates to the Update Height Page
     * Transfers the data to the next page
     */
    public void toUpdateHeightPage(View view) {
        Intent intent = new Intent(this, updateHeight.class);
        intent.putExtra("person", (Parcelable) person);
        startActivity(intent);
    }

    /*
     * Navigates to the Delete Account Page
     * Transfers the data to the next page
     */
    public void toDeleteAccountPage(View view) {
        Intent intent = new Intent(this, deleteAccount.class);
        intent.putExtra("person", (Parcelable) person);
        startActivity(intent);
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