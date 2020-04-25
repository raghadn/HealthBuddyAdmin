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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Prescriptions extends AppCompatActivity {

    FloatingActionButton writeTest;
    private RecyclerView prescriptionList;
    private DatabaseReference allSharesRef,allRecordsRef;
    private FirebaseAuth mAuth;
    BottomNavigationView Doctorbottomnav;
    private String currentHCPuid,PatientKey;
    RecordAdapter mAdapter ;
    TextView Noresult,PageTitel;
    int type;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);

        writeTest=findViewById(R.id.writeTest);

        PatientKey = getIntent().getExtras().get("PatientKey").toString();
        type=(int)getIntent().getExtras().get("type");

        ///////////// ---- redirect to activities ---- /////////////
        writeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               switch(type){
                    case 1:  Intent writePres=new Intent(Prescriptions.this, WritePrescription.class);
                        writePres.putExtra("PatientKey",PatientKey);
                        writePres.putExtra("Request","N");
                        startActivity(writePres);
                        break;
                    case 2: Intent wBloodTests = new Intent(Prescriptions.this, WriteBloodTest.class);
                        wBloodTests.putExtra("PatientKey",PatientKey);
                        wBloodTests.putExtra("Request","N");
                        startActivity(wBloodTests);
                        break;
                    case 3:  Intent wx_Rays = new Intent(Prescriptions.this, WriteXRay.class);
                        wx_Rays.putExtra("PatientKey",PatientKey);
                        wx_Rays.putExtra("Request","N");
                        startActivity(wx_Rays);
                        break;
                    case 4: Intent wVital = new Intent(Prescriptions.this, WriteVitalSigns.class);
                        wVital.putExtra("PatientKey",PatientKey);
                        wVital.putExtra("Request","N");
                        startActivity(wVital);
                        break;
                    case 5:Intent wRecord = new Intent(Prescriptions.this, WriteRecord.class);
                        wRecord.putExtra("PatientKey",PatientKey);
                        wRecord.putExtra("Request","N");
                        startActivity(wRecord);
                        break;
                }

            }
        });



            mAuth = FirebaseAuth.getInstance();
            currentHCPuid= mAuth.getCurrentUser().getUid();
            allSharesRef = FirebaseDatabase.getInstance().getReference().child("Share");
            allRecordsRef = FirebaseDatabase.getInstance().getReference().child("Records");

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
        prescriptionList = findViewById(R.id.prescriptionList);
        prescriptionList.setHasFixedSize(true);
        prescriptionList.setLayoutManager(new LinearLayoutManager(this));

        Noresult=findViewById(R.id.Noresult);
        PageTitel=findViewById(R.id.Title);

       switch(type){

            case 1: Noresult.setText("No prescriptions available  ");
                  PageTitel.setText("Prescriptions");
                break;
            case 2: Noresult.setText("No blood tests available   ");
                PageTitel.setText("Blood Tests");
                break;
            case 3: Noresult.setText("No X-Rays available  ");
               PageTitel.setText("X-Rays" );
                break;
            case 4: Noresult.setText("No vital signs available");
                PageTitel.setText("Vital Signs");

                break;
            case 5: Noresult.setText("No record available ");
               PageTitel.setText("Records");
                break;
        }


        BrowseShred();
       //BrowseWrite();

    }

    private void BrowseWrite() {
        final ArrayList<item_record>  records= new ArrayList<>();


         allRecordsRef.orderByChild("pid").equalTo(PatientKey+"\uf8ff");
         allRecordsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {

                for (DataSnapshot record : dataSnapshot1.getChildren()) {
                    Toast.makeText(Prescriptions.this, "here" + currentHCPuid, Toast.LENGTH_LONG).show();
                    if (record.child("did").getValue().toString().equals(currentHCPuid)) {
                        item_record r = record.getValue(item_record.class);
                        if (r.type == type) {
                            r.rid = record.getKey();
                            records.add(r);
                        }

                    }
                }

                mAdapter = new RecordAdapter(Prescriptions.this, records);
                prescriptionList.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String Rid) {

                        switch (type) {
                            case 1:
                                Intent intentPrescriptionList = new Intent(Prescriptions.this, ViewPrescription.class);
                                intentPrescriptionList.putExtra("recordID", Rid);
                                startActivity(intentPrescriptionList);
                                break;
                            case 2:
                                Intent intentMyBloodTests = new Intent(Prescriptions.this, ViewBloodTest.class);
                                intentMyBloodTests.putExtra("Rid", Rid);
                                startActivity(intentMyBloodTests);
                                break;
                            case 3:
                                Intent intentMyx_Rays = new Intent(Prescriptions.this, ViewXRay.class);
                                intentMyx_Rays.putExtra("Rid", Rid);
                                startActivity(intentMyx_Rays);
                                break;
                            case 4:
                                Intent intentMyVital = new Intent(Prescriptions.this, ViewVitalSigns.class);
                                intentMyVital.putExtra("Rid", Rid);
                                startActivity(intentMyVital);
                                break;
                            case 5:
                                Intent intentRecord = new Intent(Prescriptions.this, ViewRecord.class);
                                intentRecord.putExtra("Rid", Rid);
                                startActivity(intentRecord);
                                break;
                        }


                    }
                });
                if (records.size() == 0) {
                    Noresult.setVisibility(View.VISIBLE);

                } else Noresult.setVisibility(View.INVISIBLE);
            }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    public void BrowseShred() {

        final ArrayList<item_record>  records= new ArrayList<>();


       Query pAllRecord= allRecordsRef.orderByChild("pid").startAt(PatientKey+"\uf8ff").endAt(PatientKey+"\uf8ff");
        pAllRecord.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {


                Query shared =allSharesRef.orderByChild("hcp_uid").equalTo(currentHCPuid+"\uf8ff");
                shared.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        if(dataSnapshot2.exists()) {

                            for (DataSnapshot record : dataSnapshot1.getChildren()) {
                                for (DataSnapshot share : dataSnapshot2.getChildren()) {
                                    if (record.getKey().equals(share.child("record_id").getValue().toString())){
                                        if (record.child("type").getValue().toString().equals((type))){
                                            item_record r = record.getValue(item_record.class);
                                            r.rid = record.getKey();
                                            records.add(r);
                                        }

                                    }
                                }

                            }

                            mAdapter = new RecordAdapter(Prescriptions.this, records);
                            prescriptionList.setAdapter(mAdapter);
                            mAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(String Rid) {

                                    switch (type) {
                                        case 1:
                                            Intent intentPrescriptionList = new Intent(Prescriptions.this, ViewPrescription.class);
                                            intentPrescriptionList.putExtra("recordID", Rid);
                                            startActivity(intentPrescriptionList);
                                            break;
                                        case 2:
                                            Intent intentMyBloodTests = new Intent(Prescriptions.this, ViewBloodTest.class);
                                            intentMyBloodTests.putExtra("Rid", Rid);
                                            startActivity(intentMyBloodTests);
                                            break;
                                        case 3:
                                            Intent intentMyx_Rays = new Intent(Prescriptions.this, ViewXRay.class);
                                            intentMyx_Rays.putExtra("Rid", Rid);
                                            startActivity(intentMyx_Rays);
                                            break;
                                        case 4:
                                            Intent intentMyVital = new Intent(Prescriptions.this, ViewVitalSigns.class);
                                            intentMyVital.putExtra("Rid", Rid);
                                            startActivity(intentMyVital);
                                            break;
                                        case 5:
                                            Intent intentRecord = new Intent(Prescriptions.this, ViewRecord.class);
                                            intentRecord.putExtra("Rid", Rid);
                                            startActivity(intentRecord);
                                            break;
                                    }


                                }
                            });
                        }
                            if (records.size() == 0) {
                                Noresult.setVisibility(View.VISIBLE);

                            } else Noresult.setVisibility(View.INVISIBLE);


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