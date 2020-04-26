package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class CreateDoctor extends AppCompatActivity {

    EditText name,emaill,phone,specialty,gender,password;
    Button createbtn ;
    FirebaseAuth  Hauth;
    String HID;
    TextView ID;
    String Gender;
    private RadioGroup Group;
    private RadioButton radioBtn;
    private BottomNavigationView bottomnav;

    private FirebaseUser Fuser; //the Hospital User

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_doctor);


        Group=findViewById(R.id.radiogender);
        ID= findViewById(R.id.idd);
        name=findViewById(R.id.name);
        emaill=findViewById(R.id.email);
        phone=findViewById(R.id.phone);
        specialty=findViewById(R.id.sp);
        password=findViewById(R.id.pas);


        Hauth =FirebaseAuth.getInstance();

        genaratedID();

        createbtn = findViewById(R.id.createaccount);
        createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createbtn.setVisibility(View.INVISIBLE);

                int radioID=Group.getCheckedRadioButtonId();
                if(radioID==R.id.male)
                    Gender="Male";
                else
                if(radioID==R.id.female) {
                    Gender="Female";
                }

                String Uid = ID.getText().toString();
                String Uname = name.getText().toString();
                String Uemail = emaill.getText().toString();
                String Uphone = phone.getText().toString();
                String Uspecialty = specialty.getText().toString();
             // String Ugender = gender.getText().toString();
                String Ulicense = password.getText().toString();
                if(Uid.isEmpty()|| Uname.isEmpty()|| Uemail.isEmpty()|| Uphone.isEmpty()|| Uspecialty.isEmpty()|| Ulicense.isEmpty()){
                    showMessage("Please Verify All Fields");
                    createbtn.setVisibility(View.VISIBLE);
                }

                else
                    CreateHealthcareProviderAccount(Uemail,Ulicense ,Uid,Uname,Uphone,Gender,Uspecialty);
            }
        });


    }
    // the generated ID
    public String genaratedID(){

       // the firs 3 digit should be the hospital numbers
        String HospitalID = Hauth.getCurrentUser().getUid();
        showMessage(HospitalID);
        DatabaseReference Href =FirebaseDatabase.getInstance().getReference();

        Href.child("Admins").child(HospitalID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String Hid= dataSnapshot.child("Hospital").getValue().toString();
                    Random rand = new Random();
                    int id= rand.nextInt(9999+1);
                    setHID(Hid);
                     String HID= Hid+id;
                     ID.setText(HID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String Id;
        Id= "1111";

        return Id;

    }

    // the generated password
    public String genaratedpass() {
        String pass;
        pass = "1111";
        return pass;
    }


    public void setHID(String Hid){
        HID = Hid;
    }

    // method adding thr created account to database
    private void CreateHealthcareProviderAccount(final String email , final String license, final String ID , final String Name , final String Phone, final String Gender, final String Specialty) {

        if(Phone.length()>10 || Phone.length()<10 || !Phone.substring(0,2).equals("05") || !Phone.matches("[0-9]+")){
            showMessage("Please enter a valid phone number");
            phone.setError("Please enter a valid phone number starts with 05 ");
            phone.requestFocus();
            createbtn.setVisibility(View.VISIBLE);
        } if(license.length()>10 || license.length()<10 || !license.matches("[0-9]+")){
            password.setError("Please enter a valid license number ");
            password.requestFocus();
            createbtn.setVisibility(View.VISIBLE);

        }
                else {

            FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, license).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userid = user.getUid().toString();

                        // save the doctor's data in real time database
                        FirebaseDatabase.getInstance().getReference().child("Doctors").child(userid);
                        FirebaseDatabase.getInstance().getReference().child("Doctors").child(userid).child("email").setValue(email);
                        FirebaseDatabase.getInstance().getReference().child("Doctors").child(userid).child("hospital").setValue(HID);
                        FirebaseDatabase.getInstance().getReference().child("Doctors").child(userid).child("id").setValue(ID);
                        FirebaseDatabase.getInstance().getReference().child("Doctors").child(userid).child("name").setValue(Name);
                        FirebaseDatabase.getInstance().getReference().child("Doctors").child(userid).child("phone").setValue(Phone);
                        FirebaseDatabase.getInstance().getReference().child("Doctors").child(userid).child("gender").setValue(Gender);
                        FirebaseDatabase.getInstance().getReference().child("Doctors").child(userid).child("specialty").setValue(Specialty);
                        FirebaseDatabase.getInstance().getReference().child("Doctors").child(userid).child("license").setValue(license);

                        //For the doctors id (Login)
                        FirebaseDatabase.getInstance().getReference().child("DoctorIDs").child(ID).setValue(email);
                        showMessage("The Account has been created ");
                        createbtn.setVisibility(View.VISIBLE);
                    } else {
                        showMessage("Error");
                        createbtn.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }

    // method to show a message
    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_LONG).show();
    }



}
