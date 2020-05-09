package com.example.blood_donation_project.Fragments.Donor_panel;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.blood_donation_project.Adaptors.DonorMenuImagesAdapter;
import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;

import java.util.Timer;
import java.util.TimerTask;

public class DonorMainViewFragment extends Fragment implements View.OnClickListener {

    private ViewPager viewPager;
    private Button profileBtn, donationBtn, searchMapBtn, historyBtn;
    private Timer timer;

    public DonorMainViewFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.showActionBar(Constants.MainView_Fragment, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donor_main_view, container, false);

        initViews(view);
        setListeners();

        viewPager.setAdapter(new DonorMenuImagesAdapter(getActivity()));

        timer = new Timer();
        timer.scheduleAtFixedRate(new SlideImageTimer(), 3000, 6000);

        return view;
    }

    private void initViews(View view) {
        viewPager = view.findViewById(R.id.view_pager);

        profileBtn =  view.findViewById(R.id.profileBtn);
        donationBtn = view.findViewById(R.id.donationBtn);
        searchMapBtn = view.findViewById(R.id.searchMapBtn);
        historyBtn = view.findViewById(R.id.historyBtn);
    }

    private void setListeners() {
        profileBtn.setOnClickListener(this);
        donationBtn.setOnClickListener(this);
        searchMapBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.profileBtn:
                ((MainActivity) getActivity()).replaceFragment(new DonorProfileFragment(), Constants.DonorProfile_Fragment, false);
                break;
            case R.id.historyBtn:
                ((MainActivity) getActivity()).replaceFragment(new DonationHistoryFragment(), Constants.DonationHistory_Fragment, false);
                break;
            case R.id.donationBtn:
                ((MainActivity) getActivity()).replaceFragment(new DonationFragment(), Constants.Donation_Fragment, false);
                break;
            case R.id.searchMapBtn:
                ((MainActivity) getActivity()).replaceFragment(new SearchMapFragment(), Constants.SearchMap_Fragment, false);
                break;
        }

        if (view == profileBtn) {
        }
    }

    public class SlideImageTimer extends TimerTask {

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                if(viewPager.getCurrentItem() == 0){
                    viewPager.setCurrentItem(1);
                }else{
                    viewPager.setCurrentItem(0);
                }
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }
}
