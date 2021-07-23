package com.example.tiktok;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.tiktok.Adapter.VideoAdapter;
import com.example.tiktok.Fragment.HistoryFragment;
import com.example.tiktok.Fragment.LikeFragment;
import com.example.tiktok.Fragment.MineFragment;
import com.example.tiktok.Fragment.UserFragment;
import com.example.tiktok.Fragment.VideoFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private List<Fragment> fragList;
    private List<String> titleList;
    private static final int PAGE_COUNT = 2;
    private VideoAdapter adapter = new VideoAdapter();
    private String TAG = "MAIN_INTERFACE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ViewPager pager = findViewById(R.id.view_pager_like);
        TabLayout tab = findViewById(R.id.tab_layout_like);

        titleList = new ArrayList<>();
        titleList.add("History");
        titleList.add("Like");
        fragList = new ArrayList<>();
        fragList.add(new HistoryFragment());
        fragList.add(new LikeFragment());

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
    }
}