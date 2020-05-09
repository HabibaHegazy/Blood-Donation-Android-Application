package com.example.blood_donation_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.blood_donation_project.Fragments.BloodBank_panel.BloodBankMainViewFragment;
import com.example.blood_donation_project.Fragments.BloodBank_panel.BloodBankStockFragment;
import com.example.blood_donation_project.Fragments.BloodBank_panel.TimingSlotsControlFragment;
import com.example.blood_donation_project.Fragments.LoginFragment;
import com.example.blood_donation_project.Models.BloodBanks;
import com.example.blood_donation_project.Util.Constants;
import com.example.blood_donation_project.Util.CustomDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class BloodBankActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private Fragment BloodBankMainViewFragment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private BloodBanks bloodBanks = new BloodBanks();
    private NavigationView navigationView;
    private String uid;
    //private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_bank);

        this.uid = getIntent().getStringExtra("uid");
        getBloodBankData();

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        navigationView = findViewById(R.id.nav_view);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BloodBankMainViewFragment = new BloodBankMainViewFragment();
        this.replaceFragment(BloodBankMainViewFragment, Constants.MainView_Fragment, false);

    }

    public void replaceFragment(Fragment fragment, String fragment_tag, Boolean animate){

        if(fragment != null){

            FragmentManager fm = getSupportFragmentManager();

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
        menuInflater.inflate(R.menu.search_menu, menu);

        //searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        //SearchManager searchManager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.appointments_list_item:
                this.replaceFragment(new BloodBankMainViewFragment(), Constants.MainView_Fragment, false);
                break;
            case R.id.blood_bank_stock_item:
                this.replaceFragment(new BloodBankStockFragment(), Constants.BloodBankStock_Fragment, false);
                break;
            case R.id.bb_timing_slots_item:
                this.replaceFragment(new TimingSlotsControlFragment(), Constants.TimingSlotsControl_Fragment, false);
                break;
            case R.id.emergency_request_item:
                CustomDialog customDialogEmergencyRequest = new CustomDialog(2, bloodBanks);
                customDialogEmergencyRequest.show(getSupportFragmentManager(), "Emergency Request");
                break;
            case R.id.change_email_item:
                CustomDialog customDialogEmail = new CustomDialog(0);
                customDialogEmail.show(getSupportFragmentManager(), "Change Email Address");
                break;
            case R.id.change_password_item:
                CustomDialog customDialogPassword = new CustomDialog(1);
                customDialogPassword.show(getSupportFragmentManager(), "Change Password");
                break;
            case R.id.signout_item:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getBloodBankData(){

        MainActivity.databaseReference.child("tb_blood_Banks").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> item = dataSnapshot.getChildren().iterator();

                while (item.hasNext()) {

                    DataSnapshot item2 = item.next();

                        if (item2.child("uid").getValue().toString().equals(uid)) {

                            bloodBanks.setName(item2.getKey());
                            Constants.BloodBankNameForTimingSlots = item2.getKey();
                            bloodBanks.setLatitude(Double.valueOf(item2.child("Latitude").getValue().toString()));
                            bloodBanks.setLongtitude(Double.valueOf(item2.child("Longtitude").getValue().toString()));
                            bloodBanks.setDetail_location_description(item2.child("detail_location_description").getValue().toString());
                            bloodBanks.setNumber(item2.child("number").getValue().toString());
                            bloodBanks.setWorking_hours(item2.child("working_hours").getValue().toString());

                        }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

    }
}
