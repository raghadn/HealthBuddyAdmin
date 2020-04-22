package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class WriteBloodTest extends AppCompatActivity {

    EditText testT,unitT,resultT,normalT,noteT;
    String test,unit,result,normal,note;
    TextView patientN,patientID,dateV;
    Button submitRecord,cancel,addAttachment;
    Button attachmentView, deleteAttachment, b0,b1;
    ImageButton add;

    String savecurrentdate,savecurrenttime;

    String recordIDٍ, type,pid,patientName;
    String myUrl="";
    Boolean hasBT;
    private Uri fileUri;
    private static final int Gallerypick=1;
    StorageReference storageReference;
    RecyclerView recyclerV;
    private FirebaseAuth mAuth;
    private String currentuser;
    private DatabaseReference patientRef, doctorRef,recordRef;

    ImageButton cal;
    String date;
    private DatePickerDialog.OnDateSetListener mDatasetListner;
    ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_blood_test);
        /*

        loadingbar = new ProgressDialog(this);

        addAttachment=findViewById(R.id.addAttachment);
        deleteAttachment=findViewById((R.id.deleteAttachment));
        submitRecord=findViewById(R.id.submitRecord);
        cancel=findViewById(R.id.cancel);
        attachmentView=findViewById(R.id.attachmentView);
        b0=findViewById(R.id.button);
        b1=findViewById(R.id.button0);

        //underline
        attachmentView.setPaintFlags(attachmentView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        deleteAttachment.setPaintFlags(deleteAttachment.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        testT=findViewById(R.id.test);
        unitT=findViewById((R.id.unit));
        resultT=findViewById(R.id.result);
        normalT=findViewById(R.id.normal);
        noteT=findViewById(R.id.rrrr);
        patientN=findViewById(R.id.patientName);
        patientID=findViewById(R.id.patientID);


        Calendar calfordate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate=currentDate.format(calfordate.getTime());

        Calendar calfortime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        savecurrenttime=currentTime.format(calfortime.getTime());
        recordRef= FirebaseDatabase.getInstance().getReference().child("Records");
        doctorRef= FirebaseDatabase.getInstance().getReference().child("Doctors");

        pid = getIntent().getExtras().get("PatientKey").toString();
        storageReference= FirebaseStorage.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser().getUid();

        type="BloodTest";

        patientRef= FirebaseDatabase.getInstance().getReference().child("Patients");
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



        //first create record with general information
        saveRecord("");

        //add blood test
        add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test=testT.getText().toString();
                testT.getText().clear();

                unit = unitT.getText().toString();
                unitT.getText().clear();

                result = resultT.getText().toString();
                resultT.getText().clear();

                normal = normalT.getText().toString();
                normalT.getText().clear();



                if (test.isEmpty() || unit.isEmpty() || result.isEmpty() || normal.isEmpty()) {
                    showMessage("Please enter all fields..");

                } else {

                    AddBloodTest(test, unit,result,normal);

                }
            }
        });


        cal=findViewById(R.id.testDateL);
        dateV=findViewById(R.id.testDate);
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal=Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog=new DatePickerDialog(WriteBloodTest.this,android.R.style.Theme_DeviceDefault_Dialog_MinWidth,mDatasetListner,year,month,day);
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

        //addAttachment
        addAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Toast.makeText(WriteBloodTest.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //delete attachment
        deleteAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUri=null;
                myUrl=null;
                attachmentView.setVisibility(View.GONE);
                deleteAttachment.setVisibility(View.INVISIBLE);
                b0.setVisibility(View.INVISIBLE);
                b1.setVisibility(View.INVISIBLE);
            }
        });



        //check if there is a bloodtest
        recordIDٍ=generateRecordID(type);
        hasBT=false;
        recordRef.child(recordIDٍ).child("BloodTest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null) {
                    hasBT=true;
                }else{
                    hasBT=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //validate here! if there is no file or bloodtests then delete the record

        //submit button which will add the file and note
        submitRecord=findViewById(R.id.submitRecord);
        submitRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (date == null) {
                    showMessage("Please add test date");
                }else{
                    if (hasBT && fileUri==null){
                        showMessage("Please add blood tests or a file");
                    }else {
                        loadingbar.setTitle("Uploading Record");
                        loadingbar.setMessage("Please wait while we are uploading your record to the patient.");
                        loadingbar.show();

                        recordRef.child(recordIDٍ).child("testDate").setValue(date);

                        //save file
                        if(fileUri!=null&&!fileUri.equals(Uri.EMPTY)){
                            StoreFile();
                        }
                        //add note
                        note=noteT.getText().toString();
                        noteT.getText().clear();
                        if(note!=null){
                            recordRef.child(recordIDٍ).child("note").setValue(note);
                        }
                        finish();
                        Toast.makeText(WriteBloodTest.this, "Record successfully uploaded", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


       ////////////cancel button delete the record!!!
        cancel.setOnClickListener(new View.OnClickListener() {
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
                                recordRef.child(recordIDٍ).removeValue();
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



        //recycler view
        recyclerV=findViewById(R.id.recy);
        recyclerV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerV.setLayoutManager(linearLayoutManager);
        displayBloodTests();


*/
    }//onc

    private void displayBloodTests() {
        final Query query = recordRef.child(recordIDٍ).child("BloodTest");
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
                        viewHolder.setTest(btinfo.getTest());
                        viewHolder.setUnit(btinfo.getUnit());
                        viewHolder.setNormal(btinfo.getNormal());
                        viewHolder.setResult(btinfo.getResult());

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
                        recordRef.child(recordIDٍ).child("BloodTest").child(key)
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(WriteBloodTest.this, "Test deleted.", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(WriteBloodTest.this, "Test not deleted.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                });
                    }
                };


        recyclerV.setAdapter(firebaseRecyclerAdapter);

    }
    public static class viewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageButton del= itemView.findViewById(R.id.deleteBT);

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTest(String tn){
            TextView title = mView.findViewById(R.id.test);
            title.setText(tn);
        }
        public void setUnit(String tn){
            TextView title = mView.findViewById(R.id.unit);
            title.setText(tn);
        }
        public void setResult(String tn){
            TextView title = mView.findViewById(R.id.result);
            title.setText(tn);
        }
        public void setNormal(String tn){
            TextView title = mView.findViewById(R.id.normal);
            title.setText(tn);
        }
    }


    private void AddBloodTest(String test, String unit, String result, String normal) {
        DatabaseReference ref = recordRef.child(recordIDٍ).child("BloodTest");
        HashMap btmap = new HashMap();

        btmap.put("test", test);
        btmap.put("unit", unit);
        btmap.put("result", result);
        btmap.put("normal", normal);

        ref.child(test).updateChildren(btmap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()) {
                    Toast.makeText(WriteBloodTest.this, "BloodTest Added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(WriteBloodTest.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void saveRecord(final String url) {

        doctorRef.child(currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final HashMap recordMap=new HashMap();
                    recordMap.put("type",2);
                    recordMap.put("did",currentuser);
                    recordMap.put("pid",pid);
                    recordMap.put("patientName",patientName);
                    recordMap.put("date",savecurrentdate);
                    recordMap.put("time",savecurrenttime);
                    recordMap.put("doctorSpeciality",dataSnapshot.child("specialty").getValue().toString());
                    recordMap.put("doctorName",dataSnapshot.child("name").getValue().toString());
                    recordMap.put("hospital","get back to this");
                    recordMap.put("testDate",date);
                    recordRef.child(recordIDٍ).updateChildren(recordMap);

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