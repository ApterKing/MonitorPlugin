package net.medlinker.monitorplugin;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class MonitorLoadPackage implements IXposedHookLoadPackage, Serializable {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log(MonitorLoadPackage.class.getName() + " | " + lpparam.packageName + " | " + lpparam.processName);
        hookForSystemServerContext();
    }

    public void hookForSystemServerContext() {

        Observable.interval(5000,1000, TimeUnit.MICROSECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Intent intent = new Intent("net.medlinker.monitor.BOOT");
                        SystemUtils.getCurrentContext().sendBroadcast(intent);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SystemUtils.startApp("net.medlinker.monitor");
            }
        }, 5000);
    }

}
