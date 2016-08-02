package kong.qingwei.kqwwifimanagerdemo;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * Created by kqw on 2016/8/2.
 * WifiManager : https://developer.android.com/reference/android/net/wifi/WifiManager.html
 * WifiConfiguration : https://developer.android.com/reference/android/net/wifi/WifiConfiguration.html
 * ScanResult : https://developer.android.com/reference/android/net/wifi/ScanResult.html
 * Wifi管理
 */
public class KqwWifiManager {

    private static final String TAG = "KqwWifiManager";
    private final WifiManager mWifiManager;
    private final WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfiguration;

    public KqwWifiManager(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    /**
     * 打开Wifi
     */
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭Wifi
     */
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        Log.i(TAG, "--------------------------------------------------------------------");
        Log.i(TAG, "-  得到扫描结果   -----------------------------------------------");
        for (ScanResult result : mWifiList) {
            String ssid = result.SSID;
            String bssid = result.BSSID;
//            result.isPasspointNetwork()
            String capabilities = result.capabilities;
            int frequency = result.frequency;
            int level = result.level;
            Log.i(TAG, "startScan: SSID = " + ssid + " bssid = " + bssid + " capabilities = " + capabilities + " level = " + level + " frequency = " + frequency );
        }
        Log.i(TAG, "--------------------------------------------------------------------");
        Log.i(TAG, "-  得到配置好的网络连接  ---------------------------------------");
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration result : mWifiConfiguration) {
            String ssid = result.SSID;
            Log.i(TAG, "startScan: SSID = " + ssid);
        }
        Log.i(TAG, "--------------------------------------------------------------------");
    }
}
