package com.google.android.settings.dream;

import android.content.Intent;
import android.os.Bundle;
import com.android.settings.SettingsActivity;
import com.google.android.setupdesign.util.ThemeHelper;
import com.google.android.setupdesign.util.ThemeResolver;
/* loaded from: classes2.dex */
public class DreamSetupActivity extends SettingsActivity {
    @Override // com.android.settings.core.SettingsBaseActivity
    protected boolean isToolbarEnabled() {
        return false;
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", DreamSetupFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return DreamSetupFragment.class.getName().equals(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTheme(ThemeResolver.getDefault().resolve(getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        super.onCreate(bundle);
    }
}
