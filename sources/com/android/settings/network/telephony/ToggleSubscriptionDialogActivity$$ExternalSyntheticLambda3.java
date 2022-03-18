package com.android.settings.network.telephony;

import android.telephony.UiccCardInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda3 implements Predicate {
    public static final /* synthetic */ ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda3 INSTANCE = new ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda3();

    private /* synthetic */ ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda3() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean isMultipleEnabledProfilesSupported;
        isMultipleEnabledProfilesSupported = ((UiccCardInfo) obj).isMultipleEnabledProfilesSupported();
        return isMultipleEnabledProfilesSupported;
    }
}
