历史遗留问题终该解决，之前有文章[Android连接WIFI](http://blog.csdn.net/q4878802/article/details/52119203)，今天再次整理一下，梳理一下遗留的问题

## 修改或者删除配置失败，返回-1

Android 6.0以后的限制，程序本身只能修改和删除自己创建的配置，如果是在手机WIFI管理器或者其他应用程序连接的WIFI，那么只能连接，不能修改（有ROOT权限除外）。

## getScanResults()返回空

Android 6.0动态权限问题，需要添加并动态校验`ACCESS_FINE_LOCATION`或`ACCESS_COARSE_LOCATION`权限，权限通过以后就可以获取到WIFI列表。

## WIFI连接成功以后获取SSID为`<unknown ssid>`

在监听到`SUPPLICANT_STATE_CHANGED_ACTION`的广播以后获取`SupplicantState`状态，当状态为`COMPLETED`以后，再通过`WifiManager`的`getConnectionInfo()`方法获取SSID。


源码：[KqwWifiManagerDemo](https://github.com/kongqw/KqwWifiManagerDemo)
