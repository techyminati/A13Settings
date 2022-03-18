package com.android.settings.wifi;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.window.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class WifiScanModeActivity extends FragmentActivity {
    private String mApp;
    private DialogFragment mDialog;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        Intent intent = getIntent();
        if (bundle != null) {
            this.mApp = bundle.getString("app");
        } else if (intent == null || !"android.net.wifi.action.REQUEST_SCAN_ALWAYS_AVAILABLE".equals(intent.getAction())) {
            finish();
            return;
        } else {
            this.mApp = getCallingPackage();
            try {
                PackageManager packageManager = getPackageManager();
                this.mApp = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.mApp, 0));
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        createDialog();
    }

    private void createDialog() {
        if (this.mDialog == null) {
            AlertDialogFragment newInstance = AlertDialogFragment.newInstance(this.mApp);
            this.mDialog = newInstance;
            newInstance.show(getSupportFragmentManager(), "dialog");
        }
    }

    private void dismissDialog() {
        DialogFragment dialogFragment = this.mDialog;
        if (dialogFragment != null) {
            dialogFragment.dismiss();
            this.mDialog = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPositiveClick() {
        ((WifiManager) getApplicationContext().getSystemService(WifiManager.class)).setScanAlwaysAvailable(true);
        setResult(-1);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doNegativeClick() {
        setResult(0);
        finish();
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("app", this.mApp);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        dismissDialog();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        createDialog();
    }

    /* loaded from: classes.dex */
    public static class AlertDialogFragment extends InstrumentedDialogFragment {
        private final String mApp;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 543;
        }

        static AlertDialogFragment newInstance(String str) {
            return new AlertDialogFragment(str);
        }

        public AlertDialogFragment(String str) {
            this.mApp = str;
        }

        public AlertDialogFragment() {
            this.mApp = null;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getActivity()).setMessage(TextUtils.isEmpty(this.mApp) ? getString(R.string.wifi_scan_always_turn_on_message_unknown) : getString(R.string.wifi_scan_always_turnon_message, this.mApp)).setPositiveButton(R.string.wifi_scan_always_confirm_allow, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.WifiScanModeActivity.AlertDialogFragment.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((WifiScanModeActivity) AlertDialogFragment.this.getActivity()).doPositiveClick();
                }
            }).setNegativeButton(R.string.wifi_scan_always_confirm_deny, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.WifiScanModeActivity.AlertDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((WifiScanModeActivity) AlertDialogFragment.this.getActivity()).doNegativeClick();
                }
            }).create();
        }

        @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialogInterface) {
            ((WifiScanModeActivity) getActivity()).doNegativeClick();
        }
    }
}
