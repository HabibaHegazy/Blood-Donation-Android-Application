package com.example.blood_donation_project.Fragments.Admin_panel;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.Admin;
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

public class AddAdminFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private EditText firstNameET, lastNameET, phoneNumberET, emailET, passwordET, confirmPasswordET;
    private Button addAdminBtn, birthdateCalenderBtn;
    private RadioGroup radioGenderGroup;
    private RadioButton radioGenderButton;
    private String birthDate = null;

    private View view;

    public AddAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_admin, container, false);

        initialize();
        setInputFilters();
        setListener();

        return view;
    }

    private void initialize() {
        firstNameET = view.findViewById(R.id.fname_admin);
        lastNameET = view.findViewById(R.id.lname_admin);
        phoneNumberET = view.findViewById(R.id.phone_admin);
        emailET = view.findViewById(R.id.emailRegET_admin);
        passwordET = view.findViewById(R.id.passwordRegET_admin);
        confirmPasswordET = view.findViewById(R.id.confirm_password_admin);
        birthdateCalenderBtn = view.findViewById(R.id.show_dialog_admin);
        addAdminBtn = view.findViewById(R.id.regAdminBtn);
        radioGenderGroup = view.findViewById(R.id.gender_group_admin);
    }

    private void setInputFilters() {

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

    private void setListener() {

        birthdateCalenderBtn.setOnClickListener(this);
        addAdminBtn.setOnClickListener(this);
    }

    private void addAdmin(){

        final String firstName = firstNameET.getText().toString().trim();
        final String lastName = lastNameET.getText().toString().trim();
        final String phoneNumber = phoneNumberET.getText().toString().trim();
        final String email = emailET.getText().toString().trim();
        final String password  = passwordET.getText().toString().trim();

        int selectedId = radioGenderGroup.getCheckedRadioButtonId();

        radioGenderButton = view.findViewById(selectedId);
        final String gender = radioGenderButton.getText().toString().trim();

        validate();

        MainActivity.firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = MainActivity.firebaseAuth.getCurrentUser();
                    String userID = firebaseUser.getUid();

                    Admin admin = new Admin();
                    admin.setFirstName(firstName);
                    admin.setLastName(lastName);
                    admin.setPhoneNumber(phoneNumber);
                    admin.setBirthdate(birthDate);
                    admin.setEmail(email);
                    admin.setGender(gender);
                    admin.setType("Admin");

                    MainActivity.databaseReference.child("tb_users").child(userID).setValue(admin);

                    MainActivity.firebaseAuth.getCurrentUser().updateEmail(firebaseUser.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                MainActivity.firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(), "Verification Email Sent To The Entered Email..Please Verify and Login", Toast.LENGTH_LONG).show();
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

    public boolean validate() {
        boolean valid = true;

        String fname = firstNameET.getText().toString();
        String lname = lastNameET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();

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
                confirmPasswordET.setError("Passwords does not match");
                valid = false;
            }

            passwordET.setError(null);
        }



        return valid;
    }


    @Override
    public void onClick(View v) {
        if(v == birthdateCalenderBtn){
            Constants.showDatePickerDialog(getContext(), this, false);
        }
        if(v == addAdminBtn){
            addAdmin();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        birthDate = dayOfMonth + "/"+ (month+1)+ "/"+year;
        String visualDate = Constants.createVisualDate(birthDate);
        birthdateCalenderBtn.setText(visualDate);
    }
}
