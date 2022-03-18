package com.android.settings.dashboard;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.ThreadUtils;
/* loaded from: classes.dex */
public class ControllerTask implements Runnable {
    private final AbstractPreferenceController mController;
    private final int mMetricsCategory;
    private final MetricsFeatureProvider mMetricsFeature;
    private final PreferenceScreen mScreen;

    public ControllerTask(AbstractPreferenceController abstractPreferenceController, PreferenceScreen preferenceScreen, MetricsFeatureProvider metricsFeatureProvider, int i) {
        this.mController = abstractPreferenceController;
        this.mScreen = preferenceScreen;
        this.mMetricsFeature = metricsFeatureProvider;
        this.mMetricsCategory = i;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.mController.isAvailable()) {
            String preferenceKey = this.mController.getPreferenceKey();
            if (TextUtils.isEmpty(preferenceKey)) {
                Log.d("ControllerTask", String.format("Preference key is %s in Controller %s", preferenceKey, this.mController.getClass().getSimpleName()));
                return;
            }
            final Preference findPreference = this.mScreen.findPreference(preferenceKey);
            if (findPreference == null) {
                Log.d("ControllerTask", String.format("Cannot find preference with key %s in Controller %s", preferenceKey, this.mController.getClass().getSimpleName()));
            } else {
                ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.dashboard.ControllerTask$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ControllerTask.this.lambda$run$0(findPreference);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$run$0(Preference preference) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        this.mController.updateState(preference);
        int elapsedRealtime2 = (int) (SystemClock.elapsedRealtime() - elapsedRealtime);
        if (elapsedRealtime2 > 50) {
            Log.w("ControllerTask", "The updateState took " + elapsedRealtime2 + " ms in Controller " + this.mController.getClass().getSimpleName());
            MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeature;
            if (metricsFeatureProvider != null) {
                metricsFeatureProvider.action(0, 1728, this.mMetricsCategory, this.mController.getClass().getSimpleName(), elapsedRealtime2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractPreferenceController getController() {
        return this.mController;
    }
}
