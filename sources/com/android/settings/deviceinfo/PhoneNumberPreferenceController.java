package com.android.settings.deviceinfo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.DeviceInfoUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class PhoneNumberPreferenceController extends BasePreferenceController {
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_PREFERENCE_CATEGORY = "basic_info_category";
    private final List<Preference> mPreferenceList = new ArrayList();
    private final TelephonyManager mTelephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
    private final SubscriptionManager mSubscriptionManager = (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);

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
    public boolean useDynamicSliceSummary() {
        return true;
    }

    public PhoneNumberPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mTelephonyManager.isVoiceCapable() ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return getFirstPhoneNumber();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_PREFERENCE_CATEGORY);
        this.mPreferenceList.add(findPreference);
        int order = findPreference.getOrder();
        for (int i = 1; i < this.mTelephonyManager.getPhoneCount(); i++) {
            Preference createNewPreference = createNewPreference(preferenceScreen.getContext());
            createNewPreference.setOrder(order + i);
            createNewPreference.setKey(KEY_PHONE_NUMBER + i);
            createNewPreference.setSelectable(false);
            preferenceCategory.addPreference(createNewPreference);
            this.mPreferenceList.add(createNewPreference);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        for (int i = 0; i < this.mPreferenceList.size(); i++) {
            Preference preference2 = this.mPreferenceList.get(i);
            preference2.setTitle(getPreferenceTitle(i));
            preference2.setSummary(getPhoneNumber(i));
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public void copy() {
        ((ClipboardManager) this.mContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("text", getFirstPhoneNumber()));
        Context context = this.mContext;
        Toast.makeText(this.mContext, context.getString(R.string.copyable_slice_toast, context.getText(R.string.status_number)), 0).show();
    }

    private CharSequence getFirstPhoneNumber() {
        List<SubscriptionInfo> activeSubscriptionInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList == null || activeSubscriptionInfoList.isEmpty()) {
            return this.mContext.getText(R.string.device_info_default);
        }
        return getFormattedPhoneNumber(activeSubscriptionInfoList.get(0));
    }

    private CharSequence getPhoneNumber(int i) {
        SubscriptionInfo subscriptionInfo = getSubscriptionInfo(i);
        if (subscriptionInfo == null) {
            return this.mContext.getText(R.string.device_info_default);
        }
        return getFormattedPhoneNumber(subscriptionInfo);
    }

    private CharSequence getPreferenceTitle(int i) {
        if (this.mTelephonyManager.getPhoneCount() > 1) {
            return this.mContext.getString(R.string.status_number_sim_slot, Integer.valueOf(i + 1));
        }
        return this.mContext.getString(R.string.status_number);
    }

    protected SubscriptionInfo getSubscriptionInfo(int i) {
        List<SubscriptionInfo> activeSubscriptionInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList == null) {
            return null;
        }
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
            if (subscriptionInfo.getSimSlotIndex() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    protected CharSequence getFormattedPhoneNumber(SubscriptionInfo subscriptionInfo) {
        String bidiFormattedPhoneNumber = DeviceInfoUtils.getBidiFormattedPhoneNumber(this.mContext, subscriptionInfo);
        return TextUtils.isEmpty(bidiFormattedPhoneNumber) ? this.mContext.getString(R.string.device_info_default) : bidiFormattedPhoneNumber;
    }

    protected Preference createNewPreference(Context context) {
        return new PhoneNumberSummaryPreference(context);
    }
}
