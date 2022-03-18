package com.android.settings.network;

import android.telephony.UiccCardInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda1 implements Predicate {
    public static final /* synthetic */ SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda1 INSTANCE = new SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda1();

    private /* synthetic */ SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean isMultipleEnabledProfilesSupported;
        isMultipleEnabledProfilesSupported = ((UiccCardInfo) obj).isMultipleEnabledProfilesSupported();
        return isMultipleEnabledProfilesSupported;
    }
}
