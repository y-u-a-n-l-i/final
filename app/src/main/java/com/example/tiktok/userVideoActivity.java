package com.example.tiktok;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tiktok.Adapter.UserAdapter;
import com.example.tiktok.Adapter.UserVideoAdapter;
import com.example.tiktok.Data.Data;
import com.example.tiktok.Data.DataListResponse;
import com.example.tiktok.Data.DataUtil;
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
import java.util.Objects;

public class userVideoActivity extends AppCompatActivity implements UserVideoAdapter.IOnItemClickListener {

    private List<Data> msg = new ArrayList<>();
    private RecyclerView userView;
    private UserVideoAdapter userAdapter = new UserVideoAdapter();

    public userVideoActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_video);
        userView = findViewById(R.id.uservideo_recycle);
        userView.setLayoutManager(new LinearLayoutManager(this));
        userView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        userAdapter = new UserVideoAdapter();
        userAdapter.setOnItemClickListener(this);
        userView.setAdapter(userAdapter);
        getData();
        //userAdapter.setData(msg);
    }

    @Override
    public void onItemCLick0(int position, Data data) {
        Toast.makeText(this, "click on 0 : " + position, Toast.LENGTH_SHORT).show();
        DataUtil.data = data;
    }
    @Override
    public void onItemCLick1(int position, Data data) {
        Toast.makeText(this, "click on 1 : " + position, Toast.LENGTH_SHORT).show();
        DataUtil.data = data;
    }
    @Override
    public void onItemCLick2(int position, Data data) {
        Toast.makeText(this, "click on 2 : " + position, Toast.LENGTH_SHORT).show();
        DataUtil.data = data;
    }

    private void getData() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
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
    public List<Data> baseGetDataFromRemote() {
        String urlStr = String.format("https://api-android-camp.bytedance.com/zju/invoke/messages/");
        List<Data> result = null;
        try {
            URL url;
            if (DataUtil.user.getStudentId() != null) url = new URL(urlStr + "?" + "student_id=" + DataUtil.user.getStudentId());
            else url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("token", Constants.token);

            if (conn.getResponseCode() == 200) {

                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                DataListResponse msg;
                msg = new Gson().fromJson(reader, new TypeToken<DataListResponse>() {
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
                    Toast.makeText(userVideoActivity.this, "网络异常" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return result;
    }
}