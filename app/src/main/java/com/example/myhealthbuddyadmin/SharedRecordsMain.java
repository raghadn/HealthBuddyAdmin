package com.example.myhealthbuddyadmin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SharedRecordsMain extends Fragment {

    private RecyclerView prescriptionList;
    private DatabaseReference allSharesRef,allRecordsRef;
    private FirebaseAuth mAuth;
    BottomNavigationView Doctorbottomnav;
    private String currentHCPuid,PatientKey;
    RecordAdapter mAdapter ;
    TextView Noresult;
    int type;
    String stype;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_shared_records_main,container,false);


        type=(int)getActivity().getIntent().getExtras().get("type");
        stype=getActivity().getIntent().getExtras().get("type").toString();
        allSharesRef = FirebaseDatabase.getInstance().getReference().child("Share");
        allRecordsRef = FirebaseDatabase.getInstance().getReference().child("Records");


        mAuth = FirebaseAuth.getInstance();
        currentHCPuid= mAuth.getCurrentUser().getUid();



        mAuth = FirebaseAuth.getInstance();
        currentHCPuid= mAuth.getCurrentUser().getUid();



        Noresult=view.findViewById(R.id.NoResult);

        // RecyclerView
        prescriptionList = view.findViewById(R.id.shared);
        prescriptionList.setHasFixedSize(true);
        prescriptionList.setLayoutManager(new LinearLayoutManager(SharedRecordsMain.this.getActivity()));
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(view.getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        prescriptionList.setLayoutManager(linearLayoutManager);



        BrowseShare();

        return view;
    }
    private void BrowseShare() {

        final ArrayList<item_record> records= new ArrayList<>();
        final ArrayList<String> Ids= new ArrayList<>();
        mAdapter = new RecordAdapter(SharedRecordsMain.this.getActivity(), records);


        allSharesRef.orderByChild("hcp_uid").equalTo(currentHCPuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot share : dataSnapshot.getChildren()) {
                        String share_did=share.child("hcp_uid").getValue().toString();

                        if(share_did.equals(mAuth.getCurrentUser().getUid())) {
                            String id = share.child("record_id").getValue().toString();
                            if (!Ids.contains(id)) {
                                Ids.add(id);
                            }
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        allRecordsRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {
                                                    for (DataSnapshot record : dataSnapshot1.getChildren()) {
                                                        item_record r = record.getValue(item_record.class);
                                                        if(type==r.type) {
                                                            if (Ids.contains(record.getKey())) {
                                                                r.dateCreated = record.child("dateCreated").getValue().toString();
                                                                r.rid = record.getKey();
                                                                records.add(r);


                                                           }
                                                        }

                                                    }
                                                    mAdapter = new RecordAdapter(SharedRecordsMain.this.getActivity(), records);
                                                    prescriptionList.setAdapter(mAdapter);

                                                    mAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(String Rid) {

                                                            switch (type) {
                                                                case 1:
                                                                    Intent intentPrescriptionList = new Intent(SharedRecordsMain.this.getActivity(), ViewPrescription.class);
                                                                    intentPrescriptionList.putExtra("recordID", Rid);
                                                                    startActivity(intentPrescriptionList);
                                                                    break;
                                                                case 2:
                                                                    Intent intentMyBloodTests = new Intent(SharedRecordsMain.this.getActivity(), ViewBloodTest.class);
                                                                    intentMyBloodTests.putExtra("recordID", Rid);
                                                                    startActivity(intentMyBloodTests);
                                                                    break;
                                                                case 3:
                                                                    Intent intentMyx_Rays = new Intent(SharedRecordsMain.this.getActivity(), ViewXRay.class);
                                                                    intentMyx_Rays.putExtra("recordID", Rid);
                                                                    startActivity(intentMyx_Rays);
                                                                    break;
                                                                case 4:
                                                                    Intent intentMyVital = new Intent(SharedRecordsMain.this.getActivity(), ViewVitalSigns.class);
                                                                    intentMyVital.putExtra("recordID", Rid);
                                                                    startActivity(intentMyVital);
                                                                    break;
                                                                case 5:
                                                                    Intent intentRecord = new Intent(SharedRecordsMain.this.getActivity(), ViewRecord.class);
                                                                    intentRecord.putExtra("recordID", Rid);
                                                                    startActivity(intentRecord);
                                                                    break;
                                                            }


                                                        }
                                                    });
                                                    if (records.size() == 0) {
                                                        Noresult.setVisibility(View.VISIBLE);

                                                    } else Noresult.setVisibility(View.INVISIBLE);
                                                }


                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });






    }
}
