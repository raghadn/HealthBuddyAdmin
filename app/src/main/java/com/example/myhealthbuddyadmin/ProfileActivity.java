package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomnav;
    CircleImageView userImage;
    TextView userName;
    TextView userHos;
    private FirebaseUser Fuser;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef,href;
    Button Signoutbtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userImage=findViewById(R.id.userimage);
        userHos=findViewById(R.id.Hospital);
        userName=findViewById(R.id.Name);

        mAuth = FirebaseAuth.getInstance();
        Fuser =mAuth.getCurrentUser();

        Signoutbtn = findViewById(R.id.singout);
        Signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {

                    startActivity(new Intent(ProfileActivity.this, Login.class));
                }
            }
        });

        bottomnav=(BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomnav.setSelectedItemId(R.id.nav_profile);
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
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
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.nav_create:
                Intent intentCreate = new Intent(ProfileActivity.this, CreateDoctor.class);
                startActivity(intentCreate);
                break;

            case R.id.nav_home:
                Intent intentprofile = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intentprofile);
                break;

        }

    }
}


