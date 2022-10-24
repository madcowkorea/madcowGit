
package com.example.gallery;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.GalleryAdapter.AutoCastMap;
import com.example.gallery.GalleryAdapter.DataListAdapter;
import com.example.gallery.Handler.BaseActivity;

import java.util.ArrayList;

public class List_MainActivity extends BaseActivity implements CallbackListener{

    ImageView backBtn; //뒤로가기 버튼
    RecyclerView Rv;
    Button detil; //상세보기 버튼
    Button delbtn; // 이미지 삭제버튼

    private IntentSender intentSender;

    Uri uri;

    final int SUCCESS = 1 ; //리스트 성공시

    AutoCastMap dataMap;
    ArrayList<AutoCastMap> dataList_tmp;
    DataListAdapter dataListAdapter;

    ArrayList<String> imgDelList = new ArrayList<>();
    ArrayList<String> detilList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_main);
        //뒤로기가 버튼
        delbtn = findViewById(R.id.imgDel);
        detil = findViewById(R.id.Submit_Btn);
        Rv = findViewById(R.id.Recycler_List);

        backBtn = findViewById(R.id.list_Menu_Back);
        List_MainActivity.backGridButton back = new List_MainActivity.backGridButton();
        backBtn.setOnClickListener(back);

        //이미지상세보기 리스너
        List_MainActivity.detilImgClickEvent submitbtn = new List_MainActivity.detilImgClickEvent();
        detil.setOnClickListener(submitbtn);


        dataList_tmp = new ArrayList<>();
        dataListAdapter = new DataListAdapter(List_MainActivity.this, dataList_tmp);
        Rv.setAdapter(dataListAdapter);

        dataListAdapter.setCallbackListener(List_MainActivity.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        Rv.setLayoutManager(linearLayoutManager);

        //이미지 삭제버튼
        List_MainActivity.deleteClick delc = new List_MainActivity.deleteClick();
        delbtn.setOnClickListener(delc);

        //리스트 아이템 담음 (list는 Thread로)
        new Thread() {
            @Override
            public void run() {
                ArrayList<AutoCastMap> dataList = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                } else {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                String[] projection = {
                        MediaStore.MediaColumns.DATA,
                        MediaStore.MediaColumns.DISPLAY_NAME,
                        MediaStore.MediaColumns._ID,
                        MediaStore.MediaColumns.DATE_ADDED,
                        MediaStore.MediaColumns.TITLE,
                        MediaStore.MediaColumns.SIZE,
                };
                Cursor cursor = getContentResolver().query(
                        uri,
                        projection,
                        null,
                        null,
                        MediaStore.MediaColumns.DATE_ADDED + " desc"
                );
               int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
               int columnId = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
               int columnDisplayname = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
               int columnTITLE = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE);
               int columnDATE = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED);
               int columnSIZE = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE);


                while (cursor.moveToNext()) {
                    String absolutePathOfImage = cursor.getString(columnIndex);
                    String ImgId = cursor.getString(columnId);
                    String nameOfFile = cursor.getString(columnDisplayname);
                    String columnTitle = cursor.getString(columnTITLE);
                    String columnDate = cursor.getString(columnDATE);
                    String columnSize = cursor.getString(columnSIZE);

                    int lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile);
                    lastIndex = lastIndex >= 0 ? lastIndex : nameOfFile.length() - 1;

                    dataMap = new AutoCastMap();
                    dataMap.put("imgUri", Uri.parse(absolutePathOfImage));
                    dataMap.put("imgId", ImgId);
                    dataMap.put("imgChecked", false);
                    dataMap.put("imgTitle",columnTitle);
                    dataMap.put("imgDate",columnDate);
                    dataMap.put("imgSize",columnSize);

                    dataList.add(dataMap);
                }

                Message msg = mHandler.obtainMessage(SUCCESS, dataList);
                mHandler.sendMessage(msg);

            }
        }.start();
    }


    @Override
    public void handleMessage(@NonNull Message msg) {

        if(msg.what == SUCCESS ) {
            for (int i = 0; i < ((ArrayList<AutoCastMap>) msg.obj).size(); i++) {
                dataList_tmp.add(((ArrayList<AutoCastMap>) msg.obj).get(i));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataListAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void callBack(int pos, boolean isChecked) {

        if(isChecked == true){
            dataList_tmp.get(pos).put("imgChecked", true);
            imgDelList.add(String.valueOf(dataList_tmp.get(pos).get("imgId")));
            detilList.add(String.valueOf(dataList_tmp.get(pos).get("imgUri")));
        }else {
            dataList_tmp.get(pos).put("imgChecked", false);
            imgDelList.remove(String.valueOf(dataList_tmp.get(pos).get("imgId")));
            detilList.remove(String.valueOf(dataList_tmp.get(pos).get("imgUri")));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataListAdapter.notifyDataSetChanged();
            }
        });
    }

    //상세보기
    class detilImgClickEvent implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(view.getContext(), Gallery_Detil_MainActivity.class);
            intent.putExtra("detilImg",detilList);
            context.startActivity(intent);
        }
    }
    //삭제하기
    class deleteClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                deleteImg();
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    //뒤로가기 버튼 클릭시 메인으로 이동
    class backGridButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent backbtn = new Intent(List_MainActivity.this, MainActivity.class);
            startActivity(backbtn);
        }
    }

    //이미지 삭제 메서드
    private void deleteImg() throws IntentSender.SendIntentException {
        try {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME,MediaStore.MediaColumns._ID};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, 0);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id);
                if(imgDelList.contains(String.valueOf(id))){
                    getContentResolver().delete(deleteUri, null, null);
                    //새로고침
                    this.recreate();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dataListAdapter.notifyDataSetChanged();
                        }
                    });
                    Toast.makeText(getApplicationContext(),"이미지가 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                }
            }

            //안드로이드 Q버전 이상에서는 에러로 넘어온 intent를 사용해 사진삭제 권한을 받아옴!!!
            //startIntentSenderForResult()를 호출하여 팝업을 띄워 삭제처리
        } catch (SecurityException securityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                RecoverableSecurityException recoverableSecurityException;
                if (securityException instanceof RecoverableSecurityException) {
                    recoverableSecurityException =
                            (RecoverableSecurityException) securityException;
                } else {
                    throw new RuntimeException(
                            securityException.getMessage(), securityException);
                }
                intentSender = recoverableSecurityException.getUserAction().getActionIntent().getIntentSender();
                IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
                deleteVideoForQResultLauncher.launch(request);

            } else {
                throw new RuntimeException(
                        securityException.getMessage(), securityException);
            }
        }
    }

    ActivityResultLauncher<IntentSenderRequest> deleteVideoForQResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                    }
                }
            });
}
