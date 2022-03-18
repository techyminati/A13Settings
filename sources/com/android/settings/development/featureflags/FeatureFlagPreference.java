package com.android.settings.development.featureflags;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import androidx.preference.SwitchPreference;
/* loaded from: classes.dex */
public class FeatureFlagPreference extends SwitchPreference {
    private final boolean mIsPersistent;
    private final String mKey;

    public FeatureFlagPreference(Context context, String str) {
        super(context);
        boolean z;
        this.mKey = str;
        setKey(str);
        setTitle(str);
        boolean isPersistent = FeatureFlagPersistent.isPersistent(str);
        this.mIsPersistent = isPersistent;
        if (isPersistent) {
            z = FeatureFlagPersistent.isEnabled(context, str);
        } else {
            z = FeatureFlagUtils.isEnabled(context, str);
        }
        super.setChecked(z);
    }

    @Override // androidx.preference.TwoStatePreference
    public void setChecked(boolean z) {
        super.setChecked(z);
        if (this.mIsPersistent) {
            FeatureFlagPersistent.setEnabled(getContext(), this.mKey, z);
        } else {
            FeatureFlagUtils.setEnabled(getContext(), this.mKey, z);
        }
        if (TextUtils.equals(this.mKey, "settings_hide_secondary_page_back_button_in_two_pane")) {
            Settings.Global.putString(getContext().getContentResolver(), this.mKey, String.valueOf(z));
        }
    }
}
