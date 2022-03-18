package com.android.settings.password;

import com.android.settings.password.ConfirmLockPassword;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ConfirmLockPassword$ConfirmLockPasswordFragment$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ConfirmLockPassword.ConfirmLockPasswordFragment f$0;

    public /* synthetic */ ConfirmLockPassword$ConfirmLockPasswordFragment$$ExternalSyntheticLambda1(ConfirmLockPassword.ConfirmLockPasswordFragment confirmLockPasswordFragment) {
        this.f$0 = confirmLockPasswordFragment;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.updatePasswordEntry();
    }
}
