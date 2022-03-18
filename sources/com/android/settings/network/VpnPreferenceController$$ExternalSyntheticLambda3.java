package com.android.settings.network;

import com.android.internal.net.VpnConfig;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class VpnPreferenceController$$ExternalSyntheticLambda3 implements Predicate {
    public static final /* synthetic */ VpnPreferenceController$$ExternalSyntheticLambda3 INSTANCE = new VpnPreferenceController$$ExternalSyntheticLambda3();

    private /* synthetic */ VpnPreferenceController$$ExternalSyntheticLambda3() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$getNumberOfNonLegacyVpn$2;
        lambda$getNumberOfNonLegacyVpn$2 = VpnPreferenceController.lambda$getNumberOfNonLegacyVpn$2((VpnConfig) obj);
        return lambda$getNumberOfNonLegacyVpn$2;
    }
}
