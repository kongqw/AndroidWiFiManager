package kong.qingwei.kqwwifimanagerdemo;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by kongqingwei on 2017/3/8.
 * ExampleApplication
 */

public class ExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
