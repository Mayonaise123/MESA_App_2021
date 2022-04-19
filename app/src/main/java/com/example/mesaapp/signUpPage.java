package com.example.mesaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
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
 * User registers into the app
 */
public class signUpPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //Global Variables
    private int currentApiVersion;
    EditText username;
    EditText name;
    EditText password;
    Spinner heightFeet;
    Spinner heightInch;
    Spinner monthBday;
    Spinner dayBday;
    Spinner yearBday;
    RadioButton femaleGender;
    RadioButton maleGender;
    Person person;

    /*
     *Sets name, username, password
     *Generates the spinner for birthday, height
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        // Hides the status bar and navigation bar
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

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

        name = (EditText) findViewById(R.id.nameSignUp);
        password = (EditText) findViewById(R.id.passwordSignUp);
        username = (EditText) findViewById(R.id.usernameSignUp);
        Button back = (Button) findViewById(R.id.backBtnSignUp);
        Button createAccount = (Button) findViewById(R.id.createAccount);


        heightFeet = (Spinner) findViewById(R.id.heightFeetValue);
        ArrayAdapter<CharSequence> adapterFeet = ArrayAdapter.createFromResource(this, R.array.heightFeet, android.R.layout.simple_spinner_item);
        adapterFeet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightFeet.setAdapter(adapterFeet);
        heightFeet.setOnItemSelectedListener(this);

        heightInch = (Spinner) findViewById(R.id.heightInchValue);
        ArrayAdapter<CharSequence> adapterInch = ArrayAdapter.createFromResource(this, R.array.heightInch, android.R.layout.simple_spinner_item);
        adapterInch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightInch.setAdapter(adapterInch);
        heightInch.setOnItemSelectedListener(this);

        monthBday = (Spinner) findViewById(R.id.month);
        ArrayAdapter<CharSequence> adapterMonth = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthBday.setAdapter(adapterMonth);
        monthBday.setOnItemSelectedListener(this);

        dayBday = (Spinner) findViewById(R.id.day);
        ArrayAdapter<CharSequence> adapterDay = ArrayAdapter.createFromResource(this, R.array.days, android.R.layout.simple_spinner_item);
        adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayBday.setAdapter(adapterDay);
        dayBday.setOnItemSelectedListener(this);

        yearBday = (Spinner) findViewById(R.id.year);
        ArrayAdapter<CharSequence> adapterYear = ArrayAdapter.createFromResource(this, R.array.years, android.R.layout.simple_spinner_item);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearBday.setAdapter(adapterYear);
        yearBday.setOnItemSelectedListener(this);

        // Plays text to speech audio for back
        back.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MediaPlayer backSound = MediaPlayer.create(signUpPage.this, R.raw.back);
                backSound.start();
                return true;
            }
        });

        // Plays text to speech audio for create account
        createAccount.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            MediaPlayer createAccSound = MediaPlayer.create(signUpPage.this, R.raw.createaccount);
            createAccSound.start();
            return true;
        }
    });
}

    /*
     *Button navigates to the Main Menu
     *Writes the user height, gender, name, password, birthday into a txt file.
     */
    public void toMainPage(View view) throws ParseException {
        String feetStr = heightFeet.getSelectedItem().toString();
        String inchStr = heightInch.getSelectedItem().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy");

        ArrayList<String> dupUsername = new ArrayList<>();
        try {
            femaleGender = findViewById(R.id.femaleChoice);
            maleGender = findViewById(R.id.maleChoice);
            String genderString;

            // Concatenates the birthday spinners into a string
            String birthStr = monthBday.getSelectedItem().toString() + "/" + dayBday.getSelectedItem().toString() + "/" + yearBday.getSelectedItem().toString();

            // Checks the user's gender
            if (femaleGender.isChecked()) {
                genderString = "female";
            } else {
                genderString = "male";
            }

            // Creates file if file does not exist
            String path = getFilesDir().getAbsolutePath();
            File file = new File(path + "/userInfo.txt");
            if(!file.exists()) {
                file.createNewFile();
            }

            // Reads file
            FileInputStream myObj = openFileInput("userInfo.txt");
            InputStreamReader InputRead = new InputStreamReader(myObj);
            BufferedReader bufferedReader = new BufferedReader(InputRead);
            ArrayList<String> list = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                list.add(line);
                dupUsername.add(line.split("@")[1]); //Adds the username to an arrayList that can be used later to check if there are duplicates.
            }
            bufferedReader.close();
            FileOutputStream myWriter = openFileOutput("userInfo.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(myWriter);
            for (int i = 0; i < list.size(); i = i + 1) {
                outputWriter.write(list.get(i));
                outputWriter.write("\n");
            }

            // Checks if birthday is valid
            if (monthBday.getSelectedItem().toString().isEmpty() || dayBday.getSelectedItem().toString().isEmpty() || yearBday.getSelectedItem().toString().isEmpty()) {
                Toast.makeText(getBaseContext(), "Please fill out the Birthday.", Toast.LENGTH_LONG).show();
                outputWriter.close();
            } else {
                String[] monthsWith30Days = {"Apr", "Jun", "Sep", "Nov"};
                int bDay = Integer.parseInt(dayBday.getSelectedItem().toString());
                String bMonth = monthBday.getSelectedItem().toString();
                int bYear = Integer.parseInt(yearBday.getSelectedItem().toString());
                boolean bBool = true;

                if (bMonth.equals("Feb")) {
                    if (bYear % 4 != 0) {
                        if (bDay > 28) {
                            bBool = false;
                        }
                    } else {
                        if (bDay > 29) {
                            bBool = false;
                        }
                    }
                } else if (bDay == 31) {
                    if (contains(monthsWith30Days, bMonth)) {
                        bBool = false;
                    }
                }
                //Make sure that none of the fields are empty.
                //Make sures that the username is not taken.
                if (name.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please fill out the Name.", Toast.LENGTH_LONG).show();
                    outputWriter.close();
                } else if (username.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please fill out the Username.", Toast.LENGTH_LONG).show();
                    outputWriter.close();
                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please fill out the Password.", Toast.LENGTH_LONG).show();
                    outputWriter.close();
                } else if (feetStr.isEmpty() || inchStr.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please fill out the Height.", Toast.LENGTH_LONG).show();
                    outputWriter.close();
                } else if (!femaleGender.isChecked() && !maleGender.isChecked()) {
                    Toast.makeText(getBaseContext(), "Please fill out the Gender.", Toast.LENGTH_LONG).show();
                    outputWriter.close();
                } else if (!bBool) {
                    Toast.makeText(getBaseContext(), "Invalid Birthday.", Toast.LENGTH_LONG).show();
                    outputWriter.close();
                } else if (sdf.parse(birthStr).after(new Date())) {
                    Toast.makeText(getBaseContext(), "Invalid Birthday.", Toast.LENGTH_LONG).show();
                    outputWriter.close();
                } else if (dupUsername.contains(username.getText().toString())) {
                    Toast.makeText(getBaseContext(), "Username taken, please enter new Username.", Toast.LENGTH_LONG).show();
                    outputWriter.close();
                } else if (username.getText().toString().matches("[^A-Za-z0-9]") || name.getText().toString().matches("[^A-Za-z0-9]") || password.getText().toString().matches("[^A-Za-z0-9]")) {
                    Toast.makeText(getBaseContext(), "No special characters allowed in Name, Username, or Password.", Toast.LENGTH_LONG).show();
                } else {
                    int feet = Integer.parseInt(feetStr);
                    int inch = Integer.parseInt(inchStr);
                    String signUpDate = sdf.format(new Date());

                    // Saves the user's information into a string and enter -1 for fields they have not input yet
                    String longStr = name.getText().toString() + "@" + username.getText().toString() + "@" + password.getText().toString() + "@" + (feet * 12 + inch) + "@" + genderString + "@-1" + "@-1" + "@-1" + "@" + signUpDate + "@" + birthStr;
                    person = new Person(longStr);
                    outputWriter.write(longStr);
                    Toast.makeText(getBaseContext(), "Successfully Signed Up!", Toast.LENGTH_LONG).show();
                    outputWriter.close();
                    Intent intent = new Intent(this, secondaryAct.class);
                    intent.putExtra("person", (Parcelable) person);
                    startActivity(intent);
                }
            }
        } catch (IOException e) {
        }
    }

    // Button navigates back to Login Page
    public void toLoginPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*
     * Checks whether the user input a month with 30 days, with the day as the 31st
     */
    private boolean contains (String[] validMonths, String month) {
        for (String i: validMonths) {
            if (i.equals(month)) {
                return true;
            }
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