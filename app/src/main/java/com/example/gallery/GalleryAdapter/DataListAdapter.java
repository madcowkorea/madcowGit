package com.example.gallery.GalleryAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallery.CallbackListener;
import com.example.gallery.List_MainActivity;
import com.example.gallery.R;

import java.io.File;
import java.util.ArrayList;

public class DataListAdapter extends RecyclerView.Adapter<Holder>{

    ArrayList<AutoCastMap> dataItemList;
    Context mContext;
    boolean isChecked = false;

    private CallbackListener callbackListener;

    public void setCallbackListener(CallbackListener callbackListener){
        this.callbackListener = callbackListener;
    }

    public DataListAdapter(Context mContext,ArrayList<AutoCastMap> dataItemList){
        this.mContext = mContext;
        this.dataItemList = dataItemList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // View 객체에 아이템방식을 담아서 Holder에 넘겨준다
        View view = inflater.inflate(R.layout.activity_gallery_list_item, parent, false);
        return new Holder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {

        //글라이드를 이용하여 받아온 uri를 이미지로 변환하여 화면에 뿌려준다.
        Glide.with(holder.itemView).
                load(dataItemList.get(position).get("imgUri")).
                override(200, 200).
                centerCrop().
                placeholder(R.drawable.ic_launcher_background).
                into(holder.gallery_img_item);

        if (mContext.getClass().equals(List_MainActivity.class)) {
            //리스트의 날짜를 표현해준다.
            File file = new File(String.valueOf(dataItemList.get(position).get("imgUri")));
            //이미지파일 이름
            String Dataname = file.getName();
            //이미지파일 용량
            long bytes = file.length();
            //이미지파일 날짜
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd / HH:mm:ss");
            holder.ImgData.setText("파일명: " + Dataname + "\n" + "날짜: " + formatter.format(file.lastModified()) + "\n" + "용량: " + bytes + " byte");
        }

        holder.cons.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(dataItemList.get(position).getBoolean("imgChecked") == false){
                    isChecked = true;
                }else {
                    isChecked = false;
                }
                callbackListener.callBack(holder.getAdapterPosition(), isChecked);
            }
        });

        holder.checkBox.setChecked(dataItemList.get(position).getBoolean("imgChecked"));
    }

    @Override
    public int getItemCount() {return dataItemList.size();}
}

class Holder extends RecyclerView.ViewHolder{
    ImageView gallery_img_item;
    ConstraintLayout cons;
    TextView ImgData;
    CheckBox checkBox;

    public Holder(@NonNull View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.checktest);
        ImgData = itemView.findViewById(R.id.gallery_ImgData);
        cons = itemView.findViewById(R.id.constraint_item);
        gallery_img_item = itemView.findViewById(R.id.gallery_img_item);
    }
}
