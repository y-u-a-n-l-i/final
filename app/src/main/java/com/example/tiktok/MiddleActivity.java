package com.example.tiktok;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tiktok.Adapter.VideoAdapter;
import com.example.tiktok.Fragment.MineFragment;
import com.example.tiktok.Fragment.UserFragment;
import com.example.tiktok.Fragment.VideoFragment;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.tabs.TabLayout;

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
        Button lunch0 = findViewById(R.id.lunch0);

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
        tab.setupWithViewPager(pager);

        lunch0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MiddleActivity.this, CustomRecordActivity.class);
                startActivity(intent);
            }
        });
    }
}