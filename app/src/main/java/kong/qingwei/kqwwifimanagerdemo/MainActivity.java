package kong.qingwei.kqwwifimanagerdemo;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import kong.qingwei.kqwwifimanagerdemo.adapter.WifiListAdapter;
import kong.qingwei.kqwwifimanagerdemo.view.KqwRecyclerView;

public class MainActivity extends AppCompatActivity implements KqwRecyclerView.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private KqwWifiManager mKqwWifiManager;
    private KqwRecyclerView mKqwRecyclerView;

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
        if (id == R.id.action_scan_wifi) {
            // 扫描Wifi
//            List<ScanResult> scanResults = mKqwWifiManager.getWifiList();
//            WifiListAdapter mAdapter = new WifiListAdapter(scanResults);
//            mKqwRecyclerView.setAdapter(mAdapter);

            List<ScanResult> scanResults = mKqwWifiManager.startScan();
            for (ScanResult result : scanResults) {
                String ssid = result.SSID;
                String bssid = result.BSSID;
//            result.isPasspointNetwork()
                String capabilities = result.capabilities;
                int frequency = result.frequency;
                int level = result.level;
                Log.i(TAG, "startScan: SSID = " + ssid + " bssid = " + bssid + " capabilities = " + capabilities + " level = " + level + " frequency = " + frequency);
            }
            WifiListAdapter mAdapter = new WifiListAdapter(scanResults);
            mKqwRecyclerView.setAdapter(mAdapter);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 打开Wifi
     *
     * @param view view
     */
    public void openWifi(View view) {
        Toast.makeText(this, "打开Wifi", Toast.LENGTH_SHORT).show();
        mKqwWifiManager.openWifi();
    }

    /**
     * 关闭Wifi
     *
     * @param view view
     */
    public void closeWifi(View view) {
        Toast.makeText(this, "关闭Wifi", Toast.LENGTH_SHORT).show();
        mKqwWifiManager.closeWifi();
    }

    /**
     * @param view view
     */
    public void scan(View view) {
        Toast.makeText(this, "扫描", Toast.LENGTH_SHORT).show();
        mKqwWifiManager.startScan();
    }

    /**
     * @param view view
     */
    public void state(View view) {
        Toast.makeText(this, "获取当前网络状态", Toast.LENGTH_SHORT).show();
        mKqwWifiManager.startScan();
    }

    /**
     * @param view view
     */
    public void link(View view) {
        Toast.makeText(this, "连接到 BitMain_office", Toast.LENGTH_SHORT).show();
        WifiAdmin wifiAdmin = new WifiAdmin(this);
        wifiAdmin.openWifi();
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo("BitMain_office", "cisco!123", 3));
    }

    /**
     * @param view view
     */
    public void link2(View view) {
        Toast.makeText(this, "连接到 BitMain_download", Toast.LENGTH_SHORT).show();
        WifiAdmin wifiAdmin = new WifiAdmin(this);
        wifiAdmin.openWifi();
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo("BitMain_download", "cisco!123", 3));
    }


    /**
     * Wifi 列表的Item被点击
     *
     * @param v v
     */
    @Override
    public void onItemClick(RecyclerView.ViewHolder v) {
        Toast.makeText(this, "点击：" + v.getAdapterPosition(), Toast.LENGTH_SHORT).show();
    }
}
