package com.example.tiktok.Fragment;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tiktok.Adapter.VideoAdapter;
import com.example.tiktok.Constants;
import com.example.tiktok.Data.PostData;
import com.example.tiktok.Data.PostDataListResponse;
import com.example.tiktok.Data.PostDataUtil;
import com.example.tiktok.R;
import com.example.tiktok.VideoPlayActivity;
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

    private List<PostData> msg = new ArrayList<>();
    private RecyclerView videoView;
    private VideoAdapter videoAdapter = new VideoAdapter();
    private SwipeRefreshLayout swip_refresh_layout;

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
        swip_refresh_layout=myView.findViewById(R.id.video_swipe);
        videoView = myView.findViewById(R.id.video_recycle);
        videoView.setLayoutManager(new LinearLayoutManager(getContext()));
        videoView.setHasFixedSize(true);
        videoView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        videoAdapter.setOnItemClickListener(this);

        getData(null);
        //videoAdapter.setData(msg);
        videoView.setAdapter(videoAdapter);

        swip_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            getData(null);
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
    public void onItemCLickLeft(int position, PostData data) {
        Toast.makeText(this.getActivity(),"click on the left on: " + position, Toast.LENGTH_SHORT).show();
        PostDataUtil.data = data;
        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemCLickRight(int position, PostData data) {
        Toast.makeText(this.getActivity(),"click on the right on: " + position, Toast.LENGTH_SHORT).show();
        PostDataUtil.data = data;
        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
        startActivity(intent);
    }

    private void getData(String studentId){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                msg.clear();
                msg = baseGetDataFromRemote(studentId);
               if (msg != null && !msg.isEmpty()) {
                   new Handler(getActivity().getMainLooper()).post(new Runnable() {
                       @Override
                        public void run() {
                           videoAdapter.clearData();
                           videoAdapter.setData(msg);
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
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "error network" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return result;
    }
}