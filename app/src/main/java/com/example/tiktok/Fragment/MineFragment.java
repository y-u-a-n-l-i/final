package com.example.tiktok.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.tiktok.Adapter.UserVideoAdapter;
import com.example.tiktok.Constants;
import com.example.tiktok.CustomRecordActivity;
import com.example.tiktok.Data.PostData;
import com.example.tiktok.Data.PostDataListResponse;
import com.example.tiktok.Data.PostDataUtil;
<<<<<<< HEAD
import com.example.tiktok.Database.VideoContract;
import com.example.tiktok.Database.VideoDbHelper;
=======
import com.example.tiktok.MiddleActivity;
>>>>>>> origin/master
import com.example.tiktok.R;
import com.example.tiktok.RecordActivity;
import com.example.tiktok.VideoPlayActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MineFragment extends Fragment implements UserVideoAdapter.IOnItemClickListener{

    private List<PostData> msg = new ArrayList<>();
    private RecyclerView videoView;
    private UserVideoAdapter videoAdapter = new UserVideoAdapter();
    private TextView tvname;
    private TextView tvid;
    private ImageView cover0;
    private Button lunch;
    private Switch sw1;
    private SwipeRefreshLayout swip_refresh_layout;
    private Switch sw1;

    //database
    private VideoDbHelper dbHelper;
    private SQLiteDatabase database;

    public MineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new VideoDbHelper(MineFragment.this.getContext());
        database = dbHelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_mine, container, false);
        swip_refresh_layout=myView.findViewById(R.id.mine_swipe);
        videoView = myView.findViewById(R.id.mine_recycle);
        tvname = myView.findViewById(R.id.mine_name);
        tvid = myView.findViewById(R.id.mine_id);
        lunch = myView.findViewById(R.id.mine_button);
        sw1 = myView.findViewById(R.id.auto_switch);
        cover0 = myView.findViewById(R.id.cover0);
        sw1 = myView.findViewById(R.id.auto_switch);
        videoView.setLayoutManager(new LinearLayoutManager(getContext()));
        videoView.setHasFixedSize(true);
        videoView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        videoAdapter.setOnItemClickListener(this);

        //getDataFromNetWork(Constants.student_id);
        getDataFromWatchHistory();
        videoView.setAdapter(videoAdapter);
        tvname.setText(Constants.name);
        tvid.setText(Constants.student_id);

        sw1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.auto_upload = sw1.isChecked();
            }
        });

        swip_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
<<<<<<< HEAD
                        getDataFromWatchHistory();
                        //getDataFromNetWork(Constants.student_id);
=======
                        try{
                            try{
                                getData(Constants.student_id);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
>>>>>>> origin/master
                        swip_refresh_layout.setRefreshing(false);
                    }
                },3000);
            }
        });

        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomRecordActivity.class);
                startActivity(intent);
            }
        });

        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomRecordActivity.class);
                startActivity(intent);
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

    private void getDataFromNetWork(String studentId){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                msg.clear();
                msg = baseGetDataFromRemote(studentId);
                DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(500).setCrossFadeEnabled(true).build();
                if (msg != null && !msg.isEmpty()) {
                    new Handler(getActivity().getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            videoAdapter.clearData();
                            videoAdapter.setData(msg);
                            Glide.with(MineFragment.this).load(msg.get(0).getImageUrl()).transition(DrawableTransitionOptions.with(drawableCrossFadeFactory)).into(cover0);
                        }
                    });
                }
            }
        }).start();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<PostData> baseGetDataFromRemote(String studentId) {
        String urlStr = String.format(Constants.BASE_URL + "video");
        List<PostData> result = null;
        try {
            URL url;
            if(studentId != null) url = new URL(urlStr + "?" + "student_id=" + studentId);
            else url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("token", Constants.token);

            if (conn.getResponseCode() == 200) {

                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                PostDataListResponse msg;
                msg = new Gson().fromJson(reader, new TypeToken<PostDataListResponse>() {
                }.getType());
                result = msg.feeds;
                reader.close();
                in.close();
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MineFragment.this.getActivity(), "error network" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return result;
    }

    protected void getDataFromWatchHistory(){
        if (database == null) {
            return;
        }
        List<PostData> result = new LinkedList<>();
        Cursor cursor = null;
        try{
            cursor = database.query(VideoContract.VideoInfo.Name_Table,
                    null,
                    null,
                    null,
                    null,
                    null,
                    VideoContract.VideoInfo._ID );
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