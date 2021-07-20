package com.example.tiktok;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.tiktok.Adapter.VideoAdapter;
import com.example.tiktok.Data.Data;
import com.example.tiktok.Data.DataListResponse;
import com.example.tiktok.Fragment.MineFragment;
import com.example.tiktok.Fragment.UserFragment;
import com.example.tiktok.Fragment.VideoFragment;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.tabs.TabLayout;
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

public class MiddleActivity extends AppCompatActivity {

    private List<Fragment> fragList;
    private List<String> titleList;
    private static final int PAGE_COUNT = 3;
    private VideoAdapter adapter = new VideoAdapter();
    private String TAG = "MAIN_INTERFACE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle);
        ViewPager pager = findViewById(R.id.view_pager1);
        TabLayout tab = findViewById(R.id.tab_layout);

        titleList = new ArrayList<>();
        titleList.add("Video");
        titleList.add("Users");
        titleList.add("Mine");
        fragList = new ArrayList<>();
        fragList.add(new VideoFragment());
        fragList.add(new UserFragment());
        fragList.add(new MineFragment());

        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragList.get(i);
            }

            @Override
            public int getCount() {
                return PAGE_COUNT;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }
        });
        // TODO: ex3-2, 添加 TabLayout 支持 Tab
        tab.setupWithViewPager(pager);
    }
}