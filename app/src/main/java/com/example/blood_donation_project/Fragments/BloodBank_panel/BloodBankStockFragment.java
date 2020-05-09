package com.example.blood_donation_project.Fragments.BloodBank_panel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BloodBankStockFragment extends Fragment {

    AnyChartView chartView;
    String [] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    Integer [] bloodTypesAmounts = new Integer[8];
    int refreshCounter = 0;

    public BloodBankStockFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood_bank_stock, container, false);

        initalize(view);
        setupPieChart();

        return view;
    }

    private void initalize(View view){
        chartView = view.findViewById(R.id.stock_chart);
        final DatabaseReference reference = MainActivity.databaseReference.child("tb_stocks").child(Constants.BloodBankNameForTimingSlots);

        Button aplusBtn = view.findViewById(R.id.usedAplusBtn);
        aplusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --bloodTypesAmounts[0];

                if(bloodTypesAmounts[0] < 0){
                    reference.child("A+").setValue(0);
                }else{
                    reference.child("A+").setValue(bloodTypesAmounts[0]);
                }
            }
        });
        Button anegBtn = view.findViewById(R.id.usedAnegBtn);
        anegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --bloodTypesAmounts[1];

                if(bloodTypesAmounts[1] < 0){
                    reference.child("A-").setValue(0);
                }else{
                    reference.child("A-").setValue(bloodTypesAmounts[1]);
                }
            }
        });

        Button bplusBtn = view.findViewById(R.id.usedBplusBtn);
        bplusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --bloodTypesAmounts[2];

                if(bloodTypesAmounts[2] < 0){
                    reference.child("B+").setValue(0);
                }else{
                    reference.child("B+").setValue(bloodTypesAmounts[2]);
                }
            }
        });
        Button bnegBtn = view.findViewById(R.id.usedBnegsBtn);
        bnegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --bloodTypesAmounts[3];

                if(bloodTypesAmounts[3] < 0){
                    reference.child("B-").setValue(0);
                }else{
                    reference.child("B-").setValue(bloodTypesAmounts[3]);
                }
            }
        });

        Button abplusBtn = view.findViewById(R.id.usedABplusBtn);
        abplusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --bloodTypesAmounts[4];

                if(bloodTypesAmounts[4] < 0){
                    reference.child("AB+").setValue(0);
                }else{
                    reference.child("AB+").setValue(bloodTypesAmounts[4]);
                }
            }
        });
        Button abnegBtn = view.findViewById(R.id.usedABnegBtn);
        abnegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --bloodTypesAmounts[5];

                if(bloodTypesAmounts[5] < 0){
                    reference.child("AB-").setValue(0);
                }else{
                    reference.child("AB-").setValue(bloodTypesAmounts[5]);
                }

            }
        });

        Button oplusBtn = view.findViewById(R.id.usedOplusBtn);
        oplusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --bloodTypesAmounts[6];

                if(bloodTypesAmounts[6] < 0){
                    reference.child("O+").setValue(0);
                }else{
                    reference.child("O+").setValue(bloodTypesAmounts[6]);
                }
            }
        });
        Button onegBtn = view.findViewById(R.id.usedOnegBtn);
        onegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --bloodTypesAmounts[7];

                if(bloodTypesAmounts[7] < 0){
                    reference.child("O-").setValue(0);
                }else{
                    reference.child("O-").setValue(bloodTypesAmounts[7]);
                }
            }
        });

        Toast.makeText(getActivity(), "Blood Type Reduced" , Toast.LENGTH_SHORT);
    }

    public void setupPieChart(){

        final Pie pie = AnyChart.pie();
        final List<DataEntry> dataEntries = new ArrayList<>();

        MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();

        MainActivity.databaseReference.child("tb_stocks").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();

                if (refreshCounter != 0){
                    chartView.clear();
                    dataEntries.clear();
                }

                refreshCounter++;

                while (items.hasNext()) {
                    DataSnapshot item = items.next();

                    if(item.getKey().equals(Constants.BloodBankNameForTimingSlots)){

                        for (int i=0; i < bloodTypes.length; i++){
                            dataEntries.add(new ValueDataEntry(bloodTypes[i], Integer.valueOf(item.child(bloodTypes[i]).getValue().toString())));
                            bloodTypesAmounts[i] = Integer.valueOf(item.child(bloodTypes[i]).getValue().toString());
                        }
                    }
                }
                pie.data(dataEntries);
                //pie.title("Blood Bank Stock");
                chartView.setChart(pie);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
