package com.example.whc.redrocktest.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.whc.redrocktest.DonwLoad.DownloadService;
import com.example.whc.redrocktest.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

// 在音乐播放器基础上加***部分的内容即可播放视频；加上===部分即可控制拖动栏；加上————部分可解决MediaPlayer与SurfaceHolder同步的问题
public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener
        , MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        SeekBar.OnSeekBarChangeListener {
    private ImageView bt_before, bt_play, bt_next, btn_allstop;
    private Button downLoad;
    private MediaPlayer mediaPlayer;//多媒体播放器
    private SurfaceView sv;//****************SurfaceView是一个在其他线程中显示、更新画面的组件，专门用来完成在单位时间内大量画面变化的需求
    private SurfaceHolder holder;//****************SurfaceHolder接口为一个显示界面内容的容器
    private SeekBar seekBar;//===============进度条
    private static int savedPosition;//===============记录当前播放文件播放的进度
    //    private static String savedFilepath;//===============记录当前播放文件的位置
    private Timer timer;//===============定义一个计时器，每隔100ms更新一次进度条
    private TimerTask task;//===============计时器所执行的任务
    private ArrayList<String> list;
    private int position;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        holder = sv.getHolder();//****************得到显示界面内容的容器
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //设置surfaceView自己不管理缓存区。虽然提示过时，但最好还是设置下
        seekBar.setOnSeekBarChangeListener(this);//===============
        //在界面【最小化】时暂停播放，并记录holder播放的位置——————————————————————————————
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {//holder被销毁时回调。最小化时都会回调
                if (mediaPlayer != null) {
                    Log.i("bqt", "销毁了--surfaceDestroyed" + "--" + mediaPlayer.getCurrentPosition());
                    savedPosition = mediaPlayer.getCurrentPosition();//当前播放位置
                    mediaPlayer.stop();
                    try{
                        timer.cancel();
                        task.cancel();
                        timer = null;
                        task = null;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {//holder被创建时回调
//                Log.i("bqt", "创建了--" + savedPosition + "--" + savedFilepath);
//                if (savedPosition > 0) {//如果记录的数据有播放进度。
//                    try {
//                        mediaPlayer.reset();
//                        mediaPlayer.setDataSource(savedFilepath);
//                        mediaPlayer.setDisplay(holder);
//                        mediaPlayer.prepare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            }

            //holder宽高发生变化（横竖屏切换）时回调
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_play:
                pause();
                break;
            case R.id.bt_allstop:
                stop();
                break;
            case R.id.bt_before:
                playUrl(path);
                break;
            case R.id.bt_next:
                replay();
                break;
            case R.id.downLoad:
                downloadBinder.startDownload(path, "viedo");
                break;
            default:
                break;
        }
    }

    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            downloadBinder = (DownloadService.DownloadBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void Init() {
        bt_before = (ImageView) findViewById(R.id.bt_before);
        bt_play = (ImageView) findViewById(R.id.bt_play);
        bt_next = (ImageView) findViewById(R.id.bt_next);
        btn_allstop = (ImageView) findViewById(R.id.bt_allstop);
        downLoad = (Button) findViewById(R.id.downLoad);
        downLoad.setOnClickListener(this);
        btn_allstop.setOnClickListener(this);
        bt_before.setOnClickListener(this);
        bt_play.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        sv = (SurfaceView) findViewById(R.id.sv);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        path = getIntent().getStringExtra("path");
        Bundle bundle = getIntent().getExtras();
        list = bundle.getStringArrayList("list");
        position = getIntent().getIntExtra("position", -1);
        Intent intent = new Intent(MainActivity.this, DownloadService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 播放网络多媒体
     */
    public void playUrl(String filepath) {
        if (!TextUtils.isEmpty(filepath)) {
            try {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(filepath);
                mediaPlayer.setDisplay(holder);
                mediaPlayer.prepareAsync();//异步准备
                Toast.makeText(MainActivity.this, "准备中，可能需要点时间……", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "播放失败，请检查是否有网络权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                bt_play.setImageResource(R.drawable.ic_play_circle);
            } else {
                mediaPlayer.start();
                bt_play.setImageResource(R.drawable.ic_pause_circle);
            }
        }
    }

    /**
     * 停止
     */
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
    }

    /**
     * 重播
     */
    public void replay() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.seekTo(0);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(MainActivity.this, "报错了--" + what + "--" + extra, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {//只有准备好以后才能处理很多逻辑
        mediaPlayer.start();
        //=============
        mediaPlayer.seekTo(savedPosition);//开始时是从0开始播放，恢复时是从指定位置开始播放
        seekBar.setMax(mediaPlayer.getDuration());//将进度条的最大值设为文件的总时长
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());//将媒体播放器当前播放的位置赋值给进度条的进度
            }
        };
        timer.schedule(task, 0, 100);//0秒后执行，每隔100ms执行一次
        Toast.makeText(MainActivity.this, "准备好了！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(MainActivity.this, "播放完毕！", Toast.LENGTH_SHORT).show();
        mediaPlayer.reset();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {//进度发生变化时
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {//停止拖拽时回调
        int progress = seekBar.getProgress();
        mediaPlayer.seekTo(progress);//停止拖拽时进度条的进度
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
