package com.android.settings.datausage;

import com.android.settings.datausage.ChartDataUsagePreference;
import java.util.function.ToLongFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ChartDataUsagePreference$$ExternalSyntheticLambda4 implements ToLongFunction {
    public static final /* synthetic */ ChartDataUsagePreference$$ExternalSyntheticLambda4 INSTANCE = new ChartDataUsagePreference$$ExternalSyntheticLambda4();

    private /* synthetic */ ChartDataUsagePreference$$ExternalSyntheticLambda4() {
    }

    @Override // java.util.function.ToLongFunction
    public final long applyAsLong(Object obj) {
        return ((ChartDataUsagePreference.DataUsageSummaryNode) obj).getStartTime();
    }
}
