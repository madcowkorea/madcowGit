package com.example.gallery;

import android.annotation.SuppressLint;
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
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.GalleryAdapter.CustomAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Grid_MainActivity extends AppCompatActivity implements CallbackListener{

    ImageView backBtn; //뒤로가기 버튼
    RecyclerView Rv;
    Button submit;
    Button delbtn; // 이미지 삭제버튼
    Map<String, String> LayoutMap ;
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

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_main);
        //뒤로기가 버튼
        delbtn = findViewById(R.id.imgDel);
        submit = findViewById(R.id.Submit_Btn);
        Rv = findViewById(R.id.Grid_Recycler_List);

        backBtn = findViewById(R.id.list_Menu_Back);
        backGridButton back = new backGridButton();
        backBtn.setOnClickListener(back);

        IdList = new ArrayList<>();
        List = new ArrayList<>();

        LayoutMap = new HashMap<String, String>();
        LayoutMap.put("Grid", "GridList");

        //데이터를 순회하여 결과를 출력하는 코드
                /*Uri:식별자
               미디어의 파일을 읽어서 uri 변수에 넣는다
               MediaStroe로 파일을 읽으려면 READ 권한이 필요 Manifest에 권한 추가 필요*//*
                //문자열 배열에 이미지 파일의 데이터,이미지 파일의 이름을 담아준다
                getContentResolver().query (쿼리코드)
               Cursor객체를 통해 찾은 데이터를 확인할수있다.
               select 역활과 같다. */
        new Thread() {
            public void run() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                } else {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                //projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Images.Media._ID};
                String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns._ID};

                cursor = getContentResolver().query(
                        uri,
                        projection,
                        null,
                        null,
                        MediaStore.MediaColumns.DATE_ADDED + " desc");

                Log.e("COUNT " , String.valueOf(cursor.getCount()));



             /*   columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
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
                }*/
                handler.sendEmptyMessage(0);
           }
        }.start();

        //레이아웃 매니저 리사이클 레이아웃을 그리드 방식으로 표현해준다.
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        Rv.setLayoutManager(gridLayoutManager);

        //이미지 삭제버튼
        deleteClick delc = new deleteClick();
        delbtn.setOnClickListener(delc);
    }

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            //커스텀 어댑터 객체 선언후 어뎁터에 url 리스트를 던져준다.
            adapter = new CustomAdapter(IdList, (Map<String, String>) LayoutMap, submit, List);
            Rv.setAdapter(adapter);
            //콜백
            adapter.setCallbackListener(Grid_MainActivity.this);
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
    //이미지 삭제버튼
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
            Intent backbtn = new Intent(Grid_MainActivity.this, MainActivity.class);
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
                    System.out.println(deleteUri);
                   if(imgDelList.contains(String.valueOf(id))){
                       getContentResolver().delete(deleteUri, null, null);
                       //삭제된걸 db에 알림
                       getContentResolver().notifyChange(deleteUri,null);
                       //새로고침
                       this.recreate();
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


