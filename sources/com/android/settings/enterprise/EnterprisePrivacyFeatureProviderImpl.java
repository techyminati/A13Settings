package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.VpnManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import androidx.window.R;
import com.android.settings.vpn2.VpnUtils;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
/* loaded from: classes.dex */
public class EnterprisePrivacyFeatureProviderImpl implements EnterprisePrivacyFeatureProvider {
    private static final int MY_USER_ID = UserHandle.myUserId();
    private final ConnectivityManager mCm;
    private final Context mContext;
    private final DevicePolicyManager mDpm;
    private final PackageManager mPm;
    private final Resources mResources;
    private final UserManager mUm;
    private final VpnManager mVm;

    public EnterprisePrivacyFeatureProviderImpl(Context context, DevicePolicyManager devicePolicyManager, PackageManager packageManager, UserManager userManager, ConnectivityManager connectivityManager, VpnManager vpnManager, Resources resources) {
        this.mContext = context.getApplicationContext();
        this.mDpm = devicePolicyManager;
        this.mPm = packageManager;
        this.mUm = userManager;
        this.mCm = connectivityManager;
        this.mVm = vpnManager;
        this.mResources = resources;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean hasDeviceOwner() {
        return getDeviceOwnerComponent() != null;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isInCompMode() {
        return hasDeviceOwner() && getManagedProfileUserId() != -10000;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public String getDeviceOwnerOrganizationName() {
        CharSequence deviceOwnerOrganizationName = this.mDpm.getDeviceOwnerOrganizationName();
        if (deviceOwnerOrganizationName == null) {
            return null;
        }
        return deviceOwnerOrganizationName.toString();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public CharSequence getDeviceOwnerDisclosure() {
        if (!hasDeviceOwner()) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        final CharSequence deviceOwnerOrganizationName = this.mDpm.getDeviceOwnerOrganizationName();
        if (deviceOwnerOrganizationName != null) {
            spannableStringBuilder.append((CharSequence) this.mDpm.getString("Settings.DEVICE_MANAGED_WITH_NAME", new Callable() { // from class: com.android.settings.enterprise.EnterprisePrivacyFeatureProviderImpl$$ExternalSyntheticLambda1
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    String lambda$getDeviceOwnerDisclosure$0;
                    lambda$getDeviceOwnerDisclosure$0 = EnterprisePrivacyFeatureProviderImpl.this.lambda$getDeviceOwnerDisclosure$0(deviceOwnerOrganizationName);
                    return lambda$getDeviceOwnerDisclosure$0;
                }
            }, new Object[]{deviceOwnerOrganizationName}));
        } else {
            spannableStringBuilder.append((CharSequence) this.mDpm.getString("Settings.DEVICE_MANAGED_WITHOUT_NAME", new Callable() { // from class: com.android.settings.enterprise.EnterprisePrivacyFeatureProviderImpl$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    String lambda$getDeviceOwnerDisclosure$1;
                    lambda$getDeviceOwnerDisclosure$1 = EnterprisePrivacyFeatureProviderImpl.this.lambda$getDeviceOwnerDisclosure$1();
                    return lambda$getDeviceOwnerDisclosure$1;
                }
            }));
        }
        return spannableStringBuilder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDeviceOwnerDisclosure$0(CharSequence charSequence) throws Exception {
        return this.mResources.getString(R.string.do_disclosure_with_name, charSequence);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDeviceOwnerDisclosure$1() throws Exception {
        return this.mResources.getString(R.string.do_disclosure_generic);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public Date getLastSecurityLogRetrievalTime() {
        long lastSecurityLogRetrievalTime = this.mDpm.getLastSecurityLogRetrievalTime();
        if (lastSecurityLogRetrievalTime < 0) {
            return null;
        }
        return new Date(lastSecurityLogRetrievalTime);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public Date getLastBugReportRequestTime() {
        long lastBugReportRequestTime = this.mDpm.getLastBugReportRequestTime();
        if (lastBugReportRequestTime < 0) {
            return null;
        }
        return new Date(lastBugReportRequestTime);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public Date getLastNetworkLogRetrievalTime() {
        long lastNetworkLogRetrievalTime = this.mDpm.getLastNetworkLogRetrievalTime();
        if (lastNetworkLogRetrievalTime < 0) {
            return null;
        }
        return new Date(lastNetworkLogRetrievalTime);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isSecurityLoggingEnabled() {
        return this.mDpm.isSecurityLoggingEnabled(null);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isNetworkLoggingEnabled() {
        return this.mDpm.isNetworkLoggingEnabled(null);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isAlwaysOnVpnSetInCurrentUser() {
        return VpnUtils.isAlwaysOnVpnSet(this.mVm, MY_USER_ID);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isAlwaysOnVpnSetInManagedProfile() {
        int managedProfileUserId = getManagedProfileUserId();
        return managedProfileUserId != -10000 && VpnUtils.isAlwaysOnVpnSet(this.mVm, managedProfileUserId);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getMaximumFailedPasswordsBeforeWipeInCurrentUser() {
        ComponentName deviceOwnerComponentOnCallingUser = this.mDpm.getDeviceOwnerComponentOnCallingUser();
        if (deviceOwnerComponentOnCallingUser == null) {
            deviceOwnerComponentOnCallingUser = this.mDpm.getProfileOwnerAsUser(MY_USER_ID);
        }
        if (deviceOwnerComponentOnCallingUser == null) {
            return 0;
        }
        return this.mDpm.getMaximumFailedPasswordsForWipe(deviceOwnerComponentOnCallingUser, MY_USER_ID);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getMaximumFailedPasswordsBeforeWipeInManagedProfile() {
        ComponentName profileOwnerAsUser;
        int managedProfileUserId = getManagedProfileUserId();
        if (managedProfileUserId == -10000 || (profileOwnerAsUser = this.mDpm.getProfileOwnerAsUser(managedProfileUserId)) == null) {
            return 0;
        }
        return this.mDpm.getMaximumFailedPasswordsForWipe(profileOwnerAsUser, managedProfileUserId);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public String getImeLabelIfOwnerSet() {
        if (!this.mDpm.isCurrentInputMethodSetByOwner()) {
            return null;
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int i = MY_USER_ID;
        String stringForUser = Settings.Secure.getStringForUser(contentResolver, "default_input_method", i);
        if (stringForUser == null) {
            return null;
        }
        try {
            return this.mPm.getApplicationInfoAsUser(stringForUser, 0, i).loadLabel(this.mPm).toString();
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getNumberOfOwnerInstalledCaCertsForCurrentUser() {
        List ownerInstalledCaCerts = this.mDpm.getOwnerInstalledCaCerts(new UserHandle(MY_USER_ID));
        if (ownerInstalledCaCerts == null) {
            return 0;
        }
        return ownerInstalledCaCerts.size();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getNumberOfOwnerInstalledCaCertsForManagedProfile() {
        List ownerInstalledCaCerts;
        int managedProfileUserId = getManagedProfileUserId();
        if (managedProfileUserId == -10000 || (ownerInstalledCaCerts = this.mDpm.getOwnerInstalledCaCerts(new UserHandle(managedProfileUserId))) == null) {
            return 0;
        }
        return ownerInstalledCaCerts.size();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getNumberOfActiveDeviceAdminsForCurrentUserAndManagedProfile() {
        int i = 0;
        for (UserInfo userInfo : this.mUm.getProfiles(MY_USER_ID)) {
            List activeAdminsAsUser = this.mDpm.getActiveAdminsAsUser(userInfo.id);
            if (activeAdminsAsUser != null) {
                i += activeAdminsAsUser.size();
            }
        }
        return i;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean hasWorkPolicyInfo() {
        return (getWorkPolicyInfoIntentDO() == null && getWorkPolicyInfoIntentPO() == null) ? false : true;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean showWorkPolicyInfo(Context context) {
        Intent workPolicyInfoIntentDO = getWorkPolicyInfoIntentDO();
        if (workPolicyInfoIntentDO != null) {
            context.startActivity(workPolicyInfoIntentDO);
            return true;
        }
        Intent workPolicyInfoIntentPO = getWorkPolicyInfoIntentPO();
        UserInfo managedProfileUserInfo = getManagedProfileUserInfo();
        if (workPolicyInfoIntentPO == null || managedProfileUserInfo == null) {
            return false;
        }
        context.startActivityAsUser(workPolicyInfoIntentPO, managedProfileUserInfo.getUserHandle());
        return true;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean showParentalControls() {
        Intent parentalControlsIntent = getParentalControlsIntent();
        if (parentalControlsIntent == null) {
            return false;
        }
        this.mContext.startActivity(parentalControlsIntent);
        return true;
    }

    private Intent getParentalControlsIntent() {
        DevicePolicyManager devicePolicyManager = this.mDpm;
        int i = MY_USER_ID;
        ComponentName profileOwnerOrDeviceOwnerSupervisionComponent = devicePolicyManager.getProfileOwnerOrDeviceOwnerSupervisionComponent(new UserHandle(i));
        if (profileOwnerOrDeviceOwnerSupervisionComponent == null) {
            return null;
        }
        Intent addFlags = new Intent("android.settings.SHOW_PARENTAL_CONTROLS").setPackage(profileOwnerOrDeviceOwnerSupervisionComponent.getPackageName()).addFlags(268435456);
        if (this.mPm.queryIntentActivitiesAsUser(addFlags, 0, i).size() != 0) {
            return addFlags;
        }
        return null;
    }

    private ComponentName getDeviceOwnerComponent() {
        if (!this.mPm.hasSystemFeature("android.software.device_admin")) {
            return null;
        }
        return this.mDpm.getDeviceOwnerComponentOnAnyUser();
    }

    private UserInfo getManagedProfileUserInfo() {
        for (UserInfo userInfo : this.mUm.getProfiles(MY_USER_ID)) {
            if (userInfo.isManagedProfile()) {
                return userInfo;
            }
        }
        return null;
    }

    private int getManagedProfileUserId() {
        UserInfo managedProfileUserInfo = getManagedProfileUserInfo();
        if (managedProfileUserInfo != null) {
            return managedProfileUserInfo.id;
        }
        return -10000;
    }

    private Intent getWorkPolicyInfoIntentDO() {
        ComponentName deviceOwnerComponent = getDeviceOwnerComponent();
        if (deviceOwnerComponent == null) {
            return null;
        }
        Intent intent = new Intent("android.settings.SHOW_WORK_POLICY_INFO").setPackage(deviceOwnerComponent.getPackageName());
        if (this.mPm.queryIntentActivities(intent, 0).size() != 0) {
            return intent;
        }
        return null;
    }

    private Intent getWorkPolicyInfoIntentPO() {
        ComponentName profileOwnerAsUser;
        int managedProfileUserId = getManagedProfileUserId();
        if (managedProfileUserId == -10000 || (profileOwnerAsUser = this.mDpm.getProfileOwnerAsUser(managedProfileUserId)) == null) {
            return null;
        }
        Intent intent = new Intent("android.settings.SHOW_WORK_POLICY_INFO").setPackage(profileOwnerAsUser.getPackageName());
        if (this.mPm.queryIntentActivitiesAsUser(intent, 0, managedProfileUserId).size() != 0) {
            return intent;
        }
        return null;
    }
}
