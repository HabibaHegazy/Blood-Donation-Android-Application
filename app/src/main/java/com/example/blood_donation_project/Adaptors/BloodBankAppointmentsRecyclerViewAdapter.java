package com.example.blood_donation_project.Adaptors;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blood_donation_project.BloodBankActivity;
import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.Appointments;
import com.example.blood_donation_project.Models.Donor;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.example.blood_donation_project.Util.CustomDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.List;

public class BloodBankAppointmentsRecyclerViewAdapter extends RecyclerView.Adapter<BloodBankAppointmentsRecyclerViewAdapter.ViewHolder> {

    List<Appointments> appointmentsList;
    Context context;

    public BloodBankAppointmentsRecyclerViewAdapter(Context context, List<Appointments> appointments) {
        this.appointmentsList = appointments;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointments_recyclerview_items, parent, false);
        return new BloodBankAppointmentsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        final Appointments appointment = appointmentsList.get(position);

        holder.timingSlotTextView.setText(appointment.getSlot_reserved_data().getTiming());

        MainActivity.databaseReference.child("tb_users").child(appointment.getDonor_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Donor donor = dataSnapshot.getValue(Donor.class);

                // from donor table
                holder.donorNameTextView.setText(donor.getFirstName() + " " + donor.getLastName());
                holder.donorBloodTypeTextView.setText(donor.getBloodType());
                //setImage(donor.getImageName(), holder.donorImage);

                if(appointment.getAppointment_state().equals("reserved")){

                    if(donor.getBloodTypeTest() == 0){ // means needs a blood test first
                        holder.bloodTestBtn.setVisibility(View.VISIBLE);
                        holder.reciveBtn.setVisibility(View.GONE);

                        holder.bloodTestBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // show panel to save to firebase......
                                CustomDialog customDialogBloodTest = new CustomDialog(3);
                                customDialogBloodTest.show(((AppCompatActivity) context).getSupportFragmentManager(), "Blood Test");


                                holder.bloodTestBtn.setVisibility(View.GONE);
                                holder.reciveBtn.setVisibility(View.VISIBLE);
                            }
                        });


                    }else{

                        holder.reciveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final DatabaseReference reference = MainActivity.databaseReference.child("tb_stocks").child(Constants.BloodBankNameForTimingSlots);

                                reference.addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();

                                        while (items.hasNext()) {
                                            DataSnapshot item = items.next();
                                            int count = Integer.parseInt(item.child(donor.getBloodType()).getValue().toString());
                                            reference.child(donor.getBloodType()).setValue(++count);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                                });


                                MainActivity.databaseReference.child("tb_appointments").child(appointment.getAppointment_id()).child("appointment_state").setValue("donated");

                                holder.missedBtn.setVisibility(View.GONE);
                                holder.reciveBtn.setVisibility(View.GONE);

                                holder.appointmentsLayout.setBackgroundColor(Color.parseColor("#587cca"));
                            }
                        });
                    }

                    holder.missedBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity.databaseReference.child("tb_appointments").child(appointment.getAppointment_id()).child("appointment_state").setValue("missed");

                            holder.missedBtn.setVisibility(View.GONE);
                            holder.reciveBtn.setVisibility(View.GONE);

                            holder.appointmentsLayout.setBackgroundColor(Color.parseColor("#C35A48"));
                        }
                    });
                }
                else {

                    holder.missedBtn.setVisibility(View.GONE);
                    holder.reciveBtn.setVisibility(View.GONE);

                    if(appointment.getAppointment_state().equals("missed")){
                        holder.appointmentsLayout.setBackgroundColor(Color.parseColor("#C35A48"));
                    }else{
                        holder.appointmentsLayout.setBackgroundColor(Color.parseColor("#587cca"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public int getItemCount() {
        if(appointmentsList == null){
            return 0;
        }
        return appointmentsList.size();
    }

    /*private void setImage(String imageName, final ImageView imageView){

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        storageReference.child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {}
        });
    }*/

    class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout appointmentsLayout;
        ImageView donorImage;
        ImageButton reciveBtn, missedBtn, bloodTestBtn;
        TextView donorNameTextView, donorBloodTypeTextView, timingSlotTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            appointmentsLayout = itemView.findViewById(R.id.appointmentsLayout);

            //donorImage = itemView.findViewById(R.id.date_container);

            donorNameTextView = itemView.findViewById(R.id.donorNameTextView);
            donorBloodTypeTextView = itemView.findViewById(R.id.bloodTypeTextView);
            timingSlotTextView = itemView.findViewById(R.id.timingSlotTextView);

            reciveBtn = itemView.findViewById(R.id.recievedDonationBtn);
            missedBtn = itemView.findViewById(R.id.missedDonationBtn);
            bloodTestBtn = itemView.findViewById(R.id.bloodTestBtn);

        }
    }



}
