package com.android.settings.localepicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.window.R;
import com.android.internal.app.LocalePickerWithRegion;
import com.android.internal.app.LocaleStore;
import com.android.settings.core.SettingsBaseActivity;
import java.io.Serializable;
/* loaded from: classes.dex */
public class LocalePickerWithRegionActivity extends SettingsBaseActivity implements LocalePickerWithRegion.LocaleSelectedListener {
    @Override // com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().setTransition(4097).replace(R.id.content_frame, LocalePickerWithRegion.createLanguagePicker(this, this, false)).addToBackStack("localeListEditor").commit();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        handleBackPressed();
        return true;
    }

    public void onLocaleSelected(LocaleStore.LocaleInfo localeInfo) {
        Intent intent = new Intent();
        intent.putExtra("localeInfo", (Serializable) localeInfo);
        setResult(-1, intent);
        finish();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        handleBackPressed();
    }

    private void handleBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
            return;
        }
        setResult(0);
        finish();
    }
}
