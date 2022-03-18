package com.android.settings.network;

import com.android.internal.net.VpnProfile;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class VpnPreferenceController$$ExternalSyntheticLambda4 implements Predicate {
    public static final /* synthetic */ VpnPreferenceController$$ExternalSyntheticLambda4 INSTANCE = new VpnPreferenceController$$ExternalSyntheticLambda4();

    private /* synthetic */ VpnPreferenceController$$ExternalSyntheticLambda4() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$getInsecureVpnCount$4;
        lambda$getInsecureVpnCount$4 = VpnPreferenceController.lambda$getInsecureVpnCount$4((VpnProfile) obj);
        return lambda$getInsecureVpnCount$4;
    }
}
