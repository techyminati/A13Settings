package com.android.wifitrackerlib;

import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda22 implements Predicate {
    public static final /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda22 INSTANCE = new WifiPickerTracker$$ExternalSyntheticLambda22();

    private /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda22() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$updateWifiEntries$6;
        lambda$updateWifiEntries$6 = WifiPickerTracker.lambda$updateWifiEntries$6((PasspointWifiEntry) obj);
        return lambda$updateWifiEntries$6;
    }
}
