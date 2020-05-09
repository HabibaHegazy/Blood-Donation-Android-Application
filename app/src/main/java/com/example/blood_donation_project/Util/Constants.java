package com.example.blood_donation_project.Util;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Constants {

    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    public static final int PERMISSIONS_REQUEST_SEND_SMS =0;

    public static String BloodBankNameForTimingSlots = null;
    public static Boolean clickedTestingSucess = null;

    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    //Fragments Tags
    public static final String Login_Fragment = "Login";
    public static final String Register_Fragment = "Register";
    public static final String ForgotPassword_Fragment = "Forgot Password";
    public static final String DonorProfile_Fragment = "Your Profile";
    public static final String DonationHistory_Fragment = "Donation History Record";
    public static final String Donation_Fragment = "Donation Appointment";
    public static final String SearchMap_Fragment = "Search Blood Bank";
    public static final String TimingSlotsControl_Fragment = "Timing Slots";
    public static final String AddAdmin_Fragment = "Add Admin";
    public static final String ViewAdmins_Fragment = "View Admins";
    public static final String EditAdmin_Fragment = "Edit Admin";
    public static final String MainView_Fragment = "Main Menu";
    public static final String BloodBankStock_Fragment = "Blood Bank Stock";
    public static final String BloodBankAddBank = "Add New Blood Bank";
    public static final String BloodBankView = "Blood Banks";

    public static AlertDialog AlertMsg(Context context, String title, String msg){

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.project_logo);

        return alertDialog;
    }

    public static void showDatePickerDialog (Context context, DatePickerDialog.OnDateSetListener listener, Boolean appointment) {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, listener, year, month, day);

        if(appointment){
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + (1000 * 60 * 60 * 24)); // minimum date that can be reserved (Tomorrow)
            //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 30)); // maximum date that can be reserved (a month from now)
        }


        datePickerDialog.show();
    }

    public static String createVisualDate(int year, int month, int day){

        String mDay;
        String mMonth = MainActivity.months.get(month);

        if(day < 10)
            mDay = "0"+day;
        else
            mDay = String.valueOf(day);

        return (mMonth + ", " + mDay + " " + year);

    }

    public static String createVisualDate(String date){

        String[] dateNumbers = date.split("/");

        int day = Integer.parseInt(dateNumbers[0]);
        int month = Integer.parseInt(dateNumbers[1]);
        int year = Integer.parseInt(dateNumbers[2]);

        String mDay;
        String mMonth = MainActivity.months.get(month);

        if(day < 10)
            mDay = "0"+day;
        else
            mDay = String.valueOf(day);

        return (mMonth + ", " + mDay + " " + year);

    }

    public static void showActionBar(String title, FragmentActivity context){
        AppCompatActivity activity = (AppCompatActivity) context;
        if (activity != null) {
            ((MainActivity) context).setActionBarTitle(title);
            activity.getSupportActionBar().show();
        }
    }

    public static void hideActionBar(FragmentActivity context){
        AppCompatActivity activity = (AppCompatActivity) context;
        if (activity != null) {
            activity.getSupportActionBar().hide();
        }
    }

    public static void hide_soft_keyboard(View view, Context context){
        final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getDay(String date){

        Calendar c = Calendar.getInstance();
        Date theDate = null;
        try {
            theDate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(theDate);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK); // 1 -- Sunday

        switch (dayOfWeek){
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursdsy";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
        }

        return null;

    }


}
