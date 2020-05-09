package com.example.blood_donation_project.Fragments.Admin_panel;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.blood_donation_project.Fragments.Adminpanel.ViewBloodBanksFragment;
import com.example.blood_donation_project.Fragments.Adminpanel.AddBloodBank;
import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;

public class AdminMainViewFragment extends Fragment implements View.OnClickListener {

    private Button addAdminbtn, viewAdminsbtn, addBBbtn, viewBBbtn, viewDonorsbtn;

    public AdminMainViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.showActionBar(Constants.MainView_Fragment, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_main_view, container, false);

        initViews(view);
        setListeners();

        return view;

    }

    private void initViews(View view) {
        addAdminbtn =  view.findViewById(R.id.addAdminButton);
        viewAdminsbtn = view.findViewById(R.id.viewAdminsButton);
        addBBbtn = view.findViewById(R.id.addBloodbankButton);
        viewBBbtn = view.findViewById(R.id.viewBloodbanksButton);
        viewDonorsbtn = view.findViewById(R.id.viewDonorsButton);
    }

    private void setListeners() {
        addAdminbtn.setOnClickListener(this);
        viewAdminsbtn.setOnClickListener(this);
        addBBbtn.setOnClickListener(this);
        viewBBbtn.setOnClickListener(this);
        //viewDonorsbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addAdminButton:
                ((MainActivity) getActivity()).replaceFragment(new AddAdminFragment(), Constants.AddAdmin_Fragment, false);
                break;
            case R.id.viewAdminsButton:
                ((MainActivity) getActivity()).replaceFragment(new ViewAdminFragment(), Constants.ViewAdmins_Fragment, false);
                break;
            case R.id.addBloodbankButton:
                ((MainActivity) getActivity()).replaceFragment(new AddBloodBank(), Constants.BloodBankAddBank, false);
                break;
            case R.id.viewBloodbanksButton:
                ((MainActivity) getActivity()).replaceFragment(new ViewBloodBanksFragment(), Constants.BloodBankView, false);
                break;
/*
            case R.id.viewDonorsButton:
                //((MainActivity) getActivity()).replaceFragment(new y(), Constants.x, false);
                break;*/
        }
    }
}
