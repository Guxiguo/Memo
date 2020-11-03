package com.example.memo;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBService extends SQLiteOpenHelper {
    public static final String TABLE = "notes";
    public static final String ID = "_id";
    public static final String USER = "user";
    public static final String TITLE ="title";
    public static final String CONTENT = "content";
    public static final String TIME = "time";
    public static final String PHOTO = "photo";
    public DBService(Context context) {
        super(context,"notepad.db",null,1);
    }

    @Override
    //创建数据库
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+TABLE+"( "+ID+
                " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                USER+" VARCHAR(10) ,"+
                TITLE +" VARCHAR(30) ,"+
                CONTENT + " TEXT , "+
                PHOTO + " TEXT , "+
                TIME + " DATETIME NOT NULL )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
