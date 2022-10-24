package com.example.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gallery.GalleryAdapter.ViewPagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;

public class Gallery_Detil_MainActivity extends FragmentActivity {
    ImageView detilImg;
    ViewPager2 viewPager;
    ArrayList<String> detilList;
    LinearLayout layoutIndicators;

    //ImageView backBtn; //뒤로가기 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detil);

        //뒤로기가 버튼
        /*HashMap<String,String> BackBtnID = new HashMap<String,String>();
        BackBtnID.put("Grid","Grid");
        backBtn = findViewById(R.id.list_Menu_Back);
        new BackButtonItem(BackBtnID);*/


        //클릭한 이미지의 URI를 인텐트로 받아옴
        detilImg = findViewById(R.id.Detil_Img);
        Intent intent = getIntent();
        detilList = (ArrayList<String>) intent.getStringArrayListExtra("detilImg");
        Serializable s = intent.getSerializableExtra("detilImg");

        System.out.println("@@@@@@@@@@@ 상세보기" + detilList);

        layoutIndicators = findViewById(R.id.layoutIndicators);
        viewPager = findViewById(R.id.Detil_View_Pager);
        viewPager.setOffscreenPageLimit(detilList.size());
        viewPager.setAdapter(new ViewPagerAdapter(detilList, this));
    }
}