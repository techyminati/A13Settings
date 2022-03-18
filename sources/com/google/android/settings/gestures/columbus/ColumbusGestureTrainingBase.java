package com.google.android.settings.gestures.columbus;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import com.android.settings.SubSettings;
import com.android.settings.core.InstrumentedActivity;
import com.google.android.settings.gestures.columbus.ColumbusGestureHelper;
/* loaded from: classes2.dex */
public abstract class ColumbusGestureTrainingBase extends InstrumentedActivity implements ColumbusGestureHelper.GestureListener {
    protected ColumbusGestureHelper mColumbusGestureHelper;
    private boolean mColumbusWasEnabled;
    private Boolean mEnableColumbusOnPause;
    private String mLaunchedFrom;

    @Override // com.google.android.settings.gestures.columbus.ColumbusGestureHelper.GestureListener
    public void onTrigger() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ColumbusGestureHelper columbusGestureHelper = new ColumbusGestureHelper(getApplicationContext());
        this.mColumbusGestureHelper = columbusGestureHelper;
        columbusGestureHelper.setListener(this);
        this.mLaunchedFrom = getIntent().getStringExtra("launched_from");
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        boolean isColumbusEnabled = ColumbusPreferenceController.isColumbusEnabled(getApplicationContext());
        this.mColumbusWasEnabled = isColumbusEnabled;
        if (!isColumbusEnabled) {
            Settings.Secure.putIntForUser(getContentResolver(), "columbus_enabled", 1, ActivityManager.getCurrentUser());
        }
        this.mColumbusGestureHelper.bindToColumbusServiceProxy();
        this.mColumbusGestureHelper.setListener(this);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        this.mColumbusGestureHelper.setListener(null);
        this.mColumbusGestureHelper.unbindFromColumbusServiceProxy();
        if (!this.mEnableColumbusOnPause.booleanValue() && !this.mColumbusWasEnabled) {
            Settings.Secure.putIntForUser(getContentResolver(), "columbus_enabled", 0, ActivityManager.getCurrentUser());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void launchColumbusGestureSettings(int i) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClass(this, SubSettings.class);
        intent.putExtra(":settings:show_fragment", ColumbusSettings.class.getName());
        intent.putExtra(":settings:source_metrics", i);
        startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean flowTypeSetup() {
        return "setup".contentEquals(this.mLaunchedFrom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean flowTypeDeferredSetup() {
        return "deferred_setup".contentEquals(this.mLaunchedFrom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean flowTypeSettingsSuggestion() {
        return "settings_suggestion".contentEquals(this.mLaunchedFrom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean flowTypeAccidentalTrigger() {
        return "accidental_trigger".contentEquals(this.mLaunchedFrom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleDone() {
        setResult(-1);
        this.mColumbusGestureHelper.setListener(null);
        finishAndRemoveTask();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setEnableColumbusOnPause() {
        this.mEnableColumbusOnPause = Boolean.TRUE;
    }
}
