package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

public class DoctorMainActivity extends AppCompatActivity {
//added the redirect to record when clicked viewRecord method

    DatabaseReference RecordRef ,DoctorRef, PatientRef,HospitalRef;
    FirebaseAuth mAuth;
    String currentDoctorid, HospitalID, HospitalName;
    RecyclerView RecordList;
    BottomNavigationView Doctorbottomnav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();



        Doctorbottomnav=findViewById(R.id.d_bottom_navigation);
        Doctorbottomnav.setSelectedItemId(R.id.d_nav_home);
        Doctorbottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                DoctorMenuSelector(menuItem);
                return false;
            }
        });





        RecordRef = FirebaseDatabase.getInstance().getReference().child("Records");
        mAuth = FirebaseAuth.getInstance();
        currentDoctorid = mAuth.getCurrentUser().getUid();


        RecordList= findViewById(R.id.recordlist);
        RecordList.setHasFixedSize(true);
        RecyclerView myRecycler = (RecyclerView) findViewById(R.id.recordlist);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));
        RecordList.setLayoutManager(new LinearLayoutManager(this));
        //Toast.makeText(this, HID, Toast.LENGTH_LONG).show();



        mAuth = FirebaseAuth.getInstance();



        OneSignal.sendTag("User_uid",mAuth.getCurrentUser().getUid().toString());






        //Browse();
    }

    public void Browse() {

        Query DisplayInfiQuere =RecordRef.orderByChild("did").startAt(currentDoctorid).endAt(currentDoctorid+"\uf8ff");

        FirebaseRecyclerAdapter<item_record, DoctorMainActivity.RecordViweHolder> FirebaseRecycleAdapter
                = new FirebaseRecyclerAdapter<item_record, DoctorMainActivity.RecordViweHolder>
                (
                        item_record.class,
                        R.layout.record_item,
                        DoctorMainActivity.RecordViweHolder.class,
                        DisplayInfiQuere
                ){
            @Override
            protected void populateViewHolder(final DoctorMainActivity.RecordViweHolder recordViweHolder, final item_record module, final int i) {

                recordViweHolder.setDate(module.getDateCreated());

                DoctorRef = FirebaseDatabase.getInstance().getReference().child("Doctors").child(currentDoctorid);
                DoctorRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String Doctor_Name = dataSnapshot.child("name").getValue().toString();
                       // HospitalID =dataSnapshot.child("hospital").getValue().toString();
                        recordViweHolder.setDoctorName(Doctor_Name);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


                PatientRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(module.getPid());
                PatientRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String Patient_name =dataSnapshot.child("name").getValue().toString();
                        recordViweHolder.setPatientName(Patient_name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                HospitalRef= FirebaseDatabase.getInstance().getReference().child("Hospitals").child("122");
                HospitalRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HospitalName =dataSnapshot.child("Name").getValue().toString();
                        recordViweHolder.setHospitalName(HospitalName);

                        //redirect based on record type
                        viewRecord(module.getType());

                    }

                    private void viewRecord(int type) {
                        switch(type) {
                            case 1:
                                recordViweHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String recordID = getRef(i).getKey();
                                        Intent intent = new Intent(DoctorMainActivity.this, ViewPrescription.class);
                                        intent.putExtra("recordID", recordID);
                                        intent.putExtra("hospitalName", HospitalName); //for view record only not write!
                                        startActivity(intent);
                                    }
                                });

                                break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }


        };
        RecordList.setAdapter(FirebaseRecycleAdapter);
    }




    public static class RecordViweHolder extends RecyclerView.ViewHolder {
        View mViwe;


        //defolt constroctor
        public RecordViweHolder(@NonNull View itemView) {
            super(itemView);
            mViwe = itemView;
        }

        public void setDoctorName(String DName) {
            TextView MyName= (TextView)mViwe.findViewById(R.id.d_name);
            MyName.setText(DName);
        }

        public void setHospitalName(String HName) {
            TextView MyName= (TextView)mViwe.findViewById(R.id.hname);
            MyName.setText(HName);
        }

        public void setPatientName(String Pname) {
            TextView myID=(TextView)mViwe.findViewById(R.id.patient_name);
            myID.setText(Pname);
        }

        public void setDate(String Date) {
            TextView myDate=(TextView) mViwe.findViewById(R.id.record_date);
            myDate.setText(Date);
        }

    }



    private void DoctorMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.d_nav_search:
                Intent intentSearch = new Intent(DoctorMainActivity.this, SearchForPatient.class);
                startActivity(intentSearch);
                break;

            case R.id.d_nav_profile:
                Intent intentProfile = new Intent(DoctorMainActivity.this, DoctorProfile.class);
                startActivity(intentProfile);
                break;

            case R.id.d_nav_notification:
                Intent intentNotifications = new Intent(DoctorMainActivity.this, Notifications.class);
                startActivity(intentNotifications);
                break;


        }

    }


}
