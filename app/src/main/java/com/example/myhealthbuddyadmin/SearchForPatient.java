package com.example.myhealthbuddyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_patient);

        mAuth = FirebaseAuth.getInstance();
        allPatientsdatabaseRef = FirebaseDatabase.getInstance().getReference().child("Patients");




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

        public void setImage(Context ctx, String img) {

  /*CircleImageView MyImage= (CircleImageView) mViwe.findViewById(R.id.all_patient_profileImg*?);
    Picasso.with(ctx).load(img).placeholder(R.drawable.doctoricon.Into(MyImage);
    // Into or into
    */

            ImageView MyImage = (ImageView) mViwe.findViewById(R.id.all_patient_profileImg);
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
}

