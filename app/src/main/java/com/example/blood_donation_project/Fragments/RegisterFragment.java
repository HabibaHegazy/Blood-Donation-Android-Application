package com.example.blood_donation_project.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blood_donation_project.Fragments.Donor_panel.DonorMainViewFragment;
import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.Donor;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener{

    private EditText firstNameET, lastNameET, phoneNumberET, emailET, passwordET, confirmpasswordET;
    private Spinner spinner;
    private Button registrationBtn, birthdateCalenderBtn;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;

    private String birthDate = null;
    private String choosenBloodType = null;

    private View view;

    public RegisterFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);

        initialize();
        setInputFilters();
        setListener();

        return view;
    }

    private void initialize(){
        firstNameET = view.findViewById(R.id.fname);
        lastNameET = view.findViewById(R.id.lname);
        phoneNumberET = view.findViewById(R.id.phone);
        emailET = view.findViewById(R.id.emailRegET);
        passwordET = view.findViewById(R.id.passwordRegET);
        confirmpasswordET = view.findViewById(R.id.confim_password);
        birthdateCalenderBtn = view.findViewById(R.id.show_dialog);
        registrationBtn = view.findViewById(R.id.signupBtn);
        spinner = view.findViewById(R.id.blood_type_spinner);
        radioSexGroup = view.findViewById(R.id.gender_group);

    }

    private  void setInputFilters(){
        InputFilter filtertxt = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        firstNameET.setFilters(new InputFilter[]{filtertxt});
        lastNameET.setFilters(new InputFilter[]{filtertxt});
    }

    private void setListener(){
        birthdateCalenderBtn.setOnClickListener(this);
        registrationBtn.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        birthDate = day + "/"+ (month+1)+ "/"+year;
        String visualDate = Constants.createVisualDate(birthDate);
        birthdateCalenderBtn.setText(visualDate);
    }

    @Override
    public void onClick(View view) {
        if(view == birthdateCalenderBtn){
            Constants.showDatePickerDialog(getContext(), this, false);
        }
        if(view == registrationBtn){
            registerUser();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        choosenBloodType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public boolean validate() {
        boolean valid = true;

        String fname = firstNameET.getText().toString();
        String lname = lastNameET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPassword = confirmpasswordET.getText().toString();

        if (fname.isEmpty() || fname.length() < 3 ) {
            firstNameET.setError("at least 3 characters");
            valid = false;
        } else {
            firstNameET.setError(null);
        }

        if (lname.isEmpty() || lname.length() < 3) {
            lastNameET.setError("at least 3 characters");
            valid = false;
        } else {
            lastNameET.setError(null);
        }

        if(phoneNumber.isEmpty() || phoneNumber.length() < 11){
            phoneNumberET.setError("Please enter a valid phone number");
            valid = false;
        }else{
            phoneNumberET.setError(null);
        }

        if(TextUtils.isEmpty(choosenBloodType)){
            Toast.makeText(getActivity(), "Please choose a blood type", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if(TextUtils.isEmpty(birthDate)){
            birthdateCalenderBtn.setError("Please enter your birth date");
            valid = false;
        }else{
            birthdateCalenderBtn.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("enter a valid email address");
            valid = false;
        } else {
            emailET.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordET.setError("at least 6 characters");
            valid = false;
        } else {

            if(!confirmPassword.equals(password)){
                confirmpasswordET.setError("Passwords does not match");
                valid = false;
            }

            passwordET.setError(null);
        }



        return valid;
    }

    private void registerUser(){

        final String firstName = firstNameET.getText().toString().trim();
        final String lastName = lastNameET.getText().toString().trim();
        final String phoneNumber = phoneNumberET.getText().toString().trim();
        final String email = emailET.getText().toString().trim();
        final String password  = passwordET.getText().toString().trim();

        int selectedId = radioSexGroup.getCheckedRadioButtonId();

        radioSexButton = view.findViewById(selectedId);
        final String gender = radioSexButton.getText().toString().trim();

        validate();

        MainActivity.firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = MainActivity.firebaseAuth.getCurrentUser();
                    String userID = firebaseUser.getUid();

                    Donor donor = new Donor();
                    donor.setFirstName(firstName);
                    donor.setLastName(lastName);
                    donor.setPhoneNumber(phoneNumber);
                    donor.setBirthdate(birthDate);
                    donor.setBloodType(choosenBloodType);
                    donor.setBloodTypeTest(0);
                    donor.setEmail(email);
                    donor.setGender(gender);
                    donor.setImageName("profile.png");
                    donor.setType("Donor");

                    MainActivity.databaseReference.child("tb_users").child(userID).setValue(donor);

                    MainActivity.firebaseAuth.getCurrentUser().updateEmail(firebaseUser.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                MainActivity.firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(), "Verification Email Sent To Your Email.. Please Verify and Login", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });

                    Toast.makeText(getActivity(), "Registration Done", Toast.LENGTH_LONG).show();



                }
                else {

                    try
                    {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException weakPassword)
                    {
                        Toast.makeText(getActivity(), "weak_password", Toast.LENGTH_LONG).show();
                    }
                    // if user enters wrong password.
                    catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                    {
                        Toast.makeText(getActivity(), "malformed_email", Toast.LENGTH_LONG).show();
                    }
                    catch (FirebaseAuthUserCollisionException existEmail)
                    {
                        Toast.makeText(getActivity(), "already existed email", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }
}
