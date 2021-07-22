package com.example.tiktok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.tiktok.Data.PostData;
import com.example.tiktok.Data.PostDataUtil;
import com.example.tiktok.Database.VideoContract;
import com.example.tiktok.Database.VideoDbHelper;
import com.example.tiktok.View.FlowLikeView;
import com.example.tiktok.View.MyVideoPlayer;
import com.google.android.exoplayer2.SeekParameters;
import com.shuyu.gsyvideoplayer.GSYVideoADManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.yqw.hotheart.HeartFrameLayout;
import com.yqw.hotheart.minterface.OnDoubleClickListener;
import com.yqw.hotheart.minterface.OnSimpleClickListener;

import de.hdodenhof.circleimageview.CircleImageView;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

import com.example.tiktok.Database.VideoContract.VideoInfo;

public class VideoPlayActivity extends AppCompatActivity {
    private static final String TAG = "VideoFragment";
    private long CurrentPosition;
    private MyVideoPlayer VideoPlayer;
    private FlowLikeView LikeView;

    private OrientationUtils orientationUtils;
    private boolean isPlay;
    private boolean isPause;
    private CircleImageView CircleImageView;
    private Button ScreenButton;
    private HeartFrameLayout Heart;
    String mockUrl = "https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";

    //database
    private VideoDbHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        //更新历史记录
        dbHelper = new VideoDbHelper(this);
        database = dbHelper.getWritableDatabase();
        InsertVideoIntoHistory();

        CurrentPosition = 0;
        VideoPlayer = findViewById(R.id.main_player);
        VideoPlayer.getTitleTextView().setVisibility(View.GONE);
        VideoPlayer.getBackButton().setVisibility(View.GONE);

        ScreenButton = findViewById(R.id.screen_button);
        ScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VideoPlayer.getGSYVideoManager().isPlaying()){
                    VideoPlayer.onVideoPause();
                    VideoPlayer.PauseState();
                }
                else if(VideoPlayer.isInPlayingState()){
                    VideoPlayer.PlayState();
                    VideoPlayer.onVideoResume(false);
                }
            }
        });

        Heart = findViewById(R.id.heart);
        Heart.setOnDoubleClickListener(new OnDoubleClickListener() {
            @Override
            public void onDoubleClick(View view) {
                VideoPlayer.PlayState();
                VideoPlayer.onVideoResume(false);
                //Toast.makeText(VideoPlayActivity.this, "双击了", Toast.LENGTH_SHORT).show();
            }
        });

        Heart.setOnSimpleClickListener(new OnSimpleClickListener() {
            @Override
            public void onSimpleClick(View view) {
//                if(VideoPlayer.getGSYVideoManager().isPlaying()){
//                    VideoPlayer.onVideoPause();
//                    VideoPlayer.PauseState();
//                }
//                else if(VideoPlayer.isInPlayingState()){
//                    VideoPlayer.PlayState();
//                    VideoPlayer.onVideoResume(false);
//                }
            }
        });

        orientationUtils = new OrientationUtils(VideoPlayActivity.this, VideoPlayer);
        orientationUtils.setEnable(false);
        VideoPlayer.loadCoverImage(PostDataUtil.data.getImageUrl(), R.drawable.bg_round_rect);
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption
                .setIsTouchWiget(true)
                .setUrl(PostDataUtil.data.getVideo_url())
                .setRotateViewAuto(true)
                .setLockLand(true)
                .setLooping(true)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setNeedShowWifiTip(false)
                .setCacheWithPlay(true)
                .setVideoTitle("视频播放界面")
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        orientationUtils.setEnable(VideoPlayer.isRotateWithSystem());
                        isPlay = true;

                        if (VideoPlayer.getGSYVideoManager().getPlayer() instanceof Exo2PlayerManager) {
                            ((Exo2PlayerManager) VideoPlayer.getGSYVideoManager().getPlayer()).setSeekParameter(SeekParameters.NEXT_SYNC);
                        }
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                })
                .setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (orientationUtils != null) {
                            orientationUtils.setEnable(!lock);
                        }
                    }
                }
                );

        gsyVideoOption.build(VideoPlayer);
    }

    @Override
    public void onResume() {
        super.onResume();
        //GSYVideoADManager.onResume();
        if(CurrentPosition > 0){
            VideoPlayer.getCurrentPlayer().onVideoResume(false);
        } else {
            VideoPlayer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    VideoPlayer.PlayState();
                    VideoPlayer.getCurrentPlayer().startPlayLogic();
                }
            },0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //GSYVideoADManager.onPause();
        VideoPlayer.getCurrentPlayer().onVideoPause();
        CurrentPosition = VideoPlayer.getGSYVideoManager().getCurrentPosition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //GSYVideoADManager.releaseAllVideos();
        if(VideoPlayer.getGSYVideoManager().isPlaying()){
            VideoPlayer.getCurrentPlayer().release();
        }

    }

    void InsertVideoIntoHistory(){
        String selection = VideoInfo._ID + " = ?";
        String[] selectionArgs = new String[]{"0"};
        database.delete(VideoInfo.Name_Table, selection, selectionArgs);

        ContentValues values = new ContentValues();
        values.put(VideoInfo.Post_ID, PostDataUtil.data.getId());
        values.put(VideoInfo.Student_Attribute, PostDataUtil.data.getStudentId());
        values.put(VideoInfo.User_Attribute, PostDataUtil.data.getFrom());
        values.put(VideoInfo.Image_Attribute, PostDataUtil.data.getImageUrl());
        values.put(VideoInfo.Video_Attribute, PostDataUtil.data.getVideo_url());
        values.put(VideoInfo.Create_Attribute, String.valueOf(PostDataUtil.data.getCreatedAt()));
        values.put(VideoInfo.Update_Attribute, String.valueOf(PostDataUtil.data.getUpdatedAt()));
        database.insert(VideoInfo.Name_Table, null, values);
    }

}