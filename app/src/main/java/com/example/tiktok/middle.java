package com.example.tiktok;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.tiktok.Data.Data;
import com.example.tiktok.Data.DataListResponse;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class middle extends AppCompatActivity implements MyAdapter.IOnItemClickListener{

    private MyAdapter adapter = new MyAdapter();
    private String TAG = "MAIN_INTERFACE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle);
        adapter.setOnItemClickListener(this);
        RecyclerView rv1 = findViewById(R.id.recycler);
        rv1.setLayoutManager(new LinearLayoutManager(this));
        rv1.setAdapter(adapter);
        getData(null);
    }

    private void showData(List<Data> msgList) {
        Log.d(TAG, "repo list add " + msgList.size());
        adapter.setData(msgList);
    }

    private void getData(String studentId){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                List<Data> msgs = baseGetDataFromRemote(studentId);
                //  Log.d(TAG,studentId);
                if (msgs != null && !msgs.isEmpty()) {
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            showData(msgs);
                        }
                    });
                }
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<Data> baseGetDataFromRemote(String studentId) {
        String urlStr =
                String.format("https://api-android-camp.bytedance.com/zju/invoke/messages/");
        List<Data> result = null;
        try {
            URL url;
            if(studentId!=null) url = new URL(urlStr+"?"+"student_id="+studentId);
            else url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);

            conn.setRequestMethod("GET");

            //conn.setRequestProperty("student_id",studentId);
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

            } else {
                // 错误处理
            }
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(middle.this, "网络异常" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return result;
    }

    @Override
    public void onItemCLickLeft(int position, Data data) {
        Toast.makeText(middle.this,"click on the left on: "+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemCLickRight(int position, Data data) {
        Toast.makeText(middle.this,"click on the right on: "+position,Toast.LENGTH_SHORT).show();
    }
}