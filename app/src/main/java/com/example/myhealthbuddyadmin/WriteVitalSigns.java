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
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class WriteVitalSigns extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String currentuser;
    private DatabaseReference patientRef, doctorRef,recordRef;
    TextView dateV,patientN,patientID;

    EditText findingsT,impressionT,noteT;
    String findings,impression,note;

    String patientName,hospitalName;
    String type,pid;
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

        type="VitalSigns";
        loadingbar = new ProgressDialog(this);

        noteT=findViewById(R.id.note);
        findingsT=findViewById(R.id.findings);

        cal=findViewById(R.id.cal);
        dateV=findViewById(R.id.dateV);
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
        patientN=findViewById(R.id.patientN);
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
        note=noteT.getText().toString();

        //if no file have to fill all fields
        //if there is a file then all fields are optional
        //No file OR one of fields are messing  except NOTE is optional
        if (date == null) {
            Toast.makeText(this, "Please add test date", Toast.LENGTH_SHORT).show();
        }else{

            if (fileUri==null && ( findings.isEmpty() ) ){
                Toast.makeText(this, "Please fill all fields or attach a file. ", Toast.LENGTH_SHORT).show();
            }
            else
            if(fileUri!=null&&!fileUri.equals(Uri.EMPTY)){
                recordIDٍ=generateRecordID(type);
                StoreFile();}
            else{
                recordIDٍ=generateRecordID(type);
                saveRecord("");
            }

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
                    recordMap.put("patientName",patientName);
                    recordMap.put("date",savecurrentdate);
                    recordMap.put("time",savecurrenttime);
                    recordMap.put("doctorSpeciality",dataSnapshot.child("specialty").getValue().toString());
                    recordMap.put("doctorName",dataSnapshot.child("name").getValue().toString());
                    recordMap.put("hospital","hospital name");   //wrong need to use refrence

                    recordMap.put("testDate",date);


                    recordMap.put("findings",findings);

                    if(note!=null)
                        recordMap.put("note",note);

                    //file
                    if(!TextUtils.isEmpty(url)){
                        recordMap.put("file",url);
                    }
                    recordRef.child(recordIDٍ).updateChildren(recordMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                finish();
                                Toast.makeText(WriteVitalSigns.this, "Record successfully uploaded", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(WriteVitalSigns.this, "Error", Toast.LENGTH_SHORT).show();
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
}


