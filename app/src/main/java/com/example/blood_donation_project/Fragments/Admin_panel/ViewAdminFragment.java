package com.example.blood_donation_project.Fragments.Admin_panel;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.blood_donation_project.Adaptors.AdminViewAdapter;
import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.Admin;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
public class ViewAdminFragment extends Fragment{

    private ProgressDialog progressDialog;
    private View view;
    private List<Admin> adminList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    public ViewAdminFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.showActionBar(Constants.ViewAdmins_Fragment, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_view_admin, container, false);

        progressDialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);

        getAdmins();

        return view;
    }

    private void getAdmins() {


        MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();
        MainActivity.databaseReference.child("tb_users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Boolean empty = true;
                adminList = new ArrayList<Admin>();
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                while (items.hasNext())
                {
                    empty = false;
                    DataSnapshot item = items.next();

                    Admin a = item.getValue(Admin.class);
                    if(a.getType().equals("Admin")){
                        adminList.add(a);

                    }

                    recyclerView = view.findViewById(R.id.admins_recyclerView);
                    layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);

                }
                adapter = new AdminViewAdapter(adminList);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();


                if(empty){
                    progressDialog.dismiss();
                    AlertDialog alertDialog = Constants.AlertMsg(getActivity(), "Alert", "There is no admins");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) getActivity()).replaceFragment(new AdminMainViewFragment(), Constants.MainView_Fragment, false);
                        }
                    });
                    alertDialog.show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
