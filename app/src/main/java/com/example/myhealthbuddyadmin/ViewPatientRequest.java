package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPatientRequest extends AppCompatActivity {

    Toolbar mtoolbar;
    TextView PatientNameText,PatientIDText,RequestTypeText,AppointmentDateText,NoteText,RDate;
    Button WriteRecordbtn,CancelRequestBtn;
    ImageView RequestPic;
    String RequestKey;
    DatabaseReference pendingRequest,PatientsRef,declinedRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_request);

        mtoolbar=(Toolbar)findViewById(R.id.viewRequestToolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Request");




        //Page Fields
        PatientNameText=(TextView) findViewById(R.id.page_request_name1);
        PatientIDText=(TextView) findViewById(R.id.page_request_ID);
        RequestTypeText=(TextView)findViewById(R.id.page_request_type);
        RequestPic=(ImageView) findViewById(R.id.page_request_pic);
        AppointmentDateText=(TextView)findViewById(R.id.page_request_apDate);
        NoteText=(TextView)findViewById(R.id.page_request_note);
        RDate=(TextView)findViewById(R.id.page_request_requestdate);
        WriteRecordbtn=(Button)findViewById(R.id.page_request_writerecord);
        CancelRequestBtn=(Button)findViewById(R.id.page_request_cancelb);

        NoteText.setMovementMethod(new ScrollingMovementMethod());

        RequestKey = getIntent().getExtras().get("RequestKey").toString();

        pendingRequest= FirebaseDatabase.getInstance().getReference().child("Requests").child("PendingRequests").child(RequestKey);
        declinedRequest=FirebaseDatabase.getInstance().getReference().child("Requests").child("DeclinedRequests");
        PatientsRef=FirebaseDatabase.getInstance().getReference().child("Patients");


        pendingRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String pid=dataSnapshot.child("patient_id").getValue().toString();
                    PatientIDText.setText(pid);
                    String reqdate=dataSnapshot.child("request_date").getValue().toString();
                    RDate.setText(reqdate);
                    String type=dataSnapshot.child("type").getValue().toString();
                    RequestTypeText.setText(type);
                    String apDate=dataSnapshot.child("date").getValue().toString();
                    AppointmentDateText.setText(apDate);
                    if(dataSnapshot.hasChild("notes")){
                        String note=dataSnapshot.child("notes").getValue().toString();
                        NoteText.setText(note);}
                    else
                        NoteText.setText("None");


                    if(type.equals("Medical Report")){
                        RequestPic.setImageResource(R.drawable.report);
                    }
                    if(type.equals("Radiology Report")){
                        RequestPic.setImageResource(R.drawable.nuclear);
                    }
                    if(type.equals("Prescription")){
                        RequestPic.setImageResource(R.drawable.pills);
                    }
                    if(type.equals("Vital Signs")){
                        RequestPic.setImageResource(R.drawable.cardiogram);
                    }

                    PatientsRef.child(dataSnapshot.child("patient_uid").getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String pname =dataSnapshot.child("name").getValue().toString();
                            PatientNameText.setText(pname);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        CancelRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteRequest();
            }
        });


    }

    private void DeleteRequest(){
        Calendar calfordate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        String savecurrentdate=currentDate.format(calfordate.getTime());

        Calendar calfortime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        String savecurrenttime=currentTime.format(calfortime.getTime());

        final String randomname=savecurrentdate+savecurrenttime;
        AlertDialog.Builder altb= new AlertDialog.Builder(ViewPatientRequest.this);
        altb.setMessage("Are you sure you want to cancel and delete the request?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        pendingRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                declinedRequest.child(RequestKey+randomname).setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                        pendingRequest.removeValue();
                                        Toast.makeText(getApplicationContext(), "Request canceled", Toast.LENGTH_LONG).show();
                                        }
                                        else
                                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

        AlertDialog alertDialog=altb.create();
        alertDialog.setTitle("Cancel Request");
        alertDialog.show();


    }
}
