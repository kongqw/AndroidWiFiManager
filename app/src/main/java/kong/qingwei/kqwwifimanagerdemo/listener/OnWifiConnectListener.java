package kong.qingwei.kqwwifimanagerdemo.listener;

/**
 * Created by kqw on 2016/8/4.
 * WIFI连接的回调接口
 */
public interface OnWifiConnectListener {

    // 开始
    void onStart(String SSID);

    // 连接信息
    void onConnectingMessage(String message);

    // 成功
    void onSuccess(String SSID);

    // 失败
    void onFailure();

    // 结束
    void onFinish();
}
