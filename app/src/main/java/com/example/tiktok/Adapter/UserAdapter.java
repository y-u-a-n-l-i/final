package com.example.tiktok.Adapter;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktok.Constants;
import com.example.tiktok.Data.Data;
import com.example.tiktok.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<Data> mDataset ;
    private UserAdapter.IOnItemClickListener mItemClickListener;

    public UserAdapter() {
        mDataset = new ArrayList<>();
    }

    public void setData(List<Data> list){
        Map<String, Data> users = new HashMap<String, Data>();
        users.put(list.get(0).getStudentId(),list.get(0));
        for(int i=1;i<list.size();i++){
            if(!users.containsKey(list.get(i).getStudentId())|| !list.get(i).getStudentId().equals(Constants.student_id)){
                users.put(list.get(i).getStudentId(),list.get(i));
            }
        }
        Set<Map.Entry<String, Data>> user_set = users.entrySet();
        for(Map.Entry<String, Data> user:user_set){
            mDataset.add(user.getValue());
        }
        notifyDataSetChanged();
    }

    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent,false);
        return new UserAdapter.UserViewHolder(root);
    }

    @Override
    public void onBindViewHolder(UserAdapter.UserViewHolder holder, final int position) {
        holder.onBind(position, mDataset.get (position));
        holder.setOnClickListenerLeft(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick(position, mDataset.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface IOnItemClickListener {

        void onItemCLick(int position, Data data);
    }

    public UserAdapter(List<Data> myDataset) {
        mDataset.addAll(myDataset);
    }

    public void setOnItemClickListener(UserAdapter.IOnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private TextView tvId;
        private View contentView;

        public UserViewHolder(View v){
            super(v);
            contentView = v;
            tvName = v.findViewById(R.id.user_name);
            tvId = v.findViewById(R.id.user_student_id);
        }

        public void onBind(int position, Data data){
            tvName.setText(data.getFrom());
            tvId.setText(data.getStudentId());
        }

        public void setOnClickListenerLeft(View.OnClickListener listener) {
            if (listener != null) {
                contentView.setOnClickListener(listener);
            }
        }
    }
}
