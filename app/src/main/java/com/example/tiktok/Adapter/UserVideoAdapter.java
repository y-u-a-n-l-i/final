package com.example.tiktok.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.tiktok.Data.PostData;
import com.example.tiktok.R;

import java.util.ArrayList;
import java.util.List;

public class UserVideoAdapter extends RecyclerView.Adapter<UserVideoAdapter.UserVideoViewHolder> {
    private List<PostData> mDataset ;
    private UserVideoAdapter.IOnItemClickListener mItemClickListener;

    public UserVideoAdapter() {
        mDataset = new ArrayList<>();
    }

    public void setData(List<PostData> list){
        mDataset = list;
        notifyDataSetChanged();
    }

    public void clearData(){
        mDataset.clear();
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
        PostData data0, data1,data2;
        System.out.println(position);
        if(position * 3 < mDataset.size()){
            data0 = mDataset.get (position * 3);
        }else{
            data0 = null;
        }
        if(position * 3 + 1 < mDataset.size()){
            data1 = mDataset.get (position * 3 + 1);
        }else{
            data1 = null;
        }
        if(position*3+2<mDataset.size()){
            data2 = mDataset.get (position * 3 + 2);
        }else{
            data2 = null;
        }
        holder.onBind(position, data0, data1, data2);
        holder.setOnClickListener0(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick0(position, data0);
                }
            }
        });
        holder.setOnClickListener1(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick1(position, data1);
                }
            }
        });
        holder.setOnClickListener2(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick2(position, data2);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mDataset.size()+2)/3;
    }

    public interface IOnItemClickListener {

        void onItemCLick0(int position, PostData data);

        void onItemCLick1(int position, PostData data);

        void onItemCLick2(int position, PostData data);
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
        private ImageView c0;
        private ImageView c1;
        private ImageView c2;
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

        public void onBind(int position, PostData data0, PostData data1, PostData data2){
            if(data0 != null) {
                tv0.setText(data0.getUpdatedAt().toString());
                DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(500).setCrossFadeEnabled(true).build();
                c0.setVisibility(View.VISIBLE);
                Glide.with(contentView).load(data0.getImageUrl()).transition(DrawableTransitionOptions.with(drawableCrossFadeFactory)).into(c0);
            }else{
                tv0.setText("");
                c0.setVisibility(View.INVISIBLE);
                b0.setEnabled(false);
            }
            if(data1 != null) {
                tv1.setText(data1.getUpdatedAt().toString());
                DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(500).setCrossFadeEnabled(true).build();
                c1.setVisibility(View.VISIBLE);
                Glide.with(contentView).load(data1.getImageUrl()).transition(DrawableTransitionOptions.with(drawableCrossFadeFactory)).into(c1);
            }else{
                tv1.setText("");
                c1.setVisibility(View.INVISIBLE);
                b1.setEnabled(false);
            }
            if(data2 != null) {
                tv2.setText(data2.getUpdatedAt().toString());
                DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(500).setCrossFadeEnabled(true).build();
                c2.setVisibility(View.VISIBLE);
                Glide.with(contentView).load(data2.getImageUrl()).transition(DrawableTransitionOptions.with(drawableCrossFadeFactory)).into(c2);
            }else{
                System.out.println("data2 == null "+position);
                c2.setVisibility(View.INVISIBLE);
                tv2.setText("");
                b2.setEnabled(false);
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
