package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.IntentFilter;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import java.util.concurrent.Callable;
/* loaded from: classes.dex */
public class ManageDeviceAdminPreferenceController extends BasePreferenceController {
    private final DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class);
    private final EnterprisePrivacyFeatureProvider mFeatureProvider;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ManageDeviceAdminPreferenceController(Context context, String str) {
        super(context, str);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int numberOfActiveDeviceAdminsForCurrentUserAndManagedProfile = this.mFeatureProvider.getNumberOfActiveDeviceAdminsForCurrentUserAndManagedProfile();
        if (numberOfActiveDeviceAdminsForCurrentUserAndManagedProfile == 0) {
            return this.mDevicePolicyManager.getString("Settings.NUMBER_OF_DEVICE_ADMINS_NONE", new Callable() { // from class: com.android.settings.enterprise.ManageDeviceAdminPreferenceController$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    String lambda$getSummary$0;
                    lambda$getSummary$0 = ManageDeviceAdminPreferenceController.this.lambda$getSummary$0();
                    return lambda$getSummary$0;
                }
            });
        }
        return this.mContext.getResources().getQuantityString(R.plurals.number_of_device_admins, numberOfActiveDeviceAdminsForCurrentUserAndManagedProfile, Integer.valueOf(numberOfActiveDeviceAdminsForCurrentUserAndManagedProfile));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getSummary$0() throws Exception {
        return this.mContext.getResources().getString(R.string.number_of_device_admins_none);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(R.bool.config_show_manage_device_admin) ? 0 : 3;
    }
}
