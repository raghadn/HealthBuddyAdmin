package com.example.myhealthbuddyadmin;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {
    private ArrayList<item_record> mRecorslist;
    private OnItemClickListener listener;
    Context c;


    public RecordAdapter(Context c , ArrayList<item_record> records) {
        mRecorslist=records;
        this.c=c;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.record_item,parent, false);
        RecordViewHolder rvh = new RecordViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecordViewHolder holder, final int position) {

        item_record currentItem = mRecorslist.get(position);
        //the same as
        holder.mdate.setText(mRecorslist.get(position).getDate());
        holder.mdoctorName.setText(currentItem.getDoctorName());
        holder.mpatientName.setText(currentItem.getPatientName());
        holder.mrid.setText(currentItem.getRid());

        int myType =currentItem.getType();
        if(myType==1)
            holder.mtype.setText("Prescription");
        if(myType==2)
            holder.mtype.setText("Blood Test");
        if(myType==3)
            holder.mtype.setText("X-Ray");
        if(myType==4)
            holder.mtype.setText("Vital Signs");
        if(myType==5)
            holder.mtype.setText("Records");


        //holder.mhname.setText(currentItem.getHname());

    }

    @Override
    public int getItemCount() {
        return mRecorslist.size() ;
    }



    public class RecordViewHolder extends RecyclerView.ViewHolder {
        public TextView mdate;
        public TextView mrid;
        public TextView mdoctorName;
        public TextView mpatientName;
        public TextView mhname;
        public TextView mtype;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            mdoctorName =itemView.findViewById(R.id.d_name);
            mhname =itemView.findViewById(R.id.hname);
            mpatientName =itemView.findViewById(R.id.patient_name);
            mdate=itemView.findViewById(R.id.record_date);
            mrid= itemView.findViewById(R.id.Rid);
            mtype=itemView.findViewById(R.id.Type);



            Intent n = new Intent(c, Prescriptions.class);
            n.putExtra("list",mRecorslist );

        }
    }
    public interface OnItemClickListener {
        void onItemClick(ArrayList<String> mRecorslist);

    }
    public void setOnItemClickListener (OnItemClickListener listener){
        this.listener=listener;
    }


}
