package com.android.settings.fuelgauge.batterytip.detectors;

import android.os.UidBatteryConsumer;
import java.util.Comparator;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class HighUsageDetector$$ExternalSyntheticLambda0 implements Comparator {
    public static final /* synthetic */ HighUsageDetector$$ExternalSyntheticLambda0 INSTANCE = new HighUsageDetector$$ExternalSyntheticLambda0();

    private /* synthetic */ HighUsageDetector$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$detect$0;
        lambda$detect$0 = HighUsageDetector.lambda$detect$0((UidBatteryConsumer) obj, (UidBatteryConsumer) obj2);
        return lambda$detect$0;
    }
}
