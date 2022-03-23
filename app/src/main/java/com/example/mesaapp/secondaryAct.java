package com.example.mesaapp;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/*
 * Main Screen that the user navigates through
 */
public class secondaryAct extends AppCompatActivity {
    //Global Variables
    private NotificationManager mNotificationManager;
    private int currentApiVersion;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "Notification";
    TextView welcomeText;
    Boolean notifCalBool;
    Boolean notifSleepBool;
    Boolean notifWeightBool;
    Person person;

    /*
     * Generates the frame, and notification
     * Welcomes user when they enter successfully enter
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        // Welcomes the user when they sign up/sign in
        welcomeText = (TextView) findViewById(R.id.helloText);
        Bundle data = getIntent().getExtras();
        person = (Person) data.getParcelable("person");
        notifCalBool = data.getBoolean("notifCalBool");
        notifSleepBool = data.getBoolean("notifSleepBool");
        notifWeightBool = data.getBoolean("notifWeightBool");
        String welcome = "Welcome " + person.name.substring(0, 1).toUpperCase() + person.name.substring(1) + "!";
        welcomeText.setText(welcome);


        Button caloriesBtn = findViewById(R.id.goToCaloriesBtn);
        Button sleepBtn = findViewById(R.id.goToSleepBtn);
        Button weightBtn = findViewById(R.id.goToWeightBtn);
        Button dataBtn = findViewById(R.id.goToDataPage);
        Button settingsBtn = findViewById(R.id.goToSettingsPage);
        Button logoutBtn = findViewById(R.id.logOut);

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

        //Notifies the user to check plot for every 10 entries the user puts in
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (person.splitLongStr[5].split(",,,").length % 10 == 0) {
            if (notifCalBool) {
                dataNotification(secondaryAct.this, "Calories", calCreate());
            }
        } else if (person.splitLongStr[6].split(",,,").length % 10 == 0) {
            if (notifSleepBool) {
                dataNotification(secondaryAct.this, "Sleep", person.splitLongStr[6].split(",,,"));
            }
        } else if (person.splitLongStr[7].split(",,,").length % 10 == 0) {
            if (notifWeightBool) {
                dataNotification(secondaryAct.this, "Weight", person.splitLongStr[7].split(",,,"));
            }
        }
        createNotificationChannel();

        // Plays text to speech audio for calories
        caloriesBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer calories = MediaPlayer.create(secondaryAct.this, R.raw.caloriesound);
                calories.start();
                return true;
            }
        });

        // Plays text to speech audio for sleep
        sleepBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer sleep = MediaPlayer.create(secondaryAct.this, R.raw.sleepsound);
                sleep.start();
                return true;
            }
        });

        // Plays text to speech audio for weight
        weightBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer weight = MediaPlayer.create(secondaryAct.this, R.raw.weightsound);
                weight.start();
                return true;
            }
        });

        // Plays text to speech audio for data
        dataBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer data = MediaPlayer.create(secondaryAct.this, R.raw.datasound);
                data.start();
                return true;
            }
        });

        // Plays text to speech audio for settings
        settingsBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer settings = MediaPlayer.create(secondaryAct.this, R.raw.settingssound);
                settings.start();
                return true;
            }
        });

        // Plays text to speech audio for logout
        logoutBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer logout = MediaPlayer.create(secondaryAct.this, R.raw.logoutsound);
                logout.start();
                return true;
            }
        });
    }


    public String[] calCreate() {
        String[] splitCalDay = person.splitLongStr[5].split(",,,");
        String[] dataArray = new String[10];

        for (int i = splitCalDay.length - 10; i < splitCalDay.length; i = i + 1) {
            int total = 0;
            int carbs = 0;
            int protein = 0;
            int fat = 0;

            String[] splitCalEntry = splitCalDay[i].split(",,");
            String date = splitCalEntry[0].split(",")[0];
            for (int j = 0; j < splitCalEntry.length; j = j + 1) {
                String[] splitCalFinal = splitCalEntry[j].split(",");

                // If there are no dates before, the total calculates the data from the 1st value, otherwise, it starts at the 2nd value
                if (j == 0) {
                    carbs = carbs + Integer.parseInt(splitCalFinal[1]) * 4;
                    protein = protein + Integer.parseInt(splitCalFinal[2]) * 4;
                    fat = fat + Integer.parseInt(splitCalFinal[3]) * 9;
                    total = total + carbs + protein + fat;
                } else if (j > 0) {
                    carbs = carbs + Integer.parseInt(splitCalFinal[0]) * 4;
                    protein = protein + Integer.parseInt(splitCalFinal[1]) * 4;
                    fat = fat + Integer.parseInt(splitCalFinal[2]) * 9;
                    total = total + carbs + protein + fat;
                }
            }
            dataArray[i + 10 - splitCalDay.length] = date + "," + total + "," + carbs + "," + protein + "," + fat;
        }
        return dataArray;
    }


    /*
     * Button navigates to the sleep page
     */
    public void toSleepPage(View view) {
        Intent intent = new Intent(this, sleepPage.class);
        intent.putExtra("person", (Parcelable) person);
        startActivity(intent);
    }

    /*
     * Button navigates to the calories page
     */
    public void toCaloriesPage(View view) {
        Intent intent = new Intent(this, caloriesPage.class);
        intent.putExtra("person", (Parcelable) person);
        startActivity(intent);
    }

    /*
     * Button navigates to the weight page
     */
    public void toWeightPage(View view) {
        Intent intent = new Intent(this, weightPage.class);
        intent.putExtra("person", (Parcelable) person);
        startActivity(intent);
    }

    /*
     * Button navigates to the data page
     */
    public void toDataPage(View view) {
        Intent intent = new Intent(this, dataPage.class);
        intent.putExtra("person", (Parcelable) person);
        intent.putExtra("source", "noDates");
        startActivity(intent);
    }

    /*
     * Button navigates to the settings page
     */
    public void toSettingsPage(View view) {
        Intent intent = new Intent(this, settingsPage.class);
        intent.putExtra("person", (Parcelable) person);
        startActivity(intent);
    }

    /*
     * Button logs out the user to the login page
     */
    public void toLoginPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /*
     * Allows the user to change the notification settings in their phone
     */
    public void createNotificationChannel() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification to see your plot");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /*
     * Creates the notification
     */
    private void dataNotification(Context context, String catString, String[] data) {
        Intent intent = new Intent(context, plotPage.class);
        intent.putExtra("catStr", catString);
        intent.putExtra("person", (Parcelable) person);
        intent.putExtra("value", "10");
        intent.putExtra("data", data);
        intent.putExtra("startDate", data[data.length - 10].split(",")[0]);
        intent.putExtra("endDate", data[data.length - 1].split(",")[0]);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("See your graph for " + catString + " plot!")
                .setContentText("There are 10 entries, click to see your graph!")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
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