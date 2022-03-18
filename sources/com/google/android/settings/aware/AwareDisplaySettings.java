package com.google.android.settings.aware;

import android.content.ContentResolver;
import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.utils.CandidateInfoExtra;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.IllustrationPreference;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class AwareDisplaySettings extends RadioButtonPickerFragment {
    @VisibleForTesting
    static final String KEY_ALWAYS_ON = "aware_always_on";
    @VisibleForTesting
    static final String KEY_OFF = "aware_wake_off";
    @VisibleForTesting
    static final String KEY_WAKE_DISPLAY = "aware_wake_display";
    private static final int MY_USER = UserHandle.myUserId();
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.aware_wake_display_settings) { // from class: com.google.android.settings.aware.AwareDisplaySettings.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return SystemProperties.getBoolean("ro.vendor.aware_available", false);
        }
    };
    private AmbientDisplayConfiguration mConfig;
    private AwareHelper mHelper;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1750;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.aware_wake_display_settings;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mHelper = new AwareHelper(context);
        this.mConfig = new AmbientDisplayConfiguration(context);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public SelectorWithWidgetPreference bindPreference(SelectorWithWidgetPreference selectorWithWidgetPreference, String str, CandidateInfo candidateInfo, String str2) {
        if (candidateInfo instanceof CandidateInfoExtra) {
            selectorWithWidgetPreference.setSummary(((CandidateInfoExtra) candidateInfo).loadSummary());
            selectorWithWidgetPreference.setAppendixVisibility(8);
        }
        return super.bindPreference(selectorWithWidgetPreference, str, candidateInfo, str2);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected void addStaticPreferences(PreferenceScreen preferenceScreen) {
        IllustrationPreference illustrationPreference = new IllustrationPreference(getContext());
        illustrationPreference.setLottieAnimationResId(R.raw.lottie_aware_display);
        preferenceScreen.addPreference(illustrationPreference);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<? extends CandidateInfo> getCandidates() {
        Context context = getContext();
        ArrayList arrayList = new ArrayList();
        if (this.mHelper.isSupported()) {
            arrayList.add(new CandidateInfoExtra(context.getText(R.string.aware_wake_display_title), context.getText(R.string.aware_wake_display_summary), KEY_WAKE_DISPLAY, this.mHelper.isGestureConfigurable()));
        }
        if (this.mConfig.alwaysOnAvailableForUser(MY_USER)) {
            arrayList.add(new CandidateInfoExtra(context.getText(R.string.doze_always_on_title), context.getText(R.string.doze_always_on_summary), KEY_ALWAYS_ON, true));
        }
        arrayList.add(new CandidateInfoExtra(context.getText(R.string.switch_off_text), null, KEY_OFF, true));
        return arrayList;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        AmbientDisplayConfiguration ambientDisplayConfiguration = this.mConfig;
        int i = MY_USER;
        boolean wakeDisplayGestureEnabled = ambientDisplayConfiguration.wakeDisplayGestureEnabled(i);
        boolean alwaysOnEnabled = this.mConfig.alwaysOnEnabled(i);
        return (!wakeDisplayGestureEnabled || !this.mHelper.isGestureConfigurable() || !alwaysOnEnabled) ? alwaysOnEnabled ? KEY_ALWAYS_ON : KEY_OFF : KEY_WAKE_DISPLAY;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        ContentResolver contentResolver = getContext().getContentResolver();
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2133849746:
                if (str.equals(KEY_ALWAYS_ON)) {
                    c = 0;
                    break;
                }
                break;
            case -1652972184:
                if (str.equals(KEY_WAKE_DISPLAY)) {
                    c = 1;
                    break;
                }
                break;
            case 598899989:
                if (str.equals(KEY_OFF)) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.mHelper.writeFeatureEnabled("doze_always_on", true);
                Settings.Secure.putInt(contentResolver, "doze_always_on", 1);
                this.mHelper.writeFeatureEnabled("doze_wake_display_gesture", false);
                Settings.Secure.putInt(contentResolver, "doze_wake_display_gesture", 0);
                break;
            case 1:
                this.mHelper.writeFeatureEnabled("doze_always_on", true);
                Settings.Secure.putInt(contentResolver, "doze_always_on", 1);
                this.mHelper.writeFeatureEnabled("doze_wake_display_gesture", true);
                Settings.Secure.putInt(contentResolver, "doze_wake_display_gesture", 1);
                break;
            case 2:
                this.mHelper.writeFeatureEnabled("doze_always_on", false);
                Settings.Secure.putInt(contentResolver, "doze_always_on", 0);
                this.mHelper.writeFeatureEnabled("doze_wake_display_gesture", false);
                Settings.Secure.putInt(contentResolver, "doze_wake_display_gesture", 0);
                break;
        }
        return true;
    }
}
