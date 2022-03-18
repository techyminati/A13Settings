package com.android.settings.network.telephony;

import android.os.Bundle;
import android.telephony.SubscriptionManager;
import androidx.fragment.app.FragmentActivity;
/* loaded from: classes.dex */
public class SubscriptionActionDialogActivity extends FragmentActivity {
    protected SubscriptionManager mSubscriptionManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSubscriptionManager = (SubscriptionManager) getSystemService(SubscriptionManager.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showProgressDialog(String str) {
        ProgressDialogFragment.show(getFragmentManager(), str, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dismissProgressDialog() {
        ProgressDialogFragment.dismiss(getFragmentManager());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showErrorDialog(String str, String str2) {
        AlertDialogFragment.show(this, str, str2);
    }
}
