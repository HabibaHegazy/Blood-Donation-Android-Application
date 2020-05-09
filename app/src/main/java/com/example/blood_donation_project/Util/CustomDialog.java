package com.example.blood_donation_project.Util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.BloodBanks;
import com.example.blood_donation_project.Models.Slots;
import com.example.blood_donation_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class CustomDialog extends AppCompatDialogFragment implements View.OnClickListener {
    private EditText existEmailEditText, newEmailEditText;
    private EditText existPasswordEditText, newPasswordEditText, newPasswordConfirmEditText;
    private int choice; // 0 - email // 1 - password
    private View view;
    private String title, choosenBloodType = null, choosenDay = null;
    private BloodBanks bloodBanks;
    private String donorID;
    private Context mContext;
    private Slots slot;


    public CustomDialog(int choice) {
        this.choice = choice;
    }

    public CustomDialog(int choice, String donorID) {
        this.choice = choice;
        this.donorID = donorID;
    }

    public CustomDialog(int choice, Slots slot) {
        this.choice = choice;
        this.slot = slot;
    }

    public CustomDialog(int choice, BloodBanks bloodBanks) {
        this.choice = choice;
        this.bloodBanks = bloodBanks;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        if (choice < 2) {

            if (choice == 0) { //email
                view = inflater.inflate(R.layout.layout_email_dialog, null);
                title = "Change Email Address";

                existEmailEditText = view.findViewById(R.id.existEmailEditText);
                newEmailEditText = view.findViewById(R.id.newEmailEditText);
            } else if (choice == 1) { // password
                view = inflater.inflate(R.layout.layout_password_dialog, null);
                title = "Change Password";

                existPasswordEditText = view.findViewById(R.id.existPasswordEditText);
                newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
                newPasswordConfirmEditText = view.findViewById(R.id.newPasswordConfirmEditText);
            }

            builder.setView(view)
                    .setTitle(title)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (choice == 0) {
                                changeEmailFirebase();
                            } else {
                                changePasswordFirebase();
                            }
                        }
                    });

        }
        else if(choice == 2) {
            view = inflater.inflate(R.layout.layout_emergency_bb_request_dialog, null);

            Button aplus, aneg, bplus, bneg, abplus, abneg, oplus, oneg, emergency_request_btn;

            aplus = view.findViewById(R.id.groupAplus);
            aplus.setOnClickListener(this);
            aneg = view.findViewById(R.id.groupAneg);
            aneg.setOnClickListener(this);

            bplus = view.findViewById(R.id.groupBplus);
            bplus.setOnClickListener(this);
            bneg = view.findViewById(R.id.groupBneg);
            bneg.setOnClickListener(this);

            abplus = view.findViewById(R.id.groupABplus);
            abplus.setOnClickListener(this);
            abneg = view.findViewById(R.id.groupABneg);
            abneg.setOnClickListener(this);

            oplus = view.findViewById(R.id.groupOplus);
            oplus.setOnClickListener(this);
            oneg = view.findViewById(R.id.groupOneg);
            oneg.setOnClickListener(this);

            emergency_request_btn = view.findViewById(R.id.emergency_request_btn);
            emergency_request_btn.setOnClickListener(this);

            builder.setView(view).setTitle(title);
        }
        else if(choice == 3){
            view = inflater.inflate(R.layout.layout_blood_test_dialog, null);
            title = "Blood Test";

            BloodTestFirebase();

            builder.setView(view).setTitle(title);
        }
        else{
            view = inflater.inflate(R.layout.layout_timing_slot_add_dialog, null);
            title = "Add Timing Slot";
            final Button add_edit_slot = view.findViewById(R.id.add_edit_slot);
            Spinner spinner = view.findViewById(R.id.days_of_Week);
            final EditText timingSlotEditText = view.findViewById(R.id.timingSlotEditText);

            if(slot != null){
                add_edit_slot.setText("Edit Timing Slot");
                title = "Edit Timing Slot";
                timingSlotEditText.setText(slot.getTiming());
                int selectionPosition = 0;
                switch (slot.getSlot_day()){
                    case "Sunday":
                        selectionPosition = 0;
                        break;
                    case "Monday":
                        selectionPosition =1;
                        break;
                    case "Tuesday":
                        selectionPosition =2;
                        break;
                    case "Wednesday":
                        selectionPosition =3;
                        break;
                    case "Friday":
                        selectionPosition =4;
                        break;
                    case "Thursday":
                        selectionPosition =5;
                        break;
                    case "Saturday":
                        selectionPosition =6;
                        break;
                }

                spinner.setSelection(selectionPosition);
            }

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    choosenDay = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            add_edit_slot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(choice == 5){
                        if(timingSlotEditText.getText().toString() != null && !timingSlotEditText.getText().toString().equals("") && choosenDay != null) {
                            MainActivity.databaseReference.child("tb_slots").child(slot.getSlotID()).child("timing").setValue(timingSlotEditText.getText().toString());
                            MainActivity.databaseReference.child("tb_slots").child(slot.getSlotID()).child("slot_day").setValue(choosenDay);
                        }
                    }
                    else if(choice == 4){
                        if(timingSlotEditText.getText().toString() != null && !timingSlotEditText.getText().toString().equals("") && choosenDay != null){
                            Slots slots = new Slots();

                            slots.setTiming(timingSlotEditText.getText().toString());
                            slots.setSlot_day(choosenDay);
                            slots.setStatus("Enable");

                            String key = MainActivity.databaseReference.child("tb_slots").push().getKey();
                            MainActivity.databaseReference.child("tb_slots").child(key).setValue(slots);
                        }

                    }
                }
            });

            builder.setView(view).setTitle(title);

        }

        return builder.create();
    }

    private void changeEmailFirebase() {
        String existEmail = existEmailEditText.getText().toString();
        String newEmail = newEmailEditText.getText().toString();

        FirebaseUser firebaseUser = MainActivity.firebaseAuth.getCurrentUser();

        if (firebaseUser.getEmail().equals(existEmail)) {

            MainActivity.firebaseAuth.getCurrentUser().updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        MainActivity.firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //Toast.makeText(((MainActivity) getActivity()), "Verification Email Sent To Your Email.. Please Verify and Login", Toast.LENGTH_LONG).show();
                                //MainActivity.firebaseAuth.getInstance().signOut();
                                //((MainActivity) getActivity()).replaceFragment(new LoginFragment(), Constants.Login_Fragment, true);
                            }
                        });
                    }
                }
            });

        } else {
            Toast.makeText(getActivity(), "Current Email is Incorrect", Toast.LENGTH_LONG).show();
        }


    }

    private void changePasswordFirebase() {
        String currentPassword = existPasswordEditText.getText().toString();
        final String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = newPasswordConfirmEditText.getText().toString();

        if (newPassword.equals(confirmPassword)) {

            final FirebaseUser user = MainActivity.firebaseAuth.getInstance().getCurrentUser();

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(getActivity(), "Error password not updated", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Error auth failed", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Toast.makeText(getActivity(), "Passwords doesn't match", Toast.LENGTH_LONG).show();
        }


    }

    private void EmergencyRequestApi() {

        new Thread(new Runnable() {
            public void run() {

                MainActivity.databaseReference.child("tb_users").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> item = dataSnapshot.getChildren().iterator();

                        while (item.hasNext()) {

                            DataSnapshot item2 = item.next();

                            if (item2.child("type").getValue().toString().equals("Donor")) {

                                if(checkPermission(Manifest.permission.SEND_SMS) && choosenBloodType != null){

                                    String phoneNumber = item2.child("phoneNumber").getValue().toString();
                                    String smsMessage = "This is an Emergency Call from " + bloodBanks.getName() + " in need of blood type " + choosenBloodType +
                                            "\n on this address " + bloodBanks.getDetail_location_description() +
                                            "\n check the blood bank on our application BloodBank Donation App" ;

                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(phoneNumber, null, smsMessage, null, null);

                                }else {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, Constants.PERMISSIONS_REQUEST_SEND_SMS);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }

                });

            }
        }).start();

    }

    private void BloodTestFirebase(){
        Spinner spinner = view.findViewById(R.id.blood_type_spinner);
        Button emergency_request_btn = view.findViewById(R.id.emergency_request_btn);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choosenBloodType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        emergency_request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.databaseReference.child("tb_users").child(donorID).child("bloodType").setValue(choosenBloodType);
                Constants.clickedTestingSucess = true;
            }
        });
    }

    private void addEditTimingSlot(){



    }

    private boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(mContext, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.groupAplus:
                choosenBloodType = "A+";
                break;
            case R.id.groupAneg:
                choosenBloodType = "A-";
                break;
            case R.id.groupBplus:
                choosenBloodType = "B+";
                break;
            case R.id.groupBneg:
                choosenBloodType = "B-";
                break;
            case R.id.groupABplus:
                choosenBloodType = "AB+";
                break;
            case R.id.groupABneg:
                choosenBloodType = "AB-";
                break;
            case R.id.groupOplus:
                choosenBloodType = "O+";
                break;
            case R.id.groupOneg:
                choosenBloodType = "O-";
                break;
            case R.id.emergency_request_btn:
                EmergencyRequestApi();
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

}
