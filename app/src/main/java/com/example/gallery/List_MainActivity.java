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
    Button delbtn;
    private IntentSender intentSender;
    ArrayList<String> List;
    ArrayList<String> IdList;
    ArrayList<String> imgDelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_main);
        //뒤로기가 버튼
        backBtn = findViewById(R.id.list_Menu_Back);
        backGridButton back = new backGridButton();
        backBtn.setOnClickListener(back);

        Map<String,String> LayoutMap = new HashMap<String ,String>();
        LayoutMap.put("List","List");
        //uri를 담아줄 객체선언
        //리스트 객체선언
        IdList = new ArrayList<>();
        List = new ArrayList<>();
        /*Uri:식별자
            미디어의 파일을 읽어서 uri 변수에 넣는다
            MediaStroe로 파일을 읽으려면 READ 권한이 필요 Manifest에 권한 추가 필요*/
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //문자열 배열에 이미지 파일의 데이터,이미지 파일의 이름을 담아준다
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Images.Media._ID};
        /*  getContentResolver().query (쿼리코드)
            Cursor객체를 통해 찾은 데이터를 확인할수있다.
            select 역활과 같다. */
        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        //데이터를 순회하여 결과를 출력하는 코드
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int columnId = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
        int columnDisplayname = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        int lastIndex;

        while (cursor.moveToNext()) {

            String absolutePathOfImage = cursor.getString(columnIndex);
            String ImgId = cursor.getString(columnId);
            String nameOfFile = cursor.getString(columnDisplayname);
            lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile);
            lastIndex = lastIndex >= 0 ? lastIndex : nameOfFile.length() - 1;
            //순회하여 가져온 uri를 UriList에 담아준다.
            if (!TextUtils.isEmpty(absolutePathOfImage)) {
                List.add(absolutePathOfImage);
                IdList.add(ImgId);
            }
        }
        //Data어댑터 객체 선언후 어뎁터에 url 리스트를 던져준다.
        delbtn = findViewById(R.id.imgDel);
        submit = findViewById(R.id.Submit_Btn);
        Rv = findViewById(R.id.Recycler_List);
        CustomAdapter adapter = new CustomAdapter(IdList, (Map<String, String>) LayoutMap, submit, List);
        Rv.setAdapter(adapter);
        //레이아웃 매니저 리사이클 레이아웃을 리스트 방식으로 표현해준다.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        Rv.setLayoutManager(linearLayoutManager);
        //이미지 삭제버튼
        deleteClickEvent delClick = new deleteClickEvent();
        delbtn.setOnClickListener(delClick);
        //콜백
        adapter.setCallbackListener(this);
    }
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

            System.out.println("@@@@@@@@@ cursor = " + cursor);

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