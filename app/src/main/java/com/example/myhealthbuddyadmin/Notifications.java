package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.TwitterAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Notifications extends AppCompatActivity {

    BottomNavigationView Doctorbottomnav;
    private RecyclerView notificationList;

    private DatabaseReference requestsRef,PatientRef,docRef;
    String currentDoctorid,uid;
    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Doctorbottomnav=findViewById(R.id.d_bottom_navigation);
        Doctorbottomnav.setSelectedItemId(R.id.d_nav_home);
        Doctorbottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                DoctorMenuSelector(menuItem);
                return false;
            }
        });

        requestsRef= FirebaseDatabase.getInstance().getReference().child("Requests");
        //PatientRef=FirebaseDatabase.getInstance().getReference().child("Patients");
        mAuth = FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid().toString();

        docRef=FirebaseDatabase.getInstance().getReference().child("Doctors");
        docRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentDoctorid=dataSnapshot.child("id").getValue().toString();
                displayNotifications(currentDoctorid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        notificationList=(RecyclerView)findViewById(R.id.NotificationList);
        notificationList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        notificationList.setLayoutManager(linearLayoutManager);



    }

    private void displayNotifications(String currentDoctorid) {
        Query query =requestsRef.orderByChild("doctor_id").startAt(currentDoctorid).endAt(currentDoctorid+"\uf8ff");


        FirebaseRecyclerAdapter<DoctorRequests,NotificationsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<DoctorRequests, NotificationsViewHolder>(DoctorRequests.class,R.layout.display_notifications,NotificationsViewHolder.class,query) {
            @Override
            protected void populateViewHolder(final NotificationsViewHolder notificationsViewHolder, DoctorRequests doctorRequests, int i) {
                notificationsViewHolder.setPatient_id(doctorRequests.getPatient_id());
                notificationsViewHolder.setType(doctorRequests.getType());
                notificationsViewHolder.setDate(doctorRequests.getDate());

                PatientRef = FirebaseDatabase.getInstance().getReference().child("Patients");
                PatientRef.child(doctorRequests.getPatient_uid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                        String Patient_name =dataSnapshot.child("name").getValue().toString();

                        notificationsViewHolder.setPatientName(Patient_name);}

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

        };

        notificationList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class NotificationsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setPatient_id(String patient_id){
            TextView patientid=(TextView)mView.findViewById(R.id.display_request_id);
            patientid.setText(patient_id);
        }

        public void setType(String type){
            ImageView imageView=(ImageView)mView.findViewById(R.id.display_request_image);
            TextView t=(TextView)mView.findViewById(R.id.display_request_type);
            t.setText(type);
            if(type.equals("medicalreport")){
                imageView.setImageResource(R.drawable.report);
            }
            if(type.equals("Radiologyreport")){
                imageView.setImageResource(R.drawable.nuclear);
            }
            if(type.equals("prescription")){
                imageView.setImageResource(R.drawable.pills);
            }
            if(type.equals("vitalsigns")){
                imageView.setImageResource(R.drawable.cardiogram);
            }


        }
        public void setPatientName(String Pname) {
            TextView myID=(TextView)mView.findViewById(R.id.display_request_name);
            myID.setText(Pname);
        }
        public void setDate(String date){
            TextView myDate=(TextView)mView.findViewById(R.id.display_request_Date);
            myDate.setText(date);
        }

    }



    private void DoctorMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.d_nav_search:
                Intent intentSearch = new Intent(Notifications.this, SearchForPatient.class);
                startActivity(intentSearch);
                break;

            case R.id.d_nav_profile:
                Intent intentProfile = new Intent(Notifications.this, DoctorProfile.class);
                startActivity(intentProfile);
                break;

            case R.id.d_nav_home:
                Intent intentHome = new Intent(Notifications.this, DoctorMainActivity.class);
                startActivity(intentHome);
                break;


        }

    }
}
