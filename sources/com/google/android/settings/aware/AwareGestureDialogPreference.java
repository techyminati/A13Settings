package com.google.android.settings.aware;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import androidx.appcompat.app.AlertDialog;
import androidx.window.R;
import com.android.settings.core.SubSettingLauncher;
/* loaded from: classes2.dex */
abstract class AwareGestureDialogPreference extends AwareDialogPreferenceBase implements DialogInterface.OnClickListener {
    abstract String getDestination();

    abstract int getDialogDisabledMessage();

    abstract int getGestureDialogMessage();

    abstract int getGestureDialogTitle();

    public AwareGestureDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onClick(DialogInterface dialogInterface, int i) {
        new SubSettingLauncher(getContext()).setDestination(AwareSettings.class.getName()).setSourceMetricsCategory(getSourceMetricsCategory()).launch();
    }

    @Override // com.google.android.settings.aware.AwareDialogPreferenceBase
    protected boolean isAvailable() {
        return this.mHelper.isGestureConfigurable();
    }

    @Override // com.google.android.settings.aware.AwareDialogPreferenceBase
    protected void performEnabledClick() {
        super.performEnabledClick();
        new SubSettingLauncher(getContext()).setDestination(getDestination()).setSourceMetricsCategory(getSourceMetricsCategory()).launch();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        super.onPrepareDialogBuilder(builder, onClickListener);
        if (!this.mHelper.isSupported()) {
            builder.setTitle(getGestureDialogTitle()).setMessage(getDialogDisabledMessage()).setPositiveButton(R.string.gesture_aware_confirmation_action_button, (DialogInterface.OnClickListener) null).setNegativeButton("", (DialogInterface.OnClickListener) null);
        } else {
            builder.setTitle(getGestureDialogTitle()).setMessage(getGestureDialogMessage()).setPositiveButton(R.string.aware_disabled_preference_action, this).setNegativeButton(R.string.aware_disabled_preference_neutral, (DialogInterface.OnClickListener) null);
        }
    }
}
