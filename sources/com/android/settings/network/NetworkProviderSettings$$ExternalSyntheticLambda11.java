package com.android.settings.network;

import com.android.wifitrackerlib.WifiEntry;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class NetworkProviderSettings$$ExternalSyntheticLambda11 implements Predicate {
    public static final /* synthetic */ NetworkProviderSettings$$ExternalSyntheticLambda11 INSTANCE = new NetworkProviderSettings$$ExternalSyntheticLambda11();

    private /* synthetic */ NetworkProviderSettings$$ExternalSyntheticLambda11() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$onWifiEntriesChanged$8;
        lambda$onWifiEntriesChanged$8 = NetworkProviderSettings.lambda$onWifiEntriesChanged$8((WifiEntry) obj);
        return lambda$onWifiEntriesChanged$8;
    }
}
