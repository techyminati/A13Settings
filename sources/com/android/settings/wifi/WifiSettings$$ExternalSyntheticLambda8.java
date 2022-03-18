package com.android.settings.wifi;

import com.android.wifitrackerlib.WifiEntry;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class WifiSettings$$ExternalSyntheticLambda8 implements Predicate {
    public static final /* synthetic */ WifiSettings$$ExternalSyntheticLambda8 INSTANCE = new WifiSettings$$ExternalSyntheticLambda8();

    private /* synthetic */ WifiSettings$$ExternalSyntheticLambda8() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$onWifiEntriesChanged$4;
        lambda$onWifiEntriesChanged$4 = WifiSettings.lambda$onWifiEntriesChanged$4((WifiEntry) obj);
        return lambda$onWifiEntriesChanged$4;
    }
}
