package com.example.nfctest.units;

/**
 * 记录了不同客户端的不同行为的代码
 */

public class Constant {
    // 展示行程信息
    public static final int SHOW_TOKEN = 0b0000;
    // 设置交易
    public static final int SET_TRANSACTION = 0b0001;
    // 接收交易
    public static final int RECEIVE_TRANSACTION = 0b0010;
    // 交易代码
    public static final int AMOUNT = 0b0;
    // 健康情况
    public static final int HEALTH = 0b1;
}
