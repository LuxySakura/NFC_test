package com.example.nfctest.units;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.nfctest.DAGS.HealthChain;
import com.example.nfctest.R;
import com.example.nfctest.data.DataManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class NfcUnit {
    private Context context;
    private static final String TAG = "NfcUnit";
    private NfcAdapter mNfcAdapter;
    private IntentFilter[] mIntentFilter = null;
    private PendingIntent mPendingIntent = null;
    private String[][] mTechList = null;
    private final Activity activity;

    public NfcUnit(Activity activity) {
        this.activity = activity;
        check();
    }

    /**
     * nfc检测设备是否支持NFC功能以及设备是否开启NFC功能
     */
    private void check() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null) {
            ToastUnit.showShort("设备不支持NFC功能!");
        } else {
            if (!mNfcAdapter.isEnabled()) {
                showSettingDailog();
            } else {
                init();
            }
        }
    }

    /**
     * 初始化nfc设置
     */
    private void init() {
        Intent intent = new Intent(activity, activity.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        //intentFilter过滤----ndef
        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            //文本类型
            ndefFilter.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        //intentFilter过滤----非ndef
        IntentFilter techFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        //intentFilter过滤器列表
        mIntentFilter = new IntentFilter[]{ndefFilter, techFilter};
        //匹配的数据格式列表
        mTechList = new String[][]{
                {MifareClassic.class.getName()},
                {NfcA.class.getName()},
                {Ndef.class.getName()},
                {NdefFormatable.class.getName()}};
    }

    /**
     * Nfc监听intent
     */
    public void enableForegroundDispatch() {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            mNfcAdapter.enableForegroundDispatch(activity, mPendingIntent, mIntentFilter, mTechList);
        }
    } // 使用前台调度系统

    /**
     * 取消监听Nfc
     */
    public void disableForegroundDispatch() {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            mNfcAdapter.disableForegroundDispatch(activity);
        }
    }


    /**
     * 打开权限弹窗
     */
    private void showSettingDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity); // 创建一个对话框
        builder.setMessage("是否跳转到设置页面打开NFC功能");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 进入设置系统应用权限界面
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                activity.startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }


    // 实现核验健康信息的底层逻辑
    public void showToken(Intent intent) throws IOException, FormatException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        // 获取码颜色并将其转化为字节数组
        String color = HealthToken.acquireTokenColor();
        byte[] colorBytes = color.getBytes(StandardCharsets.UTF_8);
        byte[] language = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
        // 初始化状态字节
        int utfBit = 0;
        char status = (char) (utfBit + language.length);

        byte[] data = new byte[1 + language.length + colorBytes.length]; //创建存储payload的字节数组
        data[0] = (byte) status; // 设置状态字节
        System.arraycopy(language, 0, data, 1, language.length); // 设置语言编码
        System.arraycopy(colorBytes, 0, data, 1+ language.length, colorBytes.length); // 设置写入文本

        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] {record,
        NdefRecord.createApplicationRecord("com.example.nfctest")});
        //转换成字节获得大小
        int size = ndefMessage.toByteArray().length;
        //2.判断NFC标签的数据类型（通过Ndef.get方法）
        Ndef ndef = Ndef.get(tag);
        //判断是否为NDEF标签
        if (ndef != null) {
            ndef.connect();
            //判断是否支持可写
            if (!ndef.isWritable()) {
                return;
            }
            //判断标签的容量是否够用
            if (ndef.getMaxSize() < size) {
                return;
            }
            //3.写入数据
            ndef.writeNdefMessage(ndefMessage);
        } else {
            //当我们买回来的NFC标签是没有格式化的，或者没有分区的执行此步
            //Ndef格式类
            NdefFormatable format = NdefFormatable.get(tag);
            //判断是否获得了NdefFormatable对象，有一些标签是只读的或者不允许格式化的
            if (format != null) {
                //连接
                format.connect();
                //格式化并将信息写入标签
                format.format(ndefMessage);
            }
        }

        HealthChain.updateLocus();
    }

    public void setTransaction(Intent intent, float amount, String message) throws IOException, FormatException {
        context.getApplicationContext();
        String my_add = context.getString(R.string.address); // 获取用户该账户链上唯一地址
        // 请求发送交易信息
        sendNFCMessage(0b0, my_add, message, 0, intent);
        // 如果用户成功接收并同意则会收到来自用户的信息
        if (receiveNFCMessage() != null) {
            // 将收到的信息返回打包成行程
            Locus new_locus = receiveTransaction();
            // 将新行程更新至本地行程集
            Locus.updateLocusSet(new_locus);
            // 将行程上链
            HealthChain.updateLocus();
        } else {
           ToastUnit.showLong("请求交易失败");
        }
    }

    public void receiveTransaction(int type, String sender_add, String message, float amount, Intent intent) throws IOException, FormatException {
        switch (type) {
            case 0b0: {

            }
            case 0b1: {

            }
        }
    }

    private void sendNFCMessage(int type, String sender_add, String message, float amount, Intent intent) throws IOException, FormatException {
        switch (type) {
            case 0b0: {

            } // 发送交易信息

            case 0b1: {

            } // 发送健康情况
        }
    }

    private void receiveNFCMessage()
}