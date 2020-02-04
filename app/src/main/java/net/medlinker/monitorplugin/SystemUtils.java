package net.medlinker.monitorplugin;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

import de.robv.android.xposed.XposedHelpers;

public class SystemUtils {

    public static void startApp(String pkg) {
        try {
            PackageManager mPackageManager = getCurrentContext().getPackageManager();
            PackageInfo packageInfo = mPackageManager.getPackageInfo(pkg, 0);
            if (packageInfo != null) {
                ActivityManager mActivityManager = (ActivityManager) getCurrentContext().getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> infos = mActivityManager.getRunningAppProcesses();
                Boolean running = false;
                for (ActivityManager.RunningAppProcessInfo info: infos) {
                    if (info.processName.equals(pkg)) {
                        running = true;
                        break;
                    }
                }
                if (!running) {
                    Intent intent = mPackageManager.getLaunchIntentForPackage(pkg);
                    getCurrentContext().startActivity(intent);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
    }


    public static Context getCurrentContext() {
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
