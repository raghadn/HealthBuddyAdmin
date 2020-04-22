package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewPatient extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    TextView patientNameT,natIDT,genderT;
    String name, nationalId;
    CardView prescriptions,bloodTest,Xray,VitalSigns,Records;
    BottomNavigationView Doctorbottomnav;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient);

        Doctorbottomnav=findViewById(R.id.d_bottom_navigation);
        Doctorbottomnav.setSelectedItemId(R.id.d_nav_home);
        Doctorbottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                DoctorMenuSelector(menuItem);
                return false;
            }
        });
        mAuth = FirebaseAuth.getInstance();

        patientNameT=findViewById(R.id.patientName);
        natIDT=findViewById(R.id.nid);
        genderT=findViewById(R.id.gender);


        prescriptions=findViewById(R.id.prescription);
        bloodTest=findViewById(R.id.bloodTest);
        Xray=findViewById(R.id.Xray);
        VitalSigns=findViewById(R.id.VitalSigns);
        Records=findViewById(R.id.Records);



        //get the id from the recyclerView
        final String id = getIntent().getExtras().get("PatientKey").toString();
        mRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(id);


        ///////////// ---- displaying patient info ---- /////////////
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name=dataSnapshot.child("name").getValue().toString();
                //gender=dataSnapshot.child("gender").getValue().toString();
                nationalId=dataSnapshot.child("national_id").getValue().toString();
                patientNameT.setText(name);
                //genderT.setText(gender);
                natIDT.setText(nationalId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ///////////// ---- redirect to activities ---- /////////////
        prescriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(ViewPatient.this,Prescriptions.class);
                redirect.putExtra("PatientKey",id);
                startActivity(redirect);
            }
        });

        bloodTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(ViewPatient.this,BloodTest.class);
                redirect.putExtra("PatientKey",id);
                startActivity(redirect);
            }
        });

        Xray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(ViewPatient.this,XRay.class);
                redirect.putExtra("PatientKey",id);
                startActivity(redirect);
            }
        });

        Records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(ViewPatient.this,Record.class);
                redirect.putExtra("PatientKey",id);
                startActivity(redirect);
            }
        });

        VitalSigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(ViewPatient.this,VitalSigns.class);
                redirect.putExtra("PatientKey",id);
                startActivity(redirect);
            }
        });
    }

    private void DoctorMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.d_nav_search:
                Intent intentSearch = new Intent(ViewPatient.this, SearchForPatient.class);
                startActivity(intentSearch);
                break;

            case R.id.d_nav_profile:
                Intent intentProfile = new Intent(ViewPatient.this, DoctorProfile.class);
                startActivity(intentProfile);
                break;

            case R.id.d_nav_notification:
                Intent intentNotifications = new Intent(ViewPatient.this, Notifications.class);
                startActivity(intentNotifications);
                break;


        }

    }
}


