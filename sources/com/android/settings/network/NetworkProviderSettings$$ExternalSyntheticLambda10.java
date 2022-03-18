package com.android.settings.network;

import com.android.wifitrackerlib.WifiEntry;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class NetworkProviderSettings$$ExternalSyntheticLambda10 implements Predicate {
    public static final /* synthetic */ NetworkProviderSettings$$ExternalSyntheticLambda10 INSTANCE = new NetworkProviderSettings$$ExternalSyntheticLambda10();

    private /* synthetic */ NetworkProviderSettings$$ExternalSyntheticLambda10() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$onWifiEntriesChanged$7;
        lambda$onWifiEntriesChanged$7 = NetworkProviderSettings.lambda$onWifiEntriesChanged$7((WifiEntry) obj);
        return lambda$onWifiEntriesChanged$7;
    }
}
