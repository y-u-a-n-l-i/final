package com.example.tiktok.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.example.tiktok.Data.Data;
import com.example.tiktok.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    private List<Data> mDataset ;
    private IOnItemClickListener mItemClickListener;

    public VideoAdapter() {
        mDataset = new ArrayList<>();
    }

    public void setData(List<Data> list){
        mDataset = list;
        notifyDataSetChanged();
    }

    @Override
    public VideoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cover_item, parent,false);
        return new MyViewHolder(root);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.onBind(position, mDataset.get (position * 2), mDataset.get(position * 2 + 1));
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

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface IOnItemClickListener {

        void onItemCLickLeft(int position, Data data);

        void onItemCLickRight(int position, Data data);
    }

    public VideoAdapter(List<Data> myDataset) {
        mDataset.addAll(myDataset);
    }

    public void setOnItemClickListener(IOnItemClickListener listener) {
        mItemClickListener = listener;
    }

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
            tvLeft.setText(dataLeft.getFrom() + "  " + dataLeft.getUpdatedAt());
            cLeft.setImageURI(dataLeft.getImageUrl());
            if(dataRight != null) {
                tvRight.setText(dataRight.getFrom() + "  " + dataRight.getUpdatedAt());
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
                Log.d("myAdapter","click on left");
                bLeft.setOnClickListener(listener);
            }
        }

        public void setOnClickListenerRight(View.OnClickListener listener) {
            if (listener != null) {
                Log.d("myAdapter","click on right");
                bRight.setOnClickListener(listener);
            }
        }
    }
}
