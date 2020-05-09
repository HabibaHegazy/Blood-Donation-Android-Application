package com.example.blood_donation_project.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blood_donation_project.Fragments.Admin_panel.EditAdminFragment;
import com.example.blood_donation_project.MainActivity;
import com.example.blood_donation_project.Models.Admin;
import com.example.blood_donation_project.R;
import com.example.blood_donation_project.Util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminViewAdapter extends RecyclerView.Adapter<AdminViewAdapter.ViewHolder>{

    List<Admin> adminList;
    String uid;

    public AdminViewAdapter(List<Admin> adminList) {
        this.adminList = adminList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_admin_recyclerview_items, parent, false);
        return new AdminViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Admin admin = adminList.get(position);
        holder.adminNameTV.setText(admin.getFirstName()+ " " + admin.getLastName());
        holder.adminEmailTV.setText(admin.getEmail());
        //uid=admin.getId();

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> editInformation = new ArrayList<>();
                editInformation.add(admin.getFirstName());
                editInformation.add(admin.getLastName());
                editInformation.add(admin.getBirthdate());
                editInformation.add(admin.getGender());
                editInformation.add(admin.getPhoneNumber());

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragment = new EditAdminFragment(editInformation);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //MainActivity.databaseReference = FirebaseDatabase.getInstance().getReference();
                 //MainActivity.databaseReference.child("tb_users").child(uid).setValue(null);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query emailsQuery = ref.child("tb_users").orderByChild("email").equalTo(admin.getEmail());

                emailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot emailSnapshot: dataSnapshot.getChildren()) {
                            emailSnapshot.getRef().removeValue();

                        }
                        //Toast.makeText(getActivity(), "Registration Done", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                }
        });
    }

    @Override
    public int getItemCount() {

        if(adminList == null){
            return 0;
        }
        return adminList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView adminNameTV, adminEmailTV;
        public Button editBtn,deleteBtn;

        public ViewHolder(View itemView) {

            super(itemView);
            adminNameTV = itemView.findViewById(R.id.admin_name_rv);
            adminEmailTV = itemView.findViewById(R.id.admin_email_rv);
            editBtn = itemView.findViewById(R.id.admin_editBtn_rv);
            deleteBtn = itemView.findViewById(R.id.admin_deleteBtn_rv);
        }
    }

}
