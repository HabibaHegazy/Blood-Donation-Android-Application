package com.example.blood_donation_project.Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.blood_donation_project.Fragments.Adminpanel.ViewBloodBanksFragment;
import com.example.blood_donation_project.Models.BloodBanks;
import com.example.blood_donation_project.R;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class BloodBanksRecyclerViewAdapter extends RecyclerView.Adapter<BloodBanksRecyclerViewAdapter.ViewHolder> {

    List<BloodBanks> bloodBanksList;
    public BloodBanksRecyclerViewAdapter() {
    }

    public BloodBanksRecyclerViewAdapter(List<BloodBanks> bloodBanks) {
        this.bloodBanksList = bloodBanks;
    }

    @Override
    public BloodBanksRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_blood_banks_items, parent, false);
        return new BloodBanksRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BloodBanks bloodbank = bloodBanksList.get(position);

        holder.bloodBankName.setText(bloodbank.getName());
        holder.number.setText(bloodbank.getNumber());

            holder.editBloodBank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    List<String> editInformation = new ArrayList<>();

                    editInformation.add(bloodbank.getName());

                    editInformation.add(bloodbank.getNumber());

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();

                    Fragment fragment = new ViewBloodBanksFragment(editInformation);

                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

                }
            });
    }
    @Override
    public int getItemCount() {
        if (bloodBanksList == null)
            return 0;
            return 0 ;
        }
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView bloodBankName,detail_location_description, working_hours,number,Longtitude, Latitude;
        public Button editBloodBank;
        public ViewHolder(View itemView) {
            super(itemView);
            bloodBankName = itemView.findViewById(R.id.bloodBankNameTextView);
            number = itemView.findViewById(R.id.bankPhoneNumber);
            editBloodBank = itemView.findViewById(R.id.editBloodBank);
        }
    }
}

