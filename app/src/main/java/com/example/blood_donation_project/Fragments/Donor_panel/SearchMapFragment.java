package com.example.blood_donation_project.Fragments.Donor_panel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.BloodBanks;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
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

public class SearchMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {

    private GoogleMap googleMap;
    private MapView mapView;
    private AutoCompleteTextView searchEditText;

    private Map<String, BloodBanks> bloodBanks_map = new HashMap<>();

    public SearchMapFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.showActionBar(Constants.SearchMap_Fragment, getActivity());
        MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_map, container, false);

        initViews(view);
        initGoogleMap(savedInstanceState);
        setListeners();

        return view;
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
    public boolean onMarkerClick(final Marker marker) {

        BloodBanks bloodBanks = bloodBanks_map.get(marker.getTitle());

        String msg = "Phone number: " + bloodBanks.getNumber() + "\n\n" +
                "detail location description: " + bloodBanks.getDetail_location_description() + "\n\n" +
                "Working Hours: " + bloodBanks.getWorking_hours();
        AlertDialog alertDialog = Constants.AlertMsg(getActivity(), marker.getTitle(), msg);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final LatLng latLng = marker.getPosition();

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Get Directions", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String url = "http://maps.google.com/maps?daddr=" + latLng.latitude + "," + latLng.longitude;
                Intent googleAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(googleAppIntent);
            }
        });

        alertDialog.show();
        return false;

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        MainActivity.databaseReference.child("tb_blood_Banks").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                List<String> bloodBanksListName = new ArrayList<>();

                while (items.hasNext()) {
                    DataSnapshot item = items.next();

                    final String markerTitle = item.getKey().toLowerCase(); //id
                    final Double longtitude = Double.valueOf(item.child("Longtitude").getValue().toString());
                    final Double latitude = Double.valueOf(item.child("Latitude").getValue().toString());
                    String phone_number = item.child("number").getValue().toString();
                    String detail_location_description = item.child("detail_location_description").getValue().toString();
                    String working_hours = item.child("working_hours").getValue().toString();

                    LatLng markerPosition = new LatLng(latitude, longtitude);

                    // create marker
                    googleMap.addMarker(new MarkerOptions().position(markerPosition).title(markerTitle));

                    bloodBanksListName.add(markerTitle);

                    BloodBanks bloodBanks = item.getValue(BloodBanks.class);
                    bloodBanks_map.put(markerTitle, bloodBanks);
                }

                ArrayAdapter<String> searchAdaptor = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, bloodBanksListName);
                searchEditText.setAdapter(searchAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        this.googleMap.setOnMarkerClickListener(this);
    }

    private void initViews(View view){
        mapView = view.findViewById(R.id.map_view);
        searchEditText = view.findViewById(R.id.searchET);
    }

    private void setListeners(){
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                BloodBanks b_bank = bloodBanks_map.get(s.toString().toLowerCase());
                if(b_bank != null){
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(b_bank.getLatitude(), b_bank.getLongtitude()), 17f));
                    Constants.hide_soft_keyboard(getView(), getActivity());
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
}
