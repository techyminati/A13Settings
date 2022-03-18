package com.android.settingslib;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import com.android.settingslib.RestrictedLockUtilsInternal;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class RestrictedLockUtilsInternal$$ExternalSyntheticLambda2 implements RestrictedLockUtilsInternal.LockSettingCheck {
    public static final /* synthetic */ RestrictedLockUtilsInternal$$ExternalSyntheticLambda2 INSTANCE = new RestrictedLockUtilsInternal$$ExternalSyntheticLambda2();

    private /* synthetic */ RestrictedLockUtilsInternal$$ExternalSyntheticLambda2() {
    }

    @Override // com.android.settingslib.RestrictedLockUtilsInternal.LockSettingCheck
    public final boolean isEnforcing(DevicePolicyManager devicePolicyManager, ComponentName componentName, int i) {
        boolean lambda$checkIfMaximumTimeToLockIsSet$2;
        lambda$checkIfMaximumTimeToLockIsSet$2 = RestrictedLockUtilsInternal.lambda$checkIfMaximumTimeToLockIsSet$2(devicePolicyManager, componentName, i);
        return lambda$checkIfMaximumTimeToLockIsSet$2;
    }
}
