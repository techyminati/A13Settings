package com.google.android.settings.aware;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import androidx.window.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.CustomDialogPreferenceCompat;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.google.android.settings.aware.AwareHelper;
/* loaded from: classes2.dex */
public class AwareDialogPreferenceBase extends CustomDialogPreferenceCompat {
    protected AwareHelper mHelper;
    private View mInfoIcon;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private View mSummary;
    private View mTitle;

    protected boolean isAvailable() {
        return false;
    }

    public AwareDialogPreferenceBase(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    public AwareDialogPreferenceBase(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public AwareDialogPreferenceBase(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public AwareDialogPreferenceBase(Context context) {
        super(context);
        init();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mTitle = preferenceViewHolder.findViewById(16908310);
        this.mSummary = preferenceViewHolder.findViewById(16908304);
        this.mInfoIcon = preferenceViewHolder.findViewById(R.id.info_button);
        updatePreference();
    }

    @Override // androidx.preference.Preference
    public void performClick() {
        if (isAvailable()) {
            performEnabledClick();
        } else {
            super.performClick();
        }
    }

    protected void updatePreference() {
        View view = this.mTitle;
        if (view != null) {
            view.setEnabled(isAvailable());
        }
        View view2 = this.mSummary;
        if (view2 != null) {
            view2.setEnabled(isAvailable());
        }
        if (this.mInfoIcon != null) {
            int i = 0;
            boolean z = isAvailable() || this.mHelper.isAirplaneModeOn() || this.mHelper.isBatterySaverModeOn();
            View view3 = this.mInfoIcon;
            if (z) {
                i = 8;
            }
            view3.setVisibility(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void performEnabledClick() {
        this.mMetricsFeatureProvider.logClickedPreference(this, getSourceMetricsCategory());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getSourceMetricsCategory() {
        return getExtras().getInt(DashboardFragment.CATEGORY);
    }

    private void init() {
        Context context = getContext();
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        setWidgetLayoutResource(R.layout.preference_widget_info);
        AwareHelper awareHelper = new AwareHelper(context);
        this.mHelper = awareHelper;
        awareHelper.register(new AwareHelper.Callback() { // from class: com.google.android.settings.aware.AwareDialogPreferenceBase.1
            @Override // com.google.android.settings.aware.AwareHelper.Callback
            public void onChange(Uri uri) {
                AwareDialogPreferenceBase.this.updatePreference();
                CharSequence summary = AwareDialogPreferenceBase.this.getSummary();
                if (!TextUtils.isEmpty(summary)) {
                    AwareDialogPreferenceBase.this.setSummary(summary);
                }
            }
        });
    }
}
