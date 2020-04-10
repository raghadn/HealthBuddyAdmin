package com.example.myhealthbuddyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.FirebaseDatabase;

public class Prescriptions extends AppCompatActivity {

    Button writeTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);

        writeTest=findViewById(R.id.writeTest);

        final String PatientKey = getIntent().getExtras().get("PatientKey").toString();

        ///////////// ---- redirect to activities ---- /////////////
        writeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(Prescriptions.this,WritePrescription.class);
                redirect.putExtra("PatientKey",PatientKey);
                startActivity(redirect);
            }
        });




    }

}
