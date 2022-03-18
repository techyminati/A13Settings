package com.android.settings.safetycenter;

import android.content.Context;
import android.safetycenter.SafetyCenterManager;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class SafetyCenterStatusHolder {
    @VisibleForTesting
    public static SafetyCenterStatusHolder sInstance;

    private SafetyCenterStatusHolder() {
    }

    public static SafetyCenterStatusHolder get() {
        if (sInstance == null) {
            sInstance = new SafetyCenterStatusHolder();
        }
        return sInstance;
    }

    public boolean isEnabled(Context context) {
        if (context == null) {
            Log.e("SafetyCenterStatusHolder", "Context is null at SafetyCenterStatusHolder#isEnabled");
            return false;
        }
        SafetyCenterManager safetyCenterManager = (SafetyCenterManager) context.getSystemService(SafetyCenterManager.class);
        if (safetyCenterManager == null) {
            Log.w("SafetyCenterStatusHolder", "System service SAFETY_CENTER_SERVICE (SafetyCenterManager) is null");
            return false;
        }
        try {
            return safetyCenterManager.isSafetyCenterEnabled();
        } catch (RuntimeException e) {
            Log.e("SafetyCenterStatusHolder", "Calling isSafetyCenterEnabled failed.", e);
            return false;
        }
    }
}
