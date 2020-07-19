package com.greenrock.getorder.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenrock.getorder.R;
import com.greenrock.getorder.ui.activity.CheckActivity;
import com.greenrock.getorder.ui.activity.OrderActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private String TAG = "TableAdapterTest";
    private Context context;


    private int lineCount;
    private int lastLineTableCount;
    private List<Integer> activeTables;
    private Paint paint;
    private ArrayList<Bitmap> emptyTableBg;
    private ArrayList<Bitmap> filledTableBg;
    private ArrayList<Canvas> emptyTableCanvas;
    private ArrayList<Canvas> filledTableCanvas;
    private HashMap<String,Float> productList;
    private boolean isCashier;

    public TableAdapter(Context context, boolean isCashier){
        this.context = context;
        this.isCashier = isCashier;
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
            lineCount = tableStates.length / 3;
        this.notifyDataSetChanged();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        //holder.setIsRecyclable(false);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: line count -> " + lineCount + " position -> " + position + " last line table count -> " + lastLineTableCount);

        holder.showViews();

        if (position+1 == lineCount){
            holder.hideLastViews(lastLineTableCount);
        }

        //Setting number of tables
        setTablesBackground(holder,position);

        //Setting active tables color.
        setActiveTablesBackground(holder,position);

        //Listeners

        View.OnClickListener tableClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (isCashier){
                    intent = new Intent(context, CheckActivity.class);
                    intent.putExtra("product_list",productList);
                }else {
                    intent = new Intent(context, OrderActivity.class);
                    intent.putExtra("product_list", productList);
                }
                switch (v.getId()){
                    case R.id.lineFirstTable:
                        intent.putExtra("table_name","masa " + (position * 3 + 1));
                        break;
                    case R.id.lineSecondTable:
                        intent.putExtra("table_name","masa " + (position* 3 + 2));
                        break;
                    case R.id.lineThirdTable:
                        intent.putExtra("table_name","masa " + (position* 3 + 3));
                        break;
                }
                context.startActivity(intent);
            }
        };

        holder.resetListeners();
        holder.addListeners(tableClickListener);
    }

    @Override
    public int getItemCount() {
        return lineCount;
    }

    public void setTablesBackground(ViewHolder holder, int position){

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
    }

    public void setActiveTablesBackground(ViewHolder holder, int position){
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

    public void setProductList(HashMap<String,Float> productList){
        this.productList = productList;
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

        public void showViews(){
            lineFirstImageView.setVisibility(View.VISIBLE);
            lineSecondImageView.setVisibility(View.VISIBLE);
            lineThirdImageView.setVisibility(View.VISIBLE);
        }

        public void resetListeners(){
            lineFirstImageView.setOnClickListener(null);
            lineSecondImageView.setOnClickListener(null);
            lineThirdImageView.setOnClickListener(null);
        }

        public void addListeners(View.OnClickListener onClickListener){
            lineFirstImageView.setOnClickListener(onClickListener);
            lineSecondImageView.setOnClickListener(onClickListener);
            lineThirdImageView.setOnClickListener(onClickListener);
        }
    }


}
