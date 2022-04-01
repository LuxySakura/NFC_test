package com.example.nfctest.DAGS;

public class Hash {
    static {
        System.loadLibrary("hash");
    }
    public static native String hash (String message);
}
