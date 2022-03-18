package com.android.settings.network;

import android.telephony.SubscriptionInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SubscriptionUtil$$ExternalSyntheticLambda13 implements Predicate {
    public static final /* synthetic */ SubscriptionUtil$$ExternalSyntheticLambda13 INSTANCE = new SubscriptionUtil$$ExternalSyntheticLambda13();

    private /* synthetic */ SubscriptionUtil$$ExternalSyntheticLambda13() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$getUniqueSubscriptionDisplayNames$0;
        lambda$getUniqueSubscriptionDisplayNames$0 = SubscriptionUtil.lambda$getUniqueSubscriptionDisplayNames$0((SubscriptionInfo) obj);
        return lambda$getUniqueSubscriptionDisplayNames$0;
    }
}
