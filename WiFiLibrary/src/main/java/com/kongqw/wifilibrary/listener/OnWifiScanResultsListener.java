package com.kongqw.wifilibrary.listener;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by kqw on 2016/8/4.
 * WIFI扫描结果的回调接口
 */
public interface OnWifiScanResultsListener {
    // 扫描结果
    void onScanResults(List<ScanResult> scanResults);
}