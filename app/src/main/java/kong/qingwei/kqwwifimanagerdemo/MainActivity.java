package kong.qingwei.kqwwifimanagerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private KqwWifiManager mKqwWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mKqwWifiManager = new KqwWifiManager(this);
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
     *
     *
     * @param view view
     */
    public void scan(View view) {
        Toast.makeText(this, "扫描", Toast.LENGTH_SHORT).show();
        mKqwWifiManager.startScan();
    }
    /**
     *
     *
     * @param view view
     */
    public void state(View view) {
        Toast.makeText(this, "获取当前网络状态", Toast.LENGTH_SHORT).show();
        mKqwWifiManager.startScan();
    }

    /**
     *
     *
     * @param view view
     */
    public void link(View view) {
        Toast.makeText(this, "连接到 BitMain_office", Toast.LENGTH_SHORT).show();
        WifiAdmin wifiAdmin = new WifiAdmin(this);
        wifiAdmin.openWifi();
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo("BitMain_office", "cisco!123", 3));
    }
    /**
     *
     *
     * @param view view
     */
    public void link2(View view) {
        Toast.makeText(this, "连接到 BitMain_download", Toast.LENGTH_SHORT).show();
        WifiAdmin wifiAdmin = new WifiAdmin(this);
        wifiAdmin.openWifi();
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo("BitMain_download", "cisco!123", 3));
    }
}
