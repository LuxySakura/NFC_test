package com.example.nfctest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfctest.units.Constant;
import com.example.nfctest.units.NfcUnit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnShowToken, btnSetTransaction, btnReceiveTransaction;
    private NfcUnit nfcUnit;
    private String TAG = MainActivity.class.getSimpleName();
    private List<View> btns;
    private int mode;
    private DialogFragment iDialog;
    private String msgWrite = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUser();
        nfcUnit = new NfcUnit(this); // 创建一个NFC读写实例以供后续操作
    }

    public void onPause() {
        super.onPause();
        nfcUnit.disableForegroundDispatch();
    } // 关闭前台调度系统

    public void onResume() {
        super.onResume();
        nfcUnit.enableForegroundDispatch();
    } // 开启前台调度系统

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switch (mode) {
            // 此时模式为展示信息模式
            case Constant.SHOW_TOKEN:
                try {
                    nfcUnit.showToken(intent); // 出示用户健康信息
                } catch (IOException | FormatException e) {
                    e.printStackTrace();
                }
                showTokenDialog(); //
                break;
            // 此时模式为接收交易模式
            case Constant.RECEIVE_TRANSACTION:
                nfcUnit.receiveTransaction();
                receiveTransactionDialog();
                break;
            // 此时模式为发起交易模式
            case Constant.SET_TRANSACTION:
                nfcUnit.setTransaction();
                setTransactionDialog();
                break;
        }
    }

    private void initUser() {
        btnShowToken = findViewById(R.id.btnShowToken);
        btnShowToken.setOnClickListener(this);
        btnSetTransaction = findViewById(R.id.btnSetTransaction);
        btnSetTransaction.setOnClickListener(this);
        btnReceiveTransaction = findViewById(R.id.btnReceiveTransaction);
        btnReceiveTransaction.setOnClickListener(this);
    } // 初始化用户界面

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnShowToken:
                mode = Constant.SHOW_TOKEN;
                showTokenDialog();
                break;
            case R.id.btnSetTransaction:
                mode = Constant.SET_TRANSACTION;
                setTransactionDialog();
                break;
            case R.id.btnReceiveTransaction:
                mode = Constant.RECEIVE_TRANSACTION;
                receiveTransactionDialog();
                break;
        }
    } // 设置点击按键

    private void showTokenDialog() {

    }

    private void setTransactionDialog() {

    }

    private void receiveTransactionDialog() {

    }
}