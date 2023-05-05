# Android WIFI控制

[![](https://jitpack.io/v/kongqw/AndroidWiFiManager.svg)](https://jitpack.io/#kongqw/AndroidWiFiManager)

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

``` gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

``` gradle
dependencies {
        compile 'com.github.kongqw:AndroidWiFiManager:1.1.1'
}
```

## 初始化

``` java
// WIFI管理器
mWiFiManager = new WiFiManager(getApplicationContext());
```

## 打开WIFI

``` java
mWiFiManager.openWiFi();
```

## 关闭WIFI

``` java
mWiFiManager.closeWiFi();
```

## 添加WIFI开关状态的监听

``` java
mWiFiManager.setOnWifiEnabledListener(this);
```

### 回调

``` java
/**
 * WIFI开关状态的回调
 *
 * @param enabled true 打开 false 关闭
 */
@Override
public void onWifiEnabled(boolean enabled) {
    // TODO    
}
```

## 移除WIFI开关状态的监听

``` java
mWiFiManager.removeOnWifiEnabledListener();
```

## 获取WIFI列表

``` java
List<ScanResult> scanResults = mWiFiManager.getScanResults();
```

### 获取WIFI加密方式

``` java
mWiFiManager.getSecurityMode(scanResult)
```

> 注意：Android 6.0需要动态获取 Manifest.permission.ACCESS_FINE_LOCATION 或 Manifest.permission.ACCESS_COARSE_LOCATION 后,才能正常获取到WIFI列表。

## 添加获取WIFI列表的监听

``` java
mWiFiManager.setOnWifiScanResultsListener(this);
```

### 回调

``` java
/**
 * WIFI列表刷新后的回调
 *
 * @param scanResults 扫描结果
 */
@Override
public void onScanResults(List<ScanResult> scanResults) {
    // TODO
}
```

> mWiFiManager.getScanResults(); 是返回当前的WIFI列表，回调返回的是扫描更新以后新的WIFI列表。

## 移除获取WIFI列表的监听

``` java
mWiFiManager.removeOnWifiScanResultsListener();
```

## 连接到开放网络

``` java
mWiFiManager.connectOpenNetwork(scanResult.SSID);
```

## 连接到WPA/WPA2网络

``` java
mWiFiManager.connectWPA2Network(scanResult.SSID, password);
```

## 连接到WEP网络

``` java
mWiFiManager.connectWEPNetwork(scanResult.SSID, password);
```

## 添加连接WIFI的监听

``` java
mWiFiManager.setOnWifiConnectListener(this);
```

### 回调

``` java
/**
 * WIFI连接的Log得回调
 *
 * @param log log
 */
@Override
public void onWiFiConnectLog(String log) {
    Log.i(TAG, "onWiFiConnectLog: " + log);
    // TODO
}

/**
 * WIFI连接成功的回调
 *
 * @param SSID 热点名
 */
@Override
public void onWiFiConnectSuccess(String SSID) {
    Log.i(TAG, "onWiFiConnectSuccess:  [ " + SSID + " ] 连接成功");
    // TODO
}

/**
 * WIFI连接失败的回调
 *
 * @param SSID 热点名
 */
@Override
public void onWiFiConnectFailure(String SSID) {
    Log.i(TAG, "onWiFiConnectFailure:  [ " + SSID + " ] 连接失败");
    // TODO
}
```

## 移除连接WIFI的监听

``` java
mWiFiManager.removeOnWifiConnectListener();
```

## 断开网络连接

``` java
mWiFiManager.disconnectWifi(connectionInfo.getNetworkId());
```

## 删除网络配置

> 只能删除自己创建的配置，其他应用生成的配置需要Root权限才可以删除。

``` java
mWiFiManager.deleteConfig(wifiConfiguration.networkId);
```

