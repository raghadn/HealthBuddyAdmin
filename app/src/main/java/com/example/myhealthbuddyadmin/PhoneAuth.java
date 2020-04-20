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
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuth extends AppCompatActivity {

    Button authBtn;
    EditText PhCode;
    FirebaseAuth mAuth;
    String codeSent;
    FirebaseUser user,phuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

         user = FirebaseAuth.getInstance().getCurrentUser();
        PhCode= findViewById(R.id.phonecode);
        sendVerificationCode();

        authBtn= findViewById(R.id.phbtn);
        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               verifySignInCode();
            }
        });

        mAuth = FirebaseAuth.getInstance();


    }

    private void verifySignInCode(){

        String code = PhCode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        String ActCode = credential.getSmsCode();
        Toast.makeText(getApplicationContext(), "Act Code ="+ActCode, Toast.LENGTH_LONG).show();

        if (ActCode==code){
           Toast.makeText(getApplicationContext(), "Correct Code", Toast.LENGTH_LONG).show();

            String u=user.getUid().toString();
           Toast.makeText(getApplicationContext(), "user ="+u, Toast.LENGTH_LONG).show();

        }
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {


       mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(getApplicationContext(), "Login Successfull", Toast.LENGTH_LONG).show();
                    phuser=FirebaseAuth.getInstance().getCurrentUser();

                    phuser.delete();


                    // send Email Verification
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.sendEmailVerification();

                    Toast.makeText(getApplicationContext(), "Please Check your email", Toast.LENGTH_LONG).show();
                    String u=user.getUid().toString();
                    Toast.makeText(getApplicationContext(), "user ="+u, Toast.LENGTH_LONG).show();



                   // Intent PhoneIntent = new Intent(PhoneAuth.this, Forgotpassword.class);
                  //  startActivity(PhoneIntent);

                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(getApplicationContext(),
                                "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        Intent intentCreate = new Intent(PhoneAuth.this, Login.class);
        startActivity(intentCreate);
    }



    private void sendVerificationCode() {

        String phone ="+966508086356";
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            Toast.makeText(getApplicationContext(), "Act Code ="+code, Toast.LENGTH_LONG).show();

            Toast.makeText(PhoneAuth.this, "Verification Complete", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(PhoneAuth.this, "Verification Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(PhoneAuth.this, "Code Sent", Toast.LENGTH_SHORT).show();
            codeSent = s;
           // Toast.makeText(getApplicationContext(), "the code"+codeSent, Toast.LENGTH_LONG).show();

        }
    };
}
