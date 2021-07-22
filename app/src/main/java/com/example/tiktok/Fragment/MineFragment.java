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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktok.Adapter.UserVideoAdapter;
import com.example.tiktok.Constants;
import com.example.tiktok.Data.PostData;
import com.example.tiktok.Data.PostDataListResponse;
import com.example.tiktok.Data.PostDataUtil;
import com.example.tiktok.MiddleActivity;
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
import java.util.List;
import java.util.Objects;

public class MineFragment extends Fragment implements UserVideoAdapter.IOnItemClickListener{

    private List<PostData> msg = new ArrayList<>();
    private RecyclerView videoView;
    private UserVideoAdapter videoAdapter;
    private TextView tvname;
    private TextView tvid;
    private ImageView cover0;
    private Button lunch;
    private Switch sw1;
    private SwipeRefreshLayout swip_refresh_layout;

    public MineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        cover0 = myView.findViewById(R.id.cover0);
        sw1 = myView.findViewById(R.id.auto_switch);
        videoView.setLayoutManager(new LinearLayoutManager(getContext()));
        videoView.setHasFixedSize(true);
        videoView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        videoAdapter = new UserVideoAdapter();
        videoAdapter.setOnItemClickListener(this);
        Constants.auto_upload = sw1.isChecked();
        getData(Constants.student_id);
        //videoAdapter.setData(msg);
        videoView.setAdapter(videoAdapter);
        tvname.setText(Constants.name);
        tvid.setText(Constants.student_id);

        swip_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            try{
                                getData(Constants.student_id);
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

        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecordActivity.class);
                startActivity(intent);
            }
        });

        return myView;
    }


    @Override
    public void onItemCLick0(int position, PostData data) {
        Toast.makeText(getActivity(), "click on 0 : " + position, Toast.LENGTH_SHORT).show();
        PostDataUtil.data = data;
        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
        startActivity(intent);
    }
    @Override
    public void onItemCLick1(int position, PostData data) {
        Toast.makeText(getActivity(), "click on 1 : " + position, Toast.LENGTH_SHORT).show();
        PostDataUtil.data = data;
        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
        startActivity(intent);
    }
    @Override
    public void onItemCLick2(int position, PostData data) {
        Toast.makeText(getActivity(), "click on 2 : " + position, Toast.LENGTH_SHORT).show();
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
                            Glide.with(MineFragment.this).load(msg.get(0).getImageUrl()).into(cover0);
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