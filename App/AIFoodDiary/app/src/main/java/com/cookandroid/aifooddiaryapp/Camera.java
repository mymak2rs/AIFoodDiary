package com.cookandroid.aifooddiaryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Camera extends AppCompatActivity {
    File file;
    ImageButton btn_camera,btn_handwrite;
    String mCurrentPhotoPath;
    TextView tv_AddFood;
    String date,meal;
    String flag="camera";
    final private static String TAG = "CAMERA";
    Intent intent_c;
    Intent intent_h;
    ListView lv_recommend;
    ArrayList<list_recommend_item> mitems = new ArrayList<>();
    Adapter_Recommed adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        btn_camera=findViewById(R.id.imageButton2);
        btn_handwrite=findViewById(R.id.imageButton);
        File sdcard = Environment.getExternalStorageDirectory();
        file= new File(sdcard,"capture.jpg");
        tv_AddFood=findViewById(R.id.tv_AddFood);
        lv_recommend=findViewById(R.id.lv_recommend);

        //어댑터 연결
        adapter=new Adapter_Recommed(this,mitems);
        lv_recommend.setAdapter(adapter);
        for(int i=0; i<3; i++){
            list_recommend_item item = new list_recommend_item();
            item.setKacl("2300kcal");
            item.setCarbo("23");
            item.setProtein("12");
            item.setFat("0");
            mitems.add(item);
            adapter.setItem(mitems);
        }





        Intent intent_r = getIntent();
        if(intent_r!=null){
            meal=intent_r.getStringExtra("meal");
            date=intent_r.getStringExtra("date");
            tv_AddFood.setText(date+" 식단추가");
        }
        //카메라로 갈 인텐트와 수기작성으로 갈 인텐트 선언
        intent_c= new Intent(Camera.this,Add_Camera.class);
        intent_h= new Intent(Camera.this,Add_HandWrite.class);
        //다음 엑티비티에도 날짜 정보 끼니 정보 전달
        //카메라 엑티비티
        intent_c.putExtra("meal",meal);
        intent_c.putExtra("date",date);
        //수기작성 엑티비티
        intent_h.putExtra("meal",meal);
        intent_h.putExtra("date",date);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            }
            else {
                Log.d(TAG, "권한 설정 요청"); requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }


        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        btn_handwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent_h);
            }
        });


    }





    private void dispatchTakePictureIntent() {
        PackageManager pm= this.getPackageManager();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(pm) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            } if(photoFile != null) {
                //여기서부터 고쳐야함
                Uri photoURI = FileProvider.getUriForFile(this, "com.cookandroid.aifooddiaryapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 101);
            }
        }
    }



    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // 카메라 촬영을 하면 이미지뷰에 사진 삽입

        if(requestCode == 101 && resultCode == Activity.RESULT_OK) {
            /////////////////////////////////
            // 학습모델로 이미지 보내는 코드/////////
            /////////////////////////////////

            //사진 파일명을 다음 엑티비티로 넘겨준다
            intent_c.putExtra("file_path",mCurrentPhotoPath);
            intent_c.putExtra("flag",flag);


            //사진촬영이 완료되었을 경우 Add_Camera로 이동
            startActivity(intent_c);



        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = date+meal;
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}