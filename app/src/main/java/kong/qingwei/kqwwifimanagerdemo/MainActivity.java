package kong.qingwei.kqwwifimanagerdemo;

import android.Manifest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kongqw.wifilibrary.WiFiManager;
import com.kongqw.wifilibrary.listener.OnWifiConnectListener;
import com.kongqw.wifilibrary.listener.OnWifiScanResultsListener;

import java.util.List;

import kong.qingwei.kqwwifimanagerdemo.adapter.WifiListAdapter;
import kong.qingwei.kqwwifimanagerdemo.view.ConnectWifiDialog;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 下拉刷新
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        // WIFI列表
        mWifiList = (ListView) findViewById(R.id.wifi_list);
        mWifiList.setEmptyView(findViewById(R.id.empty_view));
        mWifiListAdapter = new WifiListAdapter(getApplicationContext());
        mWifiList.setAdapter(mWifiListAdapter);
        mWifiList.setOnItemClickListener(this);
        mWifiList.setOnItemLongClickListener(this);
        // WIFI管理器
        mWiFiManager = new WiFiManager(getApplicationContext());
        // 动态权限管理器
        mPermissionsManager = new PermissionsManager(this) {
            @Override
            public void authorized(int requestCode) {
                if (GET_WIFI_LIST_REQUEST_CODE == requestCode) {
                    // 获取WIFI列表
                    List<ScanResult> scanResults = mWiFiManager.getScanResults();
                    refreshData(scanResults);
                }
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {

            }
        };

        // 请求WIFI列表
        mPermissionsManager.checkPermissions(GET_WIFI_LIST_REQUEST_CODE, PERMISSIONS);

        // 添加扫描完成的监听
        mWiFiManager.setOnWifiScanResultsListener(new OnWifiScanResultsListener() {
            @Override
            public void onScanResults(List<ScanResult> scanResults) {
                refreshData(scanResults);
            }
        });
        // 添加WIFI连接的监听
        mWiFiManager.setOnWifiConnectListener(new OnWifiConnectListener() {
            @Override
            public void onSuccess(String SSID) {
                Toast.makeText(getApplicationContext(), SSID + "  连接成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        // 下拉刷新
        mWiFiManager.startScan();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 复查权限
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }

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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ScanResult scanResult = (ScanResult) mWifiListAdapter.getItem(position);
        WifiConfiguration wifiConfiguration = mWiFiManager.getConfigFromConfiguredNetworksBySsid(scanResult.SSID);
        if (null != wifiConfiguration) {
            boolean isDeleted = mWiFiManager.deleteConfig(wifiConfiguration.networkId);
            Toast.makeText(getApplicationContext(), isDeleted ? "配置删除成功" : "配置删除失败", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "该网络还没有配置过", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
