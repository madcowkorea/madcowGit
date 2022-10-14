package com.example.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class BackButtonItem extends AppCompatActivity{
    HashMap<String, String> backBtnId;
    ImageView backBtn;

    BackButtonItem(HashMap<String, String> backBtnId){
        this.backBtnId = backBtnId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backBtn.findViewById(R.id.list_Menu_Back);

        if(backBtnId.containsKey("Grid")){
            setContentView(R.layout.activity_gallery_detil);
            GridBackBtn gridBack = new GridBackBtn();
            backBtn.setOnClickListener(gridBack);
        }

    }
    class GridBackBtn implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(view.getContext(), Grid_MainActivity.class);
            context.startActivity(intent);
        }
    }
}
