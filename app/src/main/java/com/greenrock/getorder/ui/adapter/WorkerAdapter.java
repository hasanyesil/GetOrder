package com.greenrock.getorder.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenrock.getorder.R;
import com.greenrock.getorder.model.Worker;
import com.greenrock.getorder.ui.activity.WorkerDetailsActivity;

import java.util.ArrayList;


public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerHolder> {

    private Context context;

    private ArrayList<Worker> workerList;

    public WorkerAdapter(ArrayList<Worker> workerList, Context context){
        this.workerList = workerList;
        this.context = context;
    }

    public void setWorkers(ArrayList<Worker> workerList){
        this.workerList = workerList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.workers_recycler_item,parent,false);
        return new WorkerHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerHolder holder, int position) {
        holder.workerNameTextview.setText(workerList.get(position).isim);
        holder.itemView.setOnClickListener(null);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WorkerDetailsActivity.class);
                intent.putExtra("worker",workerList.get(position));
                ((Activity) context).startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    public class WorkerHolder extends RecyclerView.ViewHolder{

        private TextView workerNameTextview;

        public WorkerHolder(@NonNull View itemView) {
            super(itemView);

            workerNameTextview = itemView.findViewById(R.id.worker_name_textview);
        }
    }
}
