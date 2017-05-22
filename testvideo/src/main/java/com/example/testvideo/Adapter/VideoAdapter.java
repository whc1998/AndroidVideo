package com.example.testvideo.Adapter;

/**
 * Created by WHC on 2017/5/22.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.testvideo.DonwLoad.DownloadService;
import com.example.testvideo.R;
import com.example.testvideo.Util.TextureVideoView;

import static android.content.Context.BIND_AUTO_CREATE;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    Context mContext;
    private VideoViewHolder lastPlayVideo = null;
    private List<String> list_text = new ArrayList<>();
    private List<String> list_name = new ArrayList<>();
    private List<String> list_createtime = new ArrayList<>();
    private List<String> list_videourl = new ArrayList<>();
    private List<Bitmap> picture = new ArrayList<>();
    private List<Bitmap> picture_video=new ArrayList<>();
    private DownloadService.DownloadBinder downloadBinder;

    public VideoAdapter(Context context, List<String> list_text, List<String> list_name
            , List<String> list_createtime, List<String> list_videourl, List<Bitmap> picture
            ,DownloadService.DownloadBinder downloadBinder,List<Bitmap> picture_video) {
        mContext = context;
        this.list_text = list_text;
        this.list_name = list_name;
        this.list_createtime = list_createtime;
        this.list_videourl = list_videourl;
        this.picture = picture;
        this.downloadBinder=downloadBinder;
        this.picture_video=picture_video;
    }

    class VideoViewHolder extends ViewHolder {
        public VideoViewHolder(View itemView) {
            super(itemView);
        }

        TextureVideoView videoView;
        ImageView imvPreview, imagehead;
        ImageView imvPlay, bt_download;
        ProgressBar pbWaiting;
        SeekBar seekBar;
        TextView authorname, time, videoname;
    }

    @Override
    public int getItemCount() {
        return list_text.size();
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder viewHolder, final int position) {

        viewHolder.authorname.setText(list_name.get(position));
        viewHolder.videoname.setText(list_text.get(position));
        viewHolder.time.setText(list_createtime.get(position));
        viewHolder.imagehead.setImageBitmap(picture.get(position));
        viewHolder.imvPreview.setImageBitmap(picture_video.get(position));

        viewHolder.bt_download.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadBinder.startDownload(list_videourl.get(position),list_name.get(position));
            }
        });

        viewHolder.videoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lastPlayVideo == null) {
                    lastPlayVideo = viewHolder;
                } else {
                    if (!viewHolder.equals(lastPlayVideo)) {
                        lastPlayVideo.videoView.stop();
                        lastPlayVideo.pbWaiting.setVisibility(View.GONE);
                        lastPlayVideo.imvPlay.setVisibility(View.VISIBLE);
                        viewHolder.bt_download.setVisibility(View.VISIBLE);
                        lastPlayVideo = viewHolder;
                    }
                }
                TextureVideoView textureView = (TextureVideoView) v;
                if (textureView.getState() == TextureVideoView.MediaState.INIT || textureView.getState() == TextureVideoView.MediaState.RELEASE) {
                    textureView.play(list_videourl.get(position));
                    viewHolder.pbWaiting.setVisibility(View.VISIBLE);
                    viewHolder.imvPlay.setVisibility(View.GONE);
                    viewHolder.bt_download.setVisibility(View.GONE);
                } else if (textureView.getState() == TextureVideoView.MediaState.PAUSE) {
                    textureView.start();
                    viewHolder.pbWaiting.setVisibility(View.GONE);
                    viewHolder.imvPlay.setVisibility(View.GONE);
                    viewHolder.bt_download.setVisibility(View.GONE);
                } else if (textureView.getState() == TextureVideoView.MediaState.PLAYING) {
                    textureView.pause();
                    viewHolder.pbWaiting.setVisibility(View.GONE);
                    viewHolder.imvPlay.setVisibility(View.VISIBLE);
                    viewHolder.bt_download.setVisibility(View.VISIBLE);
                } else if (textureView.getState() == TextureVideoView.MediaState.PREPARING) {
                    textureView.stop();
                    viewHolder.pbWaiting.setVisibility(View.GONE);
                    viewHolder.imvPlay.setVisibility(View.VISIBLE);
                    viewHolder.bt_download.setVisibility(View.VISIBLE);
                }
            }
        });
        viewHolder.videoView.setOnStateChangeListener(new TextureVideoView.OnStateChangeListener() {
            @Override
            public void onSurfaceTextureDestroyed(SurfaceTexture surface) {
                viewHolder.videoView.stop();
                viewHolder.pbWaiting.setVisibility(View.GONE);
                viewHolder.imvPlay.setVisibility(View.VISIBLE);
                viewHolder.bt_download.setVisibility(View.VISIBLE);
                viewHolder.seekBar.setMax(1);
                viewHolder.seekBar.setProgress(0);
                viewHolder.imvPreview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPlaying() {
                viewHolder.pbWaiting.setVisibility(View.GONE);
                viewHolder.imvPlay.setVisibility(View.GONE);
                viewHolder.bt_download.setVisibility(View.GONE);
            }

            @Override
            public void onBuffering() {
                viewHolder.pbWaiting.setVisibility(View.VISIBLE);
                viewHolder.imvPlay.setVisibility(View.GONE);
                viewHolder.bt_download.setVisibility(View.GONE);
            }

            @Override
            public void onSeek(int max, int progress) {
                viewHolder.imvPreview.setVisibility(View.GONE);
                viewHolder.seekBar.setMax(max);
                viewHolder.seekBar.setProgress(progress);
            }

            @Override
            public void onStop() {
                viewHolder.seekBar.setMax(1);
                viewHolder.seekBar.setProgress(0);
                viewHolder.pbWaiting.setVisibility(View.GONE);
                viewHolder.imvPlay.setVisibility(View.VISIBLE);
                viewHolder.bt_download.setVisibility(View.VISIBLE);
                viewHolder.imvPreview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPause() {
                viewHolder.pbWaiting.setVisibility(View.GONE);
                viewHolder.imvPlay.setVisibility(View.VISIBLE);
                viewHolder.bt_download.setVisibility(View.VISIBLE);
            }

            @Override
            public void playFinish() {
                viewHolder.seekBar.setMax(1);
                viewHolder.seekBar.setProgress(0);
                viewHolder.imvPlay.setVisibility(View.GONE);
                viewHolder.bt_download.setVisibility(View.GONE);
                viewHolder.imvPreview.setVisibility(View.VISIBLE);
            }

        });

        viewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewHolder.videoView.seekTo(seekBar.getProgress());
            }
        });

    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup root, int position) {
        View containerView = LayoutInflater.from(mContext).inflate(R.layout.video_item, root, false);
        VideoViewHolder videoViewHolder = new VideoViewHolder(containerView);
        videoViewHolder.videoView = (TextureVideoView) containerView.findViewById(R.id.textureview);
        videoViewHolder.imvPreview = (ImageView) containerView.findViewById(R.id.imv_preview);
        videoViewHolder.imvPlay = (ImageView) containerView.findViewById(R.id.imv_video_play);
        videoViewHolder.pbWaiting = (ProgressBar) containerView.findViewById(R.id.pb_waiting);
        videoViewHolder.seekBar = (SeekBar) containerView.findViewById(R.id.seekbar);
        videoViewHolder.authorname = (TextView) containerView.findViewById(R.id.mainview_authorname);
        videoViewHolder.time = (TextView) containerView.findViewById(R.id.mainview_time);
        videoViewHolder.videoname = (TextView) containerView.findViewById(R.id.mainview_videoname);
        videoViewHolder.imagehead = (ImageView) containerView.findViewById(R.id.mainview_headimg);
        videoViewHolder.bt_download = (ImageView) containerView.findViewById(R.id.Button_download);
        return videoViewHolder;
    }
}
