package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewBloodTest extends AppCompatActivity {

    String recordID,hid,pid,did;
    TextView doctorNameT,doctorsSpecialtyT, patientNameT,hospitalNameT,creationDate,creationTime,patientN,patientID,patientG;
    DatabaseReference recordRef, patientRef ,hospitalRef;
    TextView testDateT,noteT;
    TextView findingsT,impressionT,durationT;
    Button attachmentView,done;
    BottomNavigationView Doctorbottomnav;
    RecyclerView recyclerV;
    ImageView edit;
    String currentuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blood_test);
edit=findViewById(R.id.editbloodtest);
        currentuser= FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                Intent edit =new Intent(ViewBloodTest.this,EditBloodTest.class);
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
        creationDate=findViewById(R.id.creationDate);
        creationTime=findViewById(R.id.creationTime);
        patientN=findViewById(R.id.patientName);
        patientID=findViewById(R.id.patientID);
        patientG=findViewById(R.id.gender);

        done=findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        testDateT=findViewById(R.id.testDate);
        noteT=findViewById(R.id.note);




        FirebaseDatabase.getInstance().getReference().child("Records").child(recordID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //record info
                String note;

                testDateT.setText(dataSnapshot.child("testDate").getValue().toString());


                if(dataSnapshot.hasChild("note")){
                    note=dataSnapshot.child("note").getValue().toString();
                    noteT.setText(note);
                }else{
                    noteT.setVisibility(View.GONE);
                    findViewById(R.id.noteL).setVisibility(View.GONE);
                }
                //tests
                if(dataSnapshot.hasChild("BloodTest")){
                    //recycler view
                    recyclerV=findViewById(R.id.recy);
                    recyclerV.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());////?
                    linearLayoutManager.setReverseLayout(true);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerV.setLayoutManager(linearLayoutManager);
                    displayBloodTests();
                }



                //doctor who wrote this record
                String doctorName, doctorsSpecialty;

                doctorName=dataSnapshot.child("doctorName").getValue().toString();
                doctorNameT.setText(doctorName);

                doctorsSpecialty=dataSnapshot.child("doctorSpeciality").getValue().toString();
                doctorsSpecialtyT.setText(doctorsSpecialty);
                did=dataSnapshot.child("did").getValue().toString();
// for edit
                if (currentuser.equals(did)){
                    System.out.println("true");
                    edit.setVisibility(View.VISIBLE);
                }
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
                        //patientG.setText(dataSnapshot.child("").getValue().toString());
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
                            Intent redirect = new Intent(ViewBloodTest.this,ReadActivity.class);
                            redirect.putExtra("url", url);
                            redirect.putExtra("recordID", recordID);
                            startActivity(redirect);
                        }
                    });
                }
                //tests
                if(dataSnapshot.hasChild("BloodTest")){
                    //recycler view
                    recyclerV=findViewById(R.id.recy);
                    recyclerV.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());////?
                    linearLayoutManager.setReverseLayout(true);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerV.setLayoutManager(linearLayoutManager);
                    displayBloodTests();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }//onc

    private void displayBloodTests() {
        final Query query = recordRef.child(recordID).child("BloodTest");
        FirebaseRecyclerAdapter<btinfo,WriteBloodTest.viewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<btinfo, WriteBloodTest.viewHolder>(
                        btinfo.class,
                        R.layout.btitem,
                        WriteBloodTest.viewHolder.class,
                        query
                ) {
                    @Override
                    protected void populateViewHolder(WriteBloodTest.viewHolder viewHolder, btinfo btinfo, final int i) {
                        //set information in each row
                        Double min, max,res;
                        max=btinfo.getNormalMax();
                        min=btinfo.getNormalMin();
                        res=btinfo.getResult();


                        viewHolder.setTest(btinfo.getTest()+"("+btinfo.getUnit()+")");
                        viewHolder.setNormalMax(max);
                        viewHolder.setNormalMin(min);
                        viewHolder.setResult(res);


                        //in between
                        if(res>=min && res<=max){
                            //green
                            viewHolder.colorbtn.setBackgroundColor(Color.parseColor("#4CAF50"));

                        }else {//less or greater
                            //red
                            viewHolder.colorbtn.setBackgroundColor(Color.parseColor("#CA0000"));
                        }


                        viewHolder.del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                                builder.setTitle("Delete Test!");
                                builder.setMessage("Are you sure?");




                                // Set click listener for alert dialog buttons
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                // User clicked the yes button
                                                deleteItem(i);
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // User clicked the no button
                                                break;
                                        }
                                    }
                                };
                                // Set the alert dialog yes button click listener
                                builder.setPositiveButton("Yes", dialogClickListener);

                                // Set the alert dialog no button click listener
                                builder.setNegativeButton("No",dialogClickListener);

                                AlertDialog dialog = builder.create();
                                // Display the alert dialog on interface
                                dialog.show();
                            }

                        });//delete onclicklistener
                    }

                    public void deleteItem(int position){
                        String key = getRef(position).getKey();
                        recordRef.child(recordID).child("BloodTest").child(key)
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ViewBloodTest.this, "Test deleted.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(ViewBloodTest.this, "Test not deleted.", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }
                };


        recyclerV.setAdapter(firebaseRecyclerAdapter);

    }

    private void DoctorMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.d_nav_search:
                Intent intentSearch = new Intent(ViewBloodTest.this, SearchForPatient.class);
                startActivity(intentSearch);
                break;

            case R.id.d_nav_profile:
                Intent intentProfile = new Intent(ViewBloodTest.this, HCP_Profile.class);
                startActivity(intentProfile);
                break;

            case R.id.d_nav_notification:
                Intent intentNotifications = new Intent(ViewBloodTest.this, Notifications.class);
                startActivity(intentNotifications);
                break;
            case R.id.d_nav_home:
                Intent intenthome = new Intent(ViewBloodTest.this, DoctorMainActivity.class);
                startActivity(intenthome);
                break;


        }

    }


}
