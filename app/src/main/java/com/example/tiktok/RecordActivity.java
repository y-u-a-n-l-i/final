package com.example.tiktok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.tiktok.Data.PostData;
import com.example.tiktok.Data.PostDataListResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecordActivity extends AppCompatActivity {
    private final static int PERMISSION_REQUEST_CODE = 1001;
    private final static int REQUEST_CODE_RECORD = 1002;

    private String mp4Path = "";
    private String coverPath = "";
    private VideoView mVideoView;
    private Button upload;
    private Button record;
    private PostData_api api;

    public static void startUI(Context context) {
        Intent intent = new Intent(context, RecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        setContentView(R.layout.activity_record);
        mVideoView = findViewById(R.id.videoview);
        upload = findViewById(R.id.upload_button);
        record = findViewById(R.id.record_button);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadData();
                    }
                }).start();
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        if(Constants.auto_upload){
            upload.setEnabled(false);
            upload.setVisibility(View.INVISIBLE);
        }else{
            upload.setEnabled(true);
            upload.setVisibility(View.VISIBLE);
        }
    }

    private void initNetwork() {
        //TODO 3
        // 创建Retrofit实例
        // 生成api对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-android-camp.bytedance.com/zju/invoke/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(PostData_api.class);
    }

    public void record(View view) throws FileNotFoundException {
        requestPermission();
    }

    private void requestPermission() throws FileNotFoundException {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {
            recordVideo();
        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.RECORD_AUDIO);
            }
            ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
        }

    }

    private void recordVideo() throws FileNotFoundException {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mp4Path = getOutputMediaPath();
        coverPath = getOutputMediaCoverPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, PathUtils.getUriForFile(this, mp4Path));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_RECORD);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                break;
            }
        }
        if (hasPermission) {
            try {
                recordVideo();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_RECORD && resultCode == RESULT_OK){
            MediaMetadataRetriever mmr=new MediaMetadataRetriever();
            mmr.setDataSource(mp4Path);
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
            mVideoView.setVideoPath(mp4Path);
            mVideoView.start();
            //TODO: 这边添加自动上传
            if(Constants.auto_upload){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadData();
                    }
                }).start();
            }
        }
    }

    public void uploadData() {
//        byte[] coverImageData = readDataFromUri(coverImageUri);
        File coverFile = new File(coverPath);
        MultipartBody.Part coverPart = MultipartBody.Part.createFormData("cover_image",
                coverPath,
                RequestBody.create(MediaType.parse("image/jpeg"), coverFile));
        File videoFile = new File(mp4Path);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video",
                mp4Path,
                RequestBody.create(MediaType.parse("video/*"), videoFile));
        Call<PostDataListResponse> result = api.submitMessage(Constants.student_id,Constants.name,"",coverPart,videoPart,Constants.token);
        result.enqueue(new Callback<PostDataListResponse>() {
            @Override
            public void onResponse(Call<PostDataListResponse> call, Response<PostDataListResponse> response) {
                if(response.isSuccessful()){
                    //Log.d(TAG,"upload successfully!");
                    Intent intent = new Intent(RecordActivity.this,MiddleActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(RecordActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostDataListResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}