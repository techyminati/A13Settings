package com.android.settings.accessibility.rtt;

import android.telephony.SubscriptionInfo;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class TelecomUtil$$ExternalSyntheticLambda0 implements Function {
    public static final /* synthetic */ TelecomUtil$$ExternalSyntheticLambda0 INSTANCE = new TelecomUtil$$ExternalSyntheticLambda0();

    private /* synthetic */ TelecomUtil$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return Integer.valueOf(((SubscriptionInfo) obj).getSubscriptionId());
    }
}
