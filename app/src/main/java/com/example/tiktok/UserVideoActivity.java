package com.example.tiktok;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tiktok.Adapter.UserVideoAdapter;
import com.example.tiktok.Data.PostData;
import com.example.tiktok.Data.PostDataListResponse;
import com.example.tiktok.Data.PostDataUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UserVideoActivity extends AppCompatActivity implements UserVideoAdapter.IOnItemClickListener {

    private List<PostData> msg = new ArrayList<>();
    private RecyclerView userView;
    private UserVideoAdapter userAdapter = new UserVideoAdapter();
    private SwipeRefreshLayout swip_refresh_layout;

    public UserVideoActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_video);
        swip_refresh_layout=findViewById(R.id.user_video_swipe);
        userView = findViewById(R.id.uservideo_recycle);
        userView.setLayoutManager(new LinearLayoutManager(this));
        userView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        userAdapter = new UserVideoAdapter();
        userAdapter.setOnItemClickListener(this);
        userView.setAdapter(userAdapter);
        getData();

        swip_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                        swip_refresh_layout.setRefreshing(false);
                    }
                },2000);
            }
        });

        //userAdapter.setData(msg);
    }

    @Override
    public void onItemCLick0(int position, PostData data) {
        //Toast.makeText(this, "click on 0 : " + position, Toast.LENGTH_SHORT).show();
        PostDataUtil.data = data;
        Intent intent = new Intent(this, VideoPlayActivity.class);
        startActivity(intent);
    }
    @Override
    public void onItemCLick1(int position, PostData data) {
        //Toast.makeText(this, "click on 1 : " + position, Toast.LENGTH_SHORT).show();
        PostDataUtil.data = data;
        Intent intent = new Intent(this, VideoPlayActivity.class);
        startActivity(intent);
    }
    @Override
    public void onItemCLick2(int position, PostData data) {
        //Toast.makeText(this, "click on 2 : " + position, Toast.LENGTH_SHORT).show();
        PostDataUtil.data = data;
        Intent intent = new Intent(this, VideoPlayActivity.class);
        startActivity(intent);
    }

    private void getData() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                msg.clear();
                msg = baseGetDataFromRemote();
                if (msg != null && !msg.isEmpty()) {
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            userAdapter.setData(msg);
                        }
                    });
                }
            }
        }).start();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<PostData> baseGetDataFromRemote() {
        String urlStr = String.format(Constants.BASE_URL + "video");
        List<PostData> result = null;
        try {
            URL url;
            if (PostDataUtil.user.getStudentId() != null) url = new URL(urlStr + "?" + "student_id=" + PostDataUtil.user.getStudentId());
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UserVideoActivity.this, "网络异常" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return result;
    }
}