package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorProfile extends AppCompatActivity {
    private BottomNavigationView bottomnav;
    CircleImageView userImage;
    TextView userName;
    TextView userHos;
    private FirebaseUser Fuser;
    private FirebaseAuth mAuth;
    private DatabaseReference dref,href;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        userImage=findViewById(R.id.doctorimage);
        userHos=findViewById(R.id.doctorHospital);
        userName=findViewById(R.id.doctorName);

        mAuth = FirebaseAuth.getInstance();
        Fuser =mAuth.getCurrentUser();

        bottomnav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomnav.setSelectedItemId(R.id.nav_profile);
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
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
                    hos = dataSnapshot.child("Hospital").getValue().toString();


                    href = FirebaseDatabase.getInstance().getReference().child("Hospitals").child(hos);
                    href.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String hospital;
                                hospital = dataSnapshot.child("Name").getValue().toString();

                                userHos.setText(hospital);
                                String imge = dataSnapshot.child("Image").getValue().toString();


                                if (dataSnapshot.hasChild("Image")) {
                                    Picasso.get().load(imge).into(userImage);
                                    // Glide.with(getApplicationContext()).load(imge).into(userImage);
                                }

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


    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_home:
                Intent intentprofile = new Intent(DoctorProfile.this, DoctorMainActivity.class);
                startActivity(intentprofile);
                break;

        }

    }
}
