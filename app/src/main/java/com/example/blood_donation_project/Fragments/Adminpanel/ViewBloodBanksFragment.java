package com.example.blood_donation_project.Fragments.Adminpanel;


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

import com.anychart.core.annotations.Line;
import com.example.blood_donation_project.Adaptors.BloodBanksRecyclerViewAdapter;
import com.example.blood_donation_project.Adaptors.DonorHistoryRecyclerViewAdapter;
import com.example.blood_donation_project.Fragments.Admin_panel.AdminMainViewFragment;
import com.example.blood_donation_project.Fragments.Donor_panel.DonorMainViewFragment;
import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.Appointments;
import com.example.blood_donation_project.Models.BloodBanks;
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

/**
 * A simple {@link Fragment} subclass.
 */

public class ViewBloodBanksFragment extends Fragment {


    private List<BloodBanks> bloodBanksList = new ArrayList<>();

    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private View view;

    private List<String> editInformations;
    public ViewBloodBanksFragment(List<String> editInformations) {
        this.editInformations = editInformations;
    }
    public ViewBloodBanksFragment() {

        editInformations = new ArrayList<>(2);
        for(int i =0; i< 2; i++)
            editInformations.add("");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view =  inflater.inflate(R.layout.activity_blood_bank_recycler_view, container, false);

        progressDialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);

        getBanks();

        /*RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.bloodBankRecyclerView);

        BloodBanksRecyclerViewAdapter adapter = new BloodBanksRecyclerViewAdapter();

        recyclerView2.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView2.setLayoutManager(layoutManager);
*/

        return view;


    }

    public void getBanks(){
        MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();

        MainActivity.databaseReference.child("tb_blood_Banks").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                Boolean empty = true;

                while (items.hasNext()) {
                    empty = false;

                    DataSnapshot item = items.next();

                    BloodBanks bloodBanks = new BloodBanks();// = item.getValue(BloodBanks.class);

                    bloodBanks.setName(item.getKey());
                    bloodBanks.setNumber(item.child("number").getValue().toString());
                    bloodBanks.setDetail_location_description(item.child("detail_location_description").getValue().toString());

                    bloodBanksList.add(bloodBanks);

                    recyclerView = view.findViewById(R.id.bloodBankRecyclerView);
                    layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);


                }

                adapter = new BloodBanksRecyclerViewAdapter(bloodBanksList);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();


                if (empty) {
                    progressDialog.dismiss();
                    AlertDialog alertDialog = Constants.AlertMsg(getActivity(), "Alert", "There is not banks");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) getActivity()).replaceFragment(new AdminMainViewFragment(), Constants.MainView_Fragment, false);
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
