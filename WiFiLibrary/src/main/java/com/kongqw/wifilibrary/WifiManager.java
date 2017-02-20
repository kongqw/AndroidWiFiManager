package com.kongqw.wifilibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;


/**
 * Created by kqw on 2016/8/2.
 * WifiManager : https://developer.android.com/reference/android/net/wifi/WifiManager.html
 * WifiConfiguration : https://developer.android.com/reference/android/net/wifi/WifiConfiguration.html
 * ScanResult : https://developer.android.com/reference/android/net/wifi/ScanResult.html
 * WifiInfo : https://developer.android.com/reference/android/net/wifi/WifiInfo.html
 * Wifi管理
 */
public class WiFiManager extends BaseWiFiManager {

    private static final String TAG = "WiFiManager";

    public WiFiManager(Context context) {
        super(context);
    }

    /**
     * 打开Wifi
     */
    public void openWifi() {
        if (!isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭Wifi
     */
    public void closeWifi() {
        if (isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 连接到开放网络
     *
     * @param ssid SSID
     */
    public void connectOpenNetwork(@NonNull String ssid) {
        // 获取networkId
        int networkId = setOpenNetwork(ssid);
        if (-1 != networkId) {
            // 保存配置
            saveConfiguration();
            // 连接网络
            enableNetwork(networkId);
        } else {
            // TODO 错误
        }
    }

    public void connectWEPNetwork(@NonNull String ssid, @NonNull String password) {
        // 获取networkId
        int networkId = setWEPNetwork(ssid, password);
        if (-1 != networkId) {
            // 保存配置
            saveConfiguration();
            // 连接网络
            enableNetwork(networkId);
        } else {
            // TODO 错误
        }
    }

    public void connectWPA2Network(@NonNull String ssid, @NonNull String password) {
        // 获取networkId
        int networkId = setWPA2Network(ssid, password);
        if (-1 != networkId) {
            // 保存配置
            saveConfiguration();
            // 连接网络
            enableNetwork(networkId);
        } else {
            // TODO 错误
        }
    }

    /********************************************************************************************/


    /**
     * 广播接收者
     */
    public static class NetworkBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION: // WIFI状态发生变化
                    switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)) {
                        case WifiManager.WIFI_STATE_ENABLING:
                            Log.i(TAG, "onReceive: 正在打开 WIFI...");
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            Log.i(TAG, "onReceive: WIFI 已打开");
                            break;
                        case WifiManager.WIFI_STATE_DISABLING:
                            Log.i(TAG, "onReceive: 正在关闭 WIFI...");
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:
                            Log.i(TAG, "onReceive: WIFI 已关闭");
                            break;
                        case WifiManager.WIFI_STATE_UNKNOWN:
                        default:
                            Log.i(TAG, "onReceive: WIFI 状态未知!");
                            break;
                    }
                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION: // WIFI扫描完成
                    Log.i(TAG, "onReceive: WIFI扫描完成");
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION: // WIFI连接状态发生改变
                    Log.i(TAG, "onReceive: WIFI连接状态发生改变");
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (null != networkInfo && networkInfo.isConnected()) {
                        WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                        Log.i(TAG, "onReceive: 网络连接成功 ： " + wifiInfo.getSSID());
                    }
                    break;
                case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION: // WIFI连接请求状态发生改变
                    WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    if (null != wifiInfo) {
                        switch (wifiInfo.getSupplicantState()) {
                            case INTERFACE_DISABLED: // 接口禁用
                                Log.i(TAG, "onReceive: INTERFACE_DISABLED 接口禁用");
                                break;
                            case DISCONNECTED:// 断开连接
                            case INACTIVE: // 不活跃的
                                Log.i(TAG, "onReceive: INACTIVE 不活跃的 DISCONNECTED:// 断开连接");

                                break;
                            case SCANNING: // 正在扫描
                                Log.i(TAG, "onReceive: SCANNING 正在扫描");
                                break;
                            case AUTHENTICATING: // 正在验证
                                Log.i(TAG, "onReceive: AUTHENTICATING: // 正在验证");

                                break;
                            case ASSOCIATING: // 正在关联
                                Log.i(TAG, "onReceive: ASSOCIATING: // 正在关联");

                                break;
                            case ASSOCIATED: // 已经关联
                                Log.i(TAG, "onReceive: ASSOCIATED: // 已经关联");

                                break;
                            case FOUR_WAY_HANDSHAKE:
                                Log.i(TAG, "onReceive: FOUR_WAY_HANDSHAKE:");
                                break;
                            case GROUP_HANDSHAKE:
                                Log.i(TAG, "onReceive: GROUP_HANDSHAKE:");
                                break;
                            case COMPLETED: // 完成
                                Log.i(TAG, "onReceive: COMPLETED: // 完成");

                                break;
                            case DORMANT:
                                Log.i(TAG, "onReceive: DORMANT:");
                                break;
                            case UNINITIALIZED: // 未初始化
                                Log.i(TAG, "onReceive: UNINITIALIZED: // 未初始化");
                                break;
                            case INVALID: // 无效的
                                Log.i(TAG, "onReceive: INVALID: // 无效的");
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
