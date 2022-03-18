package com.google.android.settings.aware;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import androidx.appcompat.app.AlertDialog;
import androidx.window.R;
import com.android.settings.core.SubSettingLauncher;
/* loaded from: classes2.dex */
public class AwareSettingsDialogPreference extends AwareDialogPreferenceBase {
    public AwareSettingsDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AwareSettingsDialogPreference(Context context) {
        super(context);
    }

    @Override // com.google.android.settings.aware.AwareDialogPreferenceBase
    protected boolean isAvailable() {
        return this.mHelper.isAvailable();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.aware.AwareDialogPreferenceBase
    public void performEnabledClick() {
        super.performEnabledClick();
        new SubSettingLauncher(getContext()).setDestination(AwareSettings.class.getName()).setSourceMetricsCategory(getSourceMetricsCategory()).launch();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        super.onPrepareDialogBuilder(builder, onClickListener);
        builder.setTitle(R.string.aware_settings_disabled_info_dialog_title).setMessage(R.string.aware_settings_disabled_info_dialog_content).setPositiveButton(R.string.nfc_how_it_works_got_it, (DialogInterface.OnClickListener) null).setNegativeButton("", (DialogInterface.OnClickListener) null);
    }
}
