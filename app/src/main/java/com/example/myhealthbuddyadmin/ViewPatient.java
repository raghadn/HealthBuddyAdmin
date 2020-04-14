package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewPatient extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    TextView patientNameT,ageT,genderT;
    String name,gender;
    int age;
    Button prescriptions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient);

        mAuth = FirebaseAuth.getInstance();

        patientNameT=findViewById(R.id.patientName);
        ageT=findViewById(R.id.Age);
        genderT=findViewById(R.id.Gender);
        prescriptions=findViewById(R.id.prescription);


        //get the id from the recyclerView
        final String id = getIntent().getExtras().get("PatientKey").toString();
        mRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(id);


        ///////////// ---- displaying patient info ---- /////////////
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name=dataSnapshot.child("name").getValue().toString();
                patientNameT.setText(name);

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
    }
}


