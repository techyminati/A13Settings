package com.google.android.settings.gestures.columbus;

import com.android.settings.SettingsActivity;
/* loaded from: classes2.dex */
public class ColumbusSettingsActivity extends SettingsActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ColumbusSettings.class.getName().equals(str);
    }
}
