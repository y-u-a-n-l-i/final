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

public class UserVideoAdapter extends RecyclerView.Adapter<UserVideoAdapter.UserVideoViewHolder> {
    private List<Data> mDataset ;
    private UserVideoAdapter.IOnItemClickListener mItemClickListener;

    public UserVideoAdapter() {
        mDataset = new ArrayList<>();
    }

    public void setData(List<Data> list){
        mDataset = list;
        notifyDataSetChanged();
    }

    @Override
    public UserVideoAdapter.UserVideoViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.uservideo_item, parent,false);
        return new UserVideoAdapter.UserVideoViewHolder(root);
    }

    @Override
    public void onBindViewHolder(UserVideoAdapter.UserVideoViewHolder holder, final int position) {
        Data data0, data1,data2;
        if(position*3<mDataset.size()){
            data0 = mDataset.get (position * 3);
        }else{
            data0 = null;
        }
        if(position*3+1<mDataset.size()){
            data1 = mDataset.get (position * 3+1);
        }else{
            data1 = null;
        }
        if(position*3+2<mDataset.size()){
            data2 = mDataset.get (position * 3+2);
        }else{
            data2 = null;
        }
        holder.onBind(position, data0, data1, data2);
        holder.setOnClickListener0(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick0(position, mDataset.get(position*3));
                }
            }
        });
        holder.setOnClickListener1(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick1(position, mDataset.get(position*3+1));
                }
            }
        });
        holder.setOnClickListener2(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick2(position, mDataset.get(position*3+2));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mDataset.size()+2)/3;
    }

    public interface IOnItemClickListener {

        void onItemCLick0(int position, Data data);

        void onItemCLick1(int position, Data data);

        void onItemCLick2(int position, Data data);
    }

//    public VideoAdapter(List<Data> myDataset) {
//        mDataset.addAll(myDataset);
//    }

    public void setOnItemClickListener(UserVideoAdapter.IOnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public class UserVideoViewHolder extends RecyclerView.ViewHolder{
        private TextView tv0;
        private TextView tv1;
        private TextView tv2;
        private SimpleDraweeView c0;
        private SimpleDraweeView c1;
        private SimpleDraweeView c2;
        private Button b0;
        private Button b1;
        private Button b2;
        private View contentView;

        public UserVideoViewHolder(View v){
            super(v);
            contentView = v;
            tv0 = v.findViewById(R.id.textView0);
            tv1 = v.findViewById(R.id.textView1);
            tv2 = v.findViewById(R.id.textView2);
            c0 = v.findViewById(R.id.cover0);
            c1 = v.findViewById(R.id.cover1);
            c2 = v.findViewById(R.id.cover2);
            b0 = v.findViewById(R.id.button0);
            b1 = v.findViewById(R.id.button1);
            b2 = v.findViewById(R.id.button2);
        }

        public void onBind(int position, Data data0, Data data1, Data data2){
            if(data0!= null) {
                tv0.setText(data0.getUpdatedAt().toString());
                c0.setImageURI(data0.getImageUrl());
            }else{
                tv0.setText("");
                tv0.setAlpha(0.0f);
                b0.setEnabled(false);
                c0.setAlpha(0.0f);
            }
            if(data1!= null) {
                tv1.setText(data1.getUpdatedAt().toString());
                c1.setImageURI(data1.getImageUrl());
            }else{
                tv1.setText("");
                tv1.setAlpha(0.0f);
                b1.setEnabled(false);
                c1.setAlpha(0.0f);
            }
            if(data2!= null) {
                tv2.setText(data2.getUpdatedAt().toString());
                c2.setImageURI(data2.getImageUrl());
            }else{
                tv2.setText("");
                tv2.setAlpha(0.0f);
                b2.setEnabled(false);
                c2.setAlpha(0.0f);
            }
        }

        public void setOnClickListener0(View.OnClickListener listener) {
            if (listener != null) {
                Log.d("myAdapter","click on left");
                b0.setOnClickListener(listener);
            }
        }

        public void setOnClickListener1(View.OnClickListener listener) {
            if (listener != null) {
                Log.d("myAdapter","click on middle");
                b1.setOnClickListener(listener);
            }
        }

        public void setOnClickListener2(View.OnClickListener listener) {
            if (listener != null) {
                Log.d("myAdapter","click on middle");
                b2.setOnClickListener(listener);
            }
        }
    }
}
