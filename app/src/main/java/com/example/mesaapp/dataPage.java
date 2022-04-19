package com.example.mesaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/*
 * User chooses starting, ending dates, and category for plot page
 */
public class dataPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // Global Variables
    private int currentApiVersion;
    String minDateStr;
    String maxDateStr;
    String catSpinnerStr;
    Spinner catSpinner;
    EditText datePickerMin;
    EditText datePickerMax;
    DatePickerDialog picker;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    Person person;

    /*
     * Generates spinner and date pickers on an edit texts
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_page);

        // Gathers the data of the user from the last page the user visits.
        Bundle data = getIntent().getExtras();
        person = (Person) data.getParcelable("person");
        Button backBtn = (Button) findViewById(R.id.backBtnData);
        Button plotBtn = (Button) findViewById(R.id.toPlotPage);
        Button plotAllBtn = (Button) findViewById(R.id.plotAll);
        // Hides status and navigation bar
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

            // Sets up the category spinner
            catSpinner = (Spinner) findViewById(R.id.chooseCategory);
            ArrayAdapter<CharSequence> adapterCat = ArrayAdapter.createFromResource(this, R.array.dataCategories, android.R.layout.simple_spinner_item);
            adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            catSpinner.setAdapter(adapterCat);
            catSpinner.setOnItemSelectedListener(this);


            // Sets up the date pickers as edit texts
            datePickerMin = (EditText) findViewById(R.id.chooseDateMin);
            datePickerMax = (EditText) findViewById(R.id.chooseDateMax);
            datePickerMax.setInputType(InputType.TYPE_NULL);
            datePickerMin.setInputType(InputType.TYPE_NULL);

            // If the user clicks to data page from plot, statistic, or more-info page, the dates would remain the same as what they chose before
            if (data.getString("source").equals("getDates")) {
                minDateStr = data.getString("startDate");
                maxDateStr = data.getString("endDate");
                catSpinnerStr = data.getString("catStr");
                datePickerMin.setText(minDateStr);
                datePickerMax.setText(maxDateStr);
                catSpinner.setSelection(adapterCat.getPosition(catSpinnerStr));
            }

            // Shows the minimum day Date Picker
            datePickerMin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);

                    picker = new DatePickerDialog(dataPage.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            datePickerMin.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                        }
                    }, year, month, day);
                    picker.show();
                }
            });

            // Shows the maximum day Date Picker
            datePickerMax.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar cldr = Calendar.getInstance();
                    cldr.getTimeInMillis();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    picker = new DatePickerDialog(dataPage.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            datePickerMax.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                        }
                    }, year, month, day);
                    picker.show();
                }
            });

            // Plays text to speech audio for back
            backBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MediaPlayer back = MediaPlayer.create(dataPage.this, R.raw.back);
                    back.start();
                    return true;
                }
            });

            // Plays text to speech audio for plot Btn
            plotBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MediaPlayer plotBtn = MediaPlayer.create(dataPage.this, R.raw.plotsound);
                    plotBtn.start();
                    return true;
                }
            });

            // Plays text to speech audio for plotAll Btn
            plotAllBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MediaPlayer plotAll = MediaPlayer.create(dataPage.this, R.raw.plotalldatasound);
                    plotAll.start();
                    return true;
                }
            });
        }
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
     * Button navigates to Plot Page using starting and ending dates
     */
    public void toPlotPageDate(View view) throws ParseException {
        Calendar minDateCal = new GregorianCalendar();
        Calendar maxDateCal = new GregorianCalendar();
        catSpinnerStr = catSpinner.getSelectedItem().toString();

        // Writes the edit texts into strings
        minDateStr = datePickerMin.getText().toString();
        maxDateStr = datePickerMax.getText().toString();

        // Checks whether the fields are empty
        if (minDateStr.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please enter a starting date", Toast.LENGTH_LONG).show();
        } else if (maxDateStr.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please enter an ending date", Toast.LENGTH_LONG).show();
        } else {
            Date currentDate = new Date();
            Date minDate = sdf.parse(datePickerMin.getText().toString());
            Date maxDate = sdf.parse(datePickerMax.getText().toString());

            // Checks whether the dates are appropriate
            if (minDate.before(sdf.parse(person.signUpDateStr))) {
                Toast.makeText(getBaseContext(), "Starting Date must be on or after " + person.signUpDateStr, Toast.LENGTH_LONG).show();
            } else if (catSpinnerStr.isEmpty()) {
                Toast.makeText(getBaseContext(), "Please enter a category.", Toast.LENGTH_LONG).show();
            } else if (maxDate.after(currentDate)) {
                Toast.makeText(getBaseContext(), "Ending Date must be on or before " + sdf.format(currentDate), Toast.LENGTH_LONG).show();
            } else if (!isData(minDate, maxDate, catSpinner.getSelectedItem().toString())) {
                Toast.makeText(getBaseContext(), "No data exists between the two dates.", Toast.LENGTH_LONG).show();
            } else {
                minDateCal.setTime(minDate);
                maxDateCal.setTime(maxDate);

                // Checks if calories or sugar is the selected field
                // calculates the total calories from the macro-calories
                // Adds the date and total into the dataList
                String[] dataArray = returnDataArray(catSpinnerStr, minDate, maxDate);

                Intent intent = new Intent(this, plotPage.class);
                intent.putExtra("person", (Parcelable) person);
                intent.putExtra("catStr", catSpinnerStr);
                intent.putExtra("startDate", minDateStr);
                intent.putExtra("endDate", maxDateStr);
                intent.putExtra("value", Integer.toString(daysBetween(minDateCal.getTime(), maxDateCal.getTime())));
                intent.putExtra("data", dataArray);
                startActivity(intent);
            }
        }
    }

    /*
     * Button navigates to Plot Page with all data
     */
    public void toPlotPageAll(View view) throws ParseException {
        catSpinnerStr = catSpinner.getSelectedItem().toString();

        // Checks whether the dates are appropriate
        if (catSpinnerStr.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please enter a category.", Toast.LENGTH_LONG).show();
        } else if (person.splitLongStr[5].equals("-1") && catSpinnerStr.equals("Calories")) {
            Toast.makeText(getBaseContext(), "There is no data in Calories.", Toast.LENGTH_LONG).show();
        } else if (person.splitLongStr[6].equals("-1") && catSpinnerStr.equals("Sleep")) {
            Toast.makeText(getBaseContext(), "There is no data in Sleep.", Toast.LENGTH_LONG).show();
        } else if (person.splitLongStr[7].equals("-1") && catSpinnerStr.equals("Weight")) {
            Toast.makeText(getBaseContext(), "There is no data in Weight.", Toast.LENGTH_LONG).show();
        } else {
            Date minDate = sdf.parse(person.signUpDateStr);
            Date maxDate = new Date();
            String[] dataArray = returnDataArray(catSpinnerStr, minDate, maxDate);

            String daysBetweenStr = Integer.toString(dataArray.length);
            Intent intent = new Intent(this, plotPage.class);
            intent.putExtra("person", (Parcelable) person);
            intent.putExtra("catStr", catSpinnerStr);
            intent.putExtra("startDate", minDateStr);
            intent.putExtra("endDate", maxDateStr);
            intent.putExtra("value", daysBetweenStr);
            intent.putExtra("data", dataArray);
            startActivity(intent);
        }
    }

    /*
     * Checks whether if there is data between the min and max dates
     */
    public boolean isData(Date minDate, Date maxDate, String catStr) throws ParseException {
        String[] currStr;
        ArrayList<String> dateStr = new ArrayList<>();
        boolean returnValue = false;

        // Splits the data into individual dates
        if (catStr.equals("Calories") || catStr.equals("Sugar")) {
            currStr = person.splitLongStr[5].split(",,,");
        } else if (catStr.equals("Sleep")) {
            currStr = person.splitLongStr[6].split(",,,");
        } else {
            currStr = person.splitLongStr[7].split(",,,");
        }

        for (int i = 0; i < currStr.length; i = i + 1) {
            if (catStr.equals("Calories") || catStr.equals("Sugar")) {
                dateStr.add(currStr[i].split(",,")[0].split(",")[0]);
            } else {
                dateStr.add(currStr[i].split(",")[0]);
            }
        }

        for (int i = 0; i < dateStr.size(); i = i + 1) {
            Date date = sdf.parse(dateStr.get(i));
            if ((date.equals(minDate) || date.after(minDate)) && (date.equals(maxDate) || date.before(maxDate))) {
                returnValue = true;
                i = dateStr.size();
            }
        }
        return returnValue;
    }

    /*
     * Returns array with data within the 2 dates for the chosen category which is then used in plot page
     */
    public String[] returnDataArray(String catSpinnerStr, Date minDate, Date maxDate) throws ParseException {
        ArrayList<String> dataList = new ArrayList<>();
        if (catSpinnerStr.equals("Calories") || catSpinnerStr.equals("Sugar")) {
            // Removes sugar from the Calories Category
            // Calculates the total calories from macronutrients
            String[] splitCalDay = person.splitLongStr[5].split(",,,");
            for (int i = 0; i < splitCalDay.length; i = i + 1) {
                String[] splitCalEntry = splitCalDay[i].split(",,");
                Date date = sdf.parse(splitCalEntry[0].split(",")[0]);
                if ((date.equals(minDate) || date.after(minDate)) && (date.equals(maxDate) || date.before(maxDate))) {
                    int total = 0;
                    int carbs = 0;
                    int protein = 0;
                    int fat = 0;
                    for (int j = 0; j < splitCalEntry.length; j = j + 1) {
                        String[] splitCalFinal = splitCalEntry[j].split(",");
                        // If there are no dates before, the total calculates the data from the 1st value, otherwise, it starts at the 2nd value
                        if (j == 0 && catSpinnerStr.equals("Calories")) {
                            carbs = carbs + Integer.parseInt(splitCalFinal[1]) * 4;
                            protein = protein + Integer.parseInt(splitCalFinal[2]) * 4;
                            fat = fat + Integer.parseInt(splitCalFinal[3]) * 9;
                            total = total + carbs + protein + fat;
                        } else if (j > 0 && catSpinnerStr.equals("Calories")) {
                            carbs = carbs + Integer.parseInt(splitCalFinal[0]) * 4;
                            protein = protein + Integer.parseInt(splitCalFinal[1]) * 4;
                            fat = fat + Integer.parseInt(splitCalFinal[2]) * 9;
                            total = total + carbs + protein + fat;
                        } else if (j == 0 && catSpinnerStr.equals("Sugar")) {
                            total = total + Integer.parseInt(splitCalFinal[4]);
                        } else if (j > 0 && catSpinnerStr.equals("Sugar")) {
                            total = total + Integer.parseInt(splitCalFinal[3]);
                        }
                    }
                    dataList.add(sdf.format(date) + "," + total + "," + carbs + "," + protein + "," + fat);
                }
            }

            // Checks if the selected category is sleep
            // Adds the date and sleep hours into the data list
        } else if (catSpinnerStr.equals("Sleep")) {
            String[] splitSleepDay = person.splitLongStr[6].split(",,,");
            for (int i = 0; i < splitSleepDay.length; i = i + 1) {
                Date date = sdf.parse(splitSleepDay[i].split(",")[0]);
                if ((date.equals(minDate) || date.after(minDate)) && (date.equals(maxDate) || date.before(maxDate))) {
                    dataList.add(splitSleepDay[i]);
                }
            }

            // Checks if the selected category is weight
            // Adds the date and the user's weight into the data list
        } else if (catSpinnerStr.equals("Weight")) {
            String[] splitWeightDay = person.splitLongStr[7].split(",,,");
            for (int i = 0; i < splitWeightDay.length; i = i + 1) {
                Date date = sdf.parse(splitWeightDay[i].split(",")[0]);
                if ((date.equals(minDate) || date.after(minDate)) && (date.equals(maxDate) || date.before(maxDate))) {
                    dataList.add(splitWeightDay[i]);
                }
            }
        }

        // Passes the array list as an array to the next frame
        String[] dataArray = new String[dataList.size()];
        for (int i = 0; i < dataArray.length; i = i + 1) {
            dataArray[i] = dataList.get(i);
        }
        return dataArray;
    }

    /*
     * Checks the days between the min and max days
     */
    public int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}