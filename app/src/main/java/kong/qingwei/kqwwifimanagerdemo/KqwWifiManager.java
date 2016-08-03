package kong.qingwei.kqwwifimanagerdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Switch;

import java.util.List;

/**
 * Created by kqw on 2016/8/2.
 * WifiManager : https://developer.android.com/reference/android/net/wifi/WifiManager.html
 * WifiConfiguration : https://developer.android.com/reference/android/net/wifi/WifiConfiguration.html
 * ScanResult : https://developer.android.com/reference/android/net/wifi/ScanResult.html
 * WifiInfo : https://developer.android.com/reference/android/net/wifi/WifiInfo.html
 * blog : http://blog.csdn.net/yuanbohx/article/details/8109042
 * Wifi管理
 */
public class KqwWifiManager {

    private static final String TAG = "KqwWifiManager";
    private final WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    public KqwWifiManager(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
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

    /**
     * 获取Wifi
     *
     * @return Wifi列表
     */
    public List<ScanResult> getWifiList() {
        try {
            mWifiManager.startScan();
            // 得到扫描结果
            return mWifiManager.getScanResults();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过密码连接到WIFI
     *
     * @param SSID WIFI名称
     * @param pwd  WIFI密码
     * @return 连接结果
     */
    public boolean connectionWifiByPassword(String SSID, String pwd, SecurityMode mode) {
        // 判断当前的网络
        mWifiInfo = mWifiManager.getConnectionInfo();
        if (null != mWifiInfo) {
            // 当前有连接网络
            String connectionSSID = mWifiInfo.getSSID();
            Log.i(TAG, "connectionWifiByPassword: 当前连接的：" + connectionSSID + "   想要连接的：" + SSID);
            if (String.format("\"%s\"", SSID).equals(connectionSSID)) {
                Log.i(TAG, "connectionWifiByPassword: 当前已经连接了 " + SSID);
                return true;
            } else {
                // 断开当前连接
                int connectionNetworkId = mWifiInfo.getNetworkId();
                boolean isDisconnect = disconnectWifi(connectionNetworkId);
                Log.i(TAG, "connectionWifiByPassword: " + (isDisconnect ? mWifiInfo.getSSID() + " 已断开" : "断开失败"));
            }
        }

        Log.i(TAG, "connectionWifiByPassword: 连接网络 SSID = " + SSID + "  pwd = " + pwd + "  mode = " + mode);
        // 生成配置文件
        WifiConfiguration addConfig = createWifiConfiguration(SSID, pwd, mode);
        Log.i(TAG, "connectionWifiByPassword: 生成配置文件: " + addConfig);

        int netId;
        // 判断当前网络是否存在
        WifiConfiguration updateConfig = isExists(addConfig);
        if (null != updateConfig) {
            // 更新
            int networkId = updateConfig.networkId;
            netId = mWifiManager.updateNetwork(updateConfig);
            Log.i(TAG, "connectionWifiByPassword: 网络已经存在，更新配置 netId = " + netId + "   networkId = " + networkId);
        } else {
            // 添加
            netId = mWifiManager.addNetwork(addConfig);
            Log.i(TAG, "connectionWifiByPassword: 网络不存在，添加配置 netId = " + netId);
        }
        boolean b = mWifiManager.enableNetwork(netId, true);
        Log.i(TAG, "connectionWifiByPassword: 启用WIFI " + b);
        return b;
    }

    /**
     * 通过NetworkId连接到WIFI
     *
     * @param networkId NetworkId
     * @return 是否连接成功
     */
    public boolean connectionWifiByNetworkId(int networkId) {
        // 判断当前的网络
        mWifiInfo = mWifiManager.getConnectionInfo();
        if (null != mWifiInfo) {
            // 当前有连接网络
            int connectionNetworkId = mWifiInfo.getNetworkId();
            if (connectionNetworkId == networkId) {
                Log.i(TAG, "connectionWifiByPassword: 当前已经连接了 networkId =" + networkId);
                return true;
            } else {
                // 断开当前连接
                boolean isDisconnect = disconnectWifi(connectionNetworkId);
                Log.i(TAG, "connectionWifiByPassword: " + (isDisconnect ? mWifiInfo.getSSID() + " 已断开" : "断开失败"));
            }
        }

        return mWifiManager.enableNetwork(networkId, true);
    }

    // 断开指定ID的网络
    public boolean disconnectWifi(int netId) {
        boolean isDisable = mWifiManager.disableNetwork(netId);
        boolean isDisconnect = mWifiManager.disconnect();
        return isDisable && isDisconnect;
    }

    /**
     * 删除配置
     *
     * @param netId netId
     * @return 是否删除成功
     */
    public boolean deleteConfig(int netId) {
        boolean isDisable = mWifiManager.disableNetwork(netId);
        boolean isRemove = mWifiManager.removeNetwork(netId);
        boolean isSave = mWifiManager.saveConfiguration();
        return isDisable && isRemove && isSave;
    }


    //网络加密模式
    public enum SecurityMode {
        OPEN, WEP, WPA, WPA2
    }

    /**
     * 生成新的配置信息 用于连接Wifi
     *
     * @param SSID     WIFI名字
     * @param Password WIFI密码
     * @param mode     WIFI类型
     * @return 配置
     */
    public WifiConfiguration createWifiConfiguration(String SSID, String Password, SecurityMode mode) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        if (mode == SecurityMode.OPEN) {
            //WIFICIPHER_NOPASS
            // config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            // config.wepTxKeyIndex = 0;
        } else if (mode == SecurityMode.WEP) {
            //WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (mode == SecurityMode.WPA) {
            //WIFICIPHER_WPA
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 获取WIFI的加密方式
     *
     * @param scanResult WIFI信息
     * @return 加密方式
     */
    public SecurityMode getSecurityMode(ScanResult scanResult) {
        String capabilities = scanResult.capabilities;
        if (capabilities.contains("WPA")) {
            return SecurityMode.WPA;
        } else if (capabilities.contains("WEP")) {
            return SecurityMode.WEP;
//        } else if (capabilities.contains("EAP")) {
//            return SecurityMode.WEP;
        } else {
            //不加密
            return SecurityMode.OPEN;
        }
    }

    /**
     * 判断配置在系统中是否存在
     *
     * @param config 新的配置
     * @return 更新的配置
     */
    private WifiConfiguration isExists(WifiConfiguration config) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        Log.i(TAG, "- 保存过的WIFI配置 ------------------------------------------------------------");
        for (WifiConfiguration existingConfig : existingConfigs) {
            Log.i(TAG, "系统保存配置的 SSID : " + existingConfig.SSID + "  networkId : " + existingConfig.networkId);
            if (existingConfig.SSID.equals(config.SSID)) {
                config.networkId = existingConfig.networkId;
                Log.i(TAG, "- return true; -------------------------------------------------------------");
                return config;
            }
        }
        Log.i(TAG, "- return false; --------------------------------------------------------------");
        return null;
    }

    /**
     * 判断该WIFI在系统里有没有配置过
     *
     * @param scanResult 扫描到的WIFI信息
     * @return 如果有配置信息则返回配置的networkId 如果没有配置过则返回-1
     */
    public int getNetworkIdFromConfig(ScanResult scanResult) {
        String SSID = String.format("\"%s\"", scanResult.SSID);
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals(SSID)) {
                return existingConfig.networkId;
            }
        }
        return -1;
    }


    // 参考   http://blog.csdn.net/h3c4lenovo/article/details/9627781
    public static class NetworkBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    Log.e("H3c", "wifiState " + wifiState);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_DISABLED:
                            break;
                        case WifiManager.WIFI_STATE_DISABLING:
                            break;
                        //
                    }
                }
                // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
                // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，当然刚打开wifi肯定还没有连接到有效的无线
                if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                    Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (null != parcelableExtra) {
                        NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                        NetworkInfo.State state = networkInfo.getState();
                        boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                        Log.e("H3c", "isConnected = " + isConnected);
                        if (isConnected) {
                        } else {

                        }
                    }
                }
                // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
                // 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
                // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
                if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    Log.i(TAG, "网络状态改变:" + wifi.isConnected() + " 3g:" + gprs.isConnected());
                    NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                    if (info != null) {
//                        Log.e("H3c", "info.getTypeName()" + info.getTypeName());
//                        Log.e("H3c", "getSubtypeName()" + info.getSubtypeName());
//                        Log.e("H3c", "getState()" + info.getState());
//                        Log.e("H3c", "getDetailedState()" + info.getDetailedState().name());
//                        Log.e("H3c", "getDetailedState()" + info.getExtraInfo());
//                        Log.e("H3c", "getType()" + info.getType());

                        if (NetworkInfo.State.CONNECTED == info.getState()) {
                        } else if (info.getType() == 1) {
                            if (NetworkInfo.State.DISCONNECTING == info.getState()) {

                            }
                        }
                    }
                }


//                String action = intent.getAction();
//                Log.i(TAG, "onReceive: action = " + action);
//                switch(action){
//                    case WifiManager.RSSI_CHANGED_ACTION: // 信号强度变化
//
//                        break;
//                    case WifiManager.NETWORK_STATE_CHANGED_ACTION: // 网络状态变化
//
//                        break;
//                    case WifiManager.WIFI_STATE_CHANGED_ACTION: // WIFI状态变化
//
//                        break;
//                }


//                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//                NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//                //如果无网络连接activeInfo为null
//                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
//
//                if (wifiInfo.isConnected()) {
//                    // wifi 网络
//                    Log.i("Network", "wifi 网络 " + wifiInfo.getSubtypeName());
//                } else {
//                    // 没有连接WIFI
//                    Log.i("Network", "没有网络");
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
