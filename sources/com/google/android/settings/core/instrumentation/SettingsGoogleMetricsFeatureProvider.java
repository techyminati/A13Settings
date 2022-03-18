package com.google.android.settings.core.instrumentation;

import com.android.settings.core.instrumentation.SettingsMetricsFeatureProvider;
/* loaded from: classes2.dex */
public class SettingsGoogleMetricsFeatureProvider extends SettingsMetricsFeatureProvider {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.instrumentation.SettingsMetricsFeatureProvider, com.android.settingslib.core.instrumentation.MetricsFeatureProvider
    public void installLogWriters() {
        super.installLogWriters();
        this.mLoggerWriters.add(new SearchResultTraceLogWriter());
    }
}
