package com.example.blood_donation_project.Fragments.Donor_panel;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.Donor;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DonorProfileFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private View view;

    private EditText firstNameET, lastNameET, phoneNumberET, bloodTypeET;
    private RadioGroup radioSexGroup;
    private RadioButton radioBtnMale, radioBtnFemale;
    private Button editProfileBtn, imageEditBtn, birthdateProfileBtn;
    private ImageView profileImg;
    private String image_name, Birthdate = null;
    private ProgressDialog dialog;
    private Donor donor;
    private Uri ImageUri = null;

    private StorageReference storageReference;

    public DonorProfileFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.showActionBar(Constants.DonorProfile_Fragment, getActivity());
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_donor_profile, container, false);

        initViews(view);

        dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);
        getProfileData();

        setListeners();

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.profileActionBtn:

                String proBtnString = editProfileBtn.getText().toString();

                if (proBtnString.equals(getString(R.string.edit_profile))) {

                    firstNameET.setEnabled(true);
                    profileImg.setEnabled(true);
                    lastNameET.setEnabled(true);
                    phoneNumberET.setEnabled(true);
                    radioBtnMale.setEnabled(true);
                    radioBtnFemale.setEnabled(true);
                    imageEditBtn.setVisibility(View.VISIBLE);
                    birthdateProfileBtn.setEnabled(true);

                    editProfileBtn.setText(getString(R.string.save_profile));

                } else {

                    firstNameET.setEnabled(false);
                    profileImg.setEnabled(false);
                    lastNameET.setEnabled(false);
                    phoneNumberET.setEnabled(false);
                    radioBtnMale.setEnabled(false);
                    radioBtnFemale.setEnabled(false);
                    imageEditBtn.setVisibility(View.GONE);
                    birthdateProfileBtn.setEnabled(false);

                    editProfileBtn.setText(getString(R.string.edit_profile));

                    updateProfileData();
                }

                break;
            case R.id.profile_img:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST);
                break;
            case R.id.birthdateProfileBtn:
                Constants.showDatePickerDialog(getContext(), this, false);
                break;
        }

    }

    private void initViews(View view) {

        profileImg = view.findViewById(R.id.profile_img);

        firstNameET = view.findViewById(R.id.firstNameEditText);
        lastNameET = view.findViewById(R.id.lastNameEditText);
        phoneNumberET = view.findViewById(R.id.phoneProfileEditText);
        bloodTypeET = view.findViewById(R.id.bloodTypeEditText);

        radioSexGroup = view.findViewById(R.id.genderProfileRadioGroup);

        radioBtnMale = view.findViewById(R.id.radioMaleProfile);
        radioBtnFemale = view.findViewById(R.id.radioFemaleProfile);

        editProfileBtn = view.findViewById(R.id.profileActionBtn);
        imageEditBtn = view.findViewById(R.id.imageEditBtn);
        birthdateProfileBtn = view.findViewById(R.id.birthdateProfileBtn);
    }

    private void setListeners() {
        profileImg.setOnClickListener(this);
        editProfileBtn.setOnClickListener(this);
        birthdateProfileBtn.setOnClickListener(this);
    }

    private void getProfileData() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();

        MainActivity.databaseReference.child("tb_users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        dialog.dismiss();
                    }
                }, 4000);

                donor = dataSnapshot.getValue(Donor.class);

                storageReference.child(donor.getImageName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Picasso.get().load(uri).fit().centerCrop().into(profileImg);
                        Picasso.get().load(uri).into(profileImg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

                firstNameET.setText(donor.getFirstName());
                lastNameET.setText(donor.getLastName());
                phoneNumberET.setText(donor.getPhoneNumber());
                bloodTypeET.setText(donor.getBloodType());
                birthdateProfileBtn.setText(Constants.createVisualDate(donor.getBirthdate()));

                switch (donor.getGender()) {
                    case "Male":
                        radioSexGroup.check(R.id.radioMaleProfile);
                        break;
                    case "Female":
                        radioSexGroup.check(R.id.radioFemaleProfile);
                        break;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void updateProfileData() {

        dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);
        Handler handler = new Handler();

        Boolean imageEdit= false;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();

        if (!donor.getFirstName().equals(firstNameET.getText().toString())) {
            databaseReference.child("tb_users").child(uid).child("firstName").setValue(firstNameET.getText().toString());
        }
        if (!donor.getLastName().equals(lastNameET.getText().toString())) {
            databaseReference.child("tb_users").child(uid).child("lastName").setValue(lastNameET.getText().toString());
        }
        if (!donor.getPhoneNumber().equals(phoneNumberET.getText().toString())) {
            databaseReference.child("tb_users").child(uid).child("phoneNumber").setValue(phoneNumberET.getText().toString());
        }
        if (ImageUri != null) {
            uploadImageToStorage();
            databaseReference.child("tb_users").child(uid).child("imageName").setValue(image_name);
            imageEdit = true;
        }
        if (Birthdate != null && !donor.getBirthdate().equals(Birthdate)){
            databaseReference.child("tb_users").child(uid).child("birthdate").setValue(Birthdate);
        }

        int selectedId = radioSexGroup.getCheckedRadioButtonId();
        RadioButton radioSexButton = view.findViewById(selectedId);

        if (!donor.getGender().equals(radioSexButton.getText().toString())) {
           databaseReference.child("tb_users").child(uid).child("gender").setValue(radioSexButton.getText().toString());
        }

        if(imageEdit)
            handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
            }
        }, 6000);
        else
            handler.postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            }, 3000);

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImageToStorage(){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();

        image_name = uid + "." + getFileExtension(ImageUri);
        StorageReference mStorageRef = storageReference.child(image_name);
        mStorageRef.putFile(ImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        AlertDialog alertDialog = Constants.AlertMsg(getActivity(), "", "Uploaded Successfully");
                        alertDialog.show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {}
                });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Birthdate = day + "/" + (month+1) + "/" + year;
        String visualDate = Constants.createVisualDate(year, month, day);
        birthdateProfileBtn.setText(visualDate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null && data.getData() != null){
            ImageUri = data.getData();
            Picasso.get().load(ImageUri).into(profileImg);
        }
    }
}
