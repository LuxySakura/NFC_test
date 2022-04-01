package com.example.nfctest.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataManager {
    private Context context;
    private static DataManager instance;

    private SQLiteDatabase mutDatabase;

    public DataManager(Context context) {
        this.context = context;
        UserData userData = new UserData(context, 1); // 创建新的数据库
        mutDatabase = userData.getWritableDatabase(); // 得到SQLiteDatabase对象，以便对数据库表进行操作
    } // SQLiteDatabase可以对数据库表进行操作

    public static DataManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager(context);
                }
            }
        }
        return instance;
    }

    public void insert() {
        ContentValues contentValues = new ContentValues();

        // .put的第一个参数是列名，第二个是值
        byte _byte = Byte.MAX_VALUE;
        contentValues.put("_byte", _byte);

        long _long = Long.MAX_VALUE;
        contentValues.put("_long", _long);

        String _text = "字符串";
        contentValues.put("_text", _text);

        short _short = Short.MAX_VALUE;
        contentValues.put("_short", _short);

        int _int = Integer.MAX_VALUE;
        contentValues.put("_int", _int);

        float _float = Float.MAX_VALUE;
        contentValues.put("_float", _float);

        double _double = Double.MAX_VALUE;
        contentValues.put("_double", _double);

        boolean _boolean = true;
        contentValues.put("_boolean", _boolean);

        byte[] _byteArr = {Byte.MIN_VALUE, Byte.MAX_VALUE};
        contentValues.put("_blob", _byteArr);

        mutDatabase.insert("user_data", null, contentValues);

    } // 增

    public void delete() {
        mutDatabase.delete("user_data", "_int = ?",
                new String[]{Integer.MAX_VALUE + ""});
        // whereClause -> 删数据的条件
        // Integer.MAX_VALUE 为待删除的值
    } // 减

    public void update() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_text", "修改后的字符串");
        mutDatabase.update("user_data", contentValues,
                "_text = ?", new String[]{"字符串"});
    }

    public String select() {
        Cursor cursor = mutDatabase.query("user_data", null, null,
                null, null, null, null, null);
        /*
         * columns：被查询的列名 new String[]{"_byte", "_long"}表示查询"_byte", "_long"两列
         * selection、selectionArgs为条件查询，例："_byte = ?", new String[]{"127"}
         * groupBy：去掉重复的内容
         * having：配合groupBy进行条件筛选
         * orderBy：排序某个字段
         * limit：限制查询数量
         */

        int position = cursor.getPosition();

        String result = "";

        while (cursor.moveToNext()) {

            byte _byte = (byte) cursor.getShort(cursor.getColumnIndex("_byte"));
            long _long = cursor.getLong(cursor.getColumnIndex("_long"));
            String _text = cursor.getString(cursor.getColumnIndex("_text"));
            short _short = cursor.getShort(cursor.getColumnIndex("_short"));
            int _int = cursor.getInt(cursor.getColumnIndex("_int"));
            float _float = cursor.getFloat(cursor.getColumnIndex("_float"));
            double _double = cursor.getDouble(cursor.getColumnIndex("_double"));
            boolean _boolean = cursor.getInt(cursor.getColumnIndex("_boolean")) == 1;
            byte[] _byteArr = cursor.getBlob(cursor.getColumnIndex("_blob"));

            result += String.format("_byte = %s, _long = %s, _text = %s, _short = %s, " +
                            "_int = %s, _float = %s, _double = %s, _boolean = %s, _byteArr = %s",
                    _byte, _long, _text, _short, _int, _float, _double, _boolean, _byteArr) + "\n";

        }

        return result;

    }

}
