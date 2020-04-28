package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewPrescription extends AppCompatActivity {

    String recordID,hid,pid;
    TextView doctorNameT,doctorsSpecialtyT, patientNameT,hospitalNameT,creationDate,creationTime;
    DatabaseReference recordRef, patientRef ,hospitalRef;
    TextView medicationT,doseT,durationT,timeT,noteT,patientN,patientID,patientG;;
    Button attachmentView,done;
    BottomNavigationView Doctorbottomnav;
    ImageView edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_prescription);
edit=findViewById(R.id.editprescription);
        Doctorbottomnav=findViewById(R.id.d_bottom_navigation);
        Doctorbottomnav.setSelectedItemId(R.id.d_nav_home);
        Doctorbottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                DoctorMenuSelector(menuItem);
                return false;
            }
        });

        recordID = getIntent().getExtras().get("recordID").toString();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit =new Intent(ViewPrescription.this,EditPrescription.class);
                Bundle extras = new Bundle();
                extras.putString("PatientKey", pid);
                extras.putString("RecordID", recordID);
                edit.putExtras(extras);
                startActivity(edit);
                finish();
            }
        });
        doctorNameT=findViewById(R.id.doctorName);
        doctorsSpecialtyT=findViewById(R.id.doctorsSpecialty);
        patientNameT=findViewById(R.id.patientName);
        hospitalNameT=findViewById(R.id.hospitalName);

        medicationT=findViewById(R.id.testDate);
        doseT=findViewById(R.id.findings);
        timeT=findViewById(R.id.impression);
        durationT=findViewById(R.id.duration);
        noteT=findViewById(R.id.note);
        creationDate=findViewById(R.id.creationDate);
        creationTime=findViewById(R.id.creationTime);

        done=findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        patientN=findViewById(R.id.patientName);
        patientID=findViewById(R.id.patientID);
        patientG=findViewById(R.id.gender);



        recordRef = FirebaseDatabase.getInstance().getReference().child("Records").child(recordID);
        recordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //record info
                String medication,dose,duration,time,note;
                if(dataSnapshot.hasChild("medication")){
                    medication=dataSnapshot.child("medication").getValue().toString();
                    medicationT.setText(medication);
                }

                if(dataSnapshot.hasChild("dose")){
                    dose=dataSnapshot.child("dose").getValue().toString();
                    doseT.setText(dose);
                }else{
                    doseT.setVisibility(View.GONE);
                    findViewById(R.id.findingsL).setVisibility(View.GONE);
                }

                if(dataSnapshot.hasChild("duration")){
                    duration=dataSnapshot.child("duration").getValue().toString();
                    durationT.setText(duration);
                }else{
                    durationT.setVisibility(View.GONE);
                    findViewById(R.id.textView55).setVisibility(View.GONE);
                }

                if(dataSnapshot.hasChild("timeOfPrescription")){
                    time=dataSnapshot.child("timeOfPrescription").getValue().toString();
                    timeT.setText(time);
                }else{
                    timeT.setVisibility(View.GONE);
                    findViewById(R.id.impressionL).setVisibility(View.GONE);
                }

                if(dataSnapshot.hasChild("note")){
                    note=dataSnapshot.child("note").getValue().toString();
                    noteT.setText(note);
                }else{
                    noteT.setVisibility(View.GONE);
                    findViewById(R.id.notesL).setVisibility(View.GONE);
                }

                //doctor who wrote this record
                String doctorName, doctorsSpecialty;

                doctorName=dataSnapshot.child("doctorName").getValue().toString();
                doctorNameT.setText(doctorName);

                doctorsSpecialty=dataSnapshot.child("doctorSpeciality").getValue().toString();
                doctorsSpecialtyT.setText(doctorsSpecialty);

                creationDate.setText(dataSnapshot.child("dateCreated").getValue().toString()+" at ");
                creationTime.setText(dataSnapshot.child("timeCreated").getValue().toString());
                hid=dataSnapshot.child("hospital").getValue().toString();
                pid=dataSnapshot.child("pid").getValue().toString();

                //patient
                patientRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(pid);
                patientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        patientN.setText(dataSnapshot.child("name").getValue().toString());
                        patientID.setText(dataSnapshot.child("national_id").getValue().toString());
                        patientG.setText(dataSnapshot.child("gender").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                //hospital
                hospitalRef = FirebaseDatabase.getInstance().getReference().child("Hospitals").child(hid);
                hospitalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        hospitalNameT.setText(dataSnapshot.child("Name").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                //pdf
                if(dataSnapshot.hasChild("file")){
                    attachmentView=findViewById(R.id.attachmentView);
                    attachmentView.setVisibility(View.VISIBLE);
                    final String url = dataSnapshot.child("file").getValue().toString();
                    attachmentView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //pdfviewer
                            Intent redirect = new Intent(ViewPrescription.this,ReadActivity.class);
                            redirect.putExtra("url", url);
                            redirect.putExtra("recordID", recordID);
                            startActivity(redirect);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void DoctorMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.d_nav_search:
                Intent intentSearch = new Intent(ViewPrescription.this, SearchForPatient.class);
                startActivity(intentSearch);
                break;

            case R.id.d_nav_profile:
                Intent intentProfile = new Intent(ViewPrescription.this, DoctorProfile.class);
                startActivity(intentProfile);
                break;

            case R.id.d_nav_notification:
                Intent intentNotifications = new Intent(ViewPrescription.this, Notifications.class);
                startActivity(intentNotifications);
                break;


        }

    }


}
