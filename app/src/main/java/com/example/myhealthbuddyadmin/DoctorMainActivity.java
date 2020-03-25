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
import com.squareup.picasso.Picasso;

public class DoctorMainActivity extends AppCompatActivity {


    DatabaseReference RecordRef ,DoctorRef, PatientRef;
    FirebaseAuth mAuth;
    String currentDoctorid;
    RecyclerView RecordList;
    BottomNavigationView Doctorbottomnav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);


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
        myRecycler.setAdapter(new DoctorMainActivity.SampleRecycler());
        RecordList.setLayoutManager(new LinearLayoutManager(this));
        //Toast.makeText(this, HID, Toast.LENGTH_LONG).show();




        Browse();
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

                recordViweHolder.setDate(module.getDate());

                DoctorRef = FirebaseDatabase.getInstance().getReference().child("Doctors").child(currentDoctorid);
                DoctorRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String Doctor_Name = dataSnapshot.child("name").getValue().toString();
                        recordViweHolder.setDoctorName(Doctor_Name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

               /* PatientRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(module.getPid());
                PatientRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String Patient_name =dataSnapshot.child("name").getValue().toString();
                        recordViweHolder.setPatientName(Patient_name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/


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
        public void setPatientName(String Pname) {
            TextView myID=(TextView)mViwe.findViewById(R.id.patient_name);
            myID.setText(Pname);
        }

        public void setDate(String Date) {
            TextView myDate=(TextView) mViwe.findViewById(R.id.record_date);
            myDate.setText(Date);
        }
        public void setGender(String gender) {
            TextView mygender=(TextView) mViwe.findViewById(R.id.all_HCP_profile_gender);
            mygender.setText(gender);
        }
    }

    public class SampleHolder extends RecyclerView.ViewHolder {
        public SampleHolder(View itemView) {
            super(itemView);
        }
    }

    public class SampleRecycler extends RecyclerView.Adapter<MainActivity.SampleHolder> {
        @Override
        public MainActivity.SampleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(MainActivity.SampleHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    private void DoctorMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.d_nav_profile:
                Intent intentProfile = new Intent(DoctorMainActivity.this, DoctorProfile.class);
                startActivity(intentProfile);
                break;



        }

    }
}
