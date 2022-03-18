package com.google.android.settings.games;

import com.android.settings.SettingsActivity;
/* loaded from: classes2.dex */
public class GameSettingsActivity extends SettingsActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return GameSettings.class.getName().equals(str);
    }
}
