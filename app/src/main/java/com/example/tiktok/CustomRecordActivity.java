package com.example.tiktok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.tiktok.Data.PathUtils;
import com.example.tiktok.Data.PostDataListResponse;
import com.example.tiktok.Data.PostData_api;
import com.example.tiktok.Data.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomRecordActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private static final int REQUEST_CODE_VIDEO = 102;
    private static final String VIDEO_TYPE = "video/*";

    private static final String TAG = "TikTok Custom Record";

    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private SurfaceHolder mHolder;
    private VideoView mVideoView;
    private Button mRecordButton;
    private Button mUploadButton;
    private Button mChooseVideoButton;
    private Button mChooseCoverButton;
    private Button tenSRecordButton;
    private boolean isRecording = false;
    private PostData_api api;
    private Uri coverImageUri;
    private Uri VideoUri;
    private int time_left;

    private CountDownTimer countDownTimer;

    private String mp4Path = "";
    private String coverPath = "";

    public static void startUI(Context context) {
        Intent intent = new Intent(context, CustomRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_record);
        initNetwork();
        mSurfaceView = findViewById(R.id.msurfaceview);
        mVideoView = findViewById(R.id.mvideoview);
        mRecordButton = findViewById(R.id.mrecord_button);
        mUploadButton = findViewById(R.id.mupload_button);

        mChooseVideoButton = findViewById(R.id.mchoose_video);
        mChooseCoverButton = findViewById(R.id.mchoose_cover);

        mHolder = mSurfaceView.getHolder();
        initCamera();
        mHolder.addCallback(this);

        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.cancelAutoFocus();
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                                      @Override
                                      public void onAutoFocus(boolean success, Camera camera) {

                                      }
                                  }
                );
            }
        });

        mChooseVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_VIDEO, VIDEO_TYPE, "choose video");
            }
        });

        mChooseCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "choose cover");
            }
        });

        if(Constants.auto_upload){
            mUploadButton.setEnabled(false);
            mUploadButton.setVisibility(View.INVISIBLE);
        }else{
            mUploadButton.setEnabled(true);
            mUploadButton.setVisibility(View.VISIBLE);
            mUploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                uploadData();
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            initCamera();
        }
        mCamera.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }
    private void initCamera() {
        mCamera = Camera.open();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.set("orientation", "portrait");
        parameters.set("rotation", 90);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
    }

    private void initNetwork() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-android-camp.bytedance.com/zju/invoke/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(PostData_api.class);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }
        mCamera.stopPreview();
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverPath = coverImageUri.toString()+".jpg";

                System.out.println(coverPath);
                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }else if(REQUEST_CODE_VIDEO == requestCode){
            if (resultCode == Activity.RESULT_OK) {
                VideoUri = data.getData();
                mp4Path = VideoUri.toString()+".mp4";
                mVideoView.setVisibility(View.VISIBLE);
                mVideoView.setVideoURI(VideoUri);
                storeFirstFrame();
                mVideoView.start();

                if (VideoUri != null) {
                    Log.d(TAG, "pick cover image " + VideoUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }
    }

    private void storeFirstFrame(){
        coverPath = getOutputMediaCoverPath();
        coverImageUri = PathUtils.getUriForFile(this,coverPath);
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        mmr.setDataSource(this,VideoUri);
        Bitmap bitmap = mmr.getFrameAtTime(0);  //0表示首帧图片
        mmr.release();
        File picture_file = new File(coverPath); // 创建路径和文件名的File对象
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picture_file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        return;
    }

    public void record(View view) throws URISyntaxException {
        if (!isRecording) {
            onRecording();
        } else {
            // 停止录制
            onStopRecording();
            if(Constants.auto_upload){
                uploadData();
            }
        }
        isRecording = !isRecording;
    }

    public void onRecording(){
        if(prepareVideoRecorder()){
            mRecordButton.setBackgroundResource(R.drawable.bg_round_rect2);
            time_left = 10;
            mRecordButton.setTextColor(getResources().getColor(R.color.white));
            mRecordButton.setTextSize(24);
            mMediaRecorder.start();

            countDownTimer = new CountDownTimer(10000,1000){
                public void onTick(long millisUntilFinished) {
                    mRecordButton.setText(String.valueOf(time_left));
                    if(time_left>0) time_left--;
                }

                public void onFinish() {
                    mRecordButton.setText(String.valueOf(0));
                }
            }.start();
        }
    }

    public void onStopRecording(){
        countDownTimer.cancel();
        mRecordButton.setBackgroundResource(R.drawable.circle);
        mRecordButton.setText("");
        mMediaRecorder.setOnErrorListener(null);
        mMediaRecorder.setOnInfoListener(null);
        mMediaRecorder.setPreviewDisplay(null);
        try {
            mMediaRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
//            mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();

        mVideoView.setVisibility(View.VISIBLE);
        mVideoView.setVideoPath(mp4Path);
        storeFirstFrame();
        mVideoView.start();
    }

    private boolean prepareVideoRecorder() {
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        // Step 4: Set output file
        mp4Path = getOutputMediaPath();
        VideoUri = PathUtils.getUriForFile(this,mp4Path);
//        coverPath = getOutputMediaCoverPath();
        mMediaRecorder.setOutputFile(mp4Path);

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        mMediaRecorder.setOrientationHint(90);

        mMediaRecorder.setMaxDuration(10000);
        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
                    isRecording = false;
                    onStopRecording();
                }
            }
        });

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException | IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if(mMediaRecorder!=null){
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    private String getOutputMediaPath() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".mp4");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    private String getOutputMediaCoverPath() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".jpg");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    public void uploadData() throws URISyntaxException {
        byte[] coverImageData = readDataFromUri(coverImageUri);
//        File coverFile = new File(coverPath);
        System.out.println("/////////////////////////////"+coverImageUri.toString());
        MultipartBody.Part coverPart = MultipartBody.Part.createFormData("cover_image",
                coverPath,
                RequestBody.create(MediaType.parse("image/*"), coverImageData));
        byte[] videoData = readDataFromUri(VideoUri);
//        File videoFile = new File(mp4Path);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video",
                mp4Path,
                RequestBody.create(MediaType.parse("video/*"), videoData));
        Call<PostDataListResponse> result = api.submitMessage(Constants.student_id,Constants.name,"",coverPart,videoPart,Constants.token);
        result.enqueue(new Callback<PostDataListResponse>() {
            @Override
            public void onResponse(Call<PostDataListResponse> call, Response<PostDataListResponse> response) {
                if(response.isSuccessful()){
                    //Log.d(TAG,"upload successfully!");
                    Intent intent = new Intent(CustomRecordActivity.this,MiddleActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(CustomRecordActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostDataListResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}