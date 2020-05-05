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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

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

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();



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
                adminLoginButton.setVisibility(View.INVISIBLE);
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

        final String license;
        final String ID = adminID.getText().toString();
        final String password = adminPassword.getText().toString();


        if (checkFields(ID,password)==false) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
            adminLoginButton.setVisibility(View.VISIBLE);
        } else {

            final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
            firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        if (dataSnapshot.child("DoctorIDs").hasChild(ID)) {

                            final String Email = dataSnapshot.child("DoctorIDs").child(ID).getValue(String.class);

                            if (!dataSnapshot.child("DeActive").hasChild(ID)) {
                                mAuth.signInWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            if (mAuth.getCurrentUser().isEmailVerified()) {

                                                String docuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                final String license = dataSnapshot.child("Doctors").child(docuser).child("license").getValue().toString();
                                                // Check if the doctor change the password
                                                if (password.equals(license)) {

                                                    mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                adminLoginButton.setVisibility(View.VISIBLE);
                                                                Toast.makeText(getApplicationContext(), "We send an Email to reset your password", Toast.LENGTH_LONG).show();
                                                                OneSignal.sendTag("User_uid", mAuth.getUid().toString());
                                                            } else {
                                                                String Error = task.getException().getMessage();
                                                                Toast.makeText(getApplicationContext(), Error, Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    // Send user to Doctors main Activity
                                                    Intent loginIntent = new Intent(com.example.myhealthbuddyadmin.Login.this, DoctorMainActivity.class);
                                                    startActivity(loginIntent);
                                                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                mAuth.getCurrentUser().sendEmailVerification();
                                                Toast.makeText(getApplicationContext(), "Please check you email", Toast.LENGTH_LONG).show();
                                                adminLoginButton.setVisibility(View.VISIBLE);
                                                //Phone auth
                                                //Intent PhoneIntent = new Intent(Login.this, PhoneAuth.class);
                                                // startActivity(PhoneIntent);
                                            }

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Wrong ID or password", Toast.LENGTH_LONG).show();
                                            adminLoginButton.setVisibility(View.VISIBLE);
                                        }

                                    }
                                });
                            } else
                                Toast.makeText(getApplicationContext(), "Your Account has been deactivated", Toast.LENGTH_LONG).show();


                        }else{
                            Toast.makeText(getApplicationContext(), "There is no account", Toast.LENGTH_LONG).show();
                            adminLoginButton.setVisibility(View.VISIBLE);
                        }



                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    public void loginAdmin() {

        final String ID = adminID.getText().toString();
        final String password = adminPassword.getText().toString();


        if (checkFields(ID,password)==false) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
            adminLoginButton.setVisibility(View.VISIBLE);
        } else {
            if (ID.length() == 5 && !password.isEmpty()) {

                DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();



                firebaseRef.child("AdminIDs")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {

                                    final String Email = dataSnapshot.child(ID).getValue(String.class);


                                    mAuth.signInWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                String adminuser=mAuth.getCurrentUser().getUid();
                                                if(!dataSnapshot.child("DeActive").hasChild(adminuser)) {

                                                    sendUserToMainActivity(Email, password);
                                                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(getApplicationContext(), "Your Account has been deactivated", Toast.LENGTH_LONG).show();
                                                    adminLoginButton.setVisibility(View.VISIBLE);
                                                }

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                                adminLoginButton.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(getApplicationContext(), "ID does not exist", Toast.LENGTH_LONG).show();
                                    adminLoginButton.setVisibility(View.VISIBLE);
                                }

                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            } else {
                Toast.makeText(getApplicationContext(), "Wrong ID or password", Toast.LENGTH_LONG).show();
                adminLoginButton.setVisibility(View.VISIBLE);
            }
        }
    }



    private void sendUserToMainActivity(String Email,String password){
        Intent loginIntent=new Intent(Login.this,MainActivity.class);
        loginIntent.putExtra("Email",Email);
        loginIntent.putExtra("Password",password);
        startActivity(loginIntent);
    }
    private void sendUserToForgotPassword(){
        Intent loginIntent=new Intent(Login.this,Forgotpassword.class);
        startActivity(loginIntent);
    }



    public Boolean IsDeactive(){
        final Boolean[] IsDeactive = {false};

        String adminuser =mAuth.getCurrentUser().getUid();
        DatabaseReference Admin = FirebaseDatabase.getInstance().getReference().child("Admins").child(adminuser).child("uu");

        /*if(Admin.child("DeActive")){
            Toast.makeText(getApplicationContext(), "Yees", Toast.LENGTH_LONG).show();
            return true;
        }*/

        Admin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("DeActive").equals("Yes")){



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return IsDeactive[0];
    }

    public boolean checkFields(String id,String password){
        boolean flag=true;
        if(id.isEmpty()||password.isEmpty()){
            return false;
        }
        if(!id.matches("[0-9]+")) {
            return false;
        }

        return flag;

    }


}
