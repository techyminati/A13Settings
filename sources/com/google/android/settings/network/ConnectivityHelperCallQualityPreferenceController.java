package com.google.android.settings.network;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.widget.FooterPreference;
/* loaded from: classes2.dex */
public class ConnectivityHelperCallQualityPreferenceController extends ConnectivityHelperBasePreferenceController {
    private static final String KEY_PREFERENCE_CATEGORY = "connectivity_helper_call_quality_category";
    private static final String KEY_PREFERENCE_FOOTER = "connectivity_helper_footer";
    private static final String LOG_TAG = "ch_callQuality";

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController
    public String getKeyName() {
        return "on_device_notifications";
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController
    public String getLogTag() {
        return LOG_TAG;
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ConnectivityHelperCallQualityPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ((PreferenceCategory) preferenceScreen.findPreference(KEY_PREFERENCE_CATEGORY)).setVisible(isAvailable());
        FooterPreference footerPreference = (FooterPreference) preferenceScreen.findPreference(KEY_PREFERENCE_FOOTER);
        footerPreference.setVisible(isAvailable());
        if (!TextUtils.isEmpty(this.mContext.getString(R.string.help_url_connectivity_helper))) {
            footerPreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.google.android.settings.network.ConnectivityHelperCallQualityPreferenceController$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ConnectivityHelperCallQualityPreferenceController.this.lambda$displayPreference$0(view);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(View view) {
        Context context = this.mContext;
        ((Activity) this.mContext).startActivityForResult(HelpUtils.getHelpIntent(context, context.getString(R.string.help_url_connectivity_helper), getClass().getName()), 0);
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController
    public boolean getDefaultValue() {
        return this.mContext.getResources().getBoolean(R.bool.config_connectivity_helper_call_quality_default_value);
    }

    @Override // com.google.android.settings.network.ConnectivityHelperBasePreferenceController
    public boolean getDeviceSupport() {
        return this.mContext.getResources().getBoolean(R.bool.config_show_connectivity_helper_call_quality);
    }
}
