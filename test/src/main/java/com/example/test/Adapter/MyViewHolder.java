package com.example.test.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.test.R;


public class MyViewHolder extends RecyclerView.ViewHolder{

    public ImageView imageView;
    public TextView authorname,time,videoname;
    public SurfaceView surfaceView;
    public Button play;
    public SeekBar seekBar;

    public MyViewHolder(View itemView) {
        super(itemView);
        imageView= (ImageView) itemView.findViewById(R.id.mainview_headimg);
        authorname= (TextView) itemView.findViewById(R.id.mainview_authorname);
        time= (TextView) itemView.findViewById(R.id.mainview_time);
        videoname= (TextView) itemView.findViewById(R.id.mainview_videoname);
        surfaceView= (SurfaceView) itemView.findViewById(R.id.mainview_surfaceview);
        play= (Button) itemView.findViewById(R.id.mainview_play);
        seekBar= (SeekBar) itemView.findViewById(R.id.mainview_seekbar);
    }
}
