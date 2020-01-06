package net.medlinker.monitorplugin;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MonitorZygoteInit implements IXposedHookZygoteInit, Serializable {

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedBridge.log(MonitorZygoteInit.class.getName() + " | " + startupParam.modulePath + " | " + startupParam.startsSystemServer);
        hookForSystemServerContext();
    }

    public void hookForSystemServerContext() {
        Observable.interval(5000,1000, TimeUnit.MICROSECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Intent intent = new Intent("net.medlinker.monitor.BOOT");
                        getCurrentContext().sendBroadcast(intent);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private Context getCurrentContext() {
        try {
            Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]);
            if (callStaticMethod == null) {
                return null;
            }
            return (Context) XposedHelpers.callMethod(callStaticMethod, "getSystemContext", new Object[0]);
        } catch (Throwable th) {
            return null;
        }
    }

}
