package com.example.mesaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/*
 * Shows the user the average data based on the input from Plot Page
 */
public class statPage extends AppCompatActivity {
    // Global Variables
    private int currentApiVersion;
    int totalEvents;
    String catStr;
    String minDateStr;
    String maxDateStr;
    TextView median;
    TextView mean;
    TextView mode;
    TextView range;
    String[] dataArray;
    Person person;

    /*
     * Sets the mean, median, mode, and range
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat_page);

        // Gets data from previous pages
        Bundle data = getIntent().getExtras();
        person = (Person) data.getParcelable("person");
        catStr = data.getString("catStr");
        totalEvents = Integer.parseInt(data.getString("value"));
        minDateStr = data.getString("startDate");
        maxDateStr = data.getString("endDate");
        dataArray = data.getStringArray("data");
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        Button back = (Button) findViewById(R.id.backBtnStats);
        Button dataBtn = (Button) findViewById(R.id.backBtnData);
        Button moreInfo = (Button) findViewById(R.id.moreBtnInfo);

        // Hides the navigation and status bar
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

        median = (TextView) findViewById(R.id.median);
        mean = (TextView) findViewById(R.id.mean);
        mode = (TextView) findViewById(R.id.mode);
        range = (TextView) findViewById(R.id.range);

        // Gathers only the data (not dates) from the dataArray
        ArrayList<Float> dataList = new ArrayList<>();
        for (int i = 0; i < dataArray.length; i = i + 1) {
            dataList.add(Float.parseFloat(dataArray[i].split(",")[1]));
        }

        // Changes "day" to "days" based on the number of dates in between (totalEvents)
        String day = " day";
        if (totalEvents > 1) {
            day = " days";
        }
        String units = "";
        String catStrLow = catStr.toLowerCase();
        if(catStrLow.equals("sleep")) {
            units = " hours";
        } else if (catStrLow.equals("weight")){
            units = " pounds";
        } else if (catStrLow.equals("sugar")) {
            units = " grams";
        }
        String meanText = "Your daily mean " + catStrLow + " is " + mean(dataList) + units + " for " + totalEvents + day;
        String medianText = "Your daily median " + catStrLow + " is " + median(dataList) + units + " for " + totalEvents + day;
        String modeText = "Your daily mode " + catStrLow + " is " + mode(dataList) + units + " for " + totalEvents + day;
        String rangeText = "Your daily range of " + catStrLow + " is " +  range(dataList) + units + " for " + totalEvents + day + " ,between the highest and lowest recorded values";

        mean.setText(meanText);
        median.setText(medianText);
        mode.setText(modeText);
        range.setText(rangeText);

        // Plays text to speech audio for back
        back.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer back = MediaPlayer.create(statPage.this, R.raw.back);
                back.start();
                return true;
            }
        });

        // Plays text to speech audio for data
        dataBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer toStatistics = MediaPlayer.create(statPage.this, R.raw.datasound);
                toStatistics.start();
                return true;
            }
        });

        // Plays text to speech audio for more information
        moreInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer toStatistics = MediaPlayer.create(statPage.this, R.raw.moreinfosound);
                toStatistics.start();
                return true;
            }
        });
    }

    /*
     * Calculates the mean based on the number of entries chosen
     */
    public String mean(ArrayList<Float> x) {
        float total = 0;

        for (float i : x) {
            total = total + i;
        }
        String returnStr = Double.toString(total / x.size());

        // Rounds the number to the thousandth's place
        if (returnStr.substring(returnStr.indexOf(".")).length() >= 4) {
            double tempDouble = Double.parseDouble(returnStr.substring(0, returnStr.indexOf(".") + 3));
            if (Integer.parseInt(returnStr.substring(returnStr.indexOf(".")).substring(4, 5)) >= 5) {
                return Double.toString(tempDouble + .01);
            } else {
                return Double.toString(tempDouble);
            }
        } else {
            return returnStr;
        }
    }

    /*
     * Calculates the median based on the number of entries chosen
     */
    public String median(ArrayList<Float> x) {
        Collections.sort(x);

        if (x.size() % 2 == 0) {
            return Double.toString((x.get(x.size() / 2 - 1) + x.get(x.size() / 2)) / 2);
        } else {
            int loc = (int) Math.ceil(x.size() / 3);
            return Double.toString(x.get(loc));
        }
    }

    /*
     * Calculates the mode based on the number of entries chosen
     */
    public String mode(ArrayList<Float> x) {
        float maxValue = 0;
        int maxCount = 0;

        for (float i : x) {
            int counter = 0;
            for (float j : x) {
                if (j == i) {
                    counter = counter + 1;
                }
            }
            if (counter > maxCount) {
                maxCount = counter;
                maxValue = i;
            }
        }
        return Double.toString(maxValue);
    }

    /*
     * Calculates the range based on the number of entries chosen
     */
    public String range(ArrayList<Float> x) {
        Collections.sort(x);
        return Double.toString(x.get(x.size() - 1) - x.get(0));
    }

    /*
     * Navigates back to the Plot Page
     * Transfers the data to the next page
     */
    public void toPlotPage(View view) {
        Intent intent = new Intent(this, plotPage.class);
        intent.putExtra("person", (Parcelable) person);
        intent.putExtra("catStr", catStr);
        intent.putExtra("value", Integer.toString(totalEvents));
        intent.putExtra("data", dataArray);
        intent.putExtra("startDate", minDateStr);
        intent.putExtra("endDate", maxDateStr);
        startActivity(intent);
    }

    /*
     * Navigates to the More Info Page
     * Transfers the data to the next page
     */
    public void toMoreInfo(View view) {
        Intent intent = new Intent(this, moreInfo.class);
        intent.putExtra("person", (Parcelable) person);
        intent.putExtra("catStr", catStr);
        intent.putExtra("value", Integer.toString(totalEvents));
        intent.putExtra("data", dataArray);
        intent.putExtra("startDate", minDateStr);
        intent.putExtra("endDate", maxDateStr);
        startActivity(intent);
    }

    /*
     * Navigates to the Data Page
     * Transfers the data to the next page
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