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
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhealthbuddyadmin.ui.main.SectionsPagerAdapter;
import com.google.firebase.database.FirebaseDatabase;

public class ViewRecordsTabbed extends AppCompatActivity {

    TextView Noresult,PageTitel;
    FloatingActionButton write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_records_tabbed);

        BottomNavigationView bottomnav;
        bottomnav = (BottomNavigationView) findViewById(R.id.d_bottom_navigation);
        bottomnav.setSelectedItemId(R.id.d_nav_search);
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });

        Noresult=findViewById(R.id.NoResult);
        PageTitel=findViewById(R.id.title);

        write=findViewById(R.id.write);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        setUpViewPager(viewPager);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        final int type=(int)getIntent().getExtras().get("type");
        final String PatientKey=getIntent().getExtras().get("PatientKey").toString();

        switch(type){

            case 1: Noresult.setText("No prescriptions available  ");
                PageTitel.setText("Prescriptions");
                break;
            case 2: Noresult.setText("No blood tests available   ");
                PageTitel.setText("Blood Tests");
                break;
            case 3: Noresult.setText("No X-Rays available  ");
                PageTitel.setText("X-Rays" );
                break;
            case 4: Noresult.setText("No vital signs available");
                PageTitel.setText("Vital Signs");

                break;
            case 5: Noresult.setText("No record available ");
                PageTitel.setText("Records");
                break;
        }

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                switch(type){
                    case 1:  Intent writePres=new Intent(ViewRecordsTabbed.this, WritePrescription.class);
                        writePres.putExtra("PatientKey",PatientKey);
                        writePres.putExtra("Request","N");
                        startActivity(writePres);
                        break;
                    case 2: Intent wBloodTests = new Intent(ViewRecordsTabbed.this, WriteBloodTest.class);
                        wBloodTests.putExtra("PatientKey",PatientKey);
                        wBloodTests.putExtra("Request","N");
                        startActivity(wBloodTests);
                        break;
                    case 3:  Intent wx_Rays = new Intent(ViewRecordsTabbed.this, WriteXRay.class);
                        wx_Rays.putExtra("PatientKey",PatientKey);
                        wx_Rays.putExtra("Request","N");
                        startActivity(wx_Rays);
                        break;
                    case 4: Intent wVital = new Intent(ViewRecordsTabbed.this, WriteVitalSigns.class);
                        wVital.putExtra("PatientKey",PatientKey);
                        wVital.putExtra("Request","N");
                        startActivity(wVital);
                        break;
                    case 5:Intent wRecord = new Intent(ViewRecordsTabbed.this, WriteRecord.class);
                        wRecord.putExtra("PatientKey",PatientKey);
                        wRecord.putExtra("Request","N");
                        startActivity(wRecord);
                        break;
                }

            }
        });







    }

    private void setUpViewPager(ViewPager viewPager){
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new MyRecordsFragment(),"Written Records");
        sectionsPagerAdapter.addFragment(new SharedRecordsFragment(),"Shared Records");
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()){

            case R.id.d_nav_home:
                Intent intentshare = new Intent(ViewRecordsTabbed.this, DoctorMainActivity.class);
                startActivity(intentshare);
                break;

            case R.id.d_nav_profile:
                Intent intentrequest=new Intent(ViewRecordsTabbed.this, DoctorProfile.class);
                startActivity(intentrequest);
                break;

            case R.id.d_nav_search:
                Intent intentsearch=new Intent(ViewRecordsTabbed.this, SearchForPatient.class);
                startActivity(intentsearch);
                break;
        }
    }
}