package com.example.memo;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {
    private static String imagePath=null;
    DBService myDb;
    private Button btnCancel;//取消
    private Button btnSave;//保存
    private EditText titleEditText;//标题
    private  EditText userEditText;//用户
    private EditText contentEditText;//内容
    private TextView timeTextView;//时间
    private ImageView picture;
    private Button chooseFromAlbum;
    private static final int CHOOSE_PHOTO=2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        init();
        //判断时间是否为空
        if(timeTextView.getText().length()==0)
            timeTextView.setText(getTime());
    }

    private void init() {

        //新建一个数据库
        myDb = new DBService(this);
        SQLiteDatabase db = myDb.getReadableDatabase();
        //加入标题框
        titleEditText = findViewById(R.id.et_title);
        userEditText = findViewById(R.id.et_user);
        //加入内容框
        contentEditText = findViewById(R.id.et_content);
        //加入时间框
        timeTextView = findViewById(R.id.edit_time);
        //加入取消按钮
        btnCancel = findViewById(R.id.btn_cancel);
        //加入保存按钮
        btnSave = findViewById(R.id.btn_save);
        picture= findViewById(R.id.et_picture);
        chooseFromAlbum = findViewById(R.id.choose_from_alnum);
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else{
                    openAlbum();
                }
            }
        });
        
        //取消按钮点击事件
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        //保存按钮点击事件
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase db = myDb.getWritableDatabase();
                ContentValues values = new ContentValues();


                //得到几个文本框中的内容
                String title= titleEditText.getText().toString();
                String user= userEditText.getText().toString();
                String content=contentEditText.getText().toString();
                String time= timeTextView.getText().toString();

                //判断标题不能为空
                if("".equals(titleEditText.getText().toString().replaceAll(" ",""))){
                    Toast.makeText(EditActivity.this,"标题不能为空",Toast.LENGTH_LONG).show();
                    return;
                }

                else if (title.length()>10){
                    Toast.makeText(EditActivity.this,"标题过长",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if("".equals(userEditText.getText().toString().replaceAll(" ",""))){
                    Toast.makeText(EditActivity.this,"用户不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                else if (user.length()>5){
                    Toast.makeText(EditActivity.this,"用户名过长",Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断内容不能为空
                else if("".equals(contentEditText.getText().toString().replaceAll(" ",""))) {
                    Toast.makeText(EditActivity.this,"内容不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                //把文本框中的相应内容加到数据库中
                values.put(DBService.TITLE,title);
                values.put(DBService.USER,user);
                values.put(DBService.CONTENT,content);
                values.put(DBService.TIME,time);
                values.put(DBService.PHOTO,imagePath);
                db.insert(DBService.TABLE,null,values);
                Toast.makeText(EditActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent);
                //关闭数据库
                db.close();
            }
        });
    }

    //获取当前时间
    private String getTime() {
        //时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //获取系统事件
        Date date = new Date(System.currentTimeMillis());
        String str = sdf.format(date);
        return str;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;

        }
    }
    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data){
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }
        else if ("cotent".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }
    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath){
        if (imagePath!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        }
        else{
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }
                else{
                    Toast.makeText(this,"YOU denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }




}

