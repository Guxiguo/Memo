package com.example.memo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowActivity extends AppCompatActivity {
    private Button btnSave;
    private Button btnCancel;
    private TextView showTime;
    private EditText showContent;
    private EditText showTitle;
    private EditText showUser;
    private ImageView showpicture;
    private Values value;
    DBService myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        init();
    }

    public void init() {
        myDb = new DBService(this);
        btnCancel = findViewById(R.id.show_cancel);
        btnSave = findViewById(R.id.show_save);
        showTime = findViewById(R.id.show_time);
        showTitle = findViewById(R.id.show_title);
        showContent = findViewById(R.id.show_content);
        showUser = findViewById(R.id.show_user);
        showpicture = findViewById(R.id.show_picture);

        Intent intent = this.getIntent();
        if (intent != null) {
            value = new Values();
            //从数据库中找到相应的信息
            value.setTime(intent.getStringExtra(DBService.TIME));
            value.setTitle(intent.getStringExtra(DBService.TITLE));
            value.setUser(intent.getStringExtra(DBService.USER));
            value.setContent(intent.getStringExtra(DBService.CONTENT));
            value.setPhoto(intent.getStringExtra(DBService.PHOTO));
            value.setId(Integer.valueOf(intent.getStringExtra(DBService.ID)));
            //把获得的东西设置到相应的位置
            showTime.setText(value.getTime());
            showTitle.setText(value.getTitle());
            showUser.setText(value.getUser());
            showContent.setText(value.getContent());
            Uri u = Uri.parse(value.getPhoto());
            showpicture.setImageURI(u);

        }


        //按钮点击事件
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读数据库
                SQLiteDatabase db = myDb.getWritableDatabase();
                ContentValues values = new ContentValues();
                //获得各个信息
                String content = showContent.getText().toString();
                String title = showTitle.getText().toString();
                String user = showUser.getText().toString();
                //写进数据库

                values.put(DBService.TIME, getTime());
                values.put(DBService.USER, user);
                values.put(DBService.TITLE,title);
                values.put(DBService.CONTENT,content);
                db.update(DBService.TABLE,values,DBService.ID+"=?",new String[]{value.getId().toString()});
                Toast.makeText(ShowActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                db.close();
                Intent intent = new Intent(ShowActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        //取消按钮的点击事件
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取文本框内容
                final String content = showContent.getText().toString();
                final String title = showTitle.getText().toString();
                final String user = showUser.getText().toString();
                new AlertDialog.Builder(ShowActivity.this)
                        .setTitle("提示框")
                        .setMessage("是否保存当前内容?")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SQLiteDatabase db = myDb.getWritableDatabase();
                                        ContentValues values = new ContentValues();
                                        values.put(DBService.TIME, getTime());
                                        values.put(DBService.TITLE,title);
                                        values.put(DBService.USER,user);
                                        values.put(DBService.CONTENT,content);
                                        //修改数据库
                                        db.update(DBService.TABLE,values,DBService.ID+"=?",new String[]{value.getId().toString()});
                                        Toast.makeText(ShowActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                                        db.close();
                                        Intent intent = new Intent(ShowActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                })
                        .setNegativeButton("no",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(ShowActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                }).show();
            }
        });
    }

    String getTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

}
