package com.android.settings.display;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.window.R;
/* loaded from: classes.dex */
public class FontSizePreferenceFragmentForSetupWizard extends ToggleFontSizePreferenceFragment {
    @Override // com.android.settings.display.ToggleFontSizePreferenceFragment, com.android.settings.display.PreviewSeekBarPreferenceFragment
    protected int getActivityLayoutResId() {
        return R.layout.suw_font_size_fragment;
    }

    @Override // com.android.settings.display.ToggleFontSizePreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 369;
    }

    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        if (getResources().getBoolean(R.bool.config_supported_large_screen)) {
            ViewPager viewPager = (ViewPager) onCreateView.findViewById(R.id.preview_pager);
            LinearLayout linearLayout = (LinearLayout) ((View) viewPager.getAdapter().instantiateItem((ViewGroup) viewPager, viewPager.getCurrentItem())).findViewById(R.id.font_size_preview_text_group);
            linearLayout.setPaddingRelative(getResources().getDimensionPixelSize(R.dimen.font_size_preview_padding_start), linearLayout.getPaddingTop(), linearLayout.getPaddingEnd(), linearLayout.getPaddingBottom());
        }
        return onCreateView;
    }

    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        if (this.mCurrentIndex != this.mInitialIndex) {
            this.mMetricsFeatureProvider.action(getContext(), 369, this.mCurrentIndex);
        }
        super.onStop();
    }
}
