package com.android.settings;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.appcompat.app.AlertDialog;
import androidx.window.R;
import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes.dex */
public class MonitoringCertInfoActivity extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private int mUserId;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int intExtra = getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
        this.mUserId = intExtra;
        UserHandle of = intExtra == -10000 ? null : UserHandle.of(intExtra);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DevicePolicyManager.class);
        int intExtra2 = getIntent().getIntExtra("android.settings.extra.number_of_certificates", 1);
        CharSequence quantityText = getResources().getQuantityText(RestrictedLockUtils.getProfileOrDeviceOwner(this, of) != null ? R.plurals.ssl_ca_cert_settings_button : R.plurals.ssl_ca_cert_dialog_title, intExtra2);
        setTitle(quantityText);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(quantityText);
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getQuantityText(R.plurals.ssl_ca_cert_settings_button, intExtra2), this);
        builder.setNeutralButton(R.string.cancel, null);
        builder.setOnDismissListener(this);
        if (devicePolicyManager.getProfileOwnerAsUser(this.mUserId) != null) {
            builder.setMessage(getResources().getQuantityString(R.plurals.ssl_ca_cert_info_message, intExtra2, devicePolicyManager.getProfileOwnerNameAsUser(this.mUserId)));
        } else if (devicePolicyManager.getDeviceOwnerComponentOnCallingUser() != null) {
            builder.setMessage(getResources().getQuantityString(R.plurals.ssl_ca_cert_info_message_device_owner, intExtra2, devicePolicyManager.getDeviceOwnerNameOnAnyUser()));
        } else {
            builder.setIcon(17301624);
            builder.setMessage(R.string.ssl_ca_cert_warning_message);
        }
        builder.show();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("com.android.settings.TRUSTED_CREDENTIALS_USER");
        intent.setFlags(335544320);
        intent.putExtra("ARG_SHOW_NEW_FOR_USER", this.mUserId);
        startActivity(intent);
        finish();
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }
}
