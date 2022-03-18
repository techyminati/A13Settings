package com.android.settings.notification;

import android.content.Intent;
import androidx.window.R;
import com.android.settings.SettingsActivity;
import com.android.settings.notification.RedactionInterstitial;
/* loaded from: classes.dex */
public class RedactionSettingsStandalone extends SettingsActivity {
    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", RedactionInterstitial.RedactionInterstitialFragment.class.getName()).putExtra("extra_prefs_show_button_bar", true).putExtra("extra_prefs_set_back_text", (String) null).putExtra("extra_prefs_set_next_text", getString(R.string.app_notifications_dialog_done));
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return RedactionInterstitial.RedactionInterstitialFragment.class.getName().equals(str);
    }
}
