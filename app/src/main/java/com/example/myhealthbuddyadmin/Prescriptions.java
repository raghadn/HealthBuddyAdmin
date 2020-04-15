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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Prescriptions extends AppCompatActivity {

    Button writeTest;
    private RecyclerView RecordList;
    private DatabaseReference allSharesRef,allSRecordsRef,DoctorRef,PatientRef,HospitalRef;
    private FirebaseAuth mAuth;
    BottomNavigationView Doctorbottomnav;
    private String currentHCPuid,HospitalID,HospitalName,PatientKey;
    ArrayList<String> recordsId= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);

        writeTest=findViewById(R.id.writeTest);

        PatientKey = getIntent().getExtras().get("PatientKey").toString();

        ///////////// ---- redirect to activities ---- /////////////
        writeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(Prescriptions.this,WritePrescription.class);
                redirect.putExtra("PatientKey",PatientKey);
                startActivity(redirect);
            }
        });



            mAuth = FirebaseAuth.getInstance();
            currentHCPuid= mAuth.getCurrentUser().getUid();
            allSharesRef = FirebaseDatabase.getInstance().getReference().child("Share");
            allSRecordsRef = FirebaseDatabase.getInstance().getReference().child("Records");

            Doctorbottomnav = findViewById(R.id.d_bottom_navigation);
            Doctorbottomnav.setSelectedItemId(R.id.d_nav_search);
            Doctorbottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    DoctorMenuSelector(menuItem);
                    return false;
                }
            });


            // RecyclerView
            RecordList = (RecyclerView) findViewById(R.id.RecordsList);
            RecordList.setHasFixedSize(true);
            RecordList.setLayoutManager(new LinearLayoutManager(this));

            RecyclerView myRecycler = (RecyclerView) findViewById(R.id.RecordsList);
            myRecycler.setLayoutManager(new LinearLayoutManager(this));
            myRecycler.setAdapter(new Prescriptions.SampleRecycler());


        Browse();

        }

    public void Browse() {

        Query DisplayInfiQuere =allSRecordsRef.orderByChild("pid").startAt(PatientKey).endAt(PatientKey+"\uf8ff");

        FirebaseRecyclerAdapter<item_record, Prescriptions.RecordsViweHolder> FirebaseRecycleAdapter
                = new FirebaseRecyclerAdapter<item_record, RecordsViweHolder>
                (
                        item_record.class,
                        R.layout.record_item,
                        Prescriptions.RecordsViweHolder.class,
                        DisplayInfiQuere
                ){
            @Override
            protected void populateViewHolder(final Prescriptions.RecordsViweHolder recordViweHolder, final item_record module, final int i) {

                recordViweHolder.setDate(module.getDate());

                PatientRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(PatientKey);
                PatientRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String  Patient_name= dataSnapshot.child("name").getValue().toString();

                        recordViweHolder.setPatientName(Patient_name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                DoctorRef = FirebaseDatabase.getInstance().getReference().child("Doctors").child(module.getDid());
                DoctorRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String  Doctor_Name=dataSnapshot.child("name").getValue().toString();
                        HospitalID =dataSnapshot.child("hospital").getValue().toString();

                        recordViweHolder.setDoctorName(Doctor_Name);
                       // recordViweHolder.setHospitalName( GetHospitalName());


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

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }
        };
        RecordList.setAdapter(FirebaseRecycleAdapter);
    }






        public static class RecordsViweHolder extends RecyclerView.ViewHolder {
            View mViwe;


            //defolt constroctor
            public RecordsViweHolder(@NonNull View itemView) {
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
            public void setRid(String rid) {
                TextView myRid=(TextView) mViwe.findViewById(R.id.Rid);
                myRid.setText(rid);
            }


        }

        public class SampleHolder extends RecyclerView.ViewHolder {
            public SampleHolder(View itemView) {
                super(itemView);
            }

        }

        public class SampleRecycler extends RecyclerView.Adapter<Prescriptions.SampleHolder> {
            @Override
            public Prescriptions.SampleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(Prescriptions.SampleHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        }


    public String GetHospitalName(String rid){



        DoctorRef=  FirebaseDatabase.getInstance().getReference().child("Records").child(rid);
        DoctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HospitalID= dataSnapshot.child("did").getValue().toString();


                PatientRef=FirebaseDatabase.getInstance().getReference().child("Doctors").child(HospitalID);
                PatientRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HospitalID=dataSnapshot.child("hospital").getValue().toString();



                        HospitalRef= FirebaseDatabase.getInstance().getReference().child("Hospitals").child(HospitalID);
                        HospitalRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HospitalName =dataSnapshot.child("Name").getValue().toString();
                                // recordViweHolder.setHospitalName(HospitalName);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return HospitalName;
    }


    private void DoctorMenuSelector (MenuItem item){
        switch (item.getItemId()) {

            case R.id.d_nav_profile:
                Intent intentProfile = new Intent(Prescriptions.this, DoctorProfile.class);
                startActivity(intentProfile);
                break;

            case R.id.d_nav_home:
                Intent intentHome = new Intent(Prescriptions.this, DoctorMainActivity.class);
                startActivity(intentHome);
                break;
        }

    }

    }

