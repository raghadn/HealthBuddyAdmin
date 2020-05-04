package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPatientRequest extends AppCompatActivity {

    Toolbar mtoolbar;
    TextView PatientNameText,PatientIDText,RequestTypeText,AppointmentDateText,NoteText,RDate,gender;
    Button WriteRecordbtn,CancelRequestBtn;
    ImageView RequestPic;
    String RequestKey,PatientKey,type;
    DatabaseReference pendingRequest,PatientsRef,declinedRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_request);

        /*
        mtoolbar=(Toolbar)findViewById(R.id.viewRequestToolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Patient Request");

*/


        //Page Fields
        PatientNameText=(TextView) findViewById(R.id.patientName);
        PatientIDText=(TextView) findViewById(R.id.patientID);
        RequestTypeText=(TextView)findViewById(R.id.page_request_type);
        gender=(TextView)findViewById(R.id.gender);


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
                    type=dataSnapshot.child("type").getValue().toString();
                    RequestTypeText.setText(type);
                    String apDate=dataSnapshot.child("date").getValue().toString();
                    AppointmentDateText.setText(apDate);
                    if(dataSnapshot.hasChild("notes")){
                        String note=dataSnapshot.child("notes").getValue().toString();
                        NoteText.setText(note);}
                    else
                        NoteText.setText("None");


                    PatientKey=dataSnapshot.child("patient_uid").getValue().toString();




                    PatientsRef.child(dataSnapshot.child("patient_uid").getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String pname =dataSnapshot.child("name").getValue().toString();
                            PatientNameText.setText(pname);
                            String gndr=dataSnapshot.child("gender").getValue().toString();
                            gender.setText(gndr);

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

        WriteRecordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("Medical Report")){
                    Intent intentwritemed = new Intent(ViewPatientRequest.this, WriteRecord.class);
                    intentwritemed.putExtra("PatientKey",PatientKey);
                    intentwritemed.putExtra("Request","Y");
                    intentwritemed.putExtra("RequestKey",RequestKey);
                    startActivity(intentwritemed);
                    finish();

                }
                if(type.equals("Radiology Report")){
                    Intent intentwriterad = new Intent(ViewPatientRequest.this, WriteXRay.class);
                    intentwriterad.putExtra("PatientKey",PatientKey);
                    intentwriterad.putExtra("Request","Y");
                    intentwriterad.putExtra("RequestKey",RequestKey);
                    startActivity(intentwriterad);
                    finish();
                }
                if(type.equals("Prescription")){
                    Intent intentwritepres = new Intent(ViewPatientRequest.this, WritePrescription.class);
                    intentwritepres.putExtra("PatientKey",PatientKey);
                    intentwritepres.putExtra("Request","Y");
                    intentwritepres.putExtra("RequestKey",RequestKey);
                    startActivity(intentwritepres);
                    finish();
                }
                if(type.equals("Vital Signs")){
                    Intent intentwritevs = new Intent(ViewPatientRequest.this, WriteVitalSigns.class);
                    intentwritevs.putExtra("PatientKey",PatientKey);
                    intentwritevs.putExtra("Request","Y");
                    intentwritevs.putExtra("RequestKey",RequestKey);
                    startActivity(intentwritevs);
                    finish();

                }
                if(type.equals("Lab Report")){
                    Intent intentwritelab = new Intent(ViewPatientRequest.this, WriteBloodTest.class);
                    intentwritelab.putExtra("PatientKey",PatientKey);
                    intentwritelab.putExtra("Request","Y");
                    intentwritelab.putExtra("RequestKey",RequestKey);
                    startActivity(intentwritelab);
                    finish();

                }



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


        Calendar calfordecDec=Calendar.getInstance();
        SimpleDateFormat decTime=new SimpleDateFormat("dd/MM/yyyy");
        final String Datecreated=decTime.format(calfordecDec.getTime());

        final String randomname=savecurrentdate+savecurrenttime;
        AlertDialog.Builder altb= new AlertDialog.Builder(ViewPatientRequest.this);
        altb.setMessage("Are you sure you want to decline and delete the request?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pendingRequest.child("declined_date").setValue(Datecreated);
                        pendingRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                declinedRequest.child(RequestKey+randomname).setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            String puid=dataSnapshot.child("patient_uid").getValue().toString();
                                        pendingRequest.removeValue();
                                        Toast.makeText(getApplicationContext(), "Request declined", Toast.LENGTH_LONG).show();
                                            sendNotification(puid);
                                            sendUserToNotificationPage();

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
        alertDialog.setTitle("Decline Request");
        alertDialog.show();


    }

    private void sendUserToNotificationPage(){
        Intent intentProfile = new Intent(ViewPatientRequest.this, Notifications.class);
        startActivity(intentProfile);
        finish();
    }

    private void sendNotification(final String puid) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String send_email;


                    //This is a Simple Logic to Send Notification different Device Programmatically....

                    send_email =puid ;


                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic MWMzMDk5YzgtMTBjMC00N2U4LTgzNjAtNjk2Yjk3NjgxOTRm");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"8feeee1a-0ae6-4662-af58-51550ce5b903\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_uid\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"رفض طلب، الرجاء تسجيل الدخول للتفاصيل...\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }
}
