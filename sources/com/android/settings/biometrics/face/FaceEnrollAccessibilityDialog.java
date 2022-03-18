package com.android.settings.biometrics.face;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.window.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class FaceEnrollAccessibilityDialog extends InstrumentedDialogFragment {
    private DialogInterface.OnClickListener mPositiveButtonListener;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1506;
    }

    public static FaceEnrollAccessibilityDialog newInstance() {
        return new FaceEnrollAccessibilityDialog();
    }

    public void setPositiveButtonListener(DialogInterface.OnClickListener onClickListener) {
        this.mPositiveButtonListener = onClickListener;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.security_settings_face_enroll_education_accessibility_dialog_message).setNegativeButton(R.string.security_settings_face_enroll_education_accessibility_dialog_negative, FaceEnrollAccessibilityDialog$$ExternalSyntheticLambda1.INSTANCE).setPositiveButton(R.string.security_settings_face_enroll_education_accessibility_dialog_positive, new DialogInterface.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceEnrollAccessibilityDialog$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                FaceEnrollAccessibilityDialog.this.lambda$onCreateDialog$1(dialogInterface, i);
            }
        });
        return builder.create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        this.mPositiveButtonListener.onClick(dialogInterface, i);
    }
}
