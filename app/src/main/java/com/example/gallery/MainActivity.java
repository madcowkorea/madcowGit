package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout gridLayout;
    ConstraintLayout listLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //그리드형식 레이아웃 클릭
        gridLayout = findViewById(R.id.grid_Layout);
        gridLayoutClick gridClick = new gridLayoutClick();
        gridLayout.setOnClickListener(gridClick);
        //리스트형식 레이아웃 클릭
        listLayout = findViewById(R.id.list_Layout);
        listLayoutClick listClick = new listLayoutClick();
        listLayout.setOnClickListener(listClick);
        //---------------------------------------------//
    }

    class gridLayoutClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            //그리드형식 갤러리로 화면전환
            Intent gridintent = new Intent(MainActivity.this,Grid_MainActivity.class);
            startActivity(gridintent);
        }
    }
    class listLayoutClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            //리스트형식 갤러리로 화면전환
            Intent listintent = new Intent(MainActivity.this, List_MainActivity.class);
            startActivity(listintent);
        }
    }
}