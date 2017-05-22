package com.example.whc.redrocktest.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.whc.redrocktest.Adapter.RVadpter;
import com.example.whc.redrocktest.Connection.UrlConnection;
import com.example.whc.redrocktest.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by WHC on 2017/5/20.
 */

public class MainView extends AppCompatActivity implements Handler.Callback {

    private RecyclerView recyclerView;
    private RVadpter rVadpter;
    private static final String path =
            "http://route.showapi.com/255-1?showapi_appid=38569&showapi_sign=1bc5e7bb3ced40feadb52592570254de&type=41&title=&page=&";

    private List<String> list_text=new ArrayList<>();
    private List<String> list_name=new ArrayList<>();
    private List<String> list_videourl=new ArrayList<>();
    private List<String> list_createtime=new ArrayList<>();
    private List<Bitmap> picture=new ArrayList<>();

    private Handler handler = new Handler(this);
    private static int thisposition;
    private MyMainView myMainView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        Init();
        Loading();
    }

    private void Init() {
        recyclerView = (RecyclerView) findViewById(R.id.mainview_recycleview);
        myMainView=new MyMainView();
        IntentFilter filter=new IntentFilter("MainView");
        registerReceiver(myMainView,filter);
    }

    private void Loading() {
        UrlConnection urlConnection = new UrlConnection(
                handler, path, list_text,list_name,list_videourl,list_createtime,picture);
        urlConnection.start();
    }

    @Override
    public boolean handleMessage(Message message) {
        Log.d("main", "handleMessage: " + list_text);
        rVadpter = new RVadpter(this, list_text,list_name,list_createtime,picture);
        recyclerView.setAdapter(rVadpter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL
                , false);
        recyclerView.setLayoutManager(linearLayoutManager);
        rVadpter.setOnItemCickListener(new RVadpter.onItemCickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainView.this, MainActivity.class);
                Bundle bundle=new Bundle();
                bundle.putStringArrayList("list", (ArrayList<String>) list_videourl);
                intent.putExtra("path",list_videourl.get(position));
                intent.putExtra("position",position);
                intent.putExtra("bundlelist",bundle);
                startActivity(intent);
            }
        });
        return false;
    }

    public class MyMainView extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int next=intent.getIntExtra("next",-1);
            int brefore=intent.getIntExtra("brefore",-1);
            if (next!=-1){
                Intent intent1=new Intent("MainActivity");
                if (thisposition==list_videourl.size()-1){
                    thisposition=0;
                }else{
                    thisposition++;
                }
                intent1.putExtra("nextvideo",list_videourl.get(thisposition));
                sendBroadcast(intent1);
            }

            if (brefore!=-1){
                Intent intent2=new Intent("MainActivity");
                if (thisposition==0){
                    thisposition=list_videourl.size()-1;
                }else {
                    thisposition--;
                }
                intent2.putExtra("beofrevideo",list_videourl.get(thisposition));
                sendBroadcast(intent2);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myMainView);
    }
}
