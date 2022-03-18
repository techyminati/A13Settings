package com.android.settings.wifi;

import com.android.wifitrackerlib.WifiEntry;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class WifiSettings$$ExternalSyntheticLambda9 implements Predicate {
    public static final /* synthetic */ WifiSettings$$ExternalSyntheticLambda9 INSTANCE = new WifiSettings$$ExternalSyntheticLambda9();

    private /* synthetic */ WifiSettings$$ExternalSyntheticLambda9() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$onWifiEntriesChanged$5;
        lambda$onWifiEntriesChanged$5 = WifiSettings.lambda$onWifiEntriesChanged$5((WifiEntry) obj);
        return lambda$onWifiEntriesChanged$5;
    }
}
