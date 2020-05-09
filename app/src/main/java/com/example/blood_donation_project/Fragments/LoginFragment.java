package com.example.blood_donation_project.Fragments;

import android.animation.Animator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blood_donation_project.BloodBankActivity;
import com.example.blood_donation_project.Fragments.Admin_panel.AdminMainViewFragment;
import com.example.blood_donation_project.Fragments.BloodBank_panel.BloodBankMainViewFragment;
import com.example.blood_donation_project.Fragments.Donor_panel.DonorMainViewFragment;
import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.example.blood_donation_project.Util.CustomToast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.LOCATION_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private EditText emailEditText, passwordEditText;
    private ImageView bookIconImageView;
    private TextView bookITextView, forgetPasswordTextView, registerTextView;
    private ProgressBar loadingProgressBar;
    private Button loginBtn;
    private RelativeLayout rootView, afterAnimationView;
    private CheckBox show_hide_password;
    private static Animation shakeAnimation;
    private View view;
    private  boolean mLocationPermissionGranted = false;


    public LoginFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkIfLogined();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);

        initViews();
        setListeners();

        Constants.hideActionBar(getActivity());

        if(!MainActivity.lunchedApp){

            new CountDownTimer(5000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    actionTaken();

                }
            }.start();

            MainActivity.lunchedApp = true;

        }else{
            actionTaken();
        }



        return view;

    }

    private void initViews() {
        // image
        bookIconImageView = view.findViewById(R.id.logoIconImage);

        // textView
        bookITextView = view.findViewById(R.id.logoTextView);
        forgetPasswordTextView = view.findViewById(R.id.forgot_password);
        registerTextView = view.findViewById(R.id.createAccount);

        SpannableString spannableString = new SpannableString(registerTextView.getText());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ((MainActivity) getActivity()).replaceFragment(new RegisterFragment(), Constants.Register_Fragment, true);
            }
        };

        spannableString.setSpan(clickableSpan, 19, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerTextView.setText(spannableString);
        registerTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // editText
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);

        // progressBar
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        //Buttons
        loginBtn = view.findViewById(R.id.loginBtn);

        // checkBox
        show_hide_password = view.findViewById(R.id.show_hide_password);

        // relativeLayout
        rootView = view.findViewById(R.id.rootView);
        afterAnimationView = view.findViewById(R.id.afterAnimationView);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);

    }

    private void setListeners() {
        loginBtn.setOnClickListener(this);
        forgetPasswordTextView.setOnClickListener(this);
        //signUp.setOnClickListener(this);

        // Set check listener over checkbox for showing and hiding password
        show_hide_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button, boolean isChecked) {

                        // If password is check then show password else hide
                        if (isChecked) {

                            show_hide_password.setText(R.string.hide_password);// change checkbox text

                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_password);// change checkbox text

                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            passwordEditText.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
    }

    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = bookIconImageView.animate();
        viewPropertyAnimator.x(50f);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                afterAnimationView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void checkValidation() {
        // Get email id and password
        String getEmailId = emailEditText.getText().toString();
        String getPassword = passwordEditText.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile(Constants.regEx);

        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
            rootView.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view, "Enter both credentials.");
        }
        // Check if email id is valid or not
        else if (!m.find())
            new CustomToast().Show_Toast(getActivity(), view, "Your Email Id is Invalid.");
            // Else do login and do your stuff
        else
            userLogin();

    }

    private void userLogin(){
        String email = emailEditText.getText().toString().trim();
        String password  = passwordEditText.getText().toString().trim();

        MainActivity.firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            switchToUserTypePanel();

                            /*if(MainActivity.firebaseAuth.getCurrentUser().isEmailVerified()){
                                switchToUserTypePanel();
                            }else{
                                new CustomToast().Show_Toast(getActivity(), view, "Please Verify your email then login");
                            }*/

                        }
                        else{

                            new CustomToast().Show_Toast(getActivity(), view, "Either Email or password is wrong");

                        }
                    }
                });

    }

    private void actionTaken(){
        bookITextView.setVisibility(GONE);
        loadingProgressBar.setVisibility(GONE);
        //rootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white_greyish));
        bookIconImageView.setImageResource(R.drawable.project_logo);
        startAnimation();
    }

    private void checkIfLogined(){
        //if the objects getcurrentuser method is not null
        //means user is already logged in
        if(MainActivity.firebaseAuth.getCurrentUser() != null){
            MainActivity.lunchedApp = true;
            switchToUserTypePanel();
        }
    }

    private void switchToUserTypePanel(){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = firebaseUser.getUid();

        MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();

        //check the type of the user
        MainActivity.databaseReference.child("tb_users").orderByKey().equalTo(uid).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();

                if(items.hasNext()){
                    String type = items.next().child("type").getValue().toString();

                    if(type.equals("Donor")){
                        ((MainActivity) getActivity()).replaceFragment(new DonorMainViewFragment(), Constants.MainView_Fragment, true);
                        MainActivity.privouseFragment = new DonorMainViewFragment();
                        MainActivity.privouseFragment_tag = Constants.MainView_Fragment;
                    }else if (type.equals("Admin")){
                        ((MainActivity) getActivity()).replaceFragment(new AdminMainViewFragment(), Constants.MainView_Fragment, true);
                        MainActivity.privouseFragment = new AdminMainViewFragment();
                        MainActivity.privouseFragment_tag = Constants.MainView_Fragment;
                    }

                }else{
                    System.out.println(uid);
                    Intent i = new Intent(getActivity(), BloodBankActivity.class);
                    i.putExtra("uid", uid);
                    startActivity(i);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, Constants.PERMISSIONS_REQUEST_ENABLE_GPS); // i need to know weather the use accepted or deinaed the accepted
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){ // weather or not gps is enable on device
        final LocationManager manager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(((MainActivity) getActivity()).getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, Constants.ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                checkValidation();
                break;

            case R.id.forgot_password:
                ((MainActivity) getActivity()).replaceFragment(new ForgotPasswordFragment(), Constants.ForgotPassword_Fragment, true);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkMapServices()){
            if (!mLocationPermissionGranted){
                getLocationPermission();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.lunchedApp = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }
}
