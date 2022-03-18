package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.concurrent.Callable;
/* loaded from: classes.dex */
public class AlwaysOnVpnCurrentUserPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final DevicePolicyManager mDevicePolicyManager;
    private final EnterprisePrivacyFeatureProvider mFeatureProvider;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "always_on_vpn_primary_user";
    }

    public AlwaysOnVpnCurrentUserPreferenceController(Context context) {
        super(context);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context);
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mFeatureProvider.isInCompMode()) {
            preference.setTitle(this.mDevicePolicyManager.getString("Settings.ALWAYS_ON_VPN_PERSONAL_PROFILE", new Callable() { // from class: com.android.settings.enterprise.AlwaysOnVpnCurrentUserPreferenceController$$ExternalSyntheticLambda1
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    String lambda$updateState$0;
                    lambda$updateState$0 = AlwaysOnVpnCurrentUserPreferenceController.this.lambda$updateState$0();
                    return lambda$updateState$0;
                }
            }));
        } else {
            preference.setTitle(this.mDevicePolicyManager.getString("Settings.ALWAYS_ON_VPN_DEVICE", new Callable() { // from class: com.android.settings.enterprise.AlwaysOnVpnCurrentUserPreferenceController$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    String lambda$updateState$1;
                    lambda$updateState$1 = AlwaysOnVpnCurrentUserPreferenceController.this.lambda$updateState$1();
                    return lambda$updateState$1;
                }
            }));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateState$0() throws Exception {
        return this.mContext.getString(R.string.enterprise_privacy_always_on_vpn_personal);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateState$1() throws Exception {
        return this.mContext.getString(R.string.enterprise_privacy_always_on_vpn_device);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mFeatureProvider.isAlwaysOnVpnSetInCurrentUser();
    }
}
