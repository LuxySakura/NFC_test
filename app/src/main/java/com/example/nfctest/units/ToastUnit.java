package com.example.nfctest.units;

import android.content.Context;
import android.widget.Toast;

public class ToastUnit {
    private static Toast toast;

    private static Context getContext() {
        return MyApplication.getContext();
    }

    public static void showShort(int resId) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT);
        toast.show();
    } // 显示短时间出现的Toast通知，显示字符串


    public static void showShort(String msg) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    } // 显示短时间出现的Toast通知，显示id

    public static void showLong(int resId) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(getContext(), resId, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showLong(String msg) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(getContext(), msg, Toast.LENGTH_LONG);
        toast.show();
    }
}
