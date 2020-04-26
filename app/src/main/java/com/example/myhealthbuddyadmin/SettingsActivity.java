package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    CircleImageView userImage;
    TextView userName;
    TextView userHos;
    private FirebaseUser Fuser;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef,href;


    CardView Signoutbtn , deActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);




        userImage=findViewById(R.id.userimage);
        userHos=findViewById(R.id.Hospital);
        userName=findViewById(R.id.Name);
        deActive=findViewById(R.id.deactivebtn);

        mAuth = FirebaseAuth.getInstance();
        Fuser =mAuth.getCurrentUser();


        Signoutbtn = findViewById(R.id.singout);
        Signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignOut();
            }
        });







        mRef = FirebaseDatabase.getInstance().getReference().child("Admins").child(Fuser.getUid());



        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstname,lastname,username,hos;
                if (dataSnapshot.exists()){
                    firstname = dataSnapshot.child("FirstName").getValue().toString();
                    lastname=dataSnapshot.child("LastName").getValue().toString();
                    username=firstname+' '+lastname;
                    hos=dataSnapshot.child("Hospital").getValue().toString();

                    userName.setText(username);




                    href=FirebaseDatabase.getInstance().getReference().child("Hospitals").child(hos);
                    href.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String hospital;
                            hospital=dataSnapshot.child("Name").getValue().toString();

                            userHos.setText(hospital);
                            String imge=dataSnapshot.child("Image").getValue().toString();


                            if(dataSnapshot.hasChild("Image")){
                                Picasso.get().load(imge).into(userImage);
                               // Glide.with(getApplicationContext()).load(imge).into(userImage);
                                 }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        // To DeActivate the Admin
        deActive.setOnClickListener(new View.OnClickListener() {
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
                                // User clicked the yes button
                                FirebaseDatabase.getInstance().getReference().child("AdminIDs").child("DeActive").child(Fuser.getUid()).setValue("Deactive");
                                Toast.makeText(getApplicationContext(), "Your Admin Acount is deactive",Toast.LENGTH_LONG).show();
                                SignOut();
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
    }

    private void SignOut() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {

            startActivity(new Intent(SettingsActivity.this, Login.class));
        }
    }


}


