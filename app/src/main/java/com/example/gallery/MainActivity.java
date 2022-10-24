package com.example.gallery;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout gridLayout;
    ConstraintLayout listLayout;

     final String[] per = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    //권한창을 띄우는 요소
    ActivityResultLauncher<String[]> location;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //거부되어 있는 권한들을 사용자에게 확인한다
            requestPermissions(per,0);
        }
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