package kong.qingwei.kqwwifimanagerdemo.adapter;

import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kong.qingwei.kqwwifimanagerdemo.R;

/**
 * Created by kqw on 2016/8/2.
 * Wifi列表的数据适配器
 */
public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ViewHolder> {

    private ArrayList<ScanResult> mScanResults;

    // RecyclerView.ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView ssid;

        public ViewHolder(View v) {
            super(v);
            ssid = (TextView) v.findViewById(R.id.ssid);
        }
    }

    // 初始化
    public WifiListAdapter(List<ScanResult> scanResults) {
        if (null != scanResults) {
            mScanResults.addAll(scanResults);
        }
    }

    // 用来创建新视图（由布局管理器调用）
    @Override
    public WifiListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi, parent, false));
    }

    // 用来替换视图的内容（由布局管理器调用）
    @Override
    public void onBindViewHolder(WifiListAdapter.ViewHolder holder, int position) {
        holder.ssid.setText(mScanResults.get(position).SSID);
    }

    // 返回数据集的大小（由布局管理器调用）
    @Override
    public int getItemCount() {
        return mScanResults.size();
    }
}