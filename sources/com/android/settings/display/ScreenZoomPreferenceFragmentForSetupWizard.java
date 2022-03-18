package com.android.settings.display;

import androidx.window.R;
/* loaded from: classes.dex */
public class ScreenZoomPreferenceFragmentForSetupWizard extends ScreenZoomSettings {
    @Override // com.android.settings.display.ScreenZoomSettings, com.android.settings.display.PreviewSeekBarPreferenceFragment
    protected int getActivityLayoutResId() {
        return R.layout.suw_screen_zoom_fragment;
    }

    @Override // com.android.settings.display.ScreenZoomSettings, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 370;
    }

    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        if (this.mCurrentIndex != this.mInitialIndex) {
            this.mMetricsFeatureProvider.action(getContext(), 370, this.mCurrentIndex);
        }
        super.onStop();
    }
}
