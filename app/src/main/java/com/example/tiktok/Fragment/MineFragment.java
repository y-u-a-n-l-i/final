package com.example.tiktok.Fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiktok.Adapter.UserVideoAdapter;
import com.example.tiktok.Adapter.VideoAdapter;
import com.example.tiktok.Constants;
import com.example.tiktok.Data.Data;
import com.example.tiktok.Data.DataListResponse;
import com.example.tiktok.Data.DataUtil;
import com.example.tiktok.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

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

    private List<Data> msg = new ArrayList<>();
    private RecyclerView videoView;
    private UserVideoAdapter videoAdapter;
    private TextView tvname;
    private TextView tvid;
    private SimpleDraweeView cover0;
    private Button lunch;

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

        videoView = myView.findViewById(R.id.mine_recycle);
        tvname = myView.findViewById(R.id.mine_name);
        tvid = myView.findViewById(R.id.mine_id);
        lunch = myView.findViewById(R.id.mine_button);
        cover0 = myView.findViewById(R.id.cover0);
        videoView.setLayoutManager(new LinearLayoutManager(getContext()));
        videoView.setHasFixedSize(true);
        videoView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        videoAdapter = new UserVideoAdapter();
        videoAdapter.setOnItemClickListener(this);

        getData(Constants.student_id);
        //videoAdapter.setData(msg);
        if(!msg.isEmpty()){
            cover0.setImageURI(msg.get(0).getImageUrl());
        }
        videoView.setAdapter(videoAdapter);
        tvname.setText(Constants.name);
        tvid.setText(Constants.student_id);
        return myView;
    }

    @Override
    public void onItemCLick0(int position, Data data) {
        Toast.makeText(getActivity(), "click on 0 : " + position, Toast.LENGTH_SHORT).show();
        DataUtil.data = data;
    }
    @Override
    public void onItemCLick1(int position, Data data) {
        Toast.makeText(getActivity(), "click on 1 : " + position, Toast.LENGTH_SHORT).show();
        DataUtil.data = data;
    }
    @Override
    public void onItemCLick2(int position, Data data) {
        Toast.makeText(getActivity(), "click on 2 : " + position, Toast.LENGTH_SHORT).show();
        DataUtil.data = data;
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