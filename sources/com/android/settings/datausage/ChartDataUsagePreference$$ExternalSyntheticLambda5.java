package com.android.settings.datausage;

import com.android.settingslib.net.NetworkCycleData;
import java.util.function.ToLongFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ChartDataUsagePreference$$ExternalSyntheticLambda5 implements ToLongFunction {
    public static final /* synthetic */ ChartDataUsagePreference$$ExternalSyntheticLambda5 INSTANCE = new ChartDataUsagePreference$$ExternalSyntheticLambda5();

    private /* synthetic */ ChartDataUsagePreference$$ExternalSyntheticLambda5() {
    }

    @Override // java.util.function.ToLongFunction
    public final long applyAsLong(Object obj) {
        return ((NetworkCycleData) obj).getTotalUsage();
    }
}
