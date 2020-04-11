package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private Button adminLoginButton;
    private EditText adminID,adminPassword;
    private FirebaseAuth mAuth;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView Forgotpass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        adminLoginButton=(Button)findViewById(R.id.AdminLoginbutton);
        adminID=(EditText)findViewById(R.id.AdminID);
        adminPassword=(EditText)findViewById(R.id.Adminpassword);
        mAuth=FirebaseAuth.getInstance();
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        Forgotpass=(TextView)findViewById(R.id.ForgotPassword);
        Forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToForgotPassword();
            }
        });
        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioID=radioGroup.getCheckedRadioButtonId();
                if(radioID==R.id.adminrButton)
                loginAdmin();
                else
                    if(radioID==R.id.providerrButton) {
                        loginProvider();
                    }


            }
        });



    }

    private void loginProvider() {
        final String ID = adminID.getText().toString();
        final String password = adminPassword.getText().toString();

        if (ID.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter Your ID", Toast.LENGTH_LONG).show();
        }
        else
        if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter Your password", Toast.LENGTH_LONG).show();
        }
        else {

            if (ID.length() == 7 && !password.isEmpty()) {
                DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
                firebaseRef.child("DoctorIDs").child(ID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String Email = dataSnapshot.getValue(String.class);
                            mAuth.signInWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Send user to Doctors main Activity
                                        Intent loginIntent = new Intent(Login.this, DoctorMainActivity.class);
                                        startActivity(loginIntent);
                                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                                    } else
                                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Wrong ID or password", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void loginAdmin() {
        final String ID = adminID.getText().toString();
        final String password = adminPassword.getText().toString();

        if (ID.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter Your ID", Toast.LENGTH_LONG).show();
        }
        else
        if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter Your password", Toast.LENGTH_LONG).show();
        } else {
            if (ID.length() == 5 && !password.isEmpty()) {

                DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();


                firebaseRef.child("AdminIDs").child(ID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String Email = dataSnapshot.getValue(String.class);
                                    mAuth.signInWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                sendUserToMainActivity();
                                                Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                                            } else
                                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                } else
                                    Toast.makeText(getApplicationContext(), "ID does not exist", Toast.LENGTH_LONG).show();

                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            } else {
                Toast.makeText(getApplicationContext(), "Wrong ID or password", Toast.LENGTH_LONG).show();
            }
        }
    }



    private void sendUserToMainActivity(){
        Intent loginIntent=new Intent(Login.this,MainActivity.class);
        startActivity(loginIntent);
    }
    private void sendUserToForgotPassword(){
        Intent loginIntent=new Intent(Login.this,Forgotpassword.class);
        startActivity(loginIntent);
    }

}
