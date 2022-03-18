package com.android.settingslib.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.hardware.biometrics.ParentalControlsUtilsInternal;
import android.os.UserHandle;
import android.text.TextUtils;
/* loaded from: classes.dex */
public final class ActionDisabledByAdminControllerFactory {
    public static ActionDisabledByAdminController createInstance(Context context, String str, DeviceAdminStringProvider deviceAdminStringProvider, UserHandle userHandle) {
        if (doesBiometricRequireParentalConsent(context, str)) {
            return new BiometricActionDisabledByAdminController(deviceAdminStringProvider);
        }
        if (isFinancedDevice(context)) {
            return new FinancedDeviceActionDisabledByAdminController(deviceAdminStringProvider);
        }
        return new ManagedDeviceActionDisabledByAdminController(deviceAdminStringProvider, userHandle, ManagedDeviceActionDisabledByAdminController.DEFAULT_FOREGROUND_USER_CHECKER, ActionDisabledLearnMoreButtonLauncher.DEFAULT_RESOLVE_ACTIVITY_CHECKER);
    }

    private static boolean doesBiometricRequireParentalConsent(Context context, String str) {
        if (!TextUtils.equals("disallow_biometric", str)) {
            return false;
        }
        return ParentalControlsUtilsInternal.parentConsentRequired(context, (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class), 14, new UserHandle(UserHandle.myUserId()));
    }

    private static boolean isFinancedDevice(Context context) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        return devicePolicyManager.isDeviceManaged() && devicePolicyManager.getDeviceOwnerType(devicePolicyManager.getDeviceOwnerComponentOnAnyUser()) == 1;
    }
}
