package com.example.tiktok.Fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tiktok.Adapter.VideoAdapter;
import com.example.tiktok.Constants;
import com.example.tiktok.Data.Data;
import com.example.tiktok.Data.DataListResponse;
import com.example.tiktok.MainActivity;
import com.example.tiktok.MiddleActivity;
import com.example.tiktok.R;
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

public class VideoFragment extends Fragment implements VideoAdapter.IOnItemClickListener{

    private List<Data> msg = new ArrayList<>();
    private RecyclerView videoView;
    private VideoAdapter videoAdapter;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_video, container, false);

        videoView = myView.findViewById(R.id.video_recycle);
        videoView.setLayoutManager(new LinearLayoutManager(getContext()));
        videoView.setHasFixedSize(true);
        videoView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        videoAdapter = new VideoAdapter();
        videoAdapter.setOnItemClickListener(this);

        getData(null);
        videoAdapter.setData(msg);
        videoView.setAdapter(videoAdapter);
        return myView;
    }

    @Override
    public void onItemCLickLeft(int position, Data data) {
        Toast.makeText(this.getActivity(),"click on the left on: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemCLickRight(int position, Data data) {
        Toast.makeText(this.getActivity(),"click on the right on: " + position, Toast.LENGTH_SHORT).show();
    }

    private void getData(String studentId){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                msg = baseGetDataFromRemote(studentId);
               if (msg != null && !msg.isEmpty()) {
                   new Handler(getActivity().getMainLooper()).post(new Runnable() {
                       @Override
                        public void run() {
                            videoAdapter.setData(msg);
                        }
                    });
               }
            }
        }).start();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<Data> baseGetDataFromRemote(String studentId) {
        String urlStr = String.format("https://api-android-camp.bytedance.com/zju/invoke/messages/");
        List<Data> result = null;
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "error network" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return result;
    }
}