package com.example.blood_donation_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.blood_donation_project.Fragments.LoginFragment;
import com.example.blood_donation_project.Util.Constants;
import com.example.blood_donation_project.Util.CustomDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.net.InetAddress;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Fragment loginFragment;
    public static Fragment privouseFragment;
    public static String privouseFragment_tag;

    public static Boolean lunchedApp = false;
    public static FirebaseAuth firebaseAuth;
    public static DatabaseReference databaseReference;

    public static HashMap<Integer,String> months = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if((isNetworkConnected() && isWifiAvailable()) || is3GAvailable()){

            firebaseAuth = FirebaseAuth.getInstance();

            loginFragment = new LoginFragment();
            this.replaceFragment(loginFragment, Constants.Login_Fragment, false);

            months.put(1, "Jan");
            months.put(2, "Feb");
            months.put(3, "Mar");
            months.put(4, "Apr");
            months.put(5, "May");
            months.put(6, "Jun");
            months.put(7, "Jul");
            months.put(8, "Aug");
            months.put(9, "Sep");
            months.put(10, "Oct");
            months.put(11, "Nov");
            months.put(12, "Dec");

        }else{
            String msg = "No Wifi nor 3G is connected to the application, Would you like to open the wifi settings?";
            AlertDialog alertDialog = Constants.AlertMsg(MainActivity.this, "Alert", msg);

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                }
            });

            alertDialog.show();
        }


    }

    public void replaceFragment(Fragment fragment, String fragment_tag, Boolean animate){

        if(fragment != null){

            FragmentManager fm = getSupportFragmentManager();
            //fm.popBackStack();

            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack(null);

            if(animate){
                ft.setCustomAnimations(R.anim.right_enter, R.anim.left_out);
            }

            ft.replace(R.id.fragment_container, fragment, fragment_tag);
            getSupportActionBar().setTitle(fragment_tag);
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.signOutBtn:
                FirebaseAuth.getInstance().signOut();
                this.replaceFragment(loginFragment, Constants.Login_Fragment, true);
                break;
            case R.id.backBtn:
                this.replaceFragment(privouseFragment, privouseFragment_tag, true);
                this.setActionBarTitle(privouseFragment_tag);
                break;
            case R.id.emailSettingsBtn:
                CustomDialog customDialogEmail = new CustomDialog(0);
                customDialogEmail.show(getSupportFragmentManager(), "Change Email Address");
                break;
            case R.id.passwordSettingsBtn:
                CustomDialog customDialogPassword = new CustomDialog(1);
                customDialogPassword.show(getSupportFragmentManager(), "Change Password");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void setActionBarDrawable() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean isWifiAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean is3GAvailable(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        return manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
