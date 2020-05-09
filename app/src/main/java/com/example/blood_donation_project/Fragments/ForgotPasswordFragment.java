package com.example.blood_donation_project.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.example.blood_donation_project.Util.CustomToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    private static View view;
    private EditText emailEditText;
    private TextView submitTextViewBtn, backTextViewBtn;

    public ForgotPasswordFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        initViews();
        setListeners();
        return view;
    }

    private void initViews() {
        emailEditText = view.findViewById(R.id.registered_emailid);
        submitTextViewBtn = view.findViewById(R.id.forgot_button);
        backTextViewBtn = view.findViewById(R.id.backToLoginBtn);
    }

    private void setListeners() {
        backTextViewBtn.setOnClickListener(this);
        submitTextViewBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backToLoginBtn:
                ((MainActivity) getActivity()).replaceFragment(new LoginFragment(), Constants.Login_Fragment, true);
                break;

            case R.id.forgot_button:
                submitButtonTask();
                break;

        }
    }

    private void submitButtonTask() {
        String email = emailEditText.getText().toString();

        // Pattern for email id validation
        Pattern p = Pattern.compile(Constants.regEx);

        // Match the pattern
        Matcher matcher = p.matcher(email);

        // First check if email id is not null else show error toast
        if (email.equals("") || email.length() == 0)
            new CustomToast().Show_Toast(getActivity(), view, "Please enter your Email Id.");

        // Check if email id is valid or not
        else if (!matcher.find())
            new CustomToast().Show_Toast(getActivity(), view, "the Email is Invalid.");

        // Else submit email id and fetch passwod or do your stuff
        else{
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), "Email Sent", Toast.LENGTH_LONG);
                        }
                    });
        }
    }
}
