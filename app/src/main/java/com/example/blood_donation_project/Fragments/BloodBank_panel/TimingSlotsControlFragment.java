package com.example.blood_donation_project.Fragments.BloodBank_panel;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.blood_donation_project.Adaptors.DonorHistoryRecyclerViewAdapter;
import com.example.blood_donation_project.Adaptors.TimingSlotsRecyclerViewAdaptor;
import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.Appointments;
import com.example.blood_donation_project.Models.BloodBanks;
import com.example.blood_donation_project.Models.Slots;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.example.blood_donation_project.Util.CustomDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TimingSlotsControlFragment extends Fragment {

    private List<Slots> slotsList = new ArrayList<>();

    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private Button addTimingSlotBtn;


    private View view;

    public TimingSlotsControlFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_timing_slots_control, container, false);

        progressDialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);

        recyclerView = view.findViewById(R.id.recyclerView_timingSlots);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        getSlotsPerBB();

        addTimingSlotBtn = view.findViewById(R.id.addTimingSlotBtn);
        addTimingSlotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialogTimingSlot = new CustomDialog(4);
                customDialogTimingSlot.show(getActivity().getSupportFragmentManager(), "Add Timing Slot");
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.search_menu, menu);

    }

    private void getSlotsPerBB() {

        MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();

        MainActivity.databaseReference.child("tb_slots").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                slotsList.clear();

                while (items.hasNext()) {

                    DataSnapshot item = items.next();

                    if (item.child("blood_bank_name").getValue().toString().equals(Constants.BloodBankNameForTimingSlots)) {
                        Slots slot = item.getValue(Slots.class);
                        slot.setSlotID(item.getKey());
                        slotsList.add(slot);
                    }

                }

                adapter = new TimingSlotsRecyclerViewAdaptor(getActivity(), slotsList);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
