package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.ImageView;
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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class WriteBloodTest extends AppCompatActivity{

    EditText noteT;
    TextView patientN,patientID,dateV,testsLabel,patientG;
    Button submitRecord,cancel,addAttachment;
    Button attachmentView, deleteAttachment, b0,b1,add;


    String savecurrentdate,savecurrenttime,note;

    String recordIDٍ, type,pid,patientName,req,requestKey;
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
    CardView card;

    ImageView hintbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_blood_test);


        loadingbar = new ProgressDialog(this);

        addAttachment=findViewById(R.id.addAttachment);
        deleteAttachment=findViewById((R.id.deleteAttachment));
        submitRecord=findViewById(R.id.submitRecord);
        cancel=findViewById(R.id.cancelRecord);
        attachmentView=findViewById(R.id.attachmentView);
        b0=findViewById(R.id.button);
        b1=findViewById(R.id.button0);

        hintbtn=findViewById(R.id.hintbtn);
        hintbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(WriteBloodTest.this);
                dialog.setContentView(R.layout.hintdialog);
                dialog.setTitle("Blood Test Requirements");
                dialog.setCancelable(true);


                Button cbutton = (Button) dialog.findViewById(R.id.done);
                cbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                TextView hint = dialog.findViewById(R.id.hint);
                hint.setText("Test Date is required");

                TextView hint2 = dialog.findViewById(R.id.hint2);
                hint2.setText("Test Added -- File optional.\n"+
                        "No Tests -- File required.");

                dialog.show();
            }
        });


        //underline
        attachmentView.setPaintFlags(attachmentView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        deleteAttachment.setPaintFlags(deleteAttachment.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);


        noteT=findViewById(R.id.note);
        patientN=findViewById(R.id.patientName);
        patientID=findViewById(R.id.patientID);
        patientG=findViewById(R.id.gender);

        Calendar calfordate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate=currentDate.format(calfordate.getTime());

        Calendar calfortime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        savecurrenttime=currentTime.format(calfortime.getTime());
        recordRef= FirebaseDatabase.getInstance().getReference().child("Records");
        doctorRef= FirebaseDatabase.getInstance().getReference().child("Doctors");

        pid = getIntent().getExtras().get("PatientKey").toString();
        req=getIntent().getExtras().get("Request").toString();

        storageReference= FirebaseStorage.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser().getUid();

        recyclerV=findViewById(R.id.recy);

        type="BloodTest";

        patientRef= FirebaseDatabase.getInstance().getReference().child("Patients");
        patientRef.child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientName=dataSnapshot.child("name").getValue().toString();
                patientN.setText(patientName);
                patientID.setText(dataSnapshot.child("national_id").getValue().toString());
                patientG.setText(dataSnapshot.child("gender").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //first create record with general information
        recordIDٍ=generateRecordID(type);
        saveRecord("");


        //add blood test
        add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // openDialog();
                final Dialog dialog = new Dialog(WriteBloodTest.this);
                dialog.setContentView(R.layout.dialog_add_test);
                dialog.setTitle("Add Test");
                dialog.setCancelable(true);

                Button cbutton = (Button) dialog.findViewById(R.id.cancelTest);
                cbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                Button button = (Button) dialog.findViewById(R.id.addtest);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText testT,unitT,resultT,normalminT,normalmaxT;

                        testT= dialog.findViewById(R.id.test);
                        unitT=dialog.findViewById((R.id.unit));
                        resultT=dialog.findViewById(R.id.result);
                        normalminT=dialog.findViewById(R.id.normalmin);
                        normalmaxT=dialog.findViewById(R.id.normalmax);

                        String test,unit, result,normalmin,normalmax;

                        test=testT.getText().toString();
                        unit = unitT.getText().toString();
                        result = resultT.getText().toString().trim();
                        normalmin = normalminT.getText().toString().trim();
                        normalmax = normalmaxT.getText().toString().trim();

                        Boolean isError = true;


                        if (((test.isEmpty() || unit.isEmpty()) || result.isEmpty()) || (normalmin.isEmpty() || normalmax.isEmpty())) {
                            showMessage("Please fill all fields.");
                            isError = true;
                        } else{

                            isError = false;
                            AddBloodTest( test, unit, Double.parseDouble(resultT.getText().toString()),  Double.parseDouble(normalminT.getText().toString()),  Double.parseDouble(normalmaxT.getText().toString()));

                        }

                        if(!isError)
                            dialog.dismiss();

                    }
                });

                dialog.show();
            }
        });


        cal=findViewById(R.id.testDateL);
        dateV=findViewById(R.id.exdate);
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal=Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog=new DatePickerDialog(WriteBloodTest.this,android.R.style.Theme_DeviceDefault_Dialog_MinWidth,mDatasetListner,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });

        dateV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal=Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog=new DatePickerDialog(WriteBloodTest.this,android.R.style.Theme_DeviceDefault_Dialog_MinWidth,mDatasetListner,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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
        card=findViewById(R.id.card);
        testsLabel=findViewById(R.id.textView6);
        hasBT=false;
        recordRef.child(recordIDٍ).child("BloodTest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null) {
                    //no blood tests
                    hasBT=true;
                    card.setVisibility(View.GONE);
                    recyclerV.setVisibility(View.GONE);
                    testsLabel.setVisibility(View.GONE);

                }else{
                    hasBT=false;
                    card.setVisibility(View.VISIBLE);
                    recyclerV.setVisibility(View.VISIBLE);
                    testsLabel.setVisibility(View.VISIBLE);
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
                        if(!note.isEmpty()){
                            recordRef.child(recordIDٍ).child("note").setValue(note);
                        }
                        if(req.equals("Y")){
                            requestKey=getIntent().getExtras().get("RequestKey").toString();
                            completeRequest(requestKey);
                        }
                        sendNotification(pid);
                        Toast.makeText(WriteBloodTest.this, "Record successfully uploaded", Toast.LENGTH_SHORT).show();
                        Intent redirect = new Intent(WriteBloodTest.this,ViewRecordsTabbed.class);
                        redirect.putExtra("PatientKey",pid);
                        redirect.putExtra("type",2);
                        startActivity(redirect);
                        finish();

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
                                Intent redirect = new Intent(WriteBloodTest.this,ViewRecordsTabbed.class);
                                redirect.putExtra("PatientKey",pid);
                                redirect.putExtra("type",2);
                                startActivity(redirect);
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
        recyclerV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerV.setLayoutManager(linearLayoutManager);
        displayBloodTests();
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

    private void AddBloodTest(String test,String unit,Double result, Double normalmin, Double normalmax) {
        DatabaseReference ref = recordRef.child(recordIDٍ).child("BloodTest");
        HashMap btmap = new HashMap();

        btmap.put("test", test);
        btmap.put("unit", unit);
        btmap.put("result", result);
        btmap.put("normalMin", normalmin);
        btmap.put("normalMax", normalmax);

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

    public static class viewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageButton del= itemView.findViewById(R.id.deleteBT);
        Button colorbtn= itemView.findViewById(R.id.button3);

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTest(String tn){
            TextView title = mView.findViewById(R.id.test);
            title.setText(tn);
        }

        public void setResult(Double tn){
            TextView title = mView.findViewById(R.id.result);
            title.setText(tn+"");
        }
        public void setNormalMin(Double tn){
            TextView title = mView.findViewById(R.id.normalMin);
            title.setText(tn+"");
        }
        public void setNormalMax(Double tn){
            TextView title = mView.findViewById(R.id.normalMax);
            title.setText(tn+"");
        }
    }




    private void saveRecord(final String url) {

        Calendar orderdate=Calendar.getInstance();
        SimpleDateFormat dateorder=new SimpleDateFormat("yyyy-MM-dd");
        final String date_order =dateorder.format(orderdate.getTime());

        doctorRef.child(currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final HashMap recordMap=new HashMap();
                    recordMap.put("type",2);
                    recordMap.put("did",currentuser);
                    recordMap.put("pid",pid);
                    recordMap.put("dateCreated",savecurrentdate);
                    recordMap.put("timeCreated",savecurrenttime);
                    recordMap.put("doctorSpeciality",dataSnapshot.child("specialty").getValue().toString());
                    recordMap.put("doctorName",dataSnapshot.child("name").getValue().toString());
                    recordMap.put("hospital",dataSnapshot.child("hospital").getValue().toString());
                    recordMap.put("testDate",date);
                    recordMap.put("date_order",date_order);

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