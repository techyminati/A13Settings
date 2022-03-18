package com.android.settings.network.telephony;

import android.telephony.UiccSlotInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda5 implements Predicate {
    public static final /* synthetic */ ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda5 INSTANCE = new ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda5();

    private /* synthetic */ ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda5() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$isRemovableSimEnabled$4;
        lambda$isRemovableSimEnabled$4 = ToggleSubscriptionDialogActivity.lambda$isRemovableSimEnabled$4((UiccSlotInfo) obj);
        return lambda$isRemovableSimEnabled$4;
    }
}
