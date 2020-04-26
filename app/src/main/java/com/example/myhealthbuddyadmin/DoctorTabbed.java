package com.example.myhealthbuddyadmin;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.myhealthbuddyadmin.ui.main.SectionsPagerAdapter;

public class DoctorTabbed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_tabbed);
        BottomNavigationView bottomnav;
        bottomnav = (BottomNavigationView) findViewById(R.id.d_bottom_navigation);
        bottomnav.setSelectedItemId(R.id.d_nav_home);
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        setUpViewPager(viewPager);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setUpViewPager(ViewPager viewPager){
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new MyWrittenRecordsMain(),"Written Records");
        sectionsPagerAdapter.addFragment(new SharedRecordsMain(),"Shared Records");
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()){

            case R.id.d_nav_home:
                Intent intentshare = new Intent(DoctorTabbed.this, DoctorMainActivity.class);
                startActivity(intentshare);
                break;

            case R.id.d_nav_profile:
                Intent intentrequest=new Intent(DoctorTabbed.this, DoctorProfile.class);
                startActivity(intentrequest);
                break;

            case R.id.d_nav_search:
                Intent intentsearch=new Intent(DoctorTabbed.this, SearchForPatient.class);
                startActivity(intentsearch);
                break;

            case R.id.d_nav_notification:
                Intent intentNot=new Intent(DoctorTabbed.this, Notifications.class);
                startActivity(intentNot);
                break;
        }
    }
}