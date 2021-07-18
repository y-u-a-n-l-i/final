package com.example.tiktok;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.example.tiktok.Data.Data;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Data> mDataset = new ArrayList<>();
    private IOnItemClickListener mItemClickListener;

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cover_item, parent, false));
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

    public void addData(int position, Data data) {
        mDataset.add(position, data);
        notifyItemInserted(position);
        if (position != mDataset.size()) {
            //刷新改变位置item下方的所有Item的位置,避免索引错乱
            notifyItemRangeChanged(position, mDataset.size() - position);
        }
    }

    public void removeData(int position) {
        if (null != mDataset && mDataset.size() > position) {
            mDataset.remove(position);
            notifyItemRemoved(position);
            if (position != mDataset.size()) {
                //刷新改变位置item下方的所有Item的位置,避免索引错乱
                notifyItemRangeChanged(position, mDataset.size() - position);
            }
        }
    }

    public void changeData(int position){

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvLeft;
        private TextView tvRight;
        private ImageButton ibLeft;
        private ImageButton ibRight;
        private View contentView;

        public MyViewHolder(View v){
            super(v);
            contentView = v;
            tvRight = v.findViewById(R.id.textViewRight);
            tvLeft = v.findViewById(R.id.textViewLeft);
            ibRight = v.findViewById(R.id.imageButtonRight);
            ibLeft = v.findViewById(R.id.imageButtonLeft);
        }

        public void onBind(int position, Data dataLeft, Data dataRight){

            tvLeft.setText(dataLeft.getUser_name()+"  "+dataLeft.getUpdatedAt());
            ibLeft.setImageURI(Uri.parse(dataLeft.getImageUrl()));
            if(dataRight!=null) {
                tvRight.setText(dataRight.getUser_name()+"  "+dataRight.getUpdatedAt());
                ibRight.setImageURI(Uri.parse(dataRight.getImageUrl()));
            }else{
                tvRight.setText("");
                tvRight.setAlpha(0.0f);
                ibRight.setEnabled(false);
                ibRight.setAlpha(0.0f);
            }
        }
        public void setOnClickListenerLeft(View.OnClickListener listener) {
            if (listener != null) {
                Log.d("myadapter","click on left");
                ibLeft.setOnClickListener(listener);
            }
        }

        public void setOnClickListenerRight(View.OnClickListener listener) {
            if (listener != null) {
                Log.d("myadapter","click on right");
                ibRight.setOnClickListener(listener);
            }
        }
    }
}
