package kong.qingwei.kqwwifimanagerdemo.listener;

/**
 * Created by kqw on 2016/8/4.
 * WIFI打开关闭的回调接口
 */
public interface OnWifiEnabledListener {

    // 开始
    void onStart(boolean isOpening);

    /**
     * WIFI开关的回调
     *
     * @param enabled true 可用 false 不可用
     */
    void onWifiEnabled(boolean enabled);

    // 结束
    void onFinish();
}
