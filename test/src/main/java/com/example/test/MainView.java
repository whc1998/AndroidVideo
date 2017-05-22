package com.example.test;

/**
 * Created by WHC on 2017/5/21.
 */

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class MainView extends Activity implements OnClickListener {

    private SurfaceView surfaceview;
    private MediaPlayer mediaPlayer;
    private ImageButton start;
    private ImageButton share;
    private ImageButton back;
    private ImageButton pause;
    private SeekBar seekBar;
    private boolean isPlaying;
    private int currentPosition = 0;

    private int postion = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        findViewById();
        initView();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());//停止拖拽时进度条的进度
            }
        });
    }

    protected void findViewById() {
        // TODO Auto-generated method stub
        surfaceview = (SurfaceView) findViewById(R.id.surfaceView);
        start = (ImageButton) findViewById(R.id.video_start);
        back = (ImageButton) findViewById(R.id.video_back);
        pause = (ImageButton) findViewById(R.id.video_pause);
        share = (ImageButton) findViewById(R.id.video_share);
        seekBar = (SeekBar) findViewById(R.id.seekBar);



    }

    protected void initView() {
        // TODO Auto-generated method stub
        mediaPlayer = new MediaPlayer();
        surfaceview.getHolder().setKeepScreenOn(true);
        surfaceview.getHolder().addCallback(new SurfaceViewLis());
        start.setOnClickListener(this);
        back.setOnClickListener(this);
        pause.setOnClickListener(this);
        share.setOnClickListener(this);
        seekBar.setOnClickListener(this);
    }

    private class SurfaceViewLis implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (currentPosition > 0) {
                // 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
                video_play(currentPosition,
                        "http://mvideo.spriteapp.cn/video/2017/0520/b5457344-3d31-11e7-b1ff-1866daeb0df1_wpc.mp4");
                currentPosition = 0;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                currentPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_start:
                video_play(0,"http://mvideo.spriteapp.cn/video/2017/0520/b5457344-3d31-11e7-b1ff-1866daeb0df1_wpc.mp4");
                break;
            case R.id.video_pause:
                pause();
                break;


            default:
                break;
        }
    }


    /**
     * 开始播放
     *
     * @param msec 播放初始位置
     */
    protected void video_play(final int msec,final String path) {
//      // 获取视频文件地址
        try {
            mediaPlayer = new MediaPlayer();
            //设置音频流类型
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置播放的视频源
//            AssetFileDescriptor fd = this.getAssets().openFd("Perspective.mp4");
//            mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),
//                    fd.getLength());
            mediaPlayer.setDataSource(path);
            // 设置显示视频的SurfaceHolder
            mediaPlayer.setDisplay(surfaceview.getHolder());//这一步是关键，制定用于显示视频的SurfaceView对象（通过setDisplay（））

            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();

                    // 按照初始位置播放
                    mediaPlayer.seekTo(msec);
                    // 设置进度条的最大进度为视频流的最大播放时长
                    seekBar.setMax(mediaPlayer.getDuration());
                    // 开始线程，更新进度条的刻度
                    new Thread() {

                        @Override
                        public void run() {
                            try {
                                isPlaying = true;
                                while (isPlaying) {
                                    int current = mediaPlayer
                                            .getCurrentPosition();
                                    seekBar.setProgress(current);

                                    sleep(1000);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                    start.setEnabled(false);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 在播放完毕被回调
                    start.setEnabled(true);
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 发生错误重新播放
                    video_play(0,"http://mvideo.spriteapp.cn/video/2017/0520/b5457344-3d31-11e7-b1ff-1866daeb0df1_wpc.mp4");
                    isPlaying = false;
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 暂停或继续
     */
    protected void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

    }
}