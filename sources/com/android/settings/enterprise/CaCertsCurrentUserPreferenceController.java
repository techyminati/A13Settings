package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import androidx.preference.Preference;
import androidx.window.R;
import java.util.concurrent.Callable;
/* loaded from: classes.dex */
public class CaCertsCurrentUserPreferenceController extends CaCertsPreferenceControllerBase {
    static final String CA_CERTS_CURRENT_USER = "ca_certs_current_user";
    DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class);

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return CA_CERTS_CURRENT_USER;
    }

    public CaCertsCurrentUserPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settings.enterprise.CaCertsPreferenceControllerBase, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mFeatureProvider.isInCompMode()) {
            preference.setTitle(this.mDevicePolicyManager.getString("Settings.CA_CERTS_PERSONAL_PROFILE", new Callable() { // from class: com.android.settings.enterprise.CaCertsCurrentUserPreferenceController$$ExternalSyntheticLambda1
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    String lambda$updateState$0;
                    lambda$updateState$0 = CaCertsCurrentUserPreferenceController.this.lambda$updateState$0();
                    return lambda$updateState$0;
                }
            }));
        } else {
            preference.setTitle(this.mDevicePolicyManager.getString("Settings.CA_CERTS_DEVICE", new Callable() { // from class: com.android.settings.enterprise.CaCertsCurrentUserPreferenceController$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    String lambda$updateState$1;
                    lambda$updateState$1 = CaCertsCurrentUserPreferenceController.this.lambda$updateState$1();
                    return lambda$updateState$1;
                }
            }));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateState$0() throws Exception {
        return this.mContext.getString(R.string.enterprise_privacy_ca_certs_personal);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateState$1() throws Exception {
        return this.mContext.getString(R.string.enterprise_privacy_ca_certs_device);
    }

    @Override // com.android.settings.enterprise.CaCertsPreferenceControllerBase
    protected int getNumberOfCaCerts() {
        return this.mFeatureProvider.getNumberOfOwnerInstalledCaCertsForCurrentUser();
    }
}
