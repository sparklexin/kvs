package xin.sparkle.kvs.demo;

import android.app.Application;

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KVSInit.initialize(this);
    }
}
