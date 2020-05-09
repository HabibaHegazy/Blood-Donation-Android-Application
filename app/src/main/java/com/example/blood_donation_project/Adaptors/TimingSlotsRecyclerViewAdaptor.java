package com.example.blood_donation_project.Adaptors;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blood_donation_project.Models.Slots;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.CustomDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TimingSlotsRecyclerViewAdaptor extends RecyclerView.Adapter<TimingSlotsRecyclerViewAdaptor.ViewHolder> {

    List<Slots> slotsList;
    Context context;

    public TimingSlotsRecyclerViewAdaptor(Context context, List<Slots> slotsList) {
        this.slotsList = slotsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timing_slots_recyclerview_items, parent, false);
        return new TimingSlotsRecyclerViewAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Slots slot = slotsList.get(position);

        holder.timingSlotDayTextView.setText(slot.getSlot_day());
        holder.timingSlotIntervalsTextView.setText(slot.getTiming());
        holder.timingSlotStatusTextView.setText(slot.getStatus());

        if(slot.getStatus().equals("Enable")){

            holder.changeStatusBtn.setText("Disable");
            holder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                CustomDialog customDialogTimingSlot = new CustomDialog(5, slot);
                customDialogTimingSlot.show(((AppCompatActivity) context).getSupportFragmentManager(), "Edit Timing Slot");
                }
            });

        }else{
            holder.timingSlotLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));
            holder.editBtn.setVisibility(View.GONE);
            holder.changeStatusBtn.setText("Enable");
        }

        holder.changeStatusBtn.setOnClickListener(new View.OnClickListener() {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            @Override
            public void onClick(View v) {
                if(holder.changeStatusBtn.getText().equals("Enable")){
                    holder.timingSlotLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    holder.changeStatusBtn.setText("Disable");
                    holder.timingSlotStatusTextView.setText("Disable");
                    databaseReference.child("tb_slots").child(slot.getSlotID()).child("status").setValue("Enable");

                }else{
                    holder.timingSlotLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    holder.changeStatusBtn.setText("Enable");
                    holder.timingSlotStatusTextView.setText("Enable");
                    databaseReference.child("tb_slots").child(slot.getSlotID()).child("status").setValue("Disable");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(slotsList == null){
            return 0;
        }
        return slotsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout timingSlotLayout;
        Button editBtn, changeStatusBtn;
        TextView timingSlotDayTextView, timingSlotIntervalsTextView, timingSlotStatusTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            timingSlotLayout = itemView.findViewById(R.id.timingSlotLayout);

            timingSlotDayTextView = itemView.findViewById(R.id.timingslotDayTextView);
            timingSlotIntervalsTextView = itemView.findViewById(R.id.timingSlotIntervalsTextView);
            timingSlotStatusTextView = itemView.findViewById(R.id.timingSlotStatusTextView);

            editBtn = itemView.findViewById(R.id.EditTimingBtn);
            changeStatusBtn = itemView.findViewById(R.id.changeStatusBtn);

        }
    }

}
