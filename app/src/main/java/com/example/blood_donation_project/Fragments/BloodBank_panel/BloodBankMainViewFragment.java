package com.example.blood_donation_project.Fragments.BloodBank_panel;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.blood_donation_project.Adaptors.BloodBankAppointmentsRecyclerViewAdapter;
import com.example.blood_donation_project.Adaptors.TimingSlotsRecyclerViewAdaptor;
import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.Appointments;
import com.example.blood_donation_project.Models.Donor;
import com.example.blood_donation_project.Models.Slots;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class BloodBankMainViewFragment extends Fragment {

    private List<Appointments> appointmentsList = new ArrayList<>();

    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    View view;


    public BloodBankMainViewFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blood_bank_main_view, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_appointments);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        progressDialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);

        getAppointmentsPerBB();


        return view;
    }

    private void getAppointmentsPerBB(){
        MainActivity.databaseReference.child("tb_appointments").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                appointmentsList.clear();

                while (items.hasNext()) {
                    DataSnapshot item = items.next();

                    final Appointments appointment = item.getValue(Appointments.class);
                    appointment.setAppointment_id(item.getKey());

                        Date todayDate = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                        String todayDatetxt = formatter.format(todayDate);

                    Date appDate = null;

                    try {
                        appDate = new SimpleDateFormat("dd/MM/yyyy").parse(appointment.getAppointment_date());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String appDatetxt = formatter.format(appDate);

                        if(todayDatetxt.equals(appDatetxt)){

                            MainActivity.databaseReference.child("tb_slots").child(appointment.getSlot_reserved()).addValueEventListener( new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Slots slot = dataSnapshot.getValue(Slots.class);
                                    appointment.setSlot_reserved_data(slot);
                                    appointmentsList.add(appointment);
                                    adapter = new BloodBankAppointmentsRecyclerViewAdapter(getActivity(), appointmentsList);
                                    recyclerView.setAdapter(adapter);
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
