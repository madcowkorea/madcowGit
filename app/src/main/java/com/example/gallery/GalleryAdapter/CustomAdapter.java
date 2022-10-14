package com.example.gallery.GalleryAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallery.CallbackListener;
import com.example.gallery.Gallery_Detil_MainActivity;
import com.example.gallery.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class CustomAdapter extends RecyclerView.Adapter<Holder>{

    ArrayList<String> Idlist;
    Map<String,String> LayoutMap;
    ArrayList<String> CheckedList = new ArrayList<>();
    ArrayList<String> CheckedIdList = new ArrayList<>();
    ArrayList<String> list;
    Button submit;
    private CallbackListener callbackListener;

    public void setCallbackListener(CallbackListener callbackListener){
        this.callbackListener = callbackListener;
    }
    public CustomAdapter(ArrayList<String> Idlist, Map<String, String> LayoutMap, Button submit,ArrayList<String> list) {
        this.list = list;
        this.Idlist = Idlist;
        this.LayoutMap = LayoutMap;
        this.submit = submit;
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
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {

                //글라이드를 이용하여 받아온 uri를 이미지로 변환하여 화면에 뿌려준다.
                Glide.with(holder.itemView).
                        load(String.valueOf(list.get(position))).
                        override(200, 200).
                        centerCrop().
                        placeholder(R.drawable.ic_launcher_background).
                        into(holder.gallery_img_item);
                if (LayoutMap.containsKey("List")) {
                    //리스트의 날짜를 표현해준다.
                    File file = new File(String.valueOf(list.get(position)));
                    //이미지파일 이름
                    String Dataname = file.getName();
                    //이미지파일 용량
                    long bytes = file.length();
                    //이미지파일 날짜
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd / HH:mm:ss");
                    holder.ImgData.setText("파일명: " + Dataname + "\n" + "날짜: " + formatter.format(file.lastModified()) + "\n" + "용량: " + bytes + " byte");
                }

        //클릭한 이미지의 uri를 나눔
        holder.cons.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String Check = "";
                holder.checkBox.toggle();

                if (holder.checkBox.isChecked() == true) {
                    CheckedList.add(list.get(position));
                    CheckedIdList.add(Idlist.get(position));
                    //콜백으로 activity에 position 전달
                    Check = "CheckOn";
                    callbackListener.callBack(holder.getAdapterPosition(),Check);
                } else {
                    CheckedList.remove(list.get(position));
                    CheckedIdList.remove(Idlist.get(position));

                    Check = "CheckOff";
                    callbackListener.callBack(holder.getAdapterPosition(),Check);
                }
            }
        });
        //이미지보기
        SubClickEvent submitbtn = new SubClickEvent();
        submit.setOnClickListener(submitbtn);
    }
    class SubClickEvent implements View.OnClickListener{

        @Override
        public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(view.getContext(), Gallery_Detil_MainActivity.class);
                intent.putExtra("CheckOn", CheckedList);
                context.startActivity(intent);
            }
        }
    @Override
    public int getItemCount() {
        return list.size();
    }
}
class Holder extends RecyclerView.ViewHolder{
    ImageView gallery_img_item;
    ConstraintLayout cons;
    TextView ImgData;
    RecyclerView GRv;
    CheckBox checkBox;

    public Holder(@NonNull View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.checktest);
        ImgData = itemView.findViewById(R.id.gallery_ImgData);
        cons = itemView.findViewById(R.id.constraint_item);
        gallery_img_item = itemView.findViewById(R.id.gallery_img_item);
        GRv = itemView.findViewById(R.id.Grid_Recycler_List);
        }
    }






