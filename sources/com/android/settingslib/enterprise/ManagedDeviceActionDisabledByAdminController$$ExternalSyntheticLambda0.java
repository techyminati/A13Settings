package com.android.settingslib.enterprise;

import android.content.Context;
import android.os.UserHandle;
import com.android.settingslib.enterprise.ManagedDeviceActionDisabledByAdminController;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ManagedDeviceActionDisabledByAdminController$$ExternalSyntheticLambda0 implements ManagedDeviceActionDisabledByAdminController.ForegroundUserChecker {
    public static final /* synthetic */ ManagedDeviceActionDisabledByAdminController$$ExternalSyntheticLambda0 INSTANCE = new ManagedDeviceActionDisabledByAdminController$$ExternalSyntheticLambda0();

    private /* synthetic */ ManagedDeviceActionDisabledByAdminController$$ExternalSyntheticLambda0() {
    }

    @Override // com.android.settingslib.enterprise.ManagedDeviceActionDisabledByAdminController.ForegroundUserChecker
    public final boolean isUserForeground(Context context, UserHandle userHandle) {
        boolean isUserForeground;
        isUserForeground = ManagedDeviceActionDisabledByAdminController.isUserForeground(context, userHandle);
        return isUserForeground;
    }
}
