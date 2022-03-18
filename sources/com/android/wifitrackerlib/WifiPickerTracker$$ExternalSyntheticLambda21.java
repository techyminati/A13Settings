package com.android.wifitrackerlib;

import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda21 implements Predicate {
    public static final /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda21 INSTANCE = new WifiPickerTracker$$ExternalSyntheticLambda21();

    private /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda21() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$updateWifiEntries$2;
        lambda$updateWifiEntries$2 = WifiPickerTracker.lambda$updateWifiEntries$2((PasspointWifiEntry) obj);
        return lambda$updateWifiEntries$2;
    }
}
