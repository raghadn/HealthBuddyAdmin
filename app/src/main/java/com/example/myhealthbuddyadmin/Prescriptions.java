package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Prescriptions extends AppCompatActivity {

    Button writeTest;
    private RecyclerView prescriptionList;
    private DatabaseReference allSharesRef,allSRecordsRef,DoctorRef,PatientRef,HospitalRef;
    private FirebaseAuth mAuth;
    BottomNavigationView Doctorbottomnav;
    private String currentHCPuid,HospitalID,HospitalName,PatientKey;
    RecordAdapter mAdapter ;
    TextView Noresult;



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
        prescriptionList = (RecyclerView) findViewById(R.id.prescriptionList);
        prescriptionList.setHasFixedSize(true);
        prescriptionList.setLayoutManager(new LinearLayoutManager(this));
            Noresult=(TextView)findViewById(R.id.Noresult);


        BrowseShredPrescription();

    }

    public void BrowseShredPrescription() {

        final ArrayList<item_record>  records= new ArrayList<>();


        allSRecordsRef.orderByChild("pid").startAt(PatientKey).endAt(PatientKey+"\uf8ff");
        allSRecordsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {


                allSharesRef.orderByChild("hcp_uid").startAt(currentHCPuid).endAt(currentHCPuid+"\uf8ff");
                allSharesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                        for(DataSnapshot record:dataSnapshot1.getChildren()) {
                            for (DataSnapshot share : dataSnapshot2.getChildren()) {

                                if (record.getKey().equals(share.child("record_id").getValue().toString())){
                                    item_record r = record.getValue(item_record.class);
                                if(r.type==1) {
                                    // item_record r = record.getValue(item_record.class);
                                    r.rid = record.getKey();
                                    records.add(r);
                                }

                                }
                            }

                        }

                        mAdapter= new RecordAdapter(Prescriptions.this,records);
                        prescriptionList.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(String Rid) {

                                Toast.makeText(Prescriptions.this,"heeere "+Rid,Toast.LENGTH_LONG).show();


                             Intent intentPrescriptionList=new Intent(Prescriptions.this, ViewPrescription.class);
                                intentPrescriptionList.putExtra("Rid",Rid);
                                startActivity(intentPrescriptionList);




                            }
                        });
                        if(records.size()==0){
                            Noresult.setVisibility(View.VISIBLE);

                        }else Noresult.setVisibility(View.INVISIBLE);

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