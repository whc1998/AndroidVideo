package com.example.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.example.test.Adapter.RVadpter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RVadpter rVadpter;
    private String filepath="http://mvideo.spriteapp.cn/video/2017/0518/104da364-3bd5-11e7-8356-1866daeb0df1_wpc.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        recyclerView.setAdapter(rVadpter);
    }

    private void Init(){
        recyclerView= (RecyclerView) findViewById(R.id.mainview_recycleview);
        rVadpter=new RVadpter(this);
    }

}
