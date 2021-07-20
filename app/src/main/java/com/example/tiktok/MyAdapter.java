package com.example.tiktok;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.example.tiktok.Data.Data;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Data> mDataset ;
    private IOnItemClickListener mItemClickListener;

    public MyAdapter() {
        mDataset = new ArrayList<>();
    }

    public void setData(List<Data> list){
        mDataset = list;
        notifyDataSetChanged();
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cover_item, parent,false);
        return new MyViewHolder(root);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.onBind(position, mDataset.get(position*2),mDataset.get(position*2+1));
        holder.setOnClickListenerLeft(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLickLeft(position, mDataset.get(position*2));
                }
            }
        });
        holder.setOnClickListenerRight(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLickRight(position, mDataset.get(position*2+1));
                }
            }

        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface IOnItemClickListener {

        void onItemCLickLeft(int position, Data data);

        void onItemCLickRight(int position, Data data);
    }

    public MyAdapter(List<Data> myDataset) {
        mDataset.addAll(myDataset);
    }

    public void setOnItemClickListener(IOnItemClickListener listener) {
        mItemClickListener = listener;
    }

//    public void addData(int position, Data data) {
//        mDataset.add(position, data);
//        notifyItemInserted(position);
//        if (position != mDataset.size()) {
//            //刷新改变位置item下方的所有Item的位置,避免索引错乱
//            notifyItemRangeChanged(position, mDataset.size() - position);
//        }
//    }
//
//    public void removeData(int position) {
//        if (null != mDataset && mDataset.size() > position) {
//            mDataset.remove(position);
//            notifyItemRemoved(position);
//            if (position != mDataset.size()) {
//                //刷新改变位置item下方的所有Item的位置,避免索引错乱
//                notifyItemRangeChanged(position, mDataset.size() - position);
//            }
//        }
//    }
//
//    public void changeData(int position){
//
//    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvLeft;
        private TextView tvRight;
        private SimpleDraweeView cLeft;
        private SimpleDraweeView cRight;
        private Button bLeft;
        private Button bRight;
        private View contentView;

        public MyViewHolder(View v){
            super(v);
            contentView = v;
            tvLeft = v.findViewById(R.id.textViewLeft);
            tvRight = v.findViewById(R.id.textViewRight);
            cLeft = v.findViewById(R.id.coverLeft);
            cRight = v.findViewById(R.id.coverRight);
            bLeft = v.findViewById(R.id.buttonLeft);
            bRight = v.findViewById(R.id.buttonRight);
        }

        public void onBind(int position, Data dataLeft, Data dataRight){
//            tvLeft.setText(dataLeft.getUser_name()+"  "+dataLeft.getUpdatedAt());
            tvLeft.setText(dataLeft.getFrom()+"  "+dataLeft.getUpdatedAt());
            cLeft.setImageURI(dataLeft.getImageUrl());
            if(dataRight!=null) {
//                tvRight.setText(dataRight.getUser_name()+"  "+dataRight.getUpdatedAt());
                tvRight.setText(dataRight.getFrom()+"  "+dataRight.getUpdatedAt());
                cRight.setImageURI(dataRight.getImageUrl());
            }else{
                tvRight.setText("");
                tvRight.setAlpha(0.0f);
                bRight.setEnabled(false);
                cRight.setAlpha(0.0f);
            }
        }
        public void setOnClickListenerLeft(View.OnClickListener listener) {
            if (listener != null) {
                Log.d("myadapter","click on left");
                bLeft.setOnClickListener(listener);
            }
        }

        public void setOnClickListenerRight(View.OnClickListener listener) {
            if (listener != null) {
                Log.d("myadapter","click on right");
                bRight.setOnClickListener(listener);
            }
        }
    }
}
