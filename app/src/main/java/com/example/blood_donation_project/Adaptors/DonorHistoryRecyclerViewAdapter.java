package com.example.blood_donation_project.Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blood_donation_project.Fragments.Donor_panel.DonationFragment;
import com.example.blood_donation_project.Models.Appointments;
import com.example.blood_donation_project.Models.Slots;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DonorHistoryRecyclerViewAdapter extends RecyclerView.Adapter<DonorHistoryRecyclerViewAdapter.ViewHolder> {

    List<Appointments> appointmentsList;
    Boolean showEdit = false;

    public DonorHistoryRecyclerViewAdapter(List<Appointments> appointments) {
        this.appointmentsList = appointments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donor_history_recyclerview_items, parent, false);
        return new DonorHistoryRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Appointments appointment = appointmentsList.get(position);
        Slots slot_reserved = appointment.getSlot_reserved_data();
        holder.bloodBankNameTextView.setText(slot_reserved.getBlood_bank_name());
        holder.appoinmentTimeTextView.setText(slot_reserved.getTiming());
        holder.appoinmentStateTextView.setText(appointment.getAppointment_state());

        String visualDate = Constants.createVisualDate(appointment.getAppointment_date());

        holder.appDayMonthTextView.setText(visualDate.substring(0, visualDate.length() - 4));
        holder.appYearTextView.setText(visualDate.substring(visualDate.length() - 4));

        if (appointment.getAppointment_state().equals("reserved")){

            checkDateTime(appointment.getAppointment_date(),
                    slot_reserved.getTiming().substring(slot_reserved.getTiming().length() - 7).trim());

            holder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    List<String> editInformation = new ArrayList<>();

                    editInformation.add(appointment.getSlot_reserved_data().getBlood_bank_name());
                    editInformation.add(appointment.getAppointment_date());
                    editInformation.add(appointment.getSlot_reserved_data().getSlot_day());
                    editInformation.add(appointment.getSlot_reserved_data().getTiming());
                    editInformation.add(appointment.getSlot_reserved());
                    editInformation.add(appointment.getAppointment_id());

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment fragment = new DonationFragment(editInformation);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

                }
            });
        }

        if(!showEdit){
            holder.editBtn.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if(appointmentsList == null){
            return 0;
        }
        return appointmentsList.size();
    }

    private void checkDateTime(String appointmentDate, String appointmentTime){

        try {

            if (new SimpleDateFormat("dd/MM/yyyy").parse(appointmentDate).before(new Date())) {

                // timing
                Calendar rightNow = Calendar.getInstance();
                int nowHour = rightNow.get(Calendar.HOUR_OF_DAY);
                int nowMin = rightNow.get(Calendar.MINUTE);
                int nowAmPm = rightNow.get(Calendar.AM_PM);

                String ampm;
                if(nowAmPm == 0)
                    ampm = "AM";
                else
                    ampm = "PM";

                String nowTime = nowHour + ":" + nowMin + " " + ampm;

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");

                Date appTime = timeFormat.parse(appointmentTime);
                Date todayTime = timeFormat.parse(nowTime);

                long difference = (todayTime.getTime() - appTime.getTime());

                long hours = difference/(1000 * 60 * 60);

                if (hours > 1)
                    showEdit = true;

            }else{
                showEdit = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView bloodBankNameTextView, appoinmentTimeTextView, appoinmentStateTextView, appDayMonthTextView, appYearTextView;
        public Button editBtn;

        public ViewHolder(View itemView) {

            super(itemView);
            bloodBankNameTextView = itemView.findViewById(R.id.bloodBankNameTextView);
            appoinmentTimeTextView = itemView.findViewById(R.id.workingHours);
            appoinmentStateTextView = itemView.findViewById(R.id.appoinmentStateTextView);
            appDayMonthTextView = itemView.findViewById(R.id.appDayMonthTextView);
            appYearTextView = itemView.findViewById(R.id.appYearTextView);

            editBtn = itemView.findViewById(R.id.editBtn);
        }
    }

}