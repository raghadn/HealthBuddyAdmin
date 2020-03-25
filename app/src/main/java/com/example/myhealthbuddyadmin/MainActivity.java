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
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

   // Button CreateDoctor;
    private BottomNavigationView bottomnav;
    private ImageButton searchbtn;
    private EditText searchInpuText;
    private RecyclerView SearchResultList;
    private DatabaseReference allUsersdatabaseRef,admins,adminid;
    private FirebaseAuth mAuth,dAuth;
    String currentUserid,HospitalID;
    int DocNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomnav=(BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomnav.setSelectedItemId(R.id.nav_home);
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });

       /* CreateDoctor = findViewById(R.id.CD);
        CreateDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, CreateDoctor.class));
            }
        });*/


        //For Search


            allUsersdatabaseRef = FirebaseDatabase.getInstance().getReference().child("Doctors");
            mAuth = FirebaseAuth.getInstance();
            currentUserid = mAuth.getCurrentUser().getUid();
            admins = FirebaseDatabase.getInstance().getReference().child("Admins");
            admins.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                       HospitalID = dataSnapshot.child(currentUserid).child("Hospital").getValue().toString();
                        Browse();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
      //  Toast.makeText(this, "1"+HospitalID, Toast.LENGTH_LONG).show();


        // search btn
        searchbtn = (ImageButton) findViewById(R.id.searchbutton);

        // SearchInput
        searchInpuText = (EditText) findViewById(R.id.SearchInput);
        searchInpuText.setOnEditorActionListener(editorListener);

        // RecyclerView
        SearchResultList = (RecyclerView) findViewById(R.id.Searchresult);
        SearchResultList.setHasFixedSize(true);

        RecyclerView myRecycler = (RecyclerView) findViewById(R.id.Searchresult);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));
        myRecycler.setAdapter(new SampleRecycler());

        SearchResultList.setLayoutManager(new LinearLayoutManager(this));
        //Toast.makeText(this, HID, Toast.LENGTH_LONG).show();


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

            if(actionId == EditorInfo.IME_ACTION_SEARCH
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


        // check health facility id
        // **** we have to check if the text less than 3 char ******
        if (SearchBoxInput.substring(0,3).equals(HospitalID)) {
            Toast.makeText(this, "جاري البحث..", Toast.LENGTH_LONG).show();
            Query searchHCPInfiQuere = allUsersdatabaseRef.orderByChild("id").startAt(SearchBoxInput).endAt(SearchBoxInput+ "\uf8ff");
            FirebaseRecyclerAdapter<search_result, SearchViweHolder> FirebaseRecycleAdapter
                    = new FirebaseRecyclerAdapter<search_result, SearchViweHolder>
                    (
                            search_result.class,
                            R.layout.display_users_for_search,
                            SearchViweHolder.class,
                            searchHCPInfiQuere
                    ) {
                @Override
                protected void populateViewHolder(SearchViweHolder searchViweHolder, search_result module, final int i) {
                    searchViweHolder.setName(module.getName());
                    searchViweHolder.setID(module.getID());
                    searchViweHolder.setSpecialty(module.getSpecialty());
                    searchViweHolder.setGender(module.getGender());
                    //searchViweHolder.setImage(getApplicationContext(),module.getImage());

                }

            };
            SearchResultList.setAdapter(FirebaseRecycleAdapter);
        } else {
            Toast.makeText(this, "الرجاء إدخال رقم تابع للمنشئه الصحيه", Toast.LENGTH_LONG).show();
        }
    }

    public void Browse() {

        Query DisplayInfiQuere =allUsersdatabaseRef.orderByChild("id").startAt(HospitalID).endAt(HospitalID+"\uf8ff");
        //Query DisplayInfiQuere = allUsersdatabaseRef.orderByKey();
        FirebaseRecyclerAdapter<search_result, SearchViweHolder> FirebaseRecycleAdapter
                = new FirebaseRecyclerAdapter<search_result, SearchViweHolder>
                (
                        search_result.class,
                        R.layout.display_users_for_search,
                        SearchViweHolder.class,
                        DisplayInfiQuere
                ){
            @Override
            protected void populateViewHolder(SearchViweHolder searchViweHolder, search_result module, final int i) {
                DocNum++;
                searchViweHolder.setName(module.getName());
                searchViweHolder.setID(module.getID());
                searchViweHolder.setSpecialty(module.getSpecialty());
                searchViweHolder.setGender(module.getGender());

                //searchViweHolder.setImage(getApplicationContext(),module.getImage());
            }
        };
        SearchResultList.setAdapter(FirebaseRecycleAdapter);
    }


    public static class SearchViweHolder extends RecyclerView.ViewHolder {
        View mViwe;


        //defolt constroctor
        public SearchViweHolder(@NonNull View itemView) {
            super(itemView);
            mViwe = itemView;
        }

        public void setName(String Name) {
            TextView MyName= (TextView)mViwe.findViewById(R.id.all_HCP_profile_name);
            MyName.setText(Name);
        }
        public void setID(String ID) {
            TextView myID=(TextView)mViwe.findViewById(R.id.all_HCP_profile_Id);
            // result was all user names (retreved from database)
            myID.setText(ID);
        }

        public void setSpecialty(String Specialty) {
            TextView mySpecialty=(TextView) mViwe.findViewById(R.id.all_HCP_profile_specialty);
            mySpecialty.setText(Specialty);
        }
        public void setGender(String gender) {
            TextView mygender=(TextView) mViwe.findViewById(R.id.all_HCP_profile_gender);
            mygender.setText(gender);
        }
        public void setImage(Context ctx, String img) {

  /*  CircleImageView MyImage= (CircleImageView) mViwe.findViewById(R.id.all_HCP_profileImg);
    Picasso.with(ctx).load(img).placeholder(R.drawable.doctoricon.Into(MyImage);
    // Into or into
    */

            ImageView MyImage = (ImageView) mViwe.findViewById(R.id.all_HCP_profileImg);
            //  Picasso.get().load(img).into(post_image);
            Picasso.get().load(img).into(MyImage);
            Glide.with(ctx).load(img).into(MyImage);

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



    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_create:
                Intent intentCreate = new Intent(MainActivity.this, CreateDoctor.class);
                startActivity(intentCreate);
                break;

            case R.id.nav_profile:
                Intent intentprofile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intentprofile);
                break;

        }

    }


}





