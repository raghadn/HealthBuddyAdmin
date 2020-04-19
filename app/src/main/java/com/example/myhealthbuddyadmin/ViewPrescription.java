package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ViewPrescription extends AppCompatActivity {
    String recordID;
    TextView doctorNameT,doctorsSpecialtyT, patientNameT,hospitalNameT;
    DatabaseReference recordRef, doctorRef, patientRef ,hospitalRef;
    TextView medicationT,doseT,everyT,durationT,timeT,noteT;
    ImageButton attachmentView;


    PDFView pdf;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_prescription);

        recordID = getIntent().getExtras().get("recordID").toString();

        doctorNameT=findViewById(R.id.doctorName);
        doctorsSpecialtyT=findViewById(R.id.doctorsSpecialty);
        patientNameT=findViewById(R.id.patientName);
        hospitalNameT=findViewById(R.id.hospitalName);

        medicationT=findViewById(R.id.med);
        doseT=findViewById(R.id.findings);
        everyT=findViewById(R.id.every);
        durationT=findViewById(R.id.note);
        timeT=findViewById(R.id.impression);
        noteT=findViewById(R.id.rrrr);



        recordRef = FirebaseDatabase.getInstance().getReference().child("Records").child(recordID);
        recordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //record info
                String medication,dose,every,duration,time,note;

                medication=dataSnapshot.child("medication").getValue().toString();
                medicationT.setText(medication);

                dose=dataSnapshot.child("dose").getValue().toString();
                doseT.setText(dose);

                every=dataSnapshot.child("every").getValue().toString();
                everyT.setText(every);

                duration=dataSnapshot.child("duration").getValue().toString();
                durationT.setText(duration);

                time=dataSnapshot.child("time").getValue().toString();
                timeT.setText(time);

                note=dataSnapshot.child("note").getValue().toString();
                noteT.setText(note);


                //doctor who wrote this record
                String doctorName, doctorsSpecialty;

                doctorName=dataSnapshot.child("doctorName").getValue().toString();
                doctorNameT.setText(doctorName);

                doctorsSpecialty=dataSnapshot.child("doctorSpeciality").getValue().toString();
                doctorsSpecialtyT.setText(doctorsSpecialty);



                //patient of record
                String patientName;

                patientName=dataSnapshot.child("patientName").getValue().toString();
                patientNameT.setText(patientName);


                //pdf
                //view attachment

                attachmentView=findViewById(R.id.attachmentView);
                final String url = dataSnapshot.child("file").getValue().toString();

       attachmentView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        //pdfviewer
        Intent redirect = new Intent(ViewPrescription.this,ReadActivity.class);
        redirect.putExtra("url", url);
        redirect.putExtra("recordID", recordID);
        startActivity(redirect);


        //open with app that already exists in phone
        /*
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent newIntent = Intent.createChooser(intent, "Open File");
        try {
            startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
        */



    }
});


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








    }
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private class RetrievePDFbyte extends AsyncTask<String,Void,byte[]> {



        @Override
        protected byte [] doInBackground(String... strings) {
            InputStream inputStream = null;
            try{
                URL url=new URL(strings[0]);
                HttpsURLConnection httpsURLConnection=(HttpsURLConnection)url.openConnection();
                if (httpsURLConnection.getResponseCode()==200){
                    inputStream=new BufferedInputStream(httpsURLConnection.getInputStream());
                }
            }catch (IOException e){
                return null;
            }
            try {
                return IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            pdf.fromBytes(bytes).load();

        }

    }




}
