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
public class ImePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final EnterprisePrivacyFeatureProvider mFeatureProvider;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "input_method";
    }

    public ImePreferenceController(Context context) {
        super(context);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setSummary(((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).getString("Settings.ADMIN_ACTION_SET_INPUT_METHOD_NAME", new Callable() { // from class: com.android.settings.enterprise.ImePreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                String lambda$updateState$0;
                lambda$updateState$0 = ImePreferenceController.this.lambda$updateState$0();
                return lambda$updateState$0;
            }
        }, new Object[]{this.mFeatureProvider.getImeLabelIfOwnerSet()}));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateState$0() throws Exception {
        return this.mContext.getResources().getString(R.string.enterprise_privacy_input_method_name, this.mFeatureProvider.getImeLabelIfOwnerSet());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mFeatureProvider.getImeLabelIfOwnerSet() != null;
    }
}
