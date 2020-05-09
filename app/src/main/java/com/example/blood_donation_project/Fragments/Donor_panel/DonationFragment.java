package com.example.blood_donation_project.Fragments.Donor_panel;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.Appointments;
import com.example.blood_donation_project.Models.BloodBanks;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DonationFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener, DatePickerDialog.OnDateSetListener{

    private AutoCompleteTextView bloodBankEt;
    private Button gobtn, appointmentBtn;
    private Spinner spinner;

    private GoogleMap googleMap;
    private MapView mapView;

    private ProgressDialog dialog;

    private Map<String, BloodBanks> bloodBanks_map = new HashMap<>();
    private Map<String, String> slots = new HashMap<>();

    private ArrayAdapter<String> spinnerDataAdapter;

    private List<String> timingList = new ArrayList<>();

    private String choosenBloodBank = null, appointmentDate = null, choosenSlot = "", selectedDay;

    private List<String> editInformations;

    public DonationFragment(List<String> editInformations) {
        this.editInformations = editInformations;
    }

    public DonationFragment() {
        editInformations = new ArrayList<>(5);
        for(int i =0; i< 6; i++)
            editInformations.add("");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.showActionBar(Constants.Donation_Fragment, getActivity());
        MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donation, container, false);

        initViews(view);
        initGoogleMap(savedInstanceState);
        setListeners();

        spinnerDataAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, timingList);
        spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerDataAdapter);

        return view;
    }

    private void initViews(View view){
        bloodBankEt = view.findViewById(R.id.bloodBankET);
        gobtn = view.findViewById(R.id.goBtn);
        appointmentBtn = view.findViewById(R.id.appointmentDateBtn);
        spinner = view.findViewById(R.id.slotsSpinner);
        mapView = view.findViewById(R.id.map_view);
    }

    private void setListeners(){
        gobtn.setOnClickListener(this);
        appointmentBtn.setOnClickListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choosenSlot = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        bloodBankEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                BloodBanks b_bank = bloodBanks_map.get(s.toString().toLowerCase());
                if(b_bank != null){
                    actionOnEditText(b_bank);
                    choosenBloodBank = b_bank.getName();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void initGoogleMap(Bundle savedInstanceState) {

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    private void getBloodBanks(){
        MainActivity.databaseReference.child("tb_blood_Banks").addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                List<String> bloodBanksListName = new ArrayList<>();

                while (items.hasNext()) {
                    DataSnapshot item = items.next();

                    String BloodBankName = item.getKey();

                    BloodBanks bloodBanks = item.getValue(BloodBanks.class);
                    bloodBanks.setName(BloodBankName);

                    bloodBanks_map.put(bloodBanks.getName().toLowerCase(), bloodBanks);
                    bloodBanksListName.add(BloodBankName.toLowerCase());

                    LatLng position = new LatLng(bloodBanks.getLatitude(),bloodBanks.getLongtitude());

                    if(BloodBankName.equals(editInformations.get(0))){
                        googleMap.addMarker(new MarkerOptions().position(position).title(BloodBankName)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 13f));
                    }
                    else
                        googleMap.addMarker(new MarkerOptions().position(position).title(bloodBanks.getName()));

                }

                ArrayAdapter<String> blooadBankAdaptor = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, bloodBanksListName);
                bloodBankEt.setAdapter(blooadBankAdaptor);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {}
        });


    }

    private void gettingSlots(final String bloodBank){

        MainActivity.databaseReference.child("tb_slots").addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();

                timingList.clear();
                slots.clear();

                spinner.setEnabled(true);
                spinner.setClickable(true);

                boolean firstSlot = true;

                while (items.hasNext()) {

                    DataSnapshot item = items.next();

                    if (item.child("blood_bank_name").getValue().toString().equals(bloodBank)) {

                        if(bloodBank.equals(editInformations.get(0)) && firstSlot){
                            timingList.add(editInformations.get(3));
                            slots.put(editInformations.get(3), editInformations.get(4));
                            firstSlot = false;
                        }
                        else if (firstSlot){
                            timingList.add("Please Choose a Slot");
                            choosenSlot = "Please Choose a Slot";
                            firstSlot = false;
                        }

                        if(item.child("slot_day").getValue().toString().equals(selectedDay)){
                            String timing = item.child("timing").getValue().toString();
                            System.out.println(timing);
                            //timingList.add(timing);
                            slots.put(timing, item.getKey());
                        }
                    }
                    else{
                        if (firstSlot){
                            timingList.add("No slots avaliable");
                            choosenSlot = "No slots avaliable";
                            firstSlot = false;
                            spinner.setEnabled(false);
                            spinner.setClickable(false);
                        }
                    }
                }

                getRemoveSlotsReserved();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {}
        });
    }

    private void getRemoveSlotsReserved(){
        MainActivity.databaseReference.child("tb_appointments").addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){ // if appointment exists
                    Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();

                    while (items.hasNext()) {
                        DataSnapshot item = items.next();

                        String slotFromFirebase = item.child("slot_reserved").getValue().toString();

                        if(!slotFromFirebase.equals(editInformations.get(4))){

                            if(slots.containsValue(item.child("slot_reserved").getValue().toString())){

                                if(item.child("appointment_date").getValue().toString().equals(appointmentDate)){

                                    Iterator<Map.Entry<String,String>>  iterator = slots.entrySet().iterator();
                                    Map.Entry<String,String> entry;

                                    while (iterator.hasNext()){
                                        entry = iterator.next();
                                        if (entry.getValue().equals(item.child("slot_reserved").getValue().toString()))
                                            iterator.remove(); // instead of slots.remove(entry.getKey()); << caused java.util.concurrentmodificationexception
                                    }
                                }
                            }
                        }
                    }
                }

                Iterator iterator = slots.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry me2 = (Map.Entry) iterator.next();
                    timingList.add(me2.getKey().toString());
                }

                if (slots.size() == 0) {
                    timingList.clear();
                    timingList.add("No slots avaliable");
                    choosenSlot = "No slots avaliable";
                    spinner.setEnabled(false);
                    spinner.setClickable(false);
                }

                spinnerDataAdapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void actionOnEditText(BloodBanks b_bank){
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(b_bank.getLatitude(), b_bank.getLongtitude()), 17f));
        Constants.hide_soft_keyboard(getView(), getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(Constants.MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if(marker.getTitle().equals(editInformations.get(0))){
            String visualDate = Constants.createVisualDate(editInformations.get(1));
            appointmentBtn.setText(visualDate);
        }
        else
            appointmentBtn.setText("Choose Your Appointment");

        bloodBankEt.setText(marker.getTitle());
        choosenBloodBank = marker.getTitle();

        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);

        getBloodBanks();

        if(!editInformations.get(0).equals("")){

            gobtn.setText("Edit");

            bloodBankEt.setText(editInformations.get(0));
            gettingSlots(editInformations.get(0));
            spinnerDataAdapter.notifyDataSetChanged();

            String visualDate = Constants.createVisualDate(editInformations.get(1));
            appointmentBtn.setText(visualDate);
            selectedDay = editInformations.get(2);
        }

        this.googleMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.appointmentDateBtn:

                if(bloodBankEt.getText().toString().equals("")){
                    AlertDialog alertDialog = Constants.AlertMsg(getActivity(), "Alert Error", "You must choose a Blood Bank you can either:\n " + "1- Type the blood bank  OR\n " + "2- Select a blood bank using the map pins");
                    alertDialog.show();
                }
                else if(!bloodBanks_map.containsKey(bloodBankEt.getText().toString().toLowerCase())){
                    AlertDialog alertDialog = Constants.AlertMsg(getActivity(), "Alert Error", "this institution is unavailable you can select a blood bank using the map pins");
                    alertDialog.show();
                }else{
                    choosenBloodBank = bloodBankEt.getText().toString();
                    actionOnEditText(bloodBanks_map.get(bloodBankEt.getText().toString().toLowerCase()));
                    Constants.showDatePickerDialog(getContext(), this, true);
                }

                break;
            case R.id.goBtn:

                if (choosenSlot.equals("Please Choose a Slot")) {
                    AlertDialog alertDialog = Constants.AlertMsg(getActivity(), "Alert Error", "Please Choose a Time Slot");
                    alertDialog.show();

                } else if (choosenSlot.equals("No slots avaliable")) {
                    AlertDialog alertDialog = Constants.AlertMsg(getActivity(), "Alert Error", "No slots avaliable please choose another date or blood bank");
                    alertDialog.show();

                } else if(appointmentBtn.getText() == "Choose Your Appointment" || appointmentDate == null || choosenSlot.equals("") || choosenSlot == null){
                    AlertDialog alertDialog = Constants.AlertMsg(getActivity(), "Alert Error", "Please Choose an Appointment Date");
                    alertDialog.show();
                }

                else{

                    dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            ((MainActivity) getActivity()).replaceFragment(new DonationHistoryFragment(), Constants.DonationHistory_Fragment, false);
                        }
                    }, 6000);

                    switch (gobtn.getText().toString()){
                        case "Edit":
                            if(appointmentDate != null){
                                if(!appointmentDate.equals(editInformations.get(2))){
                                    MainActivity.databaseReference.child("tb_appointments").child(editInformations.get(5)).child("appointment_date").setValue(appointmentDate);
                                }
                            }
                            else if(choosenSlot != null) {
                                MainActivity.databaseReference.child("tb_appointments").child(editInformations.get(5)).child("slot_reserved").setValue(slots.get(choosenSlot));
                            }

                            AlertDialog alertDialog = Constants.AlertMsg(getActivity(), "", "Profile Modification is made successfully");
                            alertDialog.show();
                            break;
                        case "GO":
                            // this is what was choosen from map
                            //BloodBanks bloodBanks = bloodBanks_map.get(choosenBloodBank.toLowerCase());

                            Appointments appointments = new Appointments(MainActivity.firebaseAuth.getCurrentUser().getUid(), slots.get(choosenSlot), appointmentDate, "reserved");
                            String key = MainActivity.databaseReference.child("tb_appointments").push().getKey();
                            MainActivity.databaseReference.child("tb_appointments").child(key).setValue(appointments);

                            break;
                    }




                 }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        appointmentDate = day + "/" + (month+1) + "/" + year;
        String visualDate = Constants.createVisualDate(year, (month+1), day);
        appointmentBtn.setText(visualDate);
        selectedDay = Constants.getDay(appointmentDate);
        gettingSlots(choosenBloodBank);
    }
}
