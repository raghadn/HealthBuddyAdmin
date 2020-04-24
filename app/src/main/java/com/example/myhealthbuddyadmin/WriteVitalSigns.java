package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class WriteVitalSigns extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String currentuser;
    private DatabaseReference patientRef, doctorRef,recordRef;
    TextView dateV,patientN,patientID;

    EditText findingsT,impressionT,noteT;
    String findings,impression,note;

    String patientName,hospitalName;
    String type,pid,req,requestKey;
    Button submitRecord,cancelRecord,addAttachment;
    Button attachmentView, deleteAttachment, b0,b1;
    private Uri fileUri;
    private String myUrl="";//store the file
    private String savecurrentdate,savecurrenttime;
    private static final int Gallerypick=1;
    StorageReference storageReference;
    String recordIDٍ;
    ImageButton cal;
    String date;

    private DatePickerDialog.OnDateSetListener mDatasetListner;

    ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_vital_signs);

        loadingbar = new ProgressDialog(this);

        type="VitalSigns";

        noteT=findViewById(R.id.note);
        findingsT=findViewById(R.id.findings);
        impressionT=findViewById(R.id.impression);

        cal=findViewById(R.id.testDateL);
        dateV=findViewById(R.id.exdate);
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal=Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog=new DatePickerDialog(WriteVitalSigns.this,android.R.style.Theme_DeviceDefault_Dialog_MinWidth,mDatasetListner,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDatasetListner=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                date=dayOfMonth+"/"+month+"/"+year;
                dateV.setText(date);
            }
        };

        addAttachment=findViewById(R.id.addAttachment);
        deleteAttachment=findViewById((R.id.deleteAttachment));
        submitRecord=findViewById(R.id.submitRecord);
        cancelRecord=findViewById(R.id.cancelRecord);
        attachmentView=findViewById(R.id.attachmentView);
        b0=findViewById(R.id.button);
        b1=findViewById(R.id.button0);
        patientN=findViewById(R.id.patientName);
        patientID=findViewById(R.id.patientID);

        //underline
        attachmentView.setPaintFlags(attachmentView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        deleteAttachment.setPaintFlags(deleteAttachment.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        Calendar calfordate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate=currentDate.format(calfordate.getTime());

        Calendar calfortime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        savecurrenttime=currentTime.format(calfortime.getTime());

        pid = getIntent().getExtras().get("PatientKey").toString();
        req=getIntent().getExtras().get("Request").toString();

        storageReference= FirebaseStorage.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser().getUid();
        doctorRef= FirebaseDatabase.getInstance().getReference().child("Doctors");
        patientRef= FirebaseDatabase.getInstance().getReference().child("Patients");
        recordRef= FirebaseDatabase.getInstance().getReference().child("Records");


        patientRef.child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientName=dataSnapshot.child("name").getValue().toString();
                patientN.setText(patientName);
                patientID.setText(dataSnapshot.child("national_id").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //addAttachment
        addAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });

        //submit the record
        submitRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateRecord();
            }
        });

        //view attachment
        attachmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(fileUri, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(WriteVitalSigns.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //delete attachment
        deleteAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUri=null;
                myUrl=null;
                attachmentView.setVisibility(View.INVISIBLE);
                deleteAttachment.setVisibility(View.INVISIBLE);
                b0.setVisibility(View.INVISIBLE);
                b1.setVisibility(View.INVISIBLE);

            }
        });

        //cancel record
        cancelRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                builder.setTitle("Cancel Record");
                builder.setMessage("Are you sure you want to cancel?");
                // Set click listener for alert dialog buttons
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // User clicked the yes button
                                finish();
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
        });

    }

    private void validateRecord() {


        findings=findingsT.getText().toString();
        impression=impressionT.getText().toString();
        note=noteT.getText().toString();

        //if no file have to fill all fields
        //if there is a file then all fields are optional
        //No file OR one of fields are messing  except NOTE is optional

        if (fileUri==null ||  findings.isEmpty() || impression.isEmpty() || date == null) {
            Toast.makeText(this, "Please make sure all fields are filled and attach a file.", Toast.LENGTH_SHORT).show();
        }
        else{
            recordIDٍ=generateRecordID(type);
            StoreFile();
        }

    }

    private void saveRecord(final String url) {

        doctorRef.child(currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    loadingbar.setTitle("Uploading Record");
                    loadingbar.setMessage("Please wait while we are uploading your record to the patient.");
                    loadingbar.show();

                    final HashMap recordMap=new HashMap();
                    recordMap.put("type",4);
                    recordMap.put("did",currentuser);
                    recordMap.put("pid",pid);
                    recordMap.put("dateCreated",savecurrentdate);
                    recordMap.put("timeCreated",savecurrenttime);
                    recordMap.put("doctorSpeciality",dataSnapshot.child("specialty").getValue().toString());
                    recordMap.put("doctorName",dataSnapshot.child("name").getValue().toString());
                    recordMap.put("hospital",dataSnapshot.child("hospital").getValue().toString());
                    recordMap.put("testDate",date);

                    recordMap.put("findings",findings);
                    recordMap.put("impression",impression);

                    if(!note.isEmpty())
                        recordMap.put("note",note);

                    //file
                    if(!TextUtils.isEmpty(url)){
                        recordMap.put("file",url);
                    }
                    recordRef.child(recordIDٍ).updateChildren(recordMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                if(req.equals("Y")){
                                    requestKey=getIntent().getExtras().get("RequestKey").toString();
                                    completeRequest(requestKey);
                                }
                                sendNotification(pid);
                                Toast.makeText(WriteVitalSigns.this, "Record successfully uploaded", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                finish();
                            }
                            else {
                                Toast.makeText(WriteVitalSigns.this, "Error", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    //done but need to check whether id exist
    private String generateRecordID(String recordType) {
        // check recordType to be implemented on development phase
        if(recordType.equals("Prescription"))
            recordType="1";

        if(recordType.equals("BloodTest"))
            recordType="2";

        if(recordType.equals("XRay"))
            recordType="3";

        if(recordType.equals("VitalSigns"))
            recordType="4";

        if(recordType.equals("Record"))
            recordType="5";



        //String ID
        String StrId = "";

        // boolean variable indicate wither id exist in database or not
        boolean exist=false ;

        //random id
        long id;

        // to check id length
        String test;

        do {
            // generate a random number between 0000-9999
            //(max - min + 1) + min
            Random random = new Random();
            id = random.nextInt(99999999+1);

            //convert id to string
            test=id+"";

            do {
                if (test.length()<8) {
                    //random Number<100000000 add zeros before the number
                    test="0".concat(test);
                }

            }while(test.length()<8);


            // add the health care facility digits
            StrId=recordType.concat(test);

		/*
	       check if the number previously taken in database
	       well be implemented on the development phase
	       if ()
	       exist = true
		 */

        }while(exist);

        return StrId;
    }

    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void StoreFile() {

        final StorageReference filepath=storageReference.child("RecordsFiles").child(recordIDٍ+".pdf");

        filepath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url=String.valueOf(uri);
                        myUrl=url;
                        recordRef.child(recordIDٍ).child("file").setValue(myUrl);
                        saveRecord(url);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }

    private void openGallery() {
        Intent galleryIntent= new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("application/pdf");
        startActivityForResult(galleryIntent,Gallerypick);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallerypick&&resultCode==RESULT_OK&&data!=null){
            //chosen file
            fileUri = data.getData();
            attachmentView.setVisibility(View.VISIBLE);
            deleteAttachment.setVisibility(View.VISIBLE);
            b0.setVisibility(View.VISIBLE);
            b1.setVisibility(View.VISIBLE);
        }
    }
    private void completeRequest(final String requestKey){
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

        final DatabaseReference pendingRequest= FirebaseDatabase.getInstance().getReference().child("Requests").child("PendingRequests").child(requestKey);
        final DatabaseReference completedRequest=FirebaseDatabase.getInstance().getReference().child("Requests").child("CompletedRequests");
        pendingRequest.child("completion_date").setValue(Datecreated);
        pendingRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                completedRequest.child(requestKey+randomname).setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            pendingRequest.removeValue();
                            Toast.makeText(getApplicationContext(), "Request completed", Toast.LENGTH_LONG).show();

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
                                + "\"contents\": {\"en\": \"تم اضافة ملف جديد\"}"
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


