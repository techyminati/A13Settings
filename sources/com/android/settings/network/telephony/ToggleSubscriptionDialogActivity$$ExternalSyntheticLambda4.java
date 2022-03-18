package com.android.settings.network.telephony;

import android.telephony.UiccPortInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda4 implements Predicate {
    public static final /* synthetic */ ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda4 INSTANCE = new ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda4();

    private /* synthetic */ ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda4() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean isActive;
        isActive = ((UiccPortInfo) obj).isActive();
        return isActive;
    }
}
