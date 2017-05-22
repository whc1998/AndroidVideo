package com.example.whc.redrocktest.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.whc.redrocktest.R;

public class MyViewHolder extends RecyclerView.ViewHolder{

    public ImageView imageView;
    public TextView authorname,time,videoname;
    public ImageView videoview;

    public MyViewHolder(View itemView) {
        super(itemView);
        imageView= (ImageView) itemView.findViewById(R.id.mainview_headimg);
        authorname= (TextView) itemView.findViewById(R.id.mainview_authorname);
        time= (TextView) itemView.findViewById(R.id.mainview_time);
        videoname= (TextView) itemView.findViewById(R.id.mainview_videoname);
        videoview= (ImageView) itemView.findViewById(R.id.mainview_videoview);
    }
}
