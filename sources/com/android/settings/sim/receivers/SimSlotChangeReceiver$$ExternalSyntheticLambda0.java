package com.android.settings.sim.receivers;

import android.content.BroadcastReceiver;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SimSlotChangeReceiver$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ BroadcastReceiver.PendingResult f$0;

    public /* synthetic */ SimSlotChangeReceiver$$ExternalSyntheticLambda0(BroadcastReceiver.PendingResult pendingResult) {
        this.f$0 = pendingResult;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.finish();
    }
}
