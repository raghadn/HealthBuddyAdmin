package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;




import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button CreateDoctor,Signoutbtn;
    private BottomNavigationView bottomnav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomnav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });

        CreateDoctor = findViewById(R.id.CD);
        CreateDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, CreateDoctor.class));
            }
        });

        Signoutbtn = findViewById(R.id.singout);
        Signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {

                    startActivity(new Intent(MainActivity.this, Login.class));
                }
            }
        });


    }
    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_profile:
                Intent intentprofile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intentprofile);
                break;

        }

    }
}
