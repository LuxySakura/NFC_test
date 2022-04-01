package com.example.nfctest.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nfctest.units.Constant;

public class UserData extends SQLiteOpenHelper {
    public static final String db_name = "user.db"; // 此为数据库名称

    public UserData(Context context, int version) {
        super(context, db_name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user_data (" +
                " _byte byte," +
                " _boolean boolean," +
                " _long long," +
                " _text text," +
                " _short short," +
                " _int int," +
                " _float float," +
                " _double double," +
                " _blob blob" +
                ")");
    } // 创建一个名为“user_data”的表

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    } // 更新数据库
}
