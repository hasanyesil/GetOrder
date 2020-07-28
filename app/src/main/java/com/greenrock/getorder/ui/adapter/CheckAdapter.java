package com.greenrock.getorder.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenrock.getorder.R;
import com.greenrock.getorder.model.InactiveCheck;
import com.greenrock.getorder.ui.activity.CheckActivity;
import com.greenrock.getorder.ui.activity.OldCheckDetailActivity;
import com.greenrock.getorder.ui.activity.OldChecksActivity;

import java.util.ArrayList;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.Holder>{

    private ArrayList<InactiveCheck> mChecks;
    private Context context;

    public CheckAdapter(Context context){
        this.context = context;
        mChecks = new ArrayList<>();
    }

    public void setChecks(ArrayList<InactiveCheck> checks){
        mChecks = checks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_recycler_item,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.checkDateTextView.setText(mChecks.get(position).getTarih() + " \t SAAT: " + mChecks.get(position).getSaat());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InactiveCheck check = mChecks.get(position);
                Intent intent = new Intent(context, OldCheckDetailActivity.class);
                intent.putExtra("check",check);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChecks.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        private TextView checkDateTextView;

        public Holder(@NonNull View itemView) {
            super(itemView);

            checkDateTextView = (TextView) itemView.findViewById(R.id.check_date_textview);
        }
    }
}
