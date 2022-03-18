package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
/* loaded from: classes.dex */
public class SecondaryDisplayPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private final String[] mListSummaries;
    private final String[] mListValues;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "overlay_display_devices";
    }

    public SecondaryDisplayPreferenceController(Context context) {
        super(context);
        this.mListValues = context.getResources().getStringArray(R.array.overlay_display_devices_values);
        this.mListSummaries = context.getResources().getStringArray(R.array.overlay_display_devices_entries);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeSecondaryDisplayDevicesOption(obj.toString());
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateSecondaryDisplayDevicesOptions();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        writeSecondaryDisplayDevicesOption(null);
    }

    private void updateSecondaryDisplayDevicesOptions() {
        String string = Settings.Global.getString(this.mContext.getContentResolver(), "overlay_display_devices");
        int i = 0;
        int i2 = 0;
        while (true) {
            String[] strArr = this.mListValues;
            if (i2 >= strArr.length) {
                break;
            } else if (TextUtils.equals(string, strArr[i2])) {
                i = i2;
                break;
            } else {
                i2++;
            }
        }
        ListPreference listPreference = (ListPreference) this.mPreference;
        listPreference.setValue(this.mListValues[i]);
        listPreference.setSummary(this.mListSummaries[i]);
    }

    private void writeSecondaryDisplayDevicesOption(String str) {
        Settings.Global.putString(this.mContext.getContentResolver(), "overlay_display_devices", str);
        updateSecondaryDisplayDevicesOptions();
    }
}
