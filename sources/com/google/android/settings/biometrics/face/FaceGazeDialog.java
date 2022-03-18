package com.google.android.settings.biometrics.face;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.window.R;
/* loaded from: classes2.dex */
public class FaceGazeDialog extends DialogFragment {
    private DialogInterface.OnClickListener mButtonListener;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static FaceGazeDialog newInstance() {
        return new FaceGazeDialog();
    }

    public void setButtonListener(DialogInterface.OnClickListener onClickListener) {
        this.mButtonListener = onClickListener;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return FaceEnrollDialogFactory.newBuilder(getActivity()).setTitle(R.string.face_enrolling_gaze_dialog_title).setMessage(R.string.face_enrolling_gaze_dialog_message).setPositiveButton(R.string.face_enrolling_gaze_dialog_continue, new DialogInterface.OnClickListener() { // from class: com.google.android.settings.biometrics.face.FaceGazeDialog$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                FaceGazeDialog.this.lambda$onCreateDialog$0(dialogInterface, i);
            }
        }).setNegativeButton(R.string.face_enrolling_gaze_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.google.android.settings.biometrics.face.FaceGazeDialog$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                FaceGazeDialog.this.lambda$onCreateDialog$1(dialogInterface, i);
            }
        }).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        this.mButtonListener.onClick(dialogInterface, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        this.mButtonListener.onClick(dialogInterface, i);
    }
}
