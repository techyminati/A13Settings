package com.android.settings.network.telephony;

import android.telephony.SubscriptionInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda1 implements Predicate {
    public static final /* synthetic */ ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda1 INSTANCE = new ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda1();

    private /* synthetic */ ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean isEmbedded;
        isEmbedded = ((SubscriptionInfo) obj).isEmbedded();
        return isEmbedded;
    }
}
