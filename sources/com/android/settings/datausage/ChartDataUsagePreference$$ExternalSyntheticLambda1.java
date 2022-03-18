package com.android.settings.datausage;

import com.android.settings.datausage.ChartDataUsagePreference;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ChartDataUsagePreference$$ExternalSyntheticLambda1 implements Function {
    public static final /* synthetic */ ChartDataUsagePreference$$ExternalSyntheticLambda1 INSTANCE = new ChartDataUsagePreference$$ExternalSyntheticLambda1();

    private /* synthetic */ ChartDataUsagePreference$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return Integer.valueOf(((ChartDataUsagePreference.DataUsageSummaryNode) obj).getDataUsagePercentage());
    }
}
