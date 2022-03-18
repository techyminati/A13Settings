package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda18 implements Predicate {
    public static final /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda18 INSTANCE = new WifiPickerTracker$$ExternalSyntheticLambda18();

    private /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda18() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$updateSuggestedWifiEntryScans$12;
        lambda$updateSuggestedWifiEntryScans$12 = WifiPickerTracker.lambda$updateSuggestedWifiEntryScans$12((ScanResult) obj);
        return lambda$updateSuggestedWifiEntryScans$12;
    }
}
