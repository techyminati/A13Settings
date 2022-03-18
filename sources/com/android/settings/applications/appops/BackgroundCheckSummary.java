package com.android.settings.applications.appops;

import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentTransaction;
import androidx.window.R;
import com.android.settings.core.InstrumentedPreferenceFragment;
/* loaded from: classes.dex */
public class BackgroundCheckSummary extends InstrumentedPreferenceFragment {
    private LayoutInflater mInflater;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 258;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(R.string.background_check_pref);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mInflater = layoutInflater;
        View inflate = layoutInflater.inflate(R.layout.background_check_summary, viewGroup, false);
        if (viewGroup instanceof PreferenceFrameLayout) {
            inflate.getLayoutParams().removeBorders = true;
        }
        FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
        beginTransaction.add(R.id.appops_content, new AppOpsCategory(AppOpsState.RUN_IN_BACKGROUND_TEMPLATE), "appops");
        beginTransaction.commitAllowingStateLoss();
        return inflate;
    }
}
