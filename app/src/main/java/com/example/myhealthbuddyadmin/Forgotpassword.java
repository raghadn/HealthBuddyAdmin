package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Forgotpassword extends AppCompatActivity {
    Button forgotb;
    EditText idf;
    private FirebaseAuth mAuth;
    String Email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        forgotb=(Button)findViewById(R.id.forgotsub);
        idf=(EditText)findViewById(R.id.forgotID);

        forgotb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
                String ID=idf.getText().toString();
                if(ID.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Enter your ID", Toast.LENGTH_LONG).show();
                }
                else {
                    if(ID.length()==5){
                        firebaseRef.child("AdminIDs").child(ID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Email=dataSnapshot.getValue(String.class);
                                    if(Email!=null)
                                        mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(),"Email sent",Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(Forgotpassword.this,Login.class));

                                                }
                                                else {
                                                    String Error = task.getException().getMessage();
                                                    Toast.makeText(getApplicationContext(),Error,Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });

                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Invalid ID",Toast.LENGTH_LONG).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }


                    if(ID.length()==6){
                        firebaseRef.child("DoctorIDs").child(ID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Email=dataSnapshot.getValue(String.class);
                                    if(Email!=null)
                                        mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(),"Email sent",Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(Forgotpassword.this,Login.class));

                                                }
                                                else {
                                                    String Error = task.getException().getMessage();
                                                    Toast.makeText(getApplicationContext(),Error,Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });

                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Invalid ID",Toast.LENGTH_LONG).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    if(ID.length()!=5&&ID.length()!=6) {
                        Toast.makeText(getApplicationContext(),"please enter a valid ID",Toast.LENGTH_LONG).show();


                    }

                }
                
            }
        });



    }
}
