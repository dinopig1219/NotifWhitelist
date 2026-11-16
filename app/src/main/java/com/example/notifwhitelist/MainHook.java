package com.example.notifwhitelist;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    private static final String TAG = "[NotifWhitelist] ";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!"com.android.systemui".equals(lpparam.packageName)) return;
        ClassLoader cl = lpparam.classLoader;

        // 国际版不处理（与原模块一致）
        try {
            Class<?> build = XposedHelpers.findClassIfExists("miui.os.Build", cl);
            if (build != null && XposedHelpers.getStaticBooleanField(build, "IS_INTERNATIONAL_BUILD")) {
                XposedBridge.log(TAG + "international build, skip");
                return;
            }
        } catch (Throwable t) {
            XposedBridge.log(TAG + "check build failed: " + t);
        }

        Class<?> c = XposedHelpers.findClassIfExists(
                "com.miui.systemui.notification.NotificationSettingsManager", cl);
        if (c == null) {
            XposedBridge.log(TAG + "class not found");
            return;
        }
        try {
            XposedHelpers.setStaticBooleanField(c, "USE_WHITE_LISTS", false);
            XposedBridge.log(TAG + "USE_WHITE_LISTS=false");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "set USE_WHITE_LISTS failed: " + t);
        }
    }
}
