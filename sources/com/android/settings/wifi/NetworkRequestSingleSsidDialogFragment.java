package com.android.settings.wifi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.window.R;
/* loaded from: classes.dex */
public class NetworkRequestSingleSsidDialogFragment extends NetworkRequestDialogBaseFragment {
    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        boolean z;
        String str = "";
        if (getArguments() != null) {
            z = getArguments().getBoolean("DIALOG_IS_TRYAGAIN", true);
            str = getArguments().getString("DIALOG_REQUEST_SSID", str);
        } else {
            z = false;
        }
        Context context = getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.network_request_dialog_title, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.network_request_title_text)).setText(getTitle());
        ((TextView) inflate.findViewById(R.id.network_request_summary_text)).setText(getSummary());
        ((ProgressBar) inflate.findViewById(R.id.network_request_title_progress)).setVisibility(8);
        AlertDialog.Builder neutralButton = new AlertDialog.Builder(context).setCustomTitle(inflate).setMessage(str).setPositiveButton(z ? R.string.network_connection_timeout_dialog_ok : R.string.wifi_connect, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.NetworkRequestSingleSsidDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                NetworkRequestSingleSsidDialogFragment.this.lambda$onCreateDialog$0(dialogInterface, i);
            }
        }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.NetworkRequestSingleSsidDialogFragment$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                NetworkRequestSingleSsidDialogFragment.this.lambda$onCreateDialog$1(dialogInterface, i);
            }
        });
        setCancelable(false);
        return neutralButton.create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        onUserClickConnectButton();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        onCancel(dialogInterface);
    }

    private void onUserClickConnectButton() {
        NetworkRequestDialogActivity networkRequestDialogActivity = this.mActivity;
        if (networkRequestDialogActivity != null) {
            networkRequestDialogActivity.onClickConnectButton();
        }
    }
}
