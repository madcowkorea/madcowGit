package com.example.gallery;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.GalleryAdapter.CustomAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class List_MainActivity extends AppCompatActivity implements CallbackListener{
    ImageView backBtn; //뒤로가기 버튼
    RecyclerView Rv;
    Button submit;
    Button delbtn; // 이미지 삭제버튼
    Map<String, String> LayoutMap;
    ArrayList<String> imgDelList = new ArrayList<>();
    ArrayList<String> List;
    ArrayList<String> IdList;
    private IntentSender intentSender;
    int columnIndex ;
    int columnId ;
    int columnDisplayname ;
    int lastIndex;
    private String path = "";
    Cursor cursor;
    Uri uri;
    CustomAdapter adapter;
    String[] projection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_main);
        //뒤로기가 버튼
        backBtn = findViewById(R.id.list_Menu_Back);
        backGridButton back = new backGridButton();
        backBtn.setOnClickListener(back);

        LayoutMap = new HashMap<String, String>();
        LayoutMap.put("List","ListActivity");
        //uri를 담아줄 객체선언
        //리스트 객체선언
        IdList = new ArrayList<>();
        List = new ArrayList<>();

        new Thread() {
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                } else {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Images.Media._ID};

                cursor = getContentResolver().query(
                        uri,
                        projection,
                        null,
                        null,
                        MediaStore.MediaColumns.DATE_ADDED + " desc");

                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                columnId = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
                columnDisplayname = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);

                while (cursor.moveToNext()) {
                    String absolutePathOfImage = cursor.getString(columnIndex);
                    String ImgId = cursor.getString(columnId);
                    String nameOfFile = cursor.getString(columnDisplayname);
                    lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile);
                    lastIndex = lastIndex >= 0 ? lastIndex : nameOfFile.length() - 1;
                    //순회하여 가져온 uri를 UriList에 담아준다.
                    if (!TextUtils.isEmpty(ImgId)) {
                        List.add(absolutePathOfImage);
                        IdList.add(ImgId);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

        //Data어댑터 객체 선언후 어뎁터에 url 리스트를 던져준다.
        delbtn = findViewById(R.id.imgDel);
        submit = findViewById(R.id.Submit_Btn);
        Rv = findViewById(R.id.Recycler_List);

        //레이아웃 매니저 리사이클 레이아웃을 리스트 방식으로 표현해준다.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        Rv.setLayoutManager(linearLayoutManager);
        //이미지 삭제버튼
        deleteClickEvent delClick = new deleteClickEvent();
        delbtn.setOnClickListener(delClick);
        //콜백
    }

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            //커스텀 어댑터 객체 선언후 어뎁터에 url 리스트를 던져준다.
            adapter = new CustomAdapter(IdList, (Map<String, String>) LayoutMap, submit, List);
            Rv.setAdapter(adapter);
            //콜백
            adapter.setCallbackListener(List_MainActivity.this);
        }
    };

    @Override
    public void callBack(int pos, String Check){
        if(Check.equals("CheckOn")) {
            imgDelList.add(IdList.get(pos));
            System.out.println("CheckOn ==" + imgDelList);
        }else if(Check.equals("CheckOff")){
            imgDelList.remove(IdList.get(pos));
            System.out.println("CheckOff ==" + imgDelList);
        }
    }

    class deleteClickEvent implements View.OnClickListener {

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
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 0);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id);
                if(imgDelList.contains(String.valueOf(id))){
                    getContentResolver().delete(deleteUri, null, null);
                    //삭제된걸 db에 알림
                    getContentResolver().notifyChange(deleteUri,null);
                    //새로고침
                    this.recreate();
                }
            }
            //안드로이드 Q버전 이상에서는 에러로 넘어온 intent를 사용해 사진삭제 권한을 받아옴!!!
            //startIntentSenderForResult()를 호출하여 팝업을 띄워 삭제처리
        } catch (SecurityException securityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                RecoverableSecurityException recoverableSecurityException;
                if (securityException instanceof RecoverableSecurityException) {
                    recoverableSecurityException = (RecoverableSecurityException) securityException;
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