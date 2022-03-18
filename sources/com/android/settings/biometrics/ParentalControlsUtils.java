package com.android.settings.biometrics;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.hardware.biometrics.ParentalControlsUtilsInternal;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes.dex */
public class ParentalControlsUtils {
    public static RestrictedLockUtils.EnforcedAdmin parentConsentRequired(Context context, int i) {
        UserHandle userHandle = new UserHandle(UserHandle.myUserId());
        if (!ParentalControlsUtilsInternal.isTestModeEnabled(context)) {
            return parentConsentRequiredInternal((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class), i, userHandle);
        }
        Log.d("ParentalControlsUtils", "Requiring consent for test flow");
        return new RestrictedLockUtils.EnforcedAdmin(null, "disallow_biometric", userHandle);
    }

    @VisibleForTesting
    static RestrictedLockUtils.EnforcedAdmin parentConsentRequiredInternal(DevicePolicyManager devicePolicyManager, int i, UserHandle userHandle) {
        if (ParentalControlsUtilsInternal.parentConsentRequired(devicePolicyManager, i, userHandle)) {
            return new RestrictedLockUtils.EnforcedAdmin(ParentalControlsUtilsInternal.getSupervisionComponentName(devicePolicyManager, userHandle), "disallow_biometric", userHandle);
        }
        return null;
    }
}
