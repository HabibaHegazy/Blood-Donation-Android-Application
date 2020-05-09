package com.example.blood_donation_project.Fragments.Admin_panel;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.Admin;
import com.example.blood_donation_project.Models.Donor;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditAdminFragment extends Fragment implements View.OnClickListener , DatePickerDialog.OnDateSetListener {


    private View view;

    private EditText firstNameET, lastNameET, phoneNumberET;
    private RadioGroup radioSexGroup;
    private RadioButton radioBtnMale, radioBtnFemale;
    private Button editProfileBtn, birthdateProfileBtn;
    private String Birthdate = null;
    private ProgressDialog dialog;
    private Admin admin;

    //private String uid;

    private StorageReference storageReference;

    private List<String> editInformations;
    public EditAdminFragment(List<String> editInformations) {
        this.editInformations = editInformations;
    }
    /*public EditAdminFragment(String uid) {
        uid=this.uid;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.showActionBar(Constants.EditAdmin_Fragment, getActivity());
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_edit_admin, container, false);

        initViews(view);

        dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);
        getAdminData();

        setListeners();

        return view;

    }

    private void setListeners() {
        editProfileBtn.setOnClickListener(this);
        birthdateProfileBtn.setOnClickListener(this);
    }

    private void getAdminData() {
        //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //String uid = firebaseUser.getUid();

        MainActivity.databaseReference.child("tb_users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        dialog.dismiss();
                    }
                }, 4000);

                admin = dataSnapshot.getValue(Admin.class);

                firstNameET.setText(editInformations.get(0));
                lastNameET.setText(editInformations.get(1));
                phoneNumberET.setText(editInformations.get(4));
                birthdateProfileBtn.setText(Constants.createVisualDate(editInformations.get(2)));

                switch (editInformations.get(3)) {
                    case "Male":
                        radioSexGroup.check(R.id.radioMaleProfile_admin);
                        break;
                    case "Female":
                        radioSexGroup.check(R.id.radioFemaleProfile_admin);
                        break;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void initViews(View view) {

        firstNameET = view.findViewById(R.id.firstNameEditText_admin);
        lastNameET = view.findViewById(R.id.lastNameEditText_admin);
        phoneNumberET = view.findViewById(R.id.phoneProfileEditText_admin);

        radioSexGroup = view.findViewById(R.id.genderProfileRadioGroup_admin);
        radioBtnMale = view.findViewById(R.id.radioMaleProfile_admin);
        radioBtnFemale = view.findViewById(R.id.radioFemaleProfile_admin);

        editProfileBtn = view.findViewById(R.id.editAdminBtn);
        birthdateProfileBtn = view.findViewById(R.id.birthdateProfileBtn_admin);
    }

    @Override
    public void onClick(View v) {

        switch (view.getId()) {
            case R.id.editAdminBtn:
                updateAdminData();
                break;

            case R.id.birthdateProfileBtn_admin:
                Constants.showDatePickerDialog(getContext(), this, false);
                break;
        }
    }

    private void updateAdminData(){
        dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();


        //update admin data
        if (!admin.getFirstName().equals(firstNameET.getText().toString())) {
            databaseReference.child("tb_users").child(uid).child("firstName").setValue(firstNameET.getText().toString());
        }
        if (!admin.getLastName().equals(lastNameET.getText().toString())) {
            databaseReference.child("tb_users").child(uid).child("lastName").setValue(lastNameET.getText().toString());
        }
        if (!admin.getPhoneNumber().equals(phoneNumberET.getText().toString())) {
            databaseReference.child("tb_users").child(uid).child("phoneNumber").setValue(phoneNumberET.getText().toString());
        }
        if (Birthdate != null && !admin.getBirthdate().equals(Birthdate)){
            databaseReference.child("tb_users").child(uid).child("birthdate").setValue(Birthdate);
        }

        int selectedId = radioSexGroup.getCheckedRadioButtonId();
        RadioButton radioSexButton = view.findViewById(selectedId);

        if (!admin.getGender().equals(radioSexButton.getText().toString())) {
            databaseReference.child("tb_users").child(uid).child("gender").setValue(radioSexButton.getText().toString());
        }

    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Birthdate = dayOfMonth + "/" + (month+1) + "/" + year;
        String visualDate = Constants.createVisualDate(year, month, dayOfMonth);
        birthdateProfileBtn.setText(visualDate);
    }
}
