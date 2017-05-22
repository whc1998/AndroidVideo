package com.example.testvideo.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.testvideo.Connection.URLConnection;
import com.example.testvideo.DonwLoad.DownloadService;
import com.example.testvideo.R;
import com.example.testvideo.Adapter.VideoAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Handler.Callback {

    private RecyclerView rlVideoList;
    private Handler handler=new Handler(this);

    private static final String path=
            "http://route.showapi.com/255-1?showapi_appid=38569&showapi_sign=1bc5e7bb3ced40feadb52592570254de&type=41&title=&page=&";
    private List<String> list_text=new ArrayList<>();
    private List<String> list_name=new ArrayList<>();
    private List<String> list_videourl=new ArrayList<>();
    private List<String> list_createtime=new ArrayList<>();
    private List<Bitmap> picture=new ArrayList<>();
    private List<Bitmap> picture_video=new ArrayList<>();
    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            downloadBinder= (DownloadService.DownloadBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        Loading();
    }

    private void Init(){
        rlVideoList=(RecyclerView) findViewById(R.id.rv_vieo_list);
        Intent intent=new Intent(MainActivity.this, DownloadService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    private void Loading(){
        new URLConnection(
                handler,path,list_text,list_name,list_videourl,list_createtime,picture,picture_video).start();
    }

    @Override
    public boolean handleMessage(Message message) {
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlVideoList.setLayoutManager(layoutManager);
        VideoAdapter adapter=new VideoAdapter(
                this,list_text,list_name,list_createtime,list_videourl,picture,downloadBinder,picture_video);
        rlVideoList.setAdapter(adapter);
        return false;
    }
}
