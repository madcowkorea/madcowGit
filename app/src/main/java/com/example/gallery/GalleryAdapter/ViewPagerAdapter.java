package com.example.gallery.GalleryAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallery.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    Context context;
    ArrayList<String> viewpagerList;

    public ViewPagerAdapter(ArrayList<String> viewpagerList, Context context){
        this.viewpagerList = viewpagerList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_detil_viewpager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerAdapter.ViewHolder holder, int position) {
        holder.bindSliderImg(viewpagerList.get(position));
    }

    @Override
    public int getItemCount() {
        return viewpagerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView pagerViewImgView;
        TextView ImgDataDetil;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pagerViewImgView = itemView.findViewById(R.id.Detil_Img);
            ImgDataDetil = itemView.findViewById(R.id.ImgDataDetil);
        }

        public void bindSliderImg(String ImgUrlList){
            Glide.with(context)
                    .load(ImgUrlList)
                    .into(pagerViewImgView);
        }
    }
}
