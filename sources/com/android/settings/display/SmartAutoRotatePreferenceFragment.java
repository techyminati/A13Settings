package com.android.settings.display;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.internal.view.RotationPolicy;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
/* loaded from: classes.dex */
public class SmartAutoRotatePreferenceFragment extends DashboardFragment {
    static final String AUTO_ROTATE_SWITCH_PREFERENCE_ID = "auto_rotate_switch";
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.auto_rotate_settings);
    private RotationPolicy.RotationPolicyListener mRotationPolicyListener;
    private AutoRotateSwitchBarController mSwitchBarController;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SmartAutoRotatePreferenceFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1867;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.auto_rotate_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((SmartAutoRotateController) use(SmartAutoRotateController.class)).init(getLifecycle());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        createHeader(settingsActivity);
        Preference findPreference = findPreference("footer_preference");
        if (findPreference != null) {
            findPreference.setTitle(Html.fromHtml(getString(R.string.smart_rotate_text_headline), 63));
            findPreference.setVisible(SmartAutoRotateController.isRotationResolverServiceAvailable(settingsActivity));
        }
        return onCreateView;
    }

    void createHeader(SettingsActivity settingsActivity) {
        if (SmartAutoRotateController.isRotationResolverServiceAvailable(settingsActivity)) {
            SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
            switchBar.setTitle(getContext().getString(R.string.auto_rotate_settings_primary_switch_title));
            switchBar.show();
            this.mSwitchBarController = new AutoRotateSwitchBarController(settingsActivity, switchBar, getSettingsLifecycle());
            findPreference(AUTO_ROTATE_SWITCH_PREFERENCE_ID).setVisible(false);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mRotationPolicyListener == null) {
            this.mRotationPolicyListener = new RotationPolicy.RotationPolicyListener() { // from class: com.android.settings.display.SmartAutoRotatePreferenceFragment.1
                public void onChange() {
                    if (SmartAutoRotatePreferenceFragment.this.mSwitchBarController != null) {
                        SmartAutoRotatePreferenceFragment.this.mSwitchBarController.onChange();
                    }
                }
            };
        }
        RotationPolicy.registerRotationPolicyListener(getPrefContext(), this.mRotationPolicyListener);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (this.mRotationPolicyListener != null) {
            RotationPolicy.unregisterRotationPolicyListener(getPrefContext(), this.mRotationPolicyListener);
        }
    }
}
