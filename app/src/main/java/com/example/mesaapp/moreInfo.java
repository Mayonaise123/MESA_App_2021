package com.example.mesaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Shows more information about the user's chosen category
 */
public class moreInfo extends AppCompatActivity {
    // Global Variables
    private int currentApiVersion;
    int totalEvents;
    String catStr;
    String minDateStr;
    String maxDateStr;
    String[] dataArray;
    Person person;

    /*
     * Sets the text and image based on the category the user chooses
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        // Sets the radio buttons and button to be gone
        RadioGroup levels = findViewById(R.id.levelsExercise);
        Button save = findViewById(R.id.save);
        levels.setVisibility(View.GONE);
        levels.setEnabled(false);
        save.setVisibility(View.GONE);
        save.setEnabled(false);

        // Gathers data from what the user chooses in the previous pages
        Bundle data = getIntent().getExtras();
        person = (Person) data.getParcelable("person");
        catStr = data.getString("catStr");
        totalEvents = Integer.parseInt(data.getString("value"));
        dataArray = data.getStringArray("data");
        minDateStr = data.getString("startDate");
        maxDateStr = data.getString("endDate");

        Button back = (Button) findViewById(R.id.backBtnMoreInfo);
        Button dataBtn = (Button) findViewById(R.id.backBtnData);
        Button saveBtn = (Button) findViewById(R.id.save);

        if (person.splitLongStr[7].equals("-1")) {
            Intent intent = new Intent(this, secondaryAct.class);
            intent.putExtra("person", (Parcelable) person);
            startActivity(intent);
            Toast.makeText(getBaseContext(), "Input your weight in weight page to see your BMR", Toast.LENGTH_LONG).show();
        }

        String[] splitWeight = person.splitLongStr[7].split(",,,");
        double weight = Double.parseDouble(splitWeight[splitWeight.length - 1].split(",")[1]);
        int height = person.height;

        // Finds the image and text
        ImageView moreInfoImage1 = (ImageView) findViewById(R.id.moreInfoImage1);
        ImageView moreInfoImage2 = (ImageView) findViewById(R.id.moreInfoImage2);
        TextView moreInfoText = (TextView) findViewById(R.id.moreInfoText);

        // If the user chooses calories, shows the button and radio group
        // Calculates the user's bmr based on activeness, weight, height, age, and gender
        if (catStr.equals("Calories")) {
            levels.setVisibility(View.VISIBLE);
            levels.setEnabled(true);
            save.setVisibility(View.VISIBLE);
            save.setEnabled(true);
            moreInfoImage1.setImageResource(R.drawable.bmr_chart);

            // Finds the radio buttons
            RadioButton sedentary = findViewById(R.id.sedentary);
            RadioButton light = findViewById(R.id.light);
            RadioButton moderate = findViewById(R.id.moderate);
            RadioButton heavy = findViewById(R.id.heavy);
            RadioButton extreme = findViewById(R.id.extreme);


            // Checks which radio button is chosen
            save.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    double activityFactor = 0;
                    int bmr;
                    if (sedentary.isChecked()) {
                        activityFactor = 1.2;
                    } else if (light.isChecked()) {
                        activityFactor = 1.375;
                    } else if (moderate.isChecked()) {
                        activityFactor = 1.55;
                    } else if (heavy.isChecked()) {
                        activityFactor = 1.725;
                    } else if (extreme.isChecked()) {
                        activityFactor = 1.9;
                    } else {
                        Toast.makeText(getBaseContext(), "Enter level of activeness.", Toast.LENGTH_LONG).show();
                    }
                    String gender = person.gender;

                    // Calculates the user's bmr
                    if (gender.equals("male")) {
                        bmr = (int) (((4.536 * weight) + (15.88 * person.height) - (5 * person.age) + 5) * activityFactor);
                    } else {
                        bmr = (int) (((4.536 * weight) + (15.88 * person.height) - (5 * person.age) - 161) * activityFactor);
                    }

                    // Shows the user their bmr, formula, and which level of activeness they belong to
                    String bmrText = "Using Mifflin-St Jeor Equation, your BMR is " + Double.toString(bmr);
                    moreInfoText.setText(bmrText);
                    moreInfoImage2.setImageResource(R.drawable.bmr_formula);
                }
            });

            // If the user chooses sleep, shows their age and how many hours they should be sleeping based on their age
        } else if (catStr.equals("Sleep")) {
            String age = "Your age: " + person.age;
            moreInfoText.setText(age);
            moreInfoImage1.setImageResource(R.drawable.sleep_age);

            // If the use chooses weight, shows their bmi and classification
        } else if (catStr.equals("Weight")) {
            String bmi = Double.toString(weight / (height * height) * 703);
            String bmiText = "Your current BMI is ";

            // Rounds the bmi into the hundredths place
            if (bmi.substring(bmi.indexOf(".")).length() >= 3) {
                double tempDouble = Double.parseDouble(bmi.substring(0, bmi.indexOf(".") + 2));
                if (Integer.parseInt(bmi.substring(bmi.indexOf(".")).substring(3, 4)) >= 5) {
                    bmi = Double.toString(tempDouble + .1);
                } else {
                    bmi = Double.toString(tempDouble);
                }
            }
            bmiText = bmiText + bmi;
            moreInfoText.setText(bmiText);
            moreInfoImage1.setImageResource(R.drawable.bmi_chart);
            moreInfoImage2.setImageResource(R.drawable.formula_bmi);

            // If the user chooses sugar, shows their age, gender, and how many grams of sugar they should be eating based on these fields
        } else if (catStr.equals("Sugar")) {
            String sugarText = "Your age: " + person.age + "\n" + "Your gender: " + person.gender;
            moreInfoText.setText(sugarText);
            moreInfoImage1.setImageResource(R.drawable.sugar_age);
            moreInfoImage2.setImageResource(R.drawable.sugar_gender);
        }

        // Hides the navigation and status bar
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

            // Plays text to speech audio for back
            back.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MediaPlayer back = MediaPlayer.create(moreInfo.this, R.raw.back);
                    back.start();
                    return true;
                }
            });

            // Plays text to speech audio for data
            dataBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MediaPlayer toStatistics = MediaPlayer.create(moreInfo.this, R.raw.datasound);
                    toStatistics.start();
                    return true;
                }
            });

            // Plays text to speech audio for save
            saveBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MediaPlayer back = MediaPlayer.create(moreInfo.this, R.raw.save);
                    back.start();
                    return true;
                }
            });
        }
    }

    /*
     * Navigates back to the stats page
     * Transfers the data and what the user chooses to the next page
     */
    public void toStatsPage(View view) {
        Intent intent = new Intent(this, statPage.class);
        intent.putExtra("person", (Parcelable) person);
        intent.putExtra("startDate", minDateStr);
        intent.putExtra("value", Integer.toString(totalEvents));
        intent.putExtra("data", dataArray);
        intent.putExtra("catStr", catStr);
        intent.putExtra("endDate", maxDateStr);
        startActivity(intent);
    }

    /*
     * Navigates back to the data page
     * Transfers the data and what the user chooses to the next page
     */
    public void toDataPage(View view) {
        Intent intent = new Intent(this, dataPage.class);
        intent.putExtra("person", (Parcelable) person);
        intent.putExtra("catStr", catStr);
        intent.putExtra("value", Integer.toString(totalEvents));
        intent.putExtra("source", "getDates");
        intent.putExtra("startDate", minDateStr);
        intent.putExtra("endDate", maxDateStr);
        startActivity(intent);
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