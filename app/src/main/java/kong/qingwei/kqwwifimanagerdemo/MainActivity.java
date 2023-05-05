package kong.qingwei.kqwwifimanagerdemo;

import android.Manifest;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.kongqw.permissionslibrary.PermissionsManager;
import com.kongqw.wifilibrary.WiFiManager;
import com.kongqw.wifilibrary.listener.OnWifiConnectListener;
import com.kongqw.wifilibrary.listener.OnWifiEnabledListener;
import com.kongqw.wifilibrary.listener.OnWifiScanResultsListener;

import java.util.List;

import kong.qingwei.kqwwifimanagerdemo.adapter.WifiListAdapter;
import kong.qingwei.kqwwifimanagerdemo.view.ConnectWifiDialog;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, OnWifiScanResultsListener, OnWifiConnectListener, OnWifiEnabledListener {

    private static final String TAG = "MainActivity";

    private ListView mWifiList;
    private SwipeRefreshLayout mSwipeLayout;
    private PermissionsManager mPermissionsManager;

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private final int GET_WIFI_LIST_REQUEST_CODE = 0;
    private WiFiManager mWiFiManager;
    private WifiListAdapter mWifiListAdapter;
    private SwitchCompat switchCompat;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 加载View
        initView();
        // 添加WIFI开关的监听
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWiFiManager.openWiFi();
                } else {
                    mWiFiManager.closeWiFi();
                }
            }
        });
        // 添加下拉刷新的监听
        mSwipeLayout.setOnRefreshListener(this);
        // 初始化WIFI列表
        mWifiList.setEmptyView(findViewById(R.id.empty_view));
        mWifiListAdapter = new WifiListAdapter(getApplicationContext());
        mWifiList.setAdapter(mWifiListAdapter);
        mWifiList.setOnItemClickListener(this);
        mWifiList.setOnItemLongClickListener(this);
        // WIFI管理器
        mWiFiManager = WiFiManager.getInstance(getApplicationContext());
        // 动态权限管理器
        mPermissionsManager = new PermissionsManager(this) {
            @Override
            public void authorized(int requestCode) {
                // 6.0 以上系统授权通过
                if (GET_WIFI_LIST_REQUEST_CODE == requestCode) {
                    // 获取WIFI列表
                    List<ScanResult> scanResults = mWiFiManager.getScanResults();
                    refreshData(scanResults);
                }
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
                // 6.0 以上系统授权失败
            }

            @Override
            public void ignore() {
                // 6.0 以下系统 获取WIFI列表
                List<ScanResult> scanResults = mWiFiManager.getScanResults();
                refreshData(scanResults);
            }
        };
        // 请求WIFI列表
        mPermissionsManager.checkPermissions(GET_WIFI_LIST_REQUEST_CODE, PERMISSIONS);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        // WIFI 开关
        switchCompat = (SwitchCompat) findViewById(R.id.switch_wifi);
        // 显示WIFI信息的布局
        frameLayout = (FrameLayout) findViewById(R.id.fl_wifi);
        // 下拉刷新
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        // WIFI列表
        mWifiList = (ListView) findViewById(R.id.wifi_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 添加监听
        mWiFiManager.setOnWifiEnabledListener(this);
        mWiFiManager.setOnWifiScanResultsListener(this);
        mWiFiManager.setOnWifiConnectListener(this);
        // 更新WIFI开关状态
        switchCompat.setChecked(mWiFiManager.isWifiEnabled());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 移除监听
        mWiFiManager.removeOnWifiEnabledListener();
        mWiFiManager.removeOnWifiScanResultsListener();
        mWiFiManager.removeOnWifiConnectListener();
    }

    /**
     * 刷新页面
     *
     * @param scanResults WIFI数据
     */
    public void refreshData(List<ScanResult> scanResults) {
        mSwipeLayout.setRefreshing(false);
        // 刷新界面
        mWifiListAdapter.refreshData(scanResults);

        Snackbar.make(mWifiList, "WIFI列表刷新成功", Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Android 6.0 权限校验
     *
     * @param requestCode  requestCode
     * @param permissions  permissions
     * @param grantResults grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 复查权限
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }

    /**
     * WIFI列表单击
     *
     * @param parent   parent
     * @param view     view
     * @param position position
     * @param id       id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ScanResult scanResult = (ScanResult) mWifiListAdapter.getItem(position);
        switch (mWiFiManager.getSecurityMode(scanResult)) {
            case WPA:
            case WPA2:
                new ConnectWifiDialog(this) {

                    @Override
                    public void connect(String password) {
                        mWiFiManager.connectWPA2Network(scanResult.SSID, password);
                    }
                }.setSsid(scanResult.SSID).show();
                break;
            case WEP:
                new ConnectWifiDialog(this) {

                    @Override
                    public void connect(String password) {
                        mWiFiManager.connectWEPNetwork(scanResult.SSID, password);
                    }
                }.setSsid(scanResult.SSID).show();
                break;
            case OPEN: // 开放网络
                mWiFiManager.connectOpenNetwork(scanResult.SSID);
                break;
        }
    }

    /**
     * WIFI列表长按
     *
     * @param parent   parent
     * @param view     view
     * @param position position
     * @param id       id
     * @return 是否拦截长按事件
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ScanResult scanResult = (ScanResult) mWifiListAdapter.getItem(position);
        final String ssid = scanResult.SSID;
        new AlertDialog.Builder(this)
                .setTitle(ssid)
                .setItems(new String[]{"断开连接", "删除网络配置"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // 断开连接
                                WifiInfo connectionInfo = mWiFiManager.getConnectionInfo();
                                Log.i(TAG, "onClick: connectionInfo :" + connectionInfo.getSSID());
                                if (mWiFiManager.addDoubleQuotation(ssid).equals(connectionInfo.getSSID())) {
                                    mWiFiManager.disconnectWifi(connectionInfo.getNetworkId());
                                } else {
                                    Toast.makeText(getApplicationContext(), "当前没有连接 [ " + ssid + " ]", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1: // 删除网络配置
                                WifiConfiguration wifiConfiguration = mWiFiManager.getConfigFromConfiguredNetworksBySsid(ssid);
                                if (null != wifiConfiguration) {
                                    boolean isDelete = mWiFiManager.deleteConfig(wifiConfiguration.networkId);
                                    Toast.makeText(getApplicationContext(), isDelete ? "删除成功！" : "其他应用配置的网络没有ROOT权限不能删除！", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "没有保存该网络！", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                })
                .show();
        return true;
    }


    /**
     * 下拉刷新的回调
     */
    @Override
    public void onRefresh() {
        // 下拉刷新
        mWiFiManager.startScan();
    }

    /**
     * WIFI列表刷新后的回调
     *
     * @param scanResults 扫描结果
     */
    @Override
    public void onScanResults(List<ScanResult> scanResults) {
        refreshData(scanResults);
    }

    /**
     * WIFI连接的Log得回调
     *
     * @param log log
     */
    @Override
    public void onWiFiConnectLog(String log) {
        Log.i(TAG, "onWiFiConnectLog: " + log);
        Snackbar.make(mWifiList, "WIFI正在连接 : " + log, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * WIFI连接成功的回调
     *
     * @param SSID 热点名
     */
    @Override
    public void onWiFiConnectSuccess(String SSID) {
        Log.i(TAG, "onWiFiConnectSuccess:  [ " + SSID + " ] 连接成功");
        Toast.makeText(getApplicationContext(), SSID + "  连接成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * WIFI连接失败的回调
     *
     * @param SSID 热点名
     */
    @Override
    public void onWiFiConnectFailure(String SSID) {
        Log.i(TAG, "onWiFiConnectFailure:  [ " + SSID + " ] 连接失败");
        Toast.makeText(getApplicationContext(), SSID + "  连接失败！请重新连接！", Toast.LENGTH_SHORT).show();
    }

    /**
     * WIFI开关状态的回调
     *
     * @param enabled true 可用 false 不可用
     */
    @Override
    public void onWifiEnabled(boolean enabled) {
        switchCompat.setChecked(enabled);
        frameLayout.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }
}
