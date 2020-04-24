package com.example.myhealthbuddyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Record extends AppCompatActivity {

    Button writeTest;
    String PatientKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        writeTest=findViewById(R.id.writeTest);

        PatientKey = getIntent().getExtras().get("PatientKey").toString();

        ///////////// ---- redirect to activities ---- /////////////
        writeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(Record.this,WriteRecord.class);
                redirect.putExtra("PatientKey",PatientKey);
                redirect.putExtra("Request","N");
                startActivity(redirect);
            }
        });

    }
}
