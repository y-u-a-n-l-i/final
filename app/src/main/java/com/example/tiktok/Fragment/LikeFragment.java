package com.example.tiktok.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tiktok.Adapter.UserVideoAdapter;
import com.example.tiktok.Constants;
import com.example.tiktok.CustomRecordActivity;
import com.example.tiktok.Data.PostData;
import com.example.tiktok.Data.PostDataUtil;
import com.example.tiktok.Database.VideoContract;
import com.example.tiktok.Database.VideoDbHelper;
import com.example.tiktok.R;
import com.example.tiktok.VideoPlayActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class LikeFragment extends Fragment implements UserVideoAdapter.IOnItemClickListener{
    private List<PostData> msg = new ArrayList<>();
    private RecyclerView videoView;
    private UserVideoAdapter videoAdapter = new UserVideoAdapter();
    private SwipeRefreshLayout swip_refresh_layout;

    //database
    private VideoDbHelper dbHelper;
    private SQLiteDatabase Database;

    public LikeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new VideoDbHelper(LikeFragment.this.getContext());
        Database = dbHelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_like, container, false);
        swip_refresh_layout=myView.findViewById(R.id.like_swipe);
        videoView = myView.findViewById(R.id.like_recycle);
        videoView.setLayoutManager(new LinearLayoutManager(getContext()));
        videoView.setHasFixedSize(true);
        videoView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        videoAdapter.setOnItemClickListener(this);

        getDataFromLike();
        videoView.setAdapter(videoAdapter);
        swip_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            try{
                                getDataFromLike();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        swip_refresh_layout.setRefreshing(false);
                    }
                },3000);
            }
        });
        return myView;
    }

    @Override
    public void onItemCLick0(int position, PostData data) {
        //Toast.makeText(getActivity(), "click on 0 : " + position, Toast.LENGTH_SHORT).show();
        PostDataUtil.data = data;
        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
        startActivity(intent);
    }
    @Override
    public void onItemCLick1(int position, PostData data) {
        //Toast.makeText(getActivity(), "click on 1 : " + position, Toast.LENGTH_SHORT).show();
        PostDataUtil.data = data;
        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
        startActivity(intent);
    }
    @Override
    public void onItemCLick2(int position, PostData data) {
        //Toast.makeText(getActivity(), "click on 2 : " + position, Toast.LENGTH_SHORT).show();
        PostDataUtil.data = data;
        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
        startActivity(intent);
    }

    protected void getDataFromLike(){
        if (Database == null) {
            return;
        }
        List<PostData> result = new LinkedList<>();
        Cursor cursor = null;
        try{
            cursor = Database.query(VideoContract.VideoInfo.Save_Table,
                    null,
                    null,
                    null,
                    null,
                    null,
                    VideoContract.VideoInfo._ID + " DESC");
            while(cursor.moveToNext()){
                String Id = cursor.getString(cursor.getColumnIndex(VideoContract.VideoInfo.Post_ID));
                String studentId = cursor.getString(cursor.getColumnIndex(VideoContract.VideoInfo.Student_Attribute));
                String user_name = cursor.getString(cursor.getColumnIndex(VideoContract.VideoInfo.User_Attribute));
                String video_url = cursor.getString(cursor.getColumnIndex(VideoContract.VideoInfo.Video_Attribute));
                String imageUrl = cursor.getString(cursor.getColumnIndex(VideoContract.VideoInfo.Image_Attribute));
                String createdAt = cursor.getString(cursor.getColumnIndex(VideoContract.VideoInfo.Create_Attribute));
                String updatedAt = cursor.getString(cursor.getColumnIndex(VideoContract.VideoInfo.Update_Attribute));
                PostData data = new PostData();
                data.setId(Id);
                data.setStudentId(studentId);
                data.setFrom(user_name);
                data.setVideo_url(video_url);
                data.setImageUrl(imageUrl);
                Date date = new Date();
                data.setCreatedAt(date);
                data.setUpdatedAtt(date);
                result.add(data);
            }
        } finally {
            if(cursor != null) cursor.close();
        }
        videoAdapter.clearData();
        videoAdapter.setData(result);
    }
}
