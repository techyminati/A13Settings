package com.android.settings.applications.specialaccess.notificationaccess;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.window.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class FriendlyWarningDialogFragment extends InstrumentedDialogFragment {
    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 552;
    }

    public FriendlyWarningDialogFragment setServiceInfo(ComponentName componentName, CharSequence charSequence, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("c", componentName.flattenToString());
        bundle.putCharSequence("l", charSequence);
        setArguments(bundle);
        setTargetFragment(fragment, 0);
        return this;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        CharSequence charSequence = arguments.getCharSequence("l");
        final ComponentName unflattenFromString = ComponentName.unflattenFromString(arguments.getString("c"));
        final NotificationAccessDetails notificationAccessDetails = (NotificationAccessDetails) getTargetFragment();
        return new AlertDialog.Builder(getContext()).setMessage(getResources().getString(R.string.notification_listener_disable_warning_summary, charSequence)).setCancelable(true).setPositiveButton(R.string.notification_listener_disable_warning_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.specialaccess.notificationaccess.FriendlyWarningDialogFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                notificationAccessDetails.disable(unflattenFromString);
            }
        }).setNegativeButton(R.string.notification_listener_disable_warning_cancel, FriendlyWarningDialogFragment$$ExternalSyntheticLambda0.INSTANCE).create();
    }
}
