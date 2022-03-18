package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.text.format.DateUtils;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Date;
import java.util.concurrent.Callable;
/* loaded from: classes.dex */
public abstract class AdminActionPreferenceControllerBase extends AbstractPreferenceController implements PreferenceControllerMixin {
    protected final EnterprisePrivacyFeatureProvider mFeatureProvider;

    protected abstract Date getAdminActionTimestamp();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AdminActionPreferenceControllerBase(Context context) {
        super(context);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        String str;
        Date adminActionTimestamp = getAdminActionTimestamp();
        if (adminActionTimestamp == null) {
            str = getEnterprisePrivacyNone();
        } else {
            str = DateUtils.formatDateTime(this.mContext, adminActionTimestamp.getTime(), 17);
        }
        preference.setSummary(str);
    }

    private String getEnterprisePrivacyNone() {
        return ((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).getString("Settings.ADMIN_ACTION_NONE", new Callable() { // from class: com.android.settings.enterprise.AdminActionPreferenceControllerBase$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                String lambda$getEnterprisePrivacyNone$0;
                lambda$getEnterprisePrivacyNone$0 = AdminActionPreferenceControllerBase.this.lambda$getEnterprisePrivacyNone$0();
                return lambda$getEnterprisePrivacyNone$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getEnterprisePrivacyNone$0() throws Exception {
        return this.mContext.getString(R.string.enterprise_privacy_none);
    }
}
