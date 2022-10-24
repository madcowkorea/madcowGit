package com.example.gallery.Handler;

import android.os.Bundle;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements MyHandelerInterface {

    protected String LOGTAG = this.getClass().getSimpleName();

    protected String globalMsg = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected final MyHandler mHandler = new MyHandler(this);


    @Override
    public void handleMessage(Message msg) {
    }
}
