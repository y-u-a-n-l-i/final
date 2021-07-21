package com.example.tiktok;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.tiktok.Data.PostDataUtil;

public class VideoPlayActivity extends AppCompatActivity {
    String mockUrl = PostDataUtil.data.getVideo_url();
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        videoView = findViewById(R.id.videoPlay);
        videoView.setVideoURI(Uri.parse(mockUrl));
        videoView.setMediaController(new MediaController(this));

        videoView.start();
    }
}