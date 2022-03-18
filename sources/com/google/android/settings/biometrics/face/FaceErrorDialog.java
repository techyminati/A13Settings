package com.google.android.settings.biometrics.face;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.window.R;
import com.google.android.settings.biometrics.face.FaceEnrollDialogFactory;
/* loaded from: classes2.dex */
public class FaceErrorDialog extends DialogFragment {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static FaceErrorDialog newInstance(CharSequence charSequence, int i, boolean z, boolean z2) {
        FaceErrorDialog faceErrorDialog = new FaceErrorDialog();
        Bundle bundle = new Bundle();
        bundle.putCharSequence("error_msg", charSequence);
        bundle.putInt("error_id", i);
        bundle.putBoolean("require_diversity", z);
        bundle.putBoolean("from_suw", z2);
        faceErrorDialog.setArguments(bundle);
        return faceErrorDialog;
    }

    private void finishWithResult(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        FragmentActivity activity = getActivity();
        activity.setResult(i);
        activity.finish();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        CharSequence charSequence = getArguments().getCharSequence("error_msg");
        final int i = getArguments().getInt("error_id");
        final boolean z = i == 3 && getArguments().getBoolean("require_diversity");
        boolean z2 = getArguments().getBoolean("from_suw");
        DialogInterface.OnClickListener faceErrorDialog$$ExternalSyntheticLambda0 = new DialogInterface.OnClickListener() { // from class: com.google.android.settings.biometrics.face.FaceErrorDialog$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                FaceErrorDialog.this.lambda$onCreateDialog$0(z, i, dialogInterface, i2);
            }
        };
        FaceEnrollDialogFactory.DialogBuilder newBuilder = FaceEnrollDialogFactory.newBuilder(getActivity());
        if (z) {
            newBuilder.setTitle(R.string.security_settings_face_enroll_timeout_title);
            newBuilder.setMessage(R.string.security_settings_face_enroll_timeout_message);
            newBuilder.setPositiveButton(R.string.security_settings_face_enroll_timeout_use_fast_setup, faceErrorDialog$$ExternalSyntheticLambda0);
            newBuilder.setNegativeButton(R.string.security_settings_face_enroll_timeout_try_again, faceErrorDialog$$ExternalSyntheticLambda0);
        } else if (i == 1003) {
            newBuilder.setTitle(R.string.security_settings_face_enroll_too_hot_title);
            newBuilder.setMessage(R.string.security_settings_face_enroll_too_hot_message);
            if (z2) {
                newBuilder.setPositiveButton(R.string.security_settings_face_enroll_too_hot_skip_face_unlock, faceErrorDialog$$ExternalSyntheticLambda0);
            } else {
                newBuilder.setPositiveButton(R.string.security_settings_face_enroll_too_hot_exit_setup, faceErrorDialog$$ExternalSyntheticLambda0);
            }
        } else {
            newBuilder.setTitle(R.string.security_settings_face_enroll_error_dialog_title);
            newBuilder.setMessage(charSequence);
            newBuilder.setPositiveButton(R.string.security_settings_face_enroll_dialog_ok, faceErrorDialog$$ExternalSyntheticLambda0);
        }
        return newBuilder.setOnBackKeyListener(new FaceEnrollDialogFactory.OnBackKeyListener() { // from class: com.google.android.settings.biometrics.face.FaceErrorDialog$$ExternalSyntheticLambda1
            @Override // com.google.android.settings.biometrics.face.FaceEnrollDialogFactory.OnBackKeyListener
            public final void onBackKeyUp(DialogInterface dialogInterface, KeyEvent keyEvent) {
                FaceErrorDialog.this.lambda$onCreateDialog$1(dialogInterface, keyEvent);
            }
        }).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(boolean z, int i, DialogInterface dialogInterface, int i2) {
        if (!z) {
            int i3 = 2;
            if (!(i == 3 || i == 1003)) {
                i3 = 1;
            }
            if (i2 == -1) {
                finishWithResult(dialogInterface, i3);
            }
        } else if (i2 == -1) {
            getActivity().overridePendingTransition(R.anim.sud_slide_next_in, R.anim.sud_slide_next_out);
            finishWithResult(dialogInterface, 4);
        } else if (i2 == -2) {
            finishWithResult(dialogInterface, 5);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, KeyEvent keyEvent) {
        finishWithResult(dialogInterface, 2);
    }
}
