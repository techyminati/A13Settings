package com.google.android.settings.gestures.assist;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.res.Resources;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import androidx.window.R;
import com.android.internal.app.AssistUtils;
import com.android.internal.widget.ILockSettings;
import com.android.settings.gestures.AssistGestureFeatureProviderImpl;
/* loaded from: classes2.dex */
public class AssistGestureFeatureProviderGoogleImpl extends AssistGestureFeatureProviderImpl {
    @Override // com.android.settings.gestures.AssistGestureFeatureProviderImpl, com.android.settings.gestures.AssistGestureFeatureProvider
    public boolean isSupported(Context context) {
        return hasAssistGestureSensor(context) && isGsaCurrentAssistant(context) && isOpaEligible(context) && isOpaEnabled(context);
    }

    @Override // com.android.settings.gestures.AssistGestureFeatureProviderImpl, com.android.settings.gestures.AssistGestureFeatureProvider
    public boolean isSensorAvailable(Context context) {
        return hasAssistGestureSensor(context);
    }

    public boolean isDeskClockSupported(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Resources resources = context.getResources();
        try {
            PermissionInfo permissionInfo = packageManager.getPermissionInfo(resources.getString(R.string.gesture_assist_deskclock_permission), 0);
            if (permissionInfo != null) {
                if (permissionInfo.packageName.equals(resources.getString(R.string.gesture_assist_deskclock_package))) {
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
        return false;
    }

    private static boolean isOpaEligible(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "systemui.google.opa_enabled", 0, ActivityManager.getCurrentUser()) != 0;
    }

    public static boolean isOpaEnabled(Context context) {
        try {
            return ILockSettings.Stub.asInterface(ServiceManager.getService("lock_settings")).getBoolean("systemui.google.opa_user_enabled", false, ActivityManager.getCurrentUser());
        } catch (RemoteException e) {
            Log.e("AssistGestureFeatureProviderGoogleImpl", "isOpaEnabled RemoteException", e);
            return false;
        }
    }

    private static boolean isGsaCurrentAssistant(Context context) {
        String string = context.getResources().getString(R.string.gesture_assist_component);
        ComponentName assistComponentForUser = new AssistUtils(context).getAssistComponentForUser(UserHandle.myUserId());
        return assistComponentForUser != null && assistComponentForUser.flattenToString().equals(string);
    }

    private static boolean hasAssistGestureSensor(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.sensor.assist");
    }
}
