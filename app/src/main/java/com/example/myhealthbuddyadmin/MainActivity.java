package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

   // Button CreateDoctor;
    private BottomNavigationView bottomnav;
    private ImageButton searchbtn;
    private EditText searchInpuText;
    private RecyclerView SearchResultList;
    private DatabaseReference allUsersdatabaseRef,admins,adminid,href;
    private FirebaseAuth mAuth,dAuth;
    String currentUserid,HospitalID;
    int DocNum;


    DrawerLayout mDrawer;
    NavigationView navigationView;
    ActionBarDrawerToggle mToggle;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Admin Drawer
        mDrawer=findViewById(R.id.maindrawer);
        toolbar=findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        navigationView =findViewById(R.id.nav22);
        mToggle =new ActionBarDrawerToggle(MainActivity.this,mDrawer,R.string.Open,R.string.Close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navigationView.setNavigationItemSelectedListener(this);


        // IMPORTANT
        String AdminEmail = getIntent().getStringExtra("Email");
        String AdminPass = getIntent().getStringExtra("Password");
        //Toast.makeText(this, AdminEmail, Toast.LENGTH_LONG).show();
       // Toast.makeText(this, AdminPass, Toast.LENGTH_LONG).show();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentrequest=new Intent(MainActivity.this, CreateDoctor.class);
                startActivity(intentrequest);
            }
        });




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


        ViewAdminProfile();
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
            protected void populateViewHolder(final SearchViweHolder searchViweHolder, final search_result module, final int i) {
                DocNum++;
                searchViweHolder.setName(module.getName());
                searchViweHolder.setID(module.getID());
                searchViweHolder.setSpecialty(module.getSpecialty());
                searchViweHolder.setGender(module.getGender());

                //PopupMenu popup =new PopupMenu(searchViweHolder.getContext())

                searchViweHolder.TextViewOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        PopupMenu popup =new PopupMenu(v.getContext(),searchViweHolder.TextViewOptions);
                        popup.inflate(R.menu.admin_options_menu);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {

                                    case R.id.docdeactive:

                                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                                        builder.setTitle("DE Active Health Care Provider");
                                        builder.setMessage("Are you sure you want to De active the health care provider?");
                                        // Set click listener for alert dialog buttons
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch(which){
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        // User clicked the yes button
                                                        allUsersdatabaseRef.getParent().child("DoctorIDs").child("DeActive").child(module.getID()).setValue("Deactive");
                                                        Toast.makeText(getApplicationContext(), "this Acount is deactive",Toast.LENGTH_LONG).show();
                                                        break;

                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        // User clicked the no button
                                                        break;
                                                }
                                            }
                                        };
                                        // Set the alert dialog yes button click listener
                                        builder.setPositiveButton("Yes", dialogClickListener);

                                        // Set the alert dialog no button click listener
                                        builder.setNegativeButton("No",dialogClickListener);

                                        AlertDialog dialog = builder.create();
                                        // Display the alert dialog on interface
                                        dialog.show();

                                        return true;


                                    case R.id.docedit:
                                        //handle menu2 click
                                        return true;

                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.show();
                    }
                });



                //searchViweHolder.setImage(getApplicationContext(),module.getImage());
            }
        };
        SearchResultList.setAdapter(FirebaseRecycleAdapter);
    }






    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.drawer_home:
                Toast.makeText(MainActivity.this, "You Are In Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_setting:
                Intent intentSetting = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSetting);
                break;


            case R.id.drawer_logout:
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null)
                    startActivity(new Intent(MainActivity.this, Login.class));
                break;


        }
        return false;
    }


    public static class SearchViweHolder extends RecyclerView.ViewHolder {
        View mViwe;
        TextView MyName;
        ImageView TextViewOptions;


        //defolt constroctor
        public SearchViweHolder(@NonNull View itemView) {
            super(itemView);
            mViwe = itemView;
            //TextView MyName;
            TextViewOptions=mViwe.findViewById(R.id.textViewOptions);
        }



        public void setName(String Name) {
             MyName= (TextView)mViwe.findViewById(R.id.all_HCP_profile_name);
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



        }

    }

    public void ViewAdminProfile(){
        NavigationView navigationView =findViewById(R.id.nav22);
        View header=navigationView.getHeaderView(0);
        final TextView AdminName = header.findViewById(R.id.adminmname);
        final TextView HospitalName = header.findViewById(R.id.hosname);
        final CircleImageView image=header.findViewById(R.id.hosimg);

        admins.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstname,lastname,username,hos;
                if (dataSnapshot.exists()){
                    firstname = dataSnapshot.child(currentUserid).child("FirstName").getValue().toString();
                    lastname=dataSnapshot.child(currentUserid).child("LastName").getValue().toString();
                    username=firstname+' '+lastname;
                    hos=dataSnapshot.child(currentUserid).child("Hospital").getValue().toString();

                    AdminName.setText(username);


                    href=FirebaseDatabase.getInstance().getReference().child("Hospitals").child(hos);
                    href.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String hospital;
                            hospital=dataSnapshot.child("Name").getValue().toString();

                            HospitalName.setText(hospital);
                            String imge=dataSnapshot.child("Image").getValue().toString();


                            if(dataSnapshot.hasChild("Image")){
                                Picasso.get().load(imge).into(image);
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


}





