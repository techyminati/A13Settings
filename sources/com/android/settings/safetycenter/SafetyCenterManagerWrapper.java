package com.android.settings.safetycenter;

import android.content.Context;
import android.safetycenter.SafetyCenterManager;
import android.safetycenter.SafetySourceData;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class SafetyCenterManagerWrapper {
    @VisibleForTesting
    public static SafetyCenterManagerWrapper sInstance;

    private SafetyCenterManagerWrapper() {
    }

    public static SafetyCenterManagerWrapper get() {
        if (sInstance == null) {
            sInstance = new SafetyCenterManagerWrapper();
        }
        return sInstance;
    }

    public void sendSafetyCenterUpdate(Context context, SafetySourceData safetySourceData) {
        SafetyCenterManager safetyCenterManager = (SafetyCenterManager) context.getSystemService(SafetyCenterManager.class);
        if (safetyCenterManager == null) {
            Log.e("SafetyCenterManagerWrapper", "System service SAFETY_CENTER_SERVICE (SafetyCenterManager) is null");
            return;
        }
        try {
            safetyCenterManager.sendSafetyCenterUpdate(safetySourceData);
        } catch (Exception e) {
            Log.e("SafetyCenterManagerWrapper", "Failed to send SafetySourceData", e);
        }
    }
}
