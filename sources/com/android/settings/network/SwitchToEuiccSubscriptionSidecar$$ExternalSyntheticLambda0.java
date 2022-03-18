package com.android.settings.network;

import android.telephony.SubscriptionInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda0 implements Predicate {
    public static final /* synthetic */ SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda0 INSTANCE = new SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda0();

    private /* synthetic */ SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean isEmbedded;
        isEmbedded = ((SubscriptionInfo) obj).isEmbedded();
        return isEmbedded;
    }
}
