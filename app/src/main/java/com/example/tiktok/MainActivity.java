package com.example.tiktok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tv_name;
    private TextView tv_id;
    private TextView tv_logo;
    private EditText et_name;
    private EditText et_id;
    private Button bt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        tv_name = findViewById(R.id.name);
        tv_id = findViewById(R.id.student_id);
        et_name = findViewById(R.id.editTextTextPersonName);
        et_id = findViewById(R.id.editTextTextPersonName2);
        tv_logo = findViewById(R.id.logo);
        bt_login = findViewById(R.id.button);

        et_name.setEnabled(false);
        et_id.setEnabled(false);
        tv_logo.setGravity(Gravity.CENTER);
        bt_login.setEnabled(false);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"login",Toast.LENGTH_SHORT).show();
                if(!et_name.getText().toString().isEmpty()) Constants.name = et_name.getText().toString();
                else Constants.name = "李渊";
                if(!et_id.getText().toString().isEmpty()) Constants.student_id = et_id.getText().toString();
                else Constants.name = "3190101986";
                Intent intent = new Intent(MainActivity.this, MiddleActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        TranslateAnimation animation1 = new TranslateAnimation(0.0f,0.0f,0.0f,-500.0f);
        animation1.setDuration(1500);
        animation1.setFillBefore(false);
        animation1.setFillAfter(true);
        tv_logo.startAnimation(animation1);

        AlphaAnimation animation2 = new AlphaAnimation(0.0f,1.0f);
        animation2.setStartOffset(1500);
        animation2.setDuration(750);
        animation2.setFillBefore(false);
        animation2.setFillAfter(true);
        et_name.startAnimation(animation2);
        et_id.setAnimation(animation2);
        tv_id.setAnimation(animation2);
        tv_name.setAnimation(animation2);
        bt_login.setAnimation(animation2);

        et_name.setEnabled(true);
        et_id.setEnabled(true);
        bt_login.setEnabled(true);
    }
}