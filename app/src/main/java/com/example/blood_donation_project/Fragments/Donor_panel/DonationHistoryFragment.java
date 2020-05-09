package com.example.blood_donation_project.Fragments.Donor_panel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blood_donation_project.Adaptors.DonorHistoryRecyclerViewAdapter;
import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.Appointments;
import com.example.blood_donation_project.Models.Slots;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DonationHistoryFragment extends Fragment {

    private List<Appointments> appointmentsList = new ArrayList<>();

    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private View view;

    public DonationHistoryFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.showActionBar(Constants.DonationHistory_Fragment, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_donation_history, container, false);

        progressDialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);

        getHistoryRecords();

        return view;
    }

    private void getHistoryRecords(){

        MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();

        MainActivity.databaseReference.child("tb_appointments").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                Boolean empty = true;

                while (items.hasNext()) {

                    empty = false;

                    final DataSnapshot item = items.next();

                    String userID = FirebaseAuth.getInstance().getUid();

                    if(userID.equals(item.child("donor_id").getValue().toString())){

                        MainActivity.databaseReference.child("tb_slots").addValueEventListener( new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterator<DataSnapshot> items2 = dataSnapshot.getChildren().iterator();

                                while (items2.hasNext()) {

                                    DataSnapshot item2 = items2.next();

                                    if (item2.getKey().equals(item.child("slot_reserved").getValue().toString())) {

                                        Appointments appointment = item.getValue(Appointments.class);

                                        Slots slots = item2.getValue(Slots.class);

                                        appointment.setAppointment_id(item.getKey());
                                        appointment.setSlot_reserved_data(slots);

                                        appointmentsList.add(appointment);

                                        recyclerView = view.findViewById(R.id.recyclerView);
                                        layoutManager = new LinearLayoutManager(getActivity());
                                        recyclerView.setLayoutManager(layoutManager);
                                    }
                                }

                                adapter = new DonorHistoryRecyclerViewAdapter(appointmentsList);
                                recyclerView.setAdapter(adapter);
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                            {}

                        });
                    }
                }

                if(empty){
                    progressDialog.dismiss();
                    AlertDialog alertDialog = Constants.AlertMsg(getActivity(), "Alert", "There is not previous history");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) getActivity()).replaceFragment(new DonorMainViewFragment(), Constants.MainView_Fragment, false);
                        }
                    });
                    alertDialog.show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });



    }

}
