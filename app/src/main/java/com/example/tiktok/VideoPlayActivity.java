package com.example.tiktok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tiktok.Data.PostDataUtil;
import com.example.tiktok.Database.VideoContract;
import com.example.tiktok.Database.VideoDbHelper;
import com.example.tiktok.View.FlowLikeView;
import com.example.tiktok.View.MyVideoPlayer;
import com.google.android.exoplayer2.SeekParameters;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
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
    private View LikeView;

    private OrientationUtils orientationUtils;
    private boolean isPlay;
    private boolean isPause;
    private boolean isLike;
    private CircleImageView CircleImageView;
    private Button ScreenButton;
    private HeartFrameLayout Heart;
    String mockUrl = "https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";

    //databasedb
    private VideoDbHelper dbHelper;
    private SQLiteDatabase Database;

    private static int PAUSE = 0;
    private static int PLAY = 1;
    private static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        dbHelper = new VideoDbHelper(this);
        Database = dbHelper.getWritableDatabase();
        InsertVideoIntoHistory();
        CurrentPosition = 0;
        VideoPlayer = findViewById(R.id.main_player);
        VideoPlayer.getTitleTextView().setVisibility(View.GONE);
        VideoPlayer.getBackButton().setVisibility(View.GONE);

        LikeView = findViewById(R.id.icon_heart);
        CheckIfLiked();
        LikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLike){
                    Toast.makeText(VideoPlayActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                    Drawable heart = getDrawable(R.mipmap.icon_heart);
                    LikeView.setBackground(heart);
                    isLike = false;
                    DeleteFromLike();
                }
                else{
                    Toast.makeText(VideoPlayActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    Drawable heart = getDrawable(R.mipmap.icon_heart_red);
                    LikeView.setBackground(heart);
                    isLike = true;
                    InsertIntoLike();
                }
            }
        });

        ScreenButton = findViewById(R.id.screen_button);
//        ScreenButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(VideoPlayer.getGSYVideoManager().isPlaying()){
//                    VideoPlayer.onVideoPause();
//                    VideoPlayer.PauseState();
//                }
//                else if(VideoPlayer.isInPlayingState()){
//                    VideoPlayer.PlayState();
//                    VideoPlayer.onVideoResume(false);
//                }
//            }
//        });

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == PAUSE){
                    VideoPlayer.onVideoPause();
                    VideoPlayer.PauseState();
                }
                else if(msg.what == PLAY){
                    VideoPlayer.PlayState();
                    VideoPlayer.onVideoResume(false);
                }
                else{
                    VideoPlayer.PlayState();
                    VideoPlayer.onVideoResume(false);
                }
            }
        };

        Heart = findViewById(R.id.heart);
        Heart.setOnDoubleClickListener(new OnDoubleClickListener() {
            @Override
            public void onDoubleClick(View view) {
            }
        });

        Heart.setOnSimpleClickListener(new OnSimpleClickListener() {
            @Override
            public void onSimpleClick(View view) {
                if(VideoPlayer.getGSYVideoManager().isPlaying()){
                    Message msg = new Message();
                    msg.what = PAUSE;
                    handler.sendMessage(msg);
                }
                else if(VideoPlayer.isInPlayingState()){
                    Message msg = new Message();
                    msg.what = PLAY;
                    handler.sendMessage(msg);
                }
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
        Database.close();
        Database = null;
        dbHelper.close();
        dbHelper = null;
        //GSYVideoADManager.releaseAllVideos();
        if(VideoPlayer.getGSYVideoManager().isPlaying()){
            VideoPlayer.getCurrentPlayer().release();
        }

    }

    void InsertVideoIntoHistory(){
        ContentValues values = new ContentValues();
        values.put(VideoInfo.Post_ID, PostDataUtil.data.getId());
        values.put(VideoInfo.Student_Attribute, PostDataUtil.data.getStudentId());
        values.put(VideoInfo.User_Attribute, PostDataUtil.data.getFrom());
        values.put(VideoInfo.Image_Attribute, PostDataUtil.data.getImageUrl());
        values.put(VideoInfo.Video_Attribute, PostDataUtil.data.getVideo_url());
        values.put(VideoInfo.Create_Attribute, String.valueOf(PostDataUtil.data.getCreatedAt()));
        values.put(VideoInfo.Update_Attribute, String.valueOf(PostDataUtil.data.getUpdatedAt()));
        String selection = VideoInfo.Post_ID + " = ?";
        String[] selectionArgs = new String[]{PostDataUtil.data.getId()};
        Database.delete(VideoInfo.History_Table, selection, selectionArgs);
        Database.insert(VideoInfo.History_Table, null, values);

        Cursor cursor = Database.query(VideoInfo.History_Table, null, null, null,
                null, null, VideoContract.VideoInfo._ID + " DESC");
        if(cursor.getCount() > 9){
            cursor.move(cursor.getCount());
            String Post_Id = cursor.getString(cursor.getColumnIndex(VideoInfo.Post_ID));
            selection = VideoInfo.Post_ID + " = ?";
            selectionArgs = new String[]{Post_Id};
            Database.delete(VideoInfo.History_Table, selection, selectionArgs);
        }
    }

    void CheckIfLiked(){
        String selection = VideoInfo.Post_ID + " = ?";
        String[] selectionArgs = new String[]{PostDataUtil.data.getId()};
        Cursor cursor = Database.query(VideoInfo.Save_Table,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);
        if(cursor.getCount() != 0){
            isLike = true;
            Drawable heart = getDrawable(R.mipmap.icon_heart_red);
            LikeView.setBackground(heart);
        }
        else{
            isLike = false;
            Drawable heart = getDrawable(R.mipmap.icon_heart);
            LikeView.setBackground(heart);
        }
    }

    void InsertIntoLike(){
        ContentValues values = new ContentValues();
        values.put(VideoInfo.Post_ID, PostDataUtil.data.getId());
        values.put(VideoInfo.Student_Attribute, PostDataUtil.data.getStudentId());
        values.put(VideoInfo.User_Attribute, PostDataUtil.data.getFrom());
        values.put(VideoInfo.Image_Attribute, PostDataUtil.data.getImageUrl());
        values.put(VideoInfo.Video_Attribute, PostDataUtil.data.getVideo_url());
        values.put(VideoInfo.Create_Attribute, String.valueOf(PostDataUtil.data.getCreatedAt()));
        values.put(VideoInfo.Update_Attribute, String.valueOf(PostDataUtil.data.getUpdatedAt()));
        Database.insert(VideoInfo.Save_Table, null, values);
    }

    void DeleteFromLike(){
        String selection = VideoInfo.Post_ID + " = ?";
        String[] selectionArgs = new String[]{PostDataUtil.data.getId()};
        Database.delete(VideoInfo.Save_Table, selection, selectionArgs);
    }
}