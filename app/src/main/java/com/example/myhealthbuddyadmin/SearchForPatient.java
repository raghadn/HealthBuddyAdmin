package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchForPatient extends AppCompatActivity {

    private ImageButton searchbtn;
    private EditText searchInpuText;
    private RecyclerView PatientResultList;
    private DatabaseReference allPatientsdatabaseRef ;
    private FirebaseAuth mAuth;
    BottomNavigationView Doctorbottomnav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_patient);

        mAuth = FirebaseAuth.getInstance();
        allPatientsdatabaseRef = FirebaseDatabase.getInstance().getReference().child("Patients");

        Doctorbottomnav=findViewById(R.id.d_bottom_navigation);
        Doctorbottomnav.setSelectedItemId(R.id.d_nav_search);
        Doctorbottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                DoctorMenuSelector(menuItem);
                return false;
            }
        });


        // search btn
        searchbtn = (ImageButton) findViewById(R.id.searchPatientbutton);

        // SearchInput
        searchInpuText = (EditText) findViewById(R.id.SearchPatientInput);
        searchInpuText.setOnEditorActionListener(editorListener);


        // RecyclerView
        PatientResultList = (RecyclerView) findViewById(R.id.Patientresult);
        PatientResultList.setHasFixedSize(true);


        RecyclerView myRecycler = (RecyclerView) findViewById(R.id.Patientresult);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));
        myRecycler.setAdapter(new SampleRecycler());

        PatientResultList.setLayoutManager(new LinearLayoutManager(this));


        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SearchBoxInput = searchInpuText.getText().toString();
                SearchMethod(SearchBoxInput);

            }
        });
    }

    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                String SearchBoxInput = searchInpuText.getText().toString();
                SearchMethod(SearchBoxInput);
                return true;

            }
            return false;
        }
    };


    public void SearchMethod(String SearchBoxInput) {


        Toast.makeText(this, "جاري البحث..", Toast.LENGTH_SHORT).show();
        Query searchPatientnQuere = allPatientsdatabaseRef.orderByChild("national_id").startAt(SearchBoxInput).endAt(SearchBoxInput+ "\uf8ff");

        FirebaseRecyclerAdapter<PatientResult, PatientViweHolder> FirebaseRecycleAdapter
                = new FirebaseRecyclerAdapter<PatientResult, PatientViweHolder>
                (
                        PatientResult.class,
                        R.layout.display_all_patient_for_search,
                        PatientViweHolder.class,
                        searchPatientnQuere
                ) {
            @Override
            protected void populateViewHolder(PatientViweHolder patientViweHolder, PatientResult module, final int i) {


                patientViweHolder.setName(module.getName());
                patientViweHolder.setNational_id(module.getNational_id());

                //redirect to the patient when clicking on it
                patientViweHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String PatientKey=getRef(i).getKey();
                        Intent intent=new Intent(SearchForPatient.this,ViewPatient.class);
                        intent.putExtra("PatientKey",PatientKey);
                        startActivity(intent);
                    }
                });


                //searchViweHolder.setImage(getApplicationContext(),module.getImage());

                   /*
                      ----------to go to specific page
                   patientViweHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Name=getRef(i).getKey();
                        Intent profielintent=new Intent(SearchForPatient.this,ViewHCP.class);
                        profielintent.putExtra("PatientName",Name);
                        startActivity(profielintent);
                    }
                }); */

            }

        };
        PatientResultList.setAdapter(FirebaseRecycleAdapter);

    }



    public static class PatientViweHolder extends RecyclerView.ViewHolder {
        View mViwe;


        //defolt constroctor
        public PatientViweHolder(@NonNull View itemView) {
            super(itemView);
            mViwe = itemView;
        }

        public void setName(String Name) {
            TextView MyName = (TextView) mViwe.findViewById(R.id.all_patient_profile_name);
            MyName.setText(Name);
        }

        public void setNational_id(String national_id) {
            TextView myID = (TextView) mViwe.findViewById(R.id.all_patient_profile_id);
            System.out.print(myID);
            // result was all user names (retreved from database)
            myID.setText(national_id);
        }


    }

    public class SampleHolder extends RecyclerView.ViewHolder {
        public SampleHolder(View itemView) {
            super(itemView);
        }

    }

    public class SampleRecycler extends RecyclerView.Adapter<SampleHolder> {
        @Override
        public SampleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(SampleHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    private void DoctorMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.d_nav_profile:
                Intent intentProfile = new Intent(SearchForPatient.this, DoctorProfile.class);
                startActivity(intentProfile);
                break;

            case R.id.d_nav_home:
                Intent intentHome = new Intent(SearchForPatient.this, DoctorMainActivity.class);
                startActivity(intentHome);
                break;

            case R.id.d_nav_notification:
                Intent intentNot=new Intent(SearchForPatient.this, Notifications.class);
                startActivity(intentNot);
                break;
        }

    }
}

