package com.android.settings.biometrics.fingerprint;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.window.R;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.android.settings.biometrics.BiometricErrorDialog;
/* loaded from: classes.dex */
public class FingerprintErrorDialog extends BiometricErrorDialog {
    private static int getErrorMessage(int i) {
        return i != 3 ? i != 18 ? R.string.security_settings_fingerprint_enroll_error_generic_dialog_message : R.string.security_settings_fingerprint_bad_calibration : R.string.security_settings_fingerprint_enroll_error_timeout_dialog_message;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 569;
    }

    @Override // com.android.settings.biometrics.BiometricErrorDialog
    public int getOkButtonTextResId() {
        return R.string.security_settings_fingerprint_enroll_dialog_ok;
    }

    @Override // com.android.settings.biometrics.BiometricErrorDialog
    public int getTitleResId() {
        return R.string.security_settings_fingerprint_enroll_error_dialog_title;
    }

    public static void showErrorDialog(BiometricEnrollBase biometricEnrollBase, int i) {
        if (!biometricEnrollBase.isFinishing()) {
            FragmentManager supportFragmentManager = biometricEnrollBase.getSupportFragmentManager();
            if (!supportFragmentManager.isDestroyed() && !supportFragmentManager.isStateSaved()) {
                newInstance(biometricEnrollBase.getText(getErrorMessage(i)), i).show(supportFragmentManager, FingerprintErrorDialog.class.getName());
            }
        }
    }

    private static FingerprintErrorDialog newInstance(CharSequence charSequence, int i) {
        FingerprintErrorDialog fingerprintErrorDialog = new FingerprintErrorDialog();
        Bundle bundle = new Bundle();
        bundle.putCharSequence("error_msg", charSequence);
        bundle.putInt("error_id", i);
        fingerprintErrorDialog.setArguments(bundle);
        return fingerprintErrorDialog;
    }
}
