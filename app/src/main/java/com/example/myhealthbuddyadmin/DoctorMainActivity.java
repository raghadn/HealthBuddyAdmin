package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorMainActivity extends AppCompatActivity {
//added the redirect to record when clicked viewRecord method

    DatabaseReference RecordRef;
    FirebaseAuth mAuth;
    String currentDoctorid;
    BottomNavigationView Doctorbottomnav;

    TextView userName;

    private FirebaseUser Fuser;
    private DatabaseReference dref;
    CardView Mypills,MyBloodTests,Myx_Rays,MyVital,Myreports;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        Doctorbottomnav=findViewById(R.id.d_bottom_navigation);
        Doctorbottomnav.setSelectedItemId(R.id.d_nav_home);
        Doctorbottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                DoctorMenuSelector(menuItem);
                return false;
            }
        });


        RecordRef = FirebaseDatabase.getInstance().getReference().child("Records");
        mAuth = FirebaseAuth.getInstance();
        currentDoctorid = mAuth.getCurrentUser().getUid();


        OneSignal.sendTag("User_uid",mAuth.getCurrentUser().getUid().toString());
        /*
        test=findViewById(R.id.button4);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DoctorMainActivity.this,DoctorTabbed.class);
                intent.putExtra("type",1);
                startActivity(intent);
            }
        });*/

        userName=findViewById(R.id.doctorName);
        mAuth = FirebaseAuth.getInstance();
        Fuser =mAuth.getCurrentUser();



        dref = FirebaseDatabase.getInstance().getReference().child("Doctors").child(Fuser.getUid());
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fName;
                    fName = dataSnapshot.child("name").getValue().toString();
                    userName.setText("Welcome "+fName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        Mypills = findViewById(R.id.pillsCard);
        Mypills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPills=new Intent(DoctorMainActivity.this, DoctorTabbed.class);
                intentPills.putExtra("type",1);
                startActivity(intentPills);
            }
        });


        MyBloodTests= findViewById(R.id.blodcard);
        MyBloodTests.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intentMyBloodTests = new Intent(DoctorMainActivity.this, DoctorTabbed.class);
                                                intentMyBloodTests.putExtra("type",2);
                                                startActivity(intentMyBloodTests);
                                            }
                                        }
        );
        Myx_Rays= findViewById(R.id.Xraycard);
        Myx_Rays.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intentMyx_Rays = new Intent(DoctorMainActivity.this, DoctorTabbed.class);
                                            intentMyx_Rays.putExtra("type",3);
                                            startActivity(intentMyx_Rays);
                                        }
                                    }
        );
        MyVital= findViewById(R.id.cardiocard);
        MyVital.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intentMyVital = new Intent(DoctorMainActivity.this, DoctorTabbed.class);
                                           intentMyVital.putExtra("type",4);
                                           startActivity(intentMyVital);
                                       }
                                   }
        );
        Myreports = findViewById(R.id.reporcard);
        Myreports.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Intent intentRecord = new Intent(DoctorMainActivity.this, DoctorTabbed.class);
                                             intentRecord.putExtra("type",5);
                                             startActivity(intentRecord);
                                         }
                                     }
        );

    }




    private void DoctorMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.d_nav_search:
                Intent intentSearch = new Intent(DoctorMainActivity.this, SearchForPatient.class);
                startActivity(intentSearch);
                break;

            case R.id.d_nav_profile:
                Intent intentProfile = new Intent(DoctorMainActivity.this, DoctorProfile.class);
                startActivity(intentProfile);
                break;

            case R.id.d_nav_notification:
                Intent intentNotifications = new Intent(DoctorMainActivity.this, Notifications.class);
                startActivity(intentNotifications);
                break;


        }

    }


}
