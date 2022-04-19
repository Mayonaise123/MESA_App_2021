package com.example.mesaapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/*
 * Fills in the user's information when he/she signs in or signs up
 */
public class Person implements Parcelable {
    // Global Variables
    long age;
    int height;
    String gender;
    String username;
    String name;
    String password;
    String signUpDateStr;
    Date signUpDate;
    String birthdayStr;
    Date birthday;
    String localLongStr;
    String[] splitLongStr;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    /*
     * Gathers all the user's information they put in
     */
    public Person(String longStr) throws ParseException {
        splitLongStr = longStr.split("@");
        String[] splitDay = splitLongStr[8].split("/");
        String[] splitBirth = splitLongStr[9].split("/");
        name = splitLongStr[0];
        username = splitLongStr[1];
        password = splitLongStr[2];
        height = Integer.parseInt(splitLongStr[3]);
        gender = splitLongStr[4];
        signUpDate = sdf.parse(determineMonth(splitDay[0]) + "/" + splitDay[1] + "/" + splitDay[2]);
        birthday = sdf.parse(determineMonth(splitBirth[0]) + "/" + splitBirth[1] + "/" + splitBirth[2]);
        signUpDateStr = sdf.format(signUpDate);
        birthdayStr = sdf.format(birthday);
        age = findAge();
        localLongStr = longStr;
    }

    protected Person(Parcel in) {
        age = in.readLong();
        height = in.readInt();
        gender = in.readString();
        username = in.readString();
        name = in.readString();
        password = in.readString();
        localLongStr = in.readString();
        splitLongStr = in.createStringArray();
        birthdayStr = in.readString();
        signUpDateStr = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    /*
     * Saves the data with the date and input data
     */
    public String writeToTxt(String category, String value) {
        String saveDataDate = sdf.format(new Date());
        String tempLongStr = splitLongStr[0];
        String tempStr = "";
        int change = 0;

        // Checks if the category is calories
        if (category.equals("cal")) {
            tempStr = splitLongStr[5];

            // If the user has never inputted data, change the -1 to empty
            if (tempStr.equals("-1")) {
                tempStr = "";
            }

            // If the user already input data for that day, add the data with ",," and date
            // If the user puts in data for a new day, add the data with ",,," and date
            if (!tempStr.contains(saveDataDate)) {
                if (tempStr.isEmpty()) {
                    tempStr = saveDataDate + "," + value;
                } else {
                    tempStr = tempStr + ",,," + saveDataDate + "," + value;
                }
            } else {
                tempStr = tempStr + ",," + value;
            }
            change = 5;

            // Checks the category is sleep
        } else if (category.equals("sleep")) {
            tempStr = splitLongStr[6];

            // Checks if the user had never inputted data, change the -1 to empty
            if (tempStr.equals("-1")) {
                tempStr = "";
            }

            // User can only input 1 data per day
            // Add the data with date
            tempStr = tempStr + saveDataDate + "," + value + ",,,";
            change = 6;

            // Checks the category is weight
        } else if (category.equals("weight")) {
            tempStr = splitLongStr[7];

            // Checks if the user had never inputted data, change the -1 to empty
            if (tempStr.equals("-1")) {
                tempStr = "";
            }

            // User can only input 1 data per day
            // Add the data with date
            tempStr = tempStr + saveDataDate + "," + value + ",,,";
            change = 7;

            // Checks the category is update height
        } else if (category.equals("update_height")) {

            // Update the old data
            tempStr = value;
            height = Integer.parseInt(value);
            change = 3;

            // Checks the category is update password
        } else if (category.equals("update_password")) {

            // Update the old data
            tempStr = value;
            password = value;
            change = 2;
        }

        // Re-assembles the text file line with the new data
        for (int i = 1; i < splitLongStr.length; i = i + 1) {
            if (i == change) {
                tempLongStr = tempLongStr + "@" + tempStr;
            } else {
                tempLongStr = tempLongStr + "@" + splitLongStr[i];
            }
        }
        localLongStr = tempLongStr;
        splitLongStr = localLongStr.split("@");
        return tempLongStr;
    }

    /*
     * Uses the user's birthday to find their age
     */
    public long findAge() {
        Date today = new Date();
        return (today.getTime() - birthday.getTime()) / DateUtils.YEAR_IN_MILLIS;
    }

    /*
     * Translates the chosen string months into int months
     */
    private String determineMonth(String monthBday) {
        if (monthBday.equals("Jan")) {
            return "01";
        } else if (monthBday.equals("Feb")) {
            return "02";
        } else if (monthBday.equals("Mar")) {
            return "03";
        } else if (monthBday.equals("Apr")) {
            return "04";
        } else if (monthBday.equals("May")) {
            return "05";
        } else if (monthBday.equals("Jun")) {
            return "06";
        } else if (monthBday.equals("Jul")) {
            return "07";
        } else if (monthBday.equals("Au")) {
            return "08";
        } else if (monthBday.equals("Sep")) {
            return "09";
        } else if (monthBday.equals("Oct")) {
            return "10";
        } else if (monthBday.equals("Nov")) {
            return "11";
        } else {
            return "12";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * Transfers these items in person class to another page
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(age);
        dest.writeInt(height);
        dest.writeString(gender);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(password);
        dest.writeString(localLongStr);
        dest.writeStringArray(splitLongStr);
        dest.writeString(birthdayStr);
        dest.writeString(signUpDateStr);
    }
}
