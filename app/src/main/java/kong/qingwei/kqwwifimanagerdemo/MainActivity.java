package kong.qingwei.kqwwifimanagerdemo;

import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import kong.qingwei.kqwwifimanagerdemo.adapter.WifiListAdapter;
import kong.qingwei.kqwwifimanagerdemo.view.KqwRecyclerView;

public class MainActivity extends AppCompatActivity implements KqwRecyclerView.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private KqwWifiManager mKqwWifiManager;
    private KqwRecyclerView mKqwRecyclerView;
    private WifiListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mKqwWifiManager = new KqwWifiManager(this);
        // 打开Wifi
        mKqwWifiManager.openWifi();

        mKqwRecyclerView = (KqwRecyclerView) findViewById(R.id.kqwRecyclerView);
        if (mKqwRecyclerView != null) {
            // 如果数据的填充不会改变RecyclerView的布局大小，那么这个设置可以提高RecyclerView的性能
            mKqwRecyclerView.setHasFixedSize(true);
            // 设置这个RecyclerView是线性布局
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            mKqwRecyclerView.setLayoutManager(mLayoutManager);
            mKqwRecyclerView.setOnItemClickListener(this);
            List<ScanResult> scanResults = mKqwWifiManager.getWifiList();
            mAdapter = new WifiListAdapter(scanResults);
            mKqwRecyclerView.setAdapter(mAdapter);
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
            Toast.makeText(this, "打开WIFI", Toast.LENGTH_SHORT).show();
            mKqwWifiManager.openWifi();
            return true;
        } else if (id == R.id.action_close_wifi) {
            Toast.makeText(this, "关闭WIFI", Toast.LENGTH_SHORT).show();
            mKqwWifiManager.closeWifi();
            return true;
        } else if (id == R.id.action_link1) {
            Toast.makeText(this, "测试连接到 BitMain_office", Toast.LENGTH_SHORT).show();
            mKqwWifiManager.connectionWifiByPassword("BitMain_office", "cisco!123", KqwWifiManager.SecurityMode.WPA);
            return true;
        } else if (id == R.id.action_link2) {
            Toast.makeText(this, "测试连接到 BitMain_download", Toast.LENGTH_SHORT).show();
            mKqwWifiManager.connectionWifiByPassword("BitMain_download", "cisco!123", KqwWifiManager.SecurityMode.WPA);
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
        ScanResult scanResult = mAdapter.getScanResult(v.getAdapterPosition());
        showDialog(scanResult);
    }

    /**
     * 输入密码的对话框
     */
    public void showDialog(ScanResult scanResult) {
        final int networkId = mKqwWifiManager.getNetworkIdFromConfig(scanResult);
        Log.i(TAG, "showDialog: networkId = " + networkId);
        final String SSID = scanResult.SSID;
        if (-1 == networkId) {
            // 系统没有保存该网络的配置
            final EditText editText = new EditText(this);
            editText.setHint("请输入密码");
            final KqwWifiManager.SecurityMode securityMode = mKqwWifiManager.getSecurityMode(scanResult);

            new AlertDialog.Builder(this)
                    .setTitle("链接WIFI:" + SSID)
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String pwd = editText.getText().toString();
                            boolean isConnected = mKqwWifiManager.connectionWifiByPassword(SSID, pwd, securityMode);
                            Toast.makeText(MainActivity.this, isConnected ? "连接成功" : "连接失败", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            // 系统保存过该网络的配置
            new AlertDialog.Builder(this)
                    .setTitle("连接")
                    .setMessage("配置过该网络:" + SSID)
                    .setPositiveButton("直接连接", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean isConnected = mKqwWifiManager.connectionWifiByNetworkId(networkId);
                            Toast.makeText(MainActivity.this, isConnected ? "连接成功" : "连接失败", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNeutralButton("删除配置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean isDelete = mKqwWifiManager.deleteConfig(networkId);
                            Toast.makeText(MainActivity.this, isDelete ? "删除成功" : "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();

        }
    }
}
