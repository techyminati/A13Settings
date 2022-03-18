package com.google.android.settings.biometrics.face;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
/* loaded from: classes2.dex */
public class FaceEnrollDialogFactory {

    /* loaded from: classes2.dex */
    public interface OnBackKeyListener {
        void onBackKeyUp(DialogInterface dialogInterface, KeyEvent keyEvent);
    }

    public static DialogBuilder newBuilder(Context context) {
        return new DialogBuilder(context);
    }

    /* loaded from: classes2.dex */
    public static class DialogBuilder {
        private AlertDialog.Builder mBuilder;
        private OnBackKeyListener mOnBackKeyListener;

        private DialogBuilder(Context context) {
            this.mBuilder = new AlertDialog.Builder(context);
        }

        public DialogBuilder setTitle(int i) {
            this.mBuilder.setTitle(i);
            return this;
        }

        public DialogBuilder setMessage(int i) {
            this.mBuilder.setMessage(i);
            return this;
        }

        public DialogBuilder setMessage(CharSequence charSequence) {
            this.mBuilder.setMessage(charSequence);
            return this;
        }

        public DialogBuilder setPositiveButton(int i, DialogInterface.OnClickListener onClickListener) {
            this.mBuilder.setPositiveButton(i, onClickListener);
            return this;
        }

        public DialogBuilder setNegativeButton(int i, DialogInterface.OnClickListener onClickListener) {
            this.mBuilder.setNegativeButton(i, onClickListener);
            return this;
        }

        public DialogBuilder setOnBackKeyListener(OnBackKeyListener onBackKeyListener) {
            this.mOnBackKeyListener = onBackKeyListener;
            return this;
        }

        public Dialog build() {
            AlertDialog create = this.mBuilder.setCancelable(false).create();
            create.setCanceledOnTouchOutside(false);
            if (this.mOnBackKeyListener != null) {
                create.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.google.android.settings.biometrics.face.FaceEnrollDialogFactory.DialogBuilder.1
                    private boolean mCanceled = false;

                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        if (i != 4) {
                            return false;
                        }
                        if (keyEvent.getAction() == 1 && !this.mCanceled) {
                            this.mCanceled = true;
                            DialogBuilder.this.mOnBackKeyListener.onBackKeyUp(dialogInterface, keyEvent);
                        }
                        return true;
                    }
                });
            }
            return create;
        }
    }
}
