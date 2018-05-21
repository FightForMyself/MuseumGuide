package com.example.museumguide;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by LQ on 2018/4/15.
 */

public class RelicAdapter extends RecyclerView.Adapter<RelicAdapter.ViewHolder> {
    private Context mContext;
    private List<Relic> mRelicList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView relicImage;
        TextView relicName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            relicImage = (ImageView) view.findViewById(R.id.relic_image);
            relicName = (TextView) view.findViewById(R.id.relic_name);
        }
    }

    public RelicAdapter(List<Relic> relicList){
        mRelicList=relicList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.relic_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Relic relic=mRelicList.get(position);
        holder.relicName.setText(relic.getName());
        Glide.with(mContext).load(relic.getImageId()).into(holder.relicImage);
    }

    @Override
    public int getItemCount() {
        return mRelicList.size();
    }
}
