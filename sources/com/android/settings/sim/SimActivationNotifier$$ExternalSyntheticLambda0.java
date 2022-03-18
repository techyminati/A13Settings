package com.android.settings.sim;

import android.telephony.SubscriptionInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SimActivationNotifier$$ExternalSyntheticLambda0 implements Predicate {
    public static final /* synthetic */ SimActivationNotifier$$ExternalSyntheticLambda0 INSTANCE = new SimActivationNotifier$$ExternalSyntheticLambda0();

    private /* synthetic */ SimActivationNotifier$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$getActiveRemovableSub$0;
        lambda$getActiveRemovableSub$0 = SimActivationNotifier.lambda$getActiveRemovableSub$0((SubscriptionInfo) obj);
        return lambda$getActiveRemovableSub$0;
    }
}
