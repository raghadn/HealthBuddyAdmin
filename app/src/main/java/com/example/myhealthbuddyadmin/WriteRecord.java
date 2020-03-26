package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class WriteRecord extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String currentuser;
    private DatabaseReference patientRef, doctorRef,recordRef;

    TextView medicationT,doseT,everyT,durationT,timeT,noteT;
    String medication,dose,every,duration,time,note;
    String type,pid;
    Button submitRecord,cancelRecord,addAttachment;
    ImageButton deleteAttachment;

    ImageButton attachmentView;

    private StorageTask uploadTask;
    private Uri fileUri;
    private String myUrl="";//store the file
    private ProgressDialog loadingBar;
    private String savecurrentdate,savecurrenttime;


    private static final int Gallerypick=1;


    StorageReference storageReference;
    String recordIDٍ;
    /*
     * id patient
     * id file
     * id record*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_record);

        medicationT=findViewById(R.id.medication);
        doseT=findViewById(R.id.dose);
        everyT=findViewById(R.id.every);
        durationT=findViewById(R.id.duration);
        timeT=findViewById(R.id.time);
        noteT=findViewById(R.id.note);

        addAttachment=findViewById(R.id.addAttachment);
        deleteAttachment=findViewById((R.id.deleteAttachment));
        submitRecord=findViewById(R.id.submitRecord);
        cancelRecord=findViewById(R.id.cancelRecord);
        attachmentView=findViewById(R.id.attachmentView);


        Calendar calfordate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate=currentDate.format(calfordate.getTime());

        Calendar calfortime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        savecurrenttime=currentTime.format(calfortime.getTime());
        recordRef= FirebaseDatabase.getInstance().getReference().child("Records");



        //submit the record
        submitRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateRecord();
            }
        });


//
//        //loadingBar = new ProgressDialog(this);
//
        //doctor details
        mAuth= FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser().getUid();


        storageReference= FirebaseStorage.getInstance().getReference();

        //patient details
        //get the id from the recyclerView
        pid = getIntent().getExtras().get("PatientKey").toString();
        patientRef= FirebaseDatabase.getInstance().getReference().child("Patients");
        doctorRef= FirebaseDatabase.getInstance().getReference().child("Doctors");

        //record details
        //from template Rid-Pid-Did-Fid-Pname-RecordFile-Date
        type="Prescription";


//
//
        //addAttachment
        addAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //what if already chosen pdf?
                openGallery();

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
                    Toast.makeText(WriteRecord.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
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
            }
        });

//



    }//onCreate




    private void validateRecord() {

        Toast.makeText(this, "validate.", Toast.LENGTH_SHORT).show();

        note=noteT.getText().toString(); //mandontary field the one i am checking if empty get back to it everywhere

        if (fileUri==null&& TextUtils.isEmpty(note)){
            Toast.makeText(this, "الرجاء ادخال محتوى أو ملف لمشاركته...", Toast.LENGTH_SHORT).show();
        }
        else
        if(fileUri!=null&&!fileUri.equals(Uri.EMPTY)){
            Toast.makeText(this, "none empty", Toast.LENGTH_SHORT).show();
            StoreFile();}
        else{
            saveRecord("");
        }
    }


    private void StoreFile() {

        recordIDٍ=generateRecordID(type);

        final StorageReference filepath=storageReference.child("RecordsFiles").child(fileUri.getLastPathSegment()+recordIDٍ+".pdf");

        filepath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url=String.valueOf(uri);
                        Toast.makeText(WriteRecord.this, "تم حفظ الملف...", Toast.LENGTH_SHORT).show();
                        myUrl=url;
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


    /*
     * This algorithm aims to generate a unique record ID for each new record in the system
     * the record ID consists of nine digits
     * the first digit is used to indicate the record type
     * the next eight digits are a randomized sequence of numbers ranging from 000000000 to 99999999
     * 99999999 records to be possibly saved in the system for each record type. */
    private String generateRecordID(String recordType) {
        // check recordType to be implemented on development phase
        if(recordType.equals("Prescription"))
            recordType="1";


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


    private void saveRecord(final String url) { //savepostinfo

//        loadingBar=new ProgressDialog(this);
//        loadingBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        loadingBar.setTitle("يتم رفع الملف");
//        loadingBar.setMessage("الرجاء الانتظار");


        doctorRef.child(currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    HashMap recordMap=new HashMap();
                    recordMap.put("did",currentuser);
                    recordMap.put("pid",pid);
                    recordMap.put("date",savecurrentdate);
                    recordMap.put("time",savecurrenttime);
                    //file
                    if(!TextUtils.isEmpty(url)){
                        recordMap.put("file",url);
                    }

                    recordRef.child(recordIDٍ).updateChildren(recordMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                finish();
                                Toast.makeText(WriteRecord.this, "تمت العملية بنجاح...", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(WriteRecord.this, "حدث خطأ...", Toast.LENGTH_SHORT).show();
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

    private void openGallery() {
        Intent galleryIntent= new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("application/pdf");
        startActivityForResult(galleryIntent,Gallerypick);
        // startActivityForResult(galleryIntent.createChooser(galleryIntent,"الرجاء اختيار الملف"),438);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallerypick&&resultCode==RESULT_OK&&data!=null){
            //chosen file
            fileUri = data.getData();
            attachmentView.setVisibility(View.VISIBLE);
            deleteAttachment.setVisibility(View.VISIBLE);
            //add imageviiew for the added pdf
            //selectedimage.setImageURI(imageuri);
            //selectedimage.setVisibility(View.VISIBLE);

            //  deleteAttachment.setVisibility(View.VISIBLE);

        }
    }

















}
