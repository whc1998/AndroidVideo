package com.example.whc.redrocktest.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.whc.redrocktest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WHC on 2017/5/20.
 */

public class RVadpter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private onItemCickListener onItemCickListener;
    private List<String> list_text= new ArrayList<>();
    private List<String> list_name=new ArrayList<>();
    private List<String> list_createtime=new ArrayList<>();
    private List<Bitmap> picture=new ArrayList<>();

    public RVadpter(Context context, List<String> list_text,List<String> list_name
    ,List<String> list_createtime,List<Bitmap> picture) {
        this.context = context;
        this.list_text = list_text;
        this.list_name=list_name;
        this.picture=picture;
        this.list_createtime=list_createtime;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.mainview_style_recyclerview, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Glide.with(context)
                .load("http://wimg.spriteapp.cn/profile/large/2017/05/12/591561119436d_mini.jpg")
                .placeholder(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.mipmap.ic_launcher)
                .fitCenter()
                .into(holder.imageView);
        holder.authorname.setText(list_name.get(position));
        holder.videoname.setText(list_text.get(position));
        holder.time.setText(list_createtime.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemCickListener!=null){
                    onItemCickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_text.size();
    }

    public interface onItemCickListener{
        void onClick(int position);
    }

    public void setOnItemCickListener(onItemCickListener onItemCickListener){
        this.onItemCickListener=onItemCickListener;
    }
}


