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

}
