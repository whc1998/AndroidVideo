package com.example.test.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test.R;


/**
 * Created by WHC on 2017/5/20.
 */

public class RVadpter extends RecyclerView.Adapter<MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private onItemCickListener onItemCickListener;

    public RVadpter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view=inflater.inflate(R.layout.mainview_style_recyclerview,parent,false);
        MyViewHolder viewHolder=new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.imageView.setImageResource(R.mipmap.ic_launcher_round);
        holder.authorname.setText("hello");
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
        return 1;
    }

    public interface onItemCickListener{
        void onClick(int position);
    }

    public void setOnItemCickListener(onItemCickListener onItemCickListener){
        this.onItemCickListener=onItemCickListener;
    }

}

