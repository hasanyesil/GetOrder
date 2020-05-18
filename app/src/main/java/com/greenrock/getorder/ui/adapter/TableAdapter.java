package com.greenrock.getorder.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenrock.getorder.R;
import com.greenrock.getorder.util.interfaces.OnRecylerItemUpdateListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private String TAG = "TableAdapterTest";
    private Context context;

    private OnRecylerItemUpdateListener itemUpdateListener;

    private int lineCount;
    private int lastLineTableCount;
    private List<Integer> activeTables;
    private Paint paint;
    private ArrayList<Bitmap> emptyTableBg;
    private ArrayList<Bitmap> filledTableBg;
    private ArrayList<Canvas> emptyTableCanvas;
    private ArrayList<Canvas> filledTableCanvas;

    public TableAdapter(OnRecylerItemUpdateListener updateListener, Context context){
        itemUpdateListener = updateListener;
        this.context = context;

        emptyTableBg = new ArrayList<Bitmap>();
        emptyTableCanvas = new ArrayList<>();

        filledTableBg = new ArrayList<Bitmap>();
        filledTableCanvas = new ArrayList<>();

        paint = new Paint();
        paint.setColor(context.getResources().getColor(android.R.color.white));
        paint.setTextSize(context.getResources().getDimension(R.dimen.text_size));
    }

    public void setTableStates(int [] tableStates){
        activeTables = new ArrayList<>();
        for (int i = 0; i < tableStates.length; i++){
            if (tableStates[i] == 1){
                activeTables.add(i+1);
                Log.d(TAG, "setTableStates: " + activeTables);
            }
        }
        lastLineTableCount = tableStates.length % 3;
        if (lastLineTableCount != 0)
            lineCount = (tableStates.length / 3) + 1;
        else
            lineCount = tableStates.length;
        this.notifyDataSetChanged();
        updateHolders(activeTables);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        holder.setIsRecyclable(false);
        super.onViewAttachedToWindow(holder);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.table_recycler_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: line count -> " + lineCount + " position -> " + position + " last line table count -> " + lastLineTableCount);
        if (position+1 == lineCount){
            holder.hideLastViews(lastLineTableCount);
        }

        //Setting number of tables
        for (int i = 0; i < 3; i++) {
            emptyTableBg.add(i,BitmapFactory.decodeResource(context.getResources(),R.drawable.empty_table_grey).copy(Bitmap.Config.ARGB_8888, true));
            filledTableBg.add(i,BitmapFactory.decodeResource(context.getResources(),R.drawable.filled_table_green).copy(Bitmap.Config.ARGB_8888,true));
            emptyTableCanvas.add(i,new Canvas(emptyTableBg.get(i)));
            emptyTableCanvas.get(i).drawText(String.valueOf(position * 3 + (i+1)),emptyTableBg.get(i).getWidth() / 2f, emptyTableBg.get(i).getHeight() / 2f,paint);
            filledTableCanvas.add(i,new Canvas(filledTableBg.get(i)));
            filledTableCanvas.get(i).drawText(String.valueOf(position * 3 + (i+1)),emptyTableBg.get(i).getWidth() / 2f, emptyTableBg.get(i).getHeight() / 2f,paint);
        }


        holder.lineFirstImageView.setImageBitmap(emptyTableBg.get(0));
        holder.lineSecondImageView.setImageBitmap(emptyTableBg.get(1));
        holder.lineThirdImageView.setImageBitmap(emptyTableBg.get(2));
        //Setting active tables color.
        for (int i : activeTables){
            if ((i-1) / 3 == position){
                int order = i % 3;
                switch (order){
                    case 0:
                        holder.lineThirdImageView.setImageBitmap(filledTableBg.get(2));
                        break;
                    case 1:
                        holder.lineFirstImageView.setImageBitmap(filledTableBg.get(0));
                        break;
                    case 2:
                        holder.lineSecondImageView.setImageBitmap(filledTableBg.get(1));
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return lineCount;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView lineFirstImageView;
        public ImageView lineSecondImageView;
        public ImageView lineThirdImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lineFirstImageView = (ImageView) itemView.findViewById(R.id.lineFirstTable);
            lineSecondImageView = (ImageView) itemView.findViewById(R.id.lineSecondTable);
            lineThirdImageView = (ImageView) itemView.findViewById(R.id.lineThirdTable);
        }

        public void hideLastViews(int lastLineTableCount){
            switch (lastLineTableCount){
                case 1:
                    lineSecondImageView.setVisibility(View.GONE);
                    lineThirdImageView.setVisibility(View.GONE);
                    break;
                case 2:
                    lineThirdImageView.setVisibility(View.GONE);
                    break;
            }

        }

    }

    public void updateHolders(List<Integer> activeTables){
        int updatedLineCount;
        int updatedTableOrder;
        for (int i : activeTables){
            updatedLineCount = i / 3;
            updatedTableOrder = i % 3;
            itemUpdateListener.onRecylerViewItemUpdate(updatedLineCount,updatedTableOrder);
        }
        int i = 0;
    }

}
