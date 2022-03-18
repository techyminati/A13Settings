package com.android.settings.display;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.dream.DreamSettings;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class ScreenSaverPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "screensaver";
    }

    public ScreenSaverPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(17891615);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setSummary(DreamSettings.getSummaryTextWithDreamName(this.mContext));
    }
}
