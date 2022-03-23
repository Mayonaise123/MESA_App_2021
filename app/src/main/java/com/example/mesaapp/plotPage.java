package com.example.mesaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/*
 * Displays the graph to the user based on inputs from Data Page
 */
public class plotPage extends AppCompatActivity {
    // Global Variables
    private int currentApiVersion;
    int totalEvents;
    String catStr;
    String minDateStr;
    String maxDateStr;
    String[] dataArray;
    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSetTotal;
    Person person;

    /*
     * Creates the line graph
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_page);

        // Gets the data from previous pages
        Bundle data = getIntent().getExtras();
        person = data.getParcelable("person");
        catStr = data.getString("catStr");
        totalEvents = Integer.parseInt(data.getString("value"));
        dataArray = data.getStringArray("data");
        minDateStr = data.getString("startDate");
        maxDateStr = data.getString("endDate");

        Button back = findViewById(R.id.backBtnPlot);
        Button statistics = findViewById(R.id.toStatsPage);

        // Allows the user to pinch and zoom
        lineChart = findViewById(R.id.graph);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        ArrayList<Entry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();
        ArrayList<Entry> yEntrysCarb = new ArrayList<>();
        ArrayList<Entry> yEntrysProtein = new ArrayList<>();
        ArrayList<Entry> yEntrysFat = new ArrayList<>();

        if (catStr.equals("Calories")) {
            for (int i = 0; i < dataArray.length; i = i + 1) {
                yEntrysCarb.add(new Entry(i, Integer.parseInt(dataArray[i].split(",")[2])));
                yEntrysProtein.add(new Entry(i, Integer.parseInt(dataArray[i].split(",")[3])));
                yEntrysFat.add(new Entry(i, Integer.parseInt(dataArray[i].split(",")[4])));
            }

            LineDataSet lineDataSetCarb = new LineDataSet(yEntrysCarb, "Carbohydrates");
            lineDataSetCarb.setColor(Color.BLUE);
            lineDataSetCarb.setCircleColor(Color.BLUE);
            LineDataSet lineDataSetProtein = new LineDataSet(yEntrysProtein, "Protein");
            lineDataSetProtein.setColor(Color.RED);
            lineDataSetProtein.setCircleColor(Color.RED);
            LineDataSet lineDataSetFat = new LineDataSet(yEntrysFat, "Fats");
            lineDataSetFat.setColor(Color.GREEN);
            lineDataSetFat.setCircleColor(Color.GREEN);

            dataSets.add(lineDataSetCarb);
            dataSets.add(lineDataSetProtein);
            dataSets.add(lineDataSetFat);
        }

        // X-Axis is the dates, Y-Axis is the data based on chosen category
        for (int i = 0; i < dataArray.length; i = i + 1) {
            yEntrys.add(new Entry(i, Integer.parseInt(dataArray[i].split(",")[1])));
            xEntrys.add(dataArray[i].split(",")[0]);
        }

        lineDataSetTotal = new LineDataSet(yEntrys, "Total");
        dataSets.add(lineDataSetTotal);

        // Formats the X-Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setLabelRotationAngle(-90);
        xAxis.setGranularityEnabled(true);
        xAxis.setEnabled(true);

        lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xEntrys));

        // Formats Y-Axis
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Generates the line graph
        lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
        Description description = lineChart.getDescription();
        description.setEnabled(false);

        // Hides the status bar and navigation bar
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
                MediaPlayer back = MediaPlayer.create(plotPage.this, R.raw.back);
                back.start();
                return true;
            }
        });

        // Plays text to speech audio for statistics
        statistics.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer toStatistics = MediaPlayer.create(plotPage.this, R.raw.statisticsound);
                toStatistics.start();
                return true;
            }
        });
    }

    /*
     * Button navigates back to the Data Page
     * Transfers the data to the next page
     */
    public void toDataPage(View view) {
        Intent intent = new Intent(this, dataPage.class);
        intent.putExtra("person", (Parcelable) person);
        intent.putExtra("catStr", catStr);
        intent.putExtra("data", dataArray);
        intent.putExtra("startDate", minDateStr);
        intent.putExtra("endDate", maxDateStr);
        intent.putExtra("source", "getDates");
        startActivity(intent);
    }

    /*
     * Button navigates to the Stats Page
     * Transfers the data to the next page
     */
    public void toStatsPage(View view) {
        Intent intent = new Intent(this, statPage.class);
        intent.putExtra("person", (Parcelable) person);
        intent.putExtra("catStr", catStr);
        intent.putExtra("value", Integer.toString(totalEvents));
        intent.putExtra("data", dataArray);
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