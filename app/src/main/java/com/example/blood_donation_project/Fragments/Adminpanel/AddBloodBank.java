package com.example.blood_donation_project.Fragments.Adminpanel;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.BloodBanks;
import com.example.blood_donation_project.R;
import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBloodBank extends Fragment {

    EditText bankName, bankAddress, bankPhone;
    Button submitButton;
    Spinner toSpinner, fromSpinner ;

    public AddBloodBank() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_bloodbanks_submittion_form, container, false);
        bankName = view.findViewById(R.id.bloodBankNameEditText);
        bankAddress = view.findViewById(R.id.addressEditText);
        bankPhone = view.findViewById(R.id.bankPhoneNumber);
        fromSpinner = view.findViewById(R.id.fromSpinner);
        toSpinner = view.findViewById(R.id.toSpinner);

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.fromSpinner, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//        fromSpinner.setAdapter(adapter);

        submitButton = view.findViewById(R.id.signupBtn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitButtonPressed();
            }
        });

        return view ;

    }

    public void submitButtonPressed(){
        MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();

        BloodBanks myBloodBank = new BloodBanks();
        myBloodBank.setName(bankName.getText().toString());
        myBloodBank.setDetail_location_description(bankAddress.getText().toString());
        myBloodBank.setNumber(bankPhone.getText().toString());
        myBloodBank.setWorking_From(fromSpinner.getSelectedItem().toString());
        myBloodBank.setWorking_To(toSpinner.getSelectedItem().toString());

        MainActivity.databaseReference.child("tb_blood_Banks").child(bankName.getText().toString()).setValue(myBloodBank);
    }



}
