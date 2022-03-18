package com.android.settings.network;

import com.android.settings.wifi.slice.WifiSliceItem;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ProviderModelSliceHelper$$ExternalSyntheticLambda2 implements Predicate {
    public static final /* synthetic */ ProviderModelSliceHelper$$ExternalSyntheticLambda2 INSTANCE = new ProviderModelSliceHelper$$ExternalSyntheticLambda2();

    private /* synthetic */ ProviderModelSliceHelper$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$getConnectedWifiItem$0;
        lambda$getConnectedWifiItem$0 = ProviderModelSliceHelper.lambda$getConnectedWifiItem$0((WifiSliceItem) obj);
        return lambda$getConnectedWifiItem$0;
    }
}
