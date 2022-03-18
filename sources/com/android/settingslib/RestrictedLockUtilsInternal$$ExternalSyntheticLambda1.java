package com.android.settingslib;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import com.android.settingslib.RestrictedLockUtilsInternal;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class RestrictedLockUtilsInternal$$ExternalSyntheticLambda1 implements RestrictedLockUtilsInternal.LockSettingCheck {
    public static final /* synthetic */ RestrictedLockUtilsInternal$$ExternalSyntheticLambda1 INSTANCE = new RestrictedLockUtilsInternal$$ExternalSyntheticLambda1();

    private /* synthetic */ RestrictedLockUtilsInternal$$ExternalSyntheticLambda1() {
    }

    @Override // com.android.settingslib.RestrictedLockUtilsInternal.LockSettingCheck
    public final boolean isEnforcing(DevicePolicyManager devicePolicyManager, ComponentName componentName, int i) {
        boolean lambda$checkIfPasswordQualityIsSet$1;
        lambda$checkIfPasswordQualityIsSet$1 = RestrictedLockUtilsInternal.lambda$checkIfPasswordQualityIsSet$1(devicePolicyManager, componentName, i);
        return lambda$checkIfPasswordQualityIsSet$1;
    }
}
