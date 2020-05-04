package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import de.hdodenhof.circleimageview.CircleImageView;

public class HCP_Profile extends AppCompatActivity {
    private BottomNavigationView bottomnav;
    CircleImageView userImage;
    TextView userName, userHos,phone, id,specialty,license;
    private FirebaseUser Fuser;
    private FirebaseAuth mAuth;
    private DatabaseReference dref,href;
    BottomNavigationView Doctorbottomnav;
    CardView LogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);


        mAuth =FirebaseAuth.getInstance();
        Fuser =mAuth.getCurrentUser();

        Doctorbottomnav=findViewById(R.id.d_bottom_navigation);
        Doctorbottomnav.setSelectedItemId(R.id.d_nav_profile);
        Doctorbottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                DoctorMenuSelector(menuItem);
                return false;
            }
        });



        userImage=findViewById(R.id.doctorimage);
        userHos=findViewById(R.id.doctorHospital);
        userName=findViewById(R.id.doctorName);
        LogOut=findViewById(R.id.logoutcard);
        specialty = findViewById(R.id.docspecialty);
        phone = findViewById(R.id.docphone);
        id = findViewById(R.id.docid);
        license=findViewById(R.id.licenseview);

        mAuth = FirebaseAuth.getInstance();
        Fuser =mAuth.getCurrentUser();
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent logoIntent = new Intent(HCP_Profile.this, Login.class);
                logoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoIntent);
                finish();


            }
        });


        dref = FirebaseDatabase.getInstance().getReference().child("Doctors").child(Fuser.getUid());
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fName, hos;
                    fName = dataSnapshot.child("name").getValue().toString();
                    userName.setText(fName);

                    id.setText( dataSnapshot.child("id").getValue().toString());
                    specialty.setText(dataSnapshot.child("specialty").getValue().toString());
                    phone.setText(dataSnapshot.child("phone").getValue().toString());
                    license.setText(dataSnapshot.child("license").getValue().toString());

                            //String imge = dataSnapshot.child("Image").getValue().toString();

                    hos = dataSnapshot.child("hospital").getValue().toString();
                    href = FirebaseDatabase.getInstance().getReference().child("Hospitals").child(hos);
                    href.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String hospital;
                                hospital = dataSnapshot.child("Name").getValue().toString();
                                userHos.setText(hospital);


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
    }




    private void DoctorMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.d_nav_search:
                Intent intentSearch = new Intent(HCP_Profile.this, SearchForPatient.class);
                startActivity(intentSearch);
                break;

            case R.id.d_nav_home:
                Intent intentHome = new Intent(HCP_Profile.this, DoctorMainActivity.class);
                startActivity(intentHome);
                break;

            case R.id.d_nav_notification:
                Intent intentNot=new Intent(HCP_Profile.this, Notifications.class);
                startActivity(intentNot);
                break;
        }

    }
}
