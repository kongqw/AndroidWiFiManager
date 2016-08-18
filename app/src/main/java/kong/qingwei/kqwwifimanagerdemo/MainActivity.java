package kong.qingwei.kqwwifimanagerdemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import kong.qingwei.kqwwifimanagerdemo.adapter.WifiListAdapter;
import kong.qingwei.kqwwifimanagerdemo.listener.OnWifiConnectListener;
import kong.qingwei.kqwwifimanagerdemo.listener.OnWifiEnabledListener;
import kong.qingwei.kqwwifimanagerdemo.listener.OnWifiScanResultsListener;
import kong.qingwei.kqwwifimanagerdemo.view.KqwRecyclerView;

public class MainActivity extends AppCompatActivity implements KqwRecyclerView.OnItemClickListener {

    private KqwWifiManager mKqwWifiManager;
    private WifiListAdapter mAdapter;
    private ProgressDialog progressDialog;
    private KqwRecyclerView mKqwRecyclerView;
    // 要连接的WIFI
    private ScanResult mScanResult;

    /**
     * WIFI可用状态的回调
     */
    private OnWifiEnabledListener mOnWifiEnabledListener = new OnWifiEnabledListener() {
        @Override
        public void onStart(boolean isOpening) {
            showProgressDialog(isOpening ? "正在打开WIFI" : "正在关闭WIFI");
        }

        @Override
        public void onWifiEnabled(boolean enabled) {
            if (enabled) {
                // WIFI可用以后刷新WIFI列表
                mKqwWifiManager.startScan(mOnWifiScanResultsListener);
            } else {
                // 情况列表数据
                mAdapter.cleanData();
            }
            Snackbar.make(mKqwRecyclerView, enabled ? "WIFI已经打开" : "WIFI已经关闭", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onFinish() {
            dismissProgressDialog();
        }
    };

    /**
     * 扫描附近的WIFI列表的回调
     */
    private OnWifiScanResultsListener mOnWifiScanResultsListener = new OnWifiScanResultsListener() {
        @Override
        public void onStart() {
            showProgressDialog("正在扫描附近的WIFI");
        }

        @Override
        public void onScanResults(List<ScanResult> scanResults) {
            // 扫描到结果
            mAdapter = new WifiListAdapter(scanResults);
            mKqwRecyclerView.setAdapter(mAdapter);
        }

        @Override
        public void onFinish() {
            dismissProgressDialog();
        }
    };

    /**
     * WIFI连接的回调
     */
    private OnWifiConnectListener mOnWifiConnectListener = new OnWifiConnectListener() {
        @Override
        public void onStart(String SSID) {
            // 开始
            showProgressDialog("准备连接...");
        }

        @Override
        public void onConnectingMessage(String message) {
            showProgressDialog(message);
        }

        @Override
        public void onSuccess(String SSID) {
            // 连接成功
            Snackbar.make(mKqwRecyclerView, SSID + " 连接成功", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure() {
            // 连接失败
            Snackbar.make(mKqwRecyclerView, "连接失败", Snackbar.LENGTH_LONG).setAction("重新连接", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mScanResult) {
                        showConnectDialog(mScanResult);
                    }
                }
            }).show();
        }

        @Override
        public void onFinish() {
            // 完成
            dismissProgressDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mKqwWifiManager = new KqwWifiManager(this);

        // WIFI列表
        mKqwRecyclerView = (KqwRecyclerView) findViewById(R.id.kqwRecyclerView);
        if (mKqwRecyclerView != null) {
            // 如果数据的填充不会改变RecyclerView的布局大小，那么这个设置可以提高RecyclerView的性能
            mKqwRecyclerView.setHasFixedSize(true);
            // 设置这个RecyclerView是线性布局
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            mKqwRecyclerView.setLayoutManager(mLayoutManager);
            mKqwRecyclerView.setOnItemClickListener(this);
        }

        // 判断WIFI是否可用
        if (mKqwWifiManager.isWifiEnabled()) {
            // WIFI可用，刷新WIFI列表
            mKqwWifiManager.startScan(mOnWifiScanResultsListener);
        } else {
            // 打开WIFI
            mKqwWifiManager.openWifi(mOnWifiEnabledListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open_wifi) {
            // 打开WIFI
            mKqwWifiManager.openWifi(mOnWifiEnabledListener);
            return true;
        } else if (id == R.id.action_close_wifi) {
            // 关闭WIFI
            mKqwWifiManager.closeWifi(mOnWifiEnabledListener);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Wifi 列表的Item被点击
     *
     * @param v v
     */
    @Override
    public void onItemClick(RecyclerView.ViewHolder v) {
        mScanResult = mAdapter.getScanResult(v.getAdapterPosition());
        if (null != mScanResult) {
            // showConnectDialog(mScanResult);
            int networkId = mKqwWifiManager.getNetworkIdFromConfig(mScanResult);
            if (-1 == networkId) {
                // WIFI没有配置过，只能重新输入密码进行连接
                showConnectDialog(mScanResult);
            } else {
                // WIFI 配置过
                showConnectedDialog(mScanResult, networkId);
            }
        }
    }

    /**
     * 输入密码的对话框
     *
     * @param scanResult 要连接的WIFI
     */
    public void showConnectDialog(@NonNull final ScanResult scanResult) {
        final String SSID = scanResult.SSID;
        // 系统没有保存该网络的配置
        final EditText editText = new EditText(this);
        editText.setHint("请输入密码");
        new AlertDialog.Builder(this)
                .setTitle("链接WIFI:" + SSID)
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pwd = editText.getText().toString();
                        mKqwWifiManager.connectionWifiByPassword(scanResult, pwd, mOnWifiConnectListener);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 直接连接的对话框
     *
     * @param scanResult 要连接的网络
     * @param networkId  NetworkId
     */
    public void showConnectedDialog(@NonNull final ScanResult scanResult, final int networkId) {
        final String SSID = scanResult.SSID;
        // 系统保存过该网络的配置
        new AlertDialog.Builder(this)
                .setTitle("网络配置过")
                .setMessage("是否直接连接到：" + SSID)
                .setPositiveButton("直接连接", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mKqwWifiManager.connectionWifiByNetworkId(SSID, networkId, mOnWifiConnectListener);
                    }
                })
                .setNeutralButton("重新输入密码连接", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showConnectDialog(scanResult);
                    }
                })
                .show();
    }

    /**
     * 显示加载框
     */
    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage(message);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    /**
     * 关闭下加载框
     */
    private void dismissProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
