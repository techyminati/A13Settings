package com.android.settings.network;

import android.telephony.SubscriptionInfo;
import java.util.function.ToIntFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda2 implements ToIntFunction {
    public static final /* synthetic */ SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda2 INSTANCE = new SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda2();

    private /* synthetic */ SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        return ((SubscriptionInfo) obj).getPortIndex();
    }
}
