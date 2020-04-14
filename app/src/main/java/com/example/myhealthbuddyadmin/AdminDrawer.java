package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class AdminDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout mDrawer;
    NavigationView  navigationView;
    ActionBarDrawerToggle mToggle;
    Toolbar tool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_drawer);

        //Admin Drawer
        mDrawer=findViewById(R.id.admindrawer);
        navigationView =findViewById(R.id.nav11);
        mToggle =new ActionBarDrawerToggle(this,mDrawer,R.string.Open,R.string.Close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.drawer_home:
                Toast.makeText(AdminDrawer.this, "home Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_setting:
                Toast.makeText(AdminDrawer.this, "Setting Selected", Toast.LENGTH_SHORT).show();
                break;


            case R.id.drawer_logout:
                Toast.makeText(AdminDrawer.this, "Logout Selected", Toast.LENGTH_SHORT).show();
                break;


        }
        return false;
    }
}
